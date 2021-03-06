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
package org.sonar.db.purge;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.Dao;
import org.sonar.db.DbSession;
import org.sonar.db.component.BranchMapper;
import org.sonar.db.component.ComponentDao;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTreeQuery;
import org.sonar.db.component.ComponentTreeQuery.Strategy;

import static java.util.Collections.emptyList;
import static org.sonar.api.utils.DateUtils.dateToLong;
import static org.sonar.db.DatabaseUtils.executeLargeInputs;

public class PurgeDao implements Dao {
  private static final Logger LOG = Loggers.get(PurgeDao.class);
  private static final String[] UNPROCESSED_STATUS = new String[] {"U"};
  private static final ImmutableSet<String> QUALIFIERS_PROJECT_VIEW = ImmutableSet.of("TRK", "VW");
  private static final ImmutableSet<String> QUALIFIERS_MODULE_SUBVIEW = ImmutableSet.of("BRC", "SVW");
  private static final String SCOPE_PROJECT = "PRJ";

  private final ComponentDao componentDao;
  private final System2 system2;

  public PurgeDao(ComponentDao componentDao, System2 system2) {
    this.componentDao = componentDao;
    this.system2 = system2;
  }

  public void purge(DbSession session, PurgeConfiguration conf, PurgeListener listener, PurgeProfiler profiler) {
    PurgeMapper mapper = session.getMapper(PurgeMapper.class);
    PurgeCommands commands = new PurgeCommands(session, mapper, profiler);
    String rootUuid = conf.rootProjectIdUuid().getUuid();
    deleteAbortedAnalyses(rootUuid, commands);
    deleteDataOfComponentsWithoutHistoricalData(session, rootUuid, conf.getScopesWithoutHistoricalData(), commands);
    purgeAnalyses(commands, rootUuid);
    purgeDisabledComponents(session, conf, listener);
    deleteOldClosedIssues(conf, mapper, listener);
    purgeStaleBranches(commands, conf, mapper, rootUuid);
  }

  private static void purgeStaleBranches(PurgeCommands commands, PurgeConfiguration conf, PurgeMapper mapper, String rootUuid) {
    Optional<Date> maxDate = conf.maxLiveDateOfInactiveShortLivingBranches();
    if (!maxDate.isPresent()) {
      // not available if branch plugin is not installed
      return;
    }
    LOG.debug("<- Purge stale branches");

    List<String> branchUuids = mapper.selectStaleShortLivingBranches(rootUuid, dateToLong(maxDate.get()));

    for (String branchUuid : branchUuids) {
      deleteRootComponent(branchUuid, mapper, commands);
    }
  }

  private static void purgeAnalyses(PurgeCommands commands, String rootUuid) {
    List<IdUuidPair> analysisUuids = commands.selectSnapshotIdUuids(
      new PurgeSnapshotQuery()
        .setComponentUuid(rootUuid)
        .setIslast(false)
        .setNotPurged(true));
    commands.purgeAnalyses(analysisUuids);
  }

  private static void deleteOldClosedIssues(PurgeConfiguration conf, PurgeMapper mapper, PurgeListener listener) {
    Date toDate = conf.maxLiveDateOfClosedIssues();
    String rootUuid = conf.rootProjectIdUuid().getUuid();
    List<String> issueKeys = mapper.selectOldClosedIssueKeys(rootUuid, dateToLong(toDate));
    executeLargeInputs(issueKeys, input -> {
      mapper.deleteIssueChangesFromIssueKeys(input);
      return emptyList();
    });
    executeLargeInputs(issueKeys, input -> {
      mapper.deleteIssuesFromKeys(input);
      return emptyList();
    });
    listener.onIssuesRemoval(rootUuid, issueKeys);
  }

  private static void deleteAbortedAnalyses(String rootUuid, PurgeCommands commands) {
    LOG.debug("<- Delete aborted builds");
    PurgeSnapshotQuery query = new PurgeSnapshotQuery()
      .setIslast(false)
      .setStatus(UNPROCESSED_STATUS)
      .setComponentUuid(rootUuid);
    commands.deleteAnalyses(query);
  }

  private void deleteDataOfComponentsWithoutHistoricalData(DbSession dbSession, String rootUuid, Collection<String> scopesWithoutHistoricalData, PurgeCommands purgeCommands) {
    if (scopesWithoutHistoricalData.isEmpty()) {
      return;
    }

    List<String> analysisUuids = purgeCommands.selectSnapshotUuids(
      new PurgeSnapshotQuery()
        .setComponentUuid(rootUuid)
        .setIslast(false)
        .setNotPurged(true));
    List<String> componentWithoutHistoricalDataUuids = componentDao
      .selectDescendants(
        dbSession,
        ComponentTreeQuery.builder()
          .setBaseUuid(rootUuid)
          .setScopes(scopesWithoutHistoricalData)
          .setStrategy(Strategy.LEAVES)
          .build())
      .stream().map(ComponentDto::uuid)
      .collect(MoreCollectors.toList());

    purgeCommands.deleteComponentMeasures(analysisUuids, componentWithoutHistoricalDataUuids);
  }

