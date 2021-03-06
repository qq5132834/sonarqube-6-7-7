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
package org.sonarqube.tests.analysis;

import com.sonar.orchestrator.Orchestrator;
import com.sonar.orchestrator.build.BuildResult;
import com.sonar.orchestrator.build.SonarScanner;
import com.sonar.orchestrator.util.NetworkUtils;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.tests.Byteman;
import org.sonarqube.tests.Tester;
import org.sonarqube.ws.Common;
import org.sonarqube.ws.Issues;
import org.sonarqube.ws.Organizations.Organization;
import org.sonarqube.ws.QualityProfiles.CreateWsResponse.QualityProfile;
import org.sonarqube.ws.WsCe;
import org.sonarqube.ws.WsProjects;
import org.sonarqube.ws.WsUsers.CreateWsResponse.User;
import org.sonarqube.ws.client.ce.TaskWsRequest;
import org.sonarqube.ws.client.component.SuggestionsWsRequest;
import org.sonarqube.ws.client.issue.SearchWsRequest;
import util.ItUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.sonarqube.tests.Byteman.Process.CE;
import static org.sonarqube.ws.WsCe.TaskStatus.FAILED;
import static util.ItUtils.projectDir;

public class AnalysisEsResilienceTest {

  @ClassRule
  public static final Orchestrator orchestrator;
  private static final Byteman byteman;
  private static final int esHttpPort = NetworkUtils.getNextAvailablePort(InetAddress.getLoopbackAddress());

  static {
    byteman = new Byteman(Orchestrator.builderEnv(), CE);
    orchestrator = byteman
      .getOrchestratorBuilder()
      .addPlugin(ItUtils.xooPlugin())
      .setServerProperty("sonar.search.httpPort", "" + esHttpPort)
      .setServerProperty("sonar.sonarcloud.enabled", "true")
      .build();
  }

  @Rule
  public Tester tester = new Tester(orchestrator);

  @After
  public void after() throws Exception {
    byteman.deactivateAllRules();
    for (String index : Arrays.asList("issues", "rules", "users", "components", "views", "tests", "projectmeasures")) {
      tester.elasticsearch().unlockWrites(index);
    }
  }

  @Test
  public void activation_and_deactivation_of_rule_is_resilient_to_indexing_errors() throws Exception {
    Organization organization = tester.organizations().generate();
    User orgAdministrator = tester.users().generateAdministrator(organization);
    WsProjects.CreateWsResponse.Project project = tester.projects().generate(organization);
    String projectKey = project.getKey();
    String fileKey = projectKey + ":src/main/xoo/sample/Sample.xoo";
    String file2Key = projectKey + ":src/main/xoo/sample/Sample2.xoo";
    String file3Key = projectKey + ":src/main/xoo/sample/Sample3.xoo";

    QualityProfile profile = tester.qProfiles().createXooProfile(organization);
    tester.qProfiles()
      .activateRule(profile, "xoo:OneIssuePerFile")
      .assignQProfileToProject(profile, project);

    executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-sample-v1", null);
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    assertThat(searchFile(file2Key, organization)).isEmpty();
    assertThat(searchFile(file3Key, organization)).isEmpty();
    Issues.SearchWsResponse issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .extracting(Issues.Issue::getComponent)
      .containsExactlyInAnyOrder(fileKey);

    byteman.activateScript("resilience/making_ce_indexation_failing.btm");
    executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-sample-v2", null);
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    assertThat(searchFile(file2Key, organization)).isEmpty();// inconsistency: in DB there is also file2Key
    assertThat(searchFile(file3Key, organization)).isEmpty();// inconsistency: in DB there is also file3Key
    issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .extracting(Issues.Issue::getComponent)
      .containsExactlyInAnyOrder(fileKey /* inconsistency: in DB there is also file2Key and file3Key */);
    byteman.deactivateAllRules();

    executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-sample-v3", null);
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    assertThat(searchFile(file2Key, organization)).isEmpty();
    assertThat(searchFile(file3Key, organization)).isNotEmpty();
    issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .extracting(Issues.Issue::getComponent, Issues.Issue::getStatus)
      .containsExactlyInAnyOrder(
        tuple(fileKey, "OPEN"),
        tuple(file2Key, "CLOSED"),
        tuple(file3Key, "OPEN"));
  }

