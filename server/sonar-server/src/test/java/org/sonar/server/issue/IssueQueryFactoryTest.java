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
package org.sonar.server.issue;

import java.time.Clock;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.DateUtils;
import org.sonar.db.DbTester;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.SnapshotDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.exceptions.BadRequestException;
import org.sonar.server.tester.UserSessionRule;
import org.sonarqube.ws.client.issue.SearchWsRequest;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.guava.api.Assertions.entry;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.sonar.api.utils.DateUtils.addDays;
import static org.sonar.db.component.ComponentTesting.newDirectory;
import static org.sonar.db.component.ComponentTesting.newFileDto;
import static org.sonar.db.component.ComponentTesting.newModuleDto;
import static org.sonar.db.component.ComponentTesting.newProjectCopy;
import static org.sonar.db.component.ComponentTesting.newSubView;

public class IssueQueryFactoryTest {

  @Rule
  public UserSessionRule userSession = UserSessionRule.standalone();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  @Rule
  public DbTester db = DbTester.create();

  private Clock clock = mock(Clock.class);
  private IssueQueryFactory underTest = new IssueQueryFactory(db.getDbClient(), clock, userSession);

  @Test
  public void create_from_parameters() {
    OrganizationDto organization = db.organizations().insert();
    ComponentDto project = db.components().insertPrivateProject(organization);
    ComponentDto module = db.components().insertComponent(newModuleDto(project));
    ComponentDto file = db.components().insertComponent(newFileDto(project));

    SearchWsRequest request = new SearchWsRequest()
      .setIssues(asList("anIssueKey"))
      .setSeverities(asList("MAJOR", "MINOR"))
      .setStatuses(asList("CLOSED"))
      .setResolutions(asList("FALSE-POSITIVE"))
      .setResolved(true)
      .setProjectKeys(asList(project.getDbKey()))
      .setModuleUuids(asList(module.uuid()))
      .setDirectories(asList("aDirPath"))
      .setFileUuids(asList(file.uuid()))
      .setAssignees(asList("joanna"))
      .setLanguages(asList("xoo"))
      .setTags(asList("tag1", "tag2"))
      .setOrganization(organization.getKey())
      .setAssigned(true)
      .setCreatedAfter("2013-04-16T09:08:24+0200")
      .setCreatedBefore("2013-04-17T09:08:24+0200")
      .setRules(asList("squid:AvoidCycle", "findbugs:NullReference"))
      .setSort("CREATION_DATE")
      .setAsc(true);

    IssueQuery query = underTest.create(request);

    assertThat(query.issueKeys()).containsOnly("anIssueKey");
    assertThat(query.severities()).containsOnly("MAJOR", "MINOR");
    assertThat(query.statuses()).containsOnly("CLOSED");
    assertThat(query.resolutions()).containsOnly("FALSE-POSITIVE");
    assertThat(query.resolved()).isTrue();
    assertThat(query.projectUuids()).containsOnly(project.uuid());
    assertThat(query.moduleUuids()).containsOnly(module.uuid());
    assertThat(query.fileUuids()).containsOnly(file.uuid());
    assertThat(query.assignees()).containsOnly("joanna");
    assertThat(query.languages()).containsOnly("xoo");
    assertThat(query.tags()).containsOnly("tag1", "tag2");
    assertThat(query.organizationUuid()).isEqualTo(organization.getUuid());
    assertThat(query.onComponentOnly()).isFalse();
    assertThat(query.assigned()).isTrue();
    assertThat(query.rules()).hasSize(2);
    assertThat(query.directories()).containsOnly("aDirPath");
    assertThat(query.createdAfter().date()).isEqualTo(DateUtils.parseDateTime("2013-04-16T09:08:24+0200"));
    assertThat(query.createdAfter().inclusive()).isTrue();
    assertThat(query.createdBefore()).isEqualTo(DateUtils.parseDateTime("2013-04-17T09:08:24+0200"));
    assertThat(query.sort()).isEqualTo(IssueQuery.SORT_BY_CREATION_DATE);
    assertThat(query.asc()).isTrue();
  }