  private void purgeDisabledComponents(DbSession session, PurgeConfiguration conf, PurgeListener listener) {
    PurgeMapper mapper = mapper(session);
    executeLargeInputs(conf.getDisabledComponentUuids(),
      input -> {
        mapper.deleteFileSourcesByFileUuid(input);
        mapper.resolveComponentIssuesNotAlreadyResolved(input, system2.now());
        return emptyList();
      });

    listener.onComponentsDisabling(conf.rootProjectIdUuid().getUuid(), conf.getDisabledComponentUuids());

    session.commit();
  }

  public List<PurgeableAnalysisDto> selectPurgeableAnalyses(String componentUuid, DbSession session) {
    List<PurgeableAnalysisDto> result = Lists.newArrayList();
    result.addAll(mapper(session).selectPurgeableAnalysesWithEvents(componentUuid));
    result.addAll(mapper(session).selectPurgeableAnalysesWithoutEvents(componentUuid));
    // sort by date
    Collections.sort(result);
    return result;
  }

  public void deleteBranch(DbSession session, String uuid) {
    PurgeProfiler profiler = new PurgeProfiler();
    PurgeMapper purgeMapper = mapper(session);
    PurgeCommands purgeCommands = new PurgeCommands(session, profiler);
    deleteRootComponent(uuid, purgeMapper, purgeCommands);
  }

  public void deleteProject(DbSession session, String uuid) {
    PurgeProfiler profiler = new PurgeProfiler();
    PurgeMapper purgeMapper = mapper(session);
    PurgeCommands purgeCommands = new PurgeCommands(session, profiler);

    session.getMapper(BranchMapper.class).selectByProjectUuid(uuid)
      .stream()
      .filter(branch -> !uuid.equals(branch.getUuid()))
      .forEach(branch -> deleteRootComponent(branch.getUuid(), purgeMapper, purgeCommands));

    deleteRootComponent(uuid, purgeMapper, purgeCommands);
  }

  private static void deleteRootComponent(String rootUuid, PurgeMapper mapper, PurgeCommands commands) {
    List<IdUuidPair> rootAndModulesOrSubviews = mapper.selectRootAndModulesOrSubviewsByProjectUuid(rootUuid);
    long rootId = rootAndModulesOrSubviews.stream()
      .filter(pair -> pair.getUuid().equals(rootUuid))
      .map(IdUuidPair::getId)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Couldn't find root component with uuid " + rootUuid));
    commands.deletePermissions(rootId);
    commands.deleteLinks(rootUuid);
    commands.deleteAnalyses(rootUuid);
    commands.deleteByRootAndModulesOrSubviews(rootAndModulesOrSubviews);
    commands.deleteComponents(rootUuid);
    commands.deleteIssues(rootUuid);
    commands.deleteFileSources(rootUuid);
    commands.deleteCeActivity(rootUuid);
    commands.deleteCeQueue(rootUuid);
    commands.deleteWebhookDeliveries(rootUuid);
    commands.deleteBranch(rootUuid);
  }

  /**
   * Delete the non root components (ie. sub-view, application or project copy) from the specified collection of {@link ComponentDto}
   * and data from their child tables.
   * <p>
   *   This method has no effect when passed an empty collection or only root components.
   * </p>
   */
  public void deleteNonRootComponentsInView(DbSession dbSession, Collection<ComponentDto> components) {
    Set<ComponentDto> nonRootComponents = components.stream().filter(PurgeDao::isNotRoot).collect(MoreCollectors.toSet());
    if (nonRootComponents.isEmpty()) {
      return;
    }

    PurgeProfiler profiler = new PurgeProfiler();
    PurgeCommands purgeCommands = new PurgeCommands(dbSession, profiler);
    deleteNonRootComponentsInView(nonRootComponents, purgeCommands);
  }

  private static void deleteNonRootComponentsInView(Set<ComponentDto> nonRootComponents, PurgeCommands purgeCommands) {
    List<IdUuidPair> subviewsOrProjectCopies = nonRootComponents.stream()
      .filter(PurgeDao::isModuleOrSubview)
      .map(PurgeDao::toIdUuidPair)
      .collect(MoreCollectors.toList());
    purgeCommands.deleteByRootAndModulesOrSubviews(subviewsOrProjectCopies);
    List<String> nonRootComponentUuids = nonRootComponents.stream().map(ComponentDto::uuid).collect(MoreCollectors.toList(nonRootComponents.size()));
    purgeCommands.deleteComponentMeasures(nonRootComponentUuids);
    purgeCommands.deleteComponents(nonRootComponentUuids);
  }

  private static boolean isNotRoot(ComponentDto dto) {
    return !(SCOPE_PROJECT.equals(dto.scope()) && QUALIFIERS_PROJECT_VIEW.contains(dto.qualifier()));
  }

  private static boolean isModuleOrSubview(ComponentDto dto) {
    return SCOPE_PROJECT.equals(dto.scope()) && QUALIFIERS_MODULE_SUBVIEW.contains(dto.qualifier());
  }

  private static IdUuidPair toIdUuidPair(ComponentDto dto) {
    return new IdUuidPair(dto.getId(), dto.uuid());
  }

  public void deleteAnalyses(DbSession session, PurgeProfiler profiler, List<IdUuidPair> analysisIdUuids) {
    new PurgeCommands(session, profiler).deleteAnalyses(analysisIdUuids);
  }

  private static PurgeMapper mapper(DbSession session) {
    return session.getMapper(PurgeMapper.class);
  }
}