  @Test
  public void purge_mechanism_must_be_resilient_at_next_analysis() throws Exception {
    Organization organization = tester.organizations().generate();
    User orgAdministrator = tester.users().generateAdministrator(organization);
    WsProjects.CreateWsResponse.Project project = tester.projects().generate(organization);
    String projectKey = project.getKey();
    String fileKey = projectKey + ":src/main/xoo/sample/Sample.xoo";

    QualityProfile profile = tester.qProfiles().createXooProfile(organization);
    tester.qProfiles()
      .activateRule(profile, "xoo:OneIssuePerFile")
      .assignQProfileToProject(profile, project);

    executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-purge", "2000-01-01");
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    Issues.SearchWsResponse issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .extracting(Issues.Issue::getComponent)
      .containsExactlyInAnyOrder(fileKey);

    tester.qProfiles()
      .deactivateRule(profile, "xoo:OneIssuePerFile");

    // We are expecting the purge to fail during this analysis
    tester.elasticsearch().lockWrites("issues");
    String taskUuid = executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-purge", "2000-01-02");

    // The task has failed
    TaskWsRequest request = TaskWsRequest.newBuilder(taskUuid).withErrorStacktrace().build();
    WsCe.Task task = tester.wsClient().ce().task(request).getTask();
    assertThat(task.getStatus()).isEqualTo(WsCe.TaskStatus.FAILED);
    assertThat(task.getErrorMessage()).contains("Unrecoverable indexation failures");
    assertThat(task.getErrorStacktrace())
      .contains("Caused by: java.lang.IllegalStateException: Unrecoverable indexation failures");

    // The issue must be present with status CLOSED in database
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .extracting(Issues.Issue::getComponent, Issues.Issue::getStatus)
      .containsExactlyInAnyOrder(
        tuple(fileKey, "CLOSED"));

    // Now we have an inconstency : ES is seeing the issue as opened
    assertThat(issues.getFacets().getFacets(0).getValuesList())
      .extracting(Common.FacetValue::getVal, Common.FacetValue::getCount)
      .containsExactlyInAnyOrder(
        tuple("OPEN", 1L),
        tuple("CONFIRMED", 0L),
        tuple("REOPENED", 0L),
        tuple("RESOLVED", 0L),
        tuple("CLOSED", 0L)
      );

    tester.elasticsearch().unlockWrites("issues");

    // Second analysis must fix the issue,
    // The purge will delete the "old" issue
    executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-purge", null);
    assertThat(searchFile(fileKey, organization)).isNotEmpty();
    issues = searchIssues(projectKey);
    assertThat(issues.getIssuesList())
      .isEmpty();
    assertThat(issues.getFacets().getFacets(0).getValuesList())
      .extracting(Common.FacetValue::getVal, Common.FacetValue::getCount)
      .containsExactlyInAnyOrder(
        tuple("OPEN", 0L),
        tuple("CONFIRMED", 0L),
        tuple("REOPENED", 0L),
        tuple("RESOLVED", 0L),
        tuple("CLOSED", 0L)
      );
  }

  @Test
  public void compute_engine_task_must_be_red_when_es_is_not_available() throws Exception {
    Organization organization = tester.organizations().generate();
    User orgAdministrator = tester.users().generateAdministrator(organization);
    WsProjects.CreateWsResponse.Project project = tester.projects().generate(organization);
    String projectKey = project.getKey();
    String fileKey = projectKey + ":src/main/xoo/sample/Sample.xoo";

    QualityProfile profile = tester.qProfiles().createXooProfile(organization);
    tester.qProfiles()
      .activateRule(profile, "xoo:OneIssuePerFile")
      .assignQProfileToProject(profile, project);

    tester.elasticsearch().lockWrites("issues");

    String analysisKey = executeAnalysis(projectKey, organization, orgAdministrator, "analysis/resilience/resilience-sample-v1", null);
    WsCe.TaskResponse task = tester.wsClient().ce().task(analysisKey);

    assertThat(task.getTask().getStatus()).isEqualTo(FAILED);
  }

  private Issues.SearchWsResponse searchIssues(String projectKey) {
    SearchWsRequest request = new SearchWsRequest()
      .setProjectKeys(Collections.singletonList(projectKey))
      .setFacets(Collections.singletonList("statuses"));
    return tester.wsClient().issues().search(request);
  }

  private List<String> searchFile(String key, Organization organization) {
    SuggestionsWsRequest query = SuggestionsWsRequest.builder()
      .setS(key)
      .build();
    Map<String, Object> response = ItUtils.jsonToMap(
      tester.wsClient().components().suggestions(query).content()
    );
    List results = (List) response.get("results");
    Map trkResult = (Map) results.stream().filter(result -> "FIL".equals(((Map) result).get("q"))).findAny().get();
    List items = (List) trkResult.get("items");
    Stream<String> x = items.stream().map(item -> (String) ((Map) item).get("key"));
    return x.collect(Collectors.toList());
  }

  private String executeAnalysis(String projectKey, Organization organization, User orgAdministrator, String projectPath, @Nullable String date) {
    SonarScanner sonarScanner = SonarScanner.create(projectDir(projectPath),
      "sonar.organization", organization.getKey(),
      "sonar.projectKey", projectKey,
      "sonar.login", orgAdministrator.getLogin(),
      "sonar.password", orgAdministrator.getLogin());
    if (date != null) {
      sonarScanner.setProperty("sonar.projectDate", date);
    }
    BuildResult buildResult = orchestrator.executeBuild(sonarScanner);
    return ItUtils.extractCeTaskId(buildResult);
  }
}