  @Test
  public void leak_period_start_date_is_exclusive() {
    long leakPeriodStart = addDays(new Date(), -14).getTime();

    ComponentDto project = db.components().insertPublicProject();
    ComponentDto file = db.components().insertComponent(newFileDto(project));

    SnapshotDto analysis = db.components().insertSnapshot(project, s -> s.setPeriodDate(leakPeriodStart));

    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(Collections.singletonList(file.uuid()))
      .setOnComponentOnly(true)
      .setSinceLeakPeriod(true);

    IssueQuery query = underTest.create(request);

    assertThat(query.componentUuids()).containsOnly(file.uuid());
    assertThat(query.createdAfter().date()).isEqualTo(new Date(leakPeriodStart));
    assertThat(query.createdAfter().inclusive()).isFalse();

  }

  @Test
  public void dates_are_inclusive() {
    SearchWsRequest request = new SearchWsRequest()
      .setCreatedAfter("2013-04-16")
      .setCreatedBefore("2013-04-17");

    IssueQuery query = underTest.create(request);

    assertThat(query.createdAfter().date()).isEqualTo(DateUtils.parseDate("2013-04-16"));
    assertThat(query.createdAfter().inclusive()).isTrue();
    assertThat(query.createdBefore()).isEqualTo(DateUtils.parseDate("2013-04-18"));
  }

  @Test
  public void creation_date_support_localdate() {
    SearchWsRequest request = new SearchWsRequest()
      .setCreatedAt("2013-04-16");

    IssueQuery query = underTest.create(request);

    assertThat(query.createdAt()).isEqualTo(DateUtils.parseDate("2013-04-16"));
  }

  @Test
  public void creation_date_support_zoneddatetime() {
    SearchWsRequest request = new SearchWsRequest()
      .setCreatedAt("2013-04-16T09:08:24+0200");

    IssueQuery query = underTest.create(request);

    assertThat(query.createdAt()).isEqualTo(DateUtils.parseDateTime("2013-04-16T09:08:24+0200"));
  }

  @Test
  public void add_unknown_when_no_component_found() {
    SearchWsRequest request = new SearchWsRequest()
      .setComponentKeys(asList("does_not_exist"));

    IssueQuery query = underTest.create(request);

    assertThat(query.componentUuids()).containsOnly("<UNKNOWN>");
  }

  @Test
  public void query_without_any_parameter() {
    SearchWsRequest request = new SearchWsRequest();

    IssueQuery query = underTest.create(request);

    assertThat(query.componentUuids()).isEmpty();
    assertThat(query.projectUuids()).isEmpty();
    assertThat(query.moduleUuids()).isEmpty();
    assertThat(query.moduleRootUuids()).isEmpty();
    assertThat(query.directories()).isEmpty();
    assertThat(query.fileUuids()).isEmpty();
    assertThat(query.viewUuids()).isEmpty();
    assertThat(query.organizationUuid()).isNull();
    assertThat(query.branchUuid()).isNull();
  }

  @Test
  public void parse_list_of_rules() {
    assertThat(IssueQueryFactory.toRules(null)).isNull();
    assertThat(IssueQueryFactory.toRules("")).isEmpty();
    assertThat(IssueQueryFactory.toRules("squid:AvoidCycle")).containsOnly(RuleKey.of("squid", "AvoidCycle"));
    assertThat(IssueQueryFactory.toRules("squid:AvoidCycle,findbugs:NullRef")).containsOnly(RuleKey.of("squid", "AvoidCycle"), RuleKey.of("findbugs", "NullRef"));
    assertThat(IssueQueryFactory.toRules(asList("squid:AvoidCycle", "findbugs:NullRef"))).containsOnly(RuleKey.of("squid", "AvoidCycle"), RuleKey.of("findbugs", "NullRef"));
  }

  @Test
  public void fail_if_components_and_components_uuid_params_are_set_at_the_same_time() {
    SearchWsRequest request = new SearchWsRequest()
      .setComponentKeys(asList("foo"))
      .setComponentUuids(asList("bar"));

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("At most one of the following parameters can be provided: componentKeys, componentUuids, components, componentRoots, componentUuids");

    underTest.create(request);
  }

