/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.projectbranch.ws;

import com.google.common.io.Resources;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.BranchDto;
import org.sonar.db.component.BranchType;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.measure.MeasureDto;
import org.sonar.db.metric.MetricDto;
import org.sonar.db.permission.OrganizationPermission;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.issue.index.BranchStatistics;
import org.sonar.server.issue.index.IssueIndex;
import org.sonar.server.user.UserSession;
import org.sonar.server.ws.WsUtils;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.WsBranches;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static org.sonar.api.measures.CoreMetrics.ALERT_STATUS_KEY;
import static org.sonar.api.resources.Qualifiers.PROJECT;
import static org.sonar.api.utils.DateUtils.formatDateTime;
import static org.sonar.core.permission.GlobalPermissions.SCAN_EXECUTION;
import static org.sonar.core.util.Protobuf.setNullable;
import static org.sonar.core.util.stream.MoreCollectors.toList;
import static org.sonar.core.util.stream.MoreCollectors.uniqueIndex;
import static org.sonar.db.component.BranchType.LONG;
import static org.sonar.db.component.BranchType.SHORT;
import static org.sonar.server.projectbranch.ws.BranchesWs.addProjectParam;
import static org.sonar.server.user.AbstractUserSession.insufficientPrivilegesException;
import static org.sonarqube.ws.client.projectbranches.ProjectBranchesParameters.ACTION_LIST;
import static org.sonarqube.ws.client.projectbranches.ProjectBranchesParameters.PARAM_PROJECT;

public class ListAction implements BranchWsAction {

  private final DbClient dbClient;
  private final UserSession userSession;
  private final ComponentFinder componentFinder;
  private final IssueIndex issueIndex;

  public ListAction(DbClient dbClient, UserSession userSession, ComponentFinder componentFinder, IssueIndex issueIndex) {
    this.dbClient = dbClient;
    this.userSession = userSession;
    this.componentFinder = componentFinder;
    this.issueIndex = issueIndex;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction(ACTION_LIST)
      .setSince("6.6")
      .setDescription("List the branches of a project.<br/>" +
        "Requires 'Browse' or 'Execute analysis' rights on the specified project.")
      .setResponseExample(Resources.getResource(getClass(), "list-example.json"))
      .setHandler(this);

    addProjectParam(action);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    String projectKey = request.mandatoryParam(PARAM_PROJECT);

    try (DbSession dbSession = dbClient.openSession(false)) {
      ComponentDto project = componentFinder.getByKey(dbSession, projectKey);
      checkPermission(project);
      checkArgument(project.isEnabled() && PROJECT.equals(project.qualifier()), "Invalid project key");

      Collection<BranchDto> branches = dbClient.branchDao().selectByComponent(dbSession, project);
      MetricDto qualityGateMetric = dbClient.metricDao().selectOrFailByKey(dbSession, ALERT_STATUS_KEY);
      Map<String, BranchDto> mergeBranchesByUuid = dbClient.branchDao()
        .selectByUuids(dbSession, branches.stream().map(BranchDto::getMergeBranchUuid).filter(Objects::nonNull).collect(toList()))
        .stream().collect(uniqueIndex(BranchDto::getUuid));
      Map<String, MeasureDto> qualityGateMeasuresByComponentUuids = dbClient.measureDao()
        .selectByComponentsAndMetrics(dbSession, branches.stream().map(BranchDto::getUuid).collect(toList()), singletonList(qualityGateMetric.getId()))
        .stream().collect(uniqueIndex(MeasureDto::getComponentUuid));
      Map<String, BranchStatistics> branchStatisticsByBranchUuid = issueIndex.searchBranchStatistics(project.uuid(), branches.stream()
        .filter(b -> b.getBranchType().equals(SHORT))
        .map(BranchDto::getUuid).collect(toList()))
        .stream().collect(uniqueIndex(BranchStatistics::getBranchUuid, Function.identity()));
      Map<String, String> analysisDateByBranchUuid = dbClient.snapshotDao()
        .selectLastAnalysesByRootComponentUuids(dbSession, branches.stream().map(BranchDto::getUuid).collect(Collectors.toList()))
        .stream().collect(uniqueIndex(SnapshotDto::getComponentUuid, s -> formatDateTime(s.getCreatedAt())));

      WsBranches.ListWsResponse.Builder protobufResponse = WsBranches.ListWsResponse.newBuilder();
      branches.stream()
        .forEach(b -> addBranch(protobufResponse, b, mergeBranchesByUuid, qualityGateMeasuresByComponentUuids.get(b.getUuid()), branchStatisticsByBranchUuid.get(b.getUuid()),
          analysisDateByBranchUuid.get(b.getUuid())));
      WsUtils.writeProtobuf(protobufResponse.build(), request, response);
    }
  }

  private static void addBranch(WsBranches.ListWsResponse.Builder response, BranchDto branch, Map<String, BranchDto> mergeBranchesByUuid, @Nullable MeasureDto qualityGateMeasure,
    BranchStatistics branchStatistics, @Nullable String analysisDate) {
    WsBranches.Branch.Builder builder = toBranchBuilder(branch, Optional.ofNullable(mergeBranchesByUuid.get(branch.getMergeBranchUuid())));
    setBranchStatus(builder, branch, qualityGateMeasure, branchStatistics);
    if (analysisDate != null) {
      builder.setAnalysisDate(analysisDate);
    }
    response.addBranches(builder);
  }

  private static WsBranches.Branch.Builder toBranchBuilder(BranchDto branch, Optional<BranchDto> mergeBranch) {
    WsBranches.Branch.Builder builder = WsBranches.Branch.newBuilder();
    String branchKey = branch.getKey();
    setNullable(branchKey, builder::setName);
    builder.setIsMain(branch.isMain());
    builder.setType(Common.BranchType.valueOf(branch.getBranchType().name()));
    if (branch.getBranchType().equals(SHORT)) {
      if (mergeBranch.isPresent()) {
        String mergeBranchKey = mergeBranch.get().getKey();
        builder.setMergeBranch(mergeBranchKey);
      } else {
        builder.setIsOrphan(true);
      }
    }
    return builder;
  }

  private static void setBranchStatus(WsBranches.Branch.Builder builder, BranchDto branch, @Nullable MeasureDto qualityGateMeasure, @Nullable BranchStatistics branchStatistics) {
    WsBranches.Branch.Status.Builder statusBuilder = WsBranches.Branch.Status.newBuilder();
    if (branch.getBranchType() == LONG && qualityGateMeasure != null) {
      statusBuilder.setQualityGateStatus(qualityGateMeasure.getData());
    }
    if (branch.getBranchType() == BranchType.SHORT) {
      statusBuilder.setBugs(branchStatistics == null ? 0L : branchStatistics.getBugs());
      statusBuilder.setVulnerabilities(branchStatistics == null ? 0L : branchStatistics.getVulnerabilities());
      statusBuilder.setCodeSmells(branchStatistics == null ? 0L : branchStatistics.getCodeSmells());
    }
    builder.setStatus(statusBuilder);
  }

  private void checkPermission(ComponentDto component) {
    if (!userSession.hasComponentPermission(UserRole.USER, component) &&
      !userSession.hasComponentPermission(SCAN_EXECUTION, component) &&
      !userSession.hasPermission(OrganizationPermission.SCAN, component.getOrganizationUuid())) {
      throw insufficientPrivilegesException();
    }
  }
}