  @Test
  public void fail_if_both_projects_and_projectUuids_params_are_set() {
    SearchWsRequest request = new SearchWsRequest()
      .setProjectKeys(asList("foo"))
      .setProjectUuids(asList("bar"));

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("Parameters projects and projectUuids cannot be set simultaneously");

    underTest.create(request);
  }

  @Test
  public void fail_if_both_componentRoots_and_componentRootUuids_params_are_set() {
    SearchWsRequest request = new SearchWsRequest()
      .setComponentRoots(asList("foo"))
      .setComponentRootUuids(asList("bar"));

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("At most one of the following parameters can be provided: componentKeys, componentUuids, components, componentRoots, componentUuids");

    underTest.create(request);
  }

  @Test
  public void fail_if_componentRoots_references_components_with_different_qualifier() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto file = db.components().insertComponent(newFileDto(project));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentRoots(asList(project.getDbKey(), file.getDbKey()));

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("All components must have the same qualifier, found FIL,TRK");

    underTest.create(request);
  }

  @Test
  public void param_componentRootUuids_enables_search_in_view_tree_if_user_has_permission_on_view() {
    ComponentDto view = db.components().insertView();
    SearchWsRequest request = new SearchWsRequest()
      .setComponentRootUuids(asList(view.uuid()));
    userSession.registerComponents(view);

    IssueQuery query = underTest.create(request);

    assertThat(query.viewUuids()).containsOnly(view.uuid());
    assertThat(query.onComponentOnly()).isFalse();
  }

  @Test
  public void application_search_project_issues() {
    ComponentDto project1 = db.components().insertPublicProject();
    ComponentDto project2 = db.components().insertPublicProject();
    ComponentDto application = db.components().insertApplication(db.getDefaultOrganization());
    db.components().insertComponents(newProjectCopy("PC1", project1, application));
    db.components().insertComponents(newProjectCopy("PC2", project2, application));
    userSession.registerComponents(application, project1, project2);

    IssueQuery result = underTest.create(new SearchWsRequest().setComponentUuids(singletonList(application.uuid())));

    assertThat(result.viewUuids()).containsExactlyInAnyOrder(application.uuid());
  }

  @Test
  public void application_search_project_issues_on_leak() {
    Date now = new Date();
    ComponentDto project1 = db.components().insertPublicProject();
    SnapshotDto analysis1 = db.components().insertSnapshot(project1, s -> s.setPeriodDate(addDays(now, -14).getTime()));
    ComponentDto project2 = db.components().insertPublicProject();
    SnapshotDto analysis2 = db.components().insertSnapshot(project2, s -> s.setPeriodDate(null));
    ComponentDto project3 = db.components().insertPublicProject();
    ComponentDto application = db.components().insertApplication(db.getDefaultOrganization());
    db.components().insertComponents(newProjectCopy("PC1", project1, application));
    db.components().insertComponents(newProjectCopy("PC2", project2, application));
    db.components().insertComponents(newProjectCopy("PC3", project3, application));
    userSession.registerComponents(application, project1, project2, project3);

    IssueQuery result = underTest.create(new SearchWsRequest()
      .setComponentUuids(singletonList(application.uuid()))
      .setSinceLeakPeriod(true));

    assertThat(result.createdAfterByProjectUuids()).hasSize(1);
    assertThat(result.createdAfterByProjectUuids().get(project1.uuid()).date().getTime()).isEqualTo(analysis1.getPeriodDate());
    assertThat(result.createdAfterByProjectUuids().get(project1.uuid()).inclusive()).isFalse();
    assertThat(result.viewUuids()).containsExactlyInAnyOrder(application.uuid());
  }

  @Test
  public void return_empty_results_if_not_allowed_to_search_for_subview() {
    ComponentDto view = db.components().insertView();
    ComponentDto subView = db.components().insertComponent(newSubView(view));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentRootUuids(asList(subView.uuid()));

    IssueQuery query = underTest.create(request);

    assertThat(query.viewUuids()).containsOnly("<UNKNOWN>");
  }

  @Test
  public void param_componentUuids_enables_search_on_project_tree_by_default() {
    ComponentDto project = db.components().insertPrivateProject();
    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(asList(project.uuid()));

    IssueQuery query = underTest.create(request);
    assertThat(query.projectUuids()).containsExactly(project.uuid());
    assertThat(query.onComponentOnly()).isFalse();
  }

  @Test
  public void onComponentOnly_restricts_search_to_specified_componentKeys() {
    ComponentDto project = db.components().insertPrivateProject();
    SearchWsRequest request = new SearchWsRequest()
      .setComponentKeys(asList(project.getDbKey()))
      .setOnComponentOnly(true);

    IssueQuery query = underTest.create(request);

    assertThat(query.projectUuids()).isEmpty();
    assertThat(query.componentUuids()).containsExactly(project.uuid());
    assertThat(query.onComponentOnly()).isTrue();
  }

  @Test
  public void should_search_in_tree_with_module_uuid() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto module = db.components().insertComponent(newModuleDto(project));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(asList(module.uuid()));

    IssueQuery query = underTest.create(request);
    assertThat(query.moduleRootUuids()).containsExactly(module.uuid());
    assertThat(query.onComponentOnly()).isFalse();
  }

  @Test
  public void param_componentUuids_enables_search_in_directory_tree() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto dir = db.components().insertComponent(newDirectory(project, "src/main/java/foo"));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(asList(dir.uuid()));

    IssueQuery query = underTest.create(request);

    assertThat(query.moduleUuids()).containsOnly(dir.moduleUuid());
    assertThat(query.directories()).containsOnly(dir.path());
    assertThat(query.onComponentOnly()).isFalse();
  }

  @Test
  public void param_componentUuids_enables_search_by_file() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto file = db.components().insertComponent(newFileDto(project));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(asList(file.uuid()));

    IssueQuery query = underTest.create(request);

    assertThat(query.fileUuids()).containsExactly(file.uuid());
  }

  @Test
  public void param_componentUuids_enables_search_by_test_file() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto file = db.components().insertComponent(newFileDto(project).setQualifier(Qualifiers.UNIT_TEST_FILE));
    SearchWsRequest request = new SearchWsRequest()
      .setComponentUuids(asList(file.uuid()));

    IssueQuery query = underTest.create(request);

    assertThat(query.fileUuids()).containsExactly(file.uuid());
  }

  @Test
  public void search_issue_from_branch() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto branch = db.components().insertProjectBranch(project);

    assertThat(underTest.create(new SearchWsRequest()
      .setProjectKeys(singletonList(branch.getKey()))
      .setBranch(branch.getBranch())))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.projectUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(project.uuid()), false);

    assertThat(underTest.create(new SearchWsRequest()
      .setComponentKeys(singletonList(branch.getKey()))
      .setBranch(branch.getBranch())))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.projectUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(project.uuid()), false);
  }

  @Test
  public void search_file_issue_from_branch() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto branch = db.components().insertProjectBranch(project);
    ComponentDto file = db.components().insertComponent(newFileDto(branch));

    assertThat(underTest.create(new SearchWsRequest()
      .setComponentKeys(singletonList(file.getKey()))
      .setBranch(branch.getBranch())))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.fileUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(file.uuid()), false);

    assertThat(underTest.create(new SearchWsRequest()
      .setComponentKeys(singletonList(branch.getKey()))
      .setFileUuids(singletonList(file.uuid()))
      .setBranch(branch.getBranch())))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.fileUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(file.uuid()), false);

    assertThat(underTest.create(new SearchWsRequest()
      .setProjectKeys(singletonList(branch.getKey()))
      .setFileUuids(singletonList(file.uuid()))
      .setBranch(branch.getBranch())))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.fileUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(file.uuid()), false);
  }

  @Test
  public void search_issue_on_component_only_from_branch() {
    ComponentDto project = db.components().insertPrivateProject();
    ComponentDto branch = db.components().insertProjectBranch(project);
    ComponentDto file = db.components().insertComponent(newFileDto(branch));

    assertThat(underTest.create(new SearchWsRequest()
      .setComponentKeys(singletonList(file.getKey()))
      .setBranch(branch.getBranch())
      .setOnComponentOnly(true)))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.componentUuids()), IssueQuery::isMainBranch)
        .containsOnly(branch.uuid(), singletonList(file.uuid()), false);
  }

  @Test
  public void search_issues_from_main_branch() {
    ComponentDto project = db.components().insertMainBranch();
    ComponentDto branch = db.components().insertProjectBranch(project);

    assertThat(underTest.create(new SearchWsRequest()
      .setProjectKeys(singletonList(project.getKey()))
      .setBranch("master")))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.projectUuids()), IssueQuery::isMainBranch)
        .containsOnly(project.uuid(), singletonList(project.uuid()), true);
    assertThat(underTest.create(new SearchWsRequest()
      .setComponentKeys(singletonList(project.getKey()))
      .setBranch("master")))
        .extracting(IssueQuery::branchUuid, query -> new ArrayList<>(query.projectUuids()), IssueQuery::isMainBranch)
        .containsOnly(project.uuid(), singletonList(project.uuid()), true);
  }

  @Test
  public void fail_if_created_after_and_created_since_are_both_set() {
    SearchWsRequest request = new SearchWsRequest()
      .setCreatedAfter("2013-07-25T07:35:00+0100")
      .setCreatedInLast("palap");

    try {
      underTest.create(request);
      fail();
    } catch (Exception e) {
      assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("Parameters createdAfter and createdInLast cannot be set simultaneously");
    }
  }

  @Test
  public void set_created_after_from_created_since() {
    Date now = DateUtils.parseDateTime("2013-07-25T07:35:00+0100");
    when(clock.instant()).thenReturn(now.toInstant());
    when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    SearchWsRequest request = new SearchWsRequest()
      .setCreatedInLast("1y2m3w4d");
    assertThat(underTest.create(request).createdAfter().date()).isEqualTo(DateUtils.parseDateTime("2012-04-30T07:35:00+0100"));
    assertThat(underTest.create(request).createdAfter().inclusive()).isTrue();

  }

  @Test
  public void fail_if_since_leak_period_and_created_after_set_at_the_same_time() {
    expectedException.expect(BadRequestException.class);
    expectedException.expectMessage("Parameters 'createdAfter' and 'sinceLeakPeriod' cannot be set simultaneously");

    underTest.create(new SearchWsRequest()
      .setSinceLeakPeriod(true)
      .setCreatedAfter("2013-07-25T07:35:00+0100"));
  }

  @Test
  public void fail_if_no_component_provided_with_since_leak_period() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("One and only one component must be provided when searching since leak period");

    underTest.create(new SearchWsRequest().setSinceLeakPeriod(true));
  }

  @Test
  public void fail_if_several_components_provided_with_since_leak_period() {
    ComponentDto project1 = db.components().insertPrivateProject();
    ComponentDto project2 = db.components().insertPrivateProject();

    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("One and only one component must be provided when searching since leak period");

    underTest.create(new SearchWsRequest()
      .setSinceLeakPeriod(true)
      .setComponentKeys(asList(project1.getKey(), project2.getKey())));
  }

  @Test
  public void fail_if_date_is_not_formatted_correctly() {
    expectedException.expect(IllegalArgumentException.class);
    expectedException.expectMessage("'unknown-date' cannot be parsed as either a date or date+time");

    underTest.create(new SearchWsRequest()
      .setCreatedAfter("unknown-date"));
  }

  @Test
  public void return_empty_results_if_organization_with_specified_key_does_not_exist() {
    SearchWsRequest request = new SearchWsRequest()
      .setOrganization("does_not_exist");

    IssueQuery query = underTest.create(request);

    assertThat(query.organizationUuid()).isEqualTo("<UNKNOWN>");
  }

}
