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
package org.sonar.server.projecttag.ws;

import java.io.IOException;
import javax.annotation.Nullable;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.api.config.internal.MapSettings;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentTesting;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.organization.OrganizationTesting;
import org.sonar.server.es.EsTester;
import org.sonar.server.measure.index.ProjectMeasuresDoc;
import org.sonar.server.measure.index.ProjectMeasuresIndex;
import org.sonar.server.measure.index.ProjectMeasuresIndexDefinition;
import org.sonar.server.measure.index.ProjectMeasuresIndexer;
import org.sonar.server.permission.index.AuthorizationTypeSupport;
import org.sonar.server.permission.index.PermissionIndexerDao;
import org.sonar.server.permission.index.PermissionIndexerTester;
import org.sonar.server.tester.UserSessionRule;
import org.sonar.server.ws.TestRequest;
import org.sonar.server.ws.WsActionTester;
import org.sonarqube.ws.WsProjectTags.SearchResponse;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.sonar.core.util.Protobuf.setNullable;
import static org.sonar.server.measure.index.ProjectMeasuresIndexDefinition.INDEX_TYPE_PROJECT_MEASURES;
import static org.sonar.test.JsonAssert.assertJson;

public class SearchActionTest {
  private static final OrganizationDto ORG = OrganizationTesting.newOrganizationDto();

  @Rule
  public EsTester es = new EsTester(new ProjectMeasuresIndexDefinition(new MapSettings().asConfig()));

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Rule
  public UserSessionRule userSession = UserSessionRule.standalone();

  private ProjectMeasuresIndexer projectMeasureIndexer = new ProjectMeasuresIndexer(null, es.client());
  private PermissionIndexerTester authorizationIndexerTester = new PermissionIndexerTester(es, projectMeasureIndexer);
  private ProjectMeasuresIndex index = new ProjectMeasuresIndex(es.client(), new AuthorizationTypeSupport(userSession), System2.INSTANCE);

  private WsActionTester ws = new WsActionTester(new SearchAction(index));

  @Test
  public void json_example() throws IOException {
    index(newDoc().setTags(newArrayList("official", "offshore", "playoff")));

    String result = ws.newRequest().execute().getInput();

    assertJson(ws.getDef().responseExampleAsString()).isSimilarTo(result);
  }

  @Test
  public void search_by_query_and_page_size() {
    index(
      newDoc().setTags(newArrayList("whatever-tag", "official", "offshore", "yet-another-tag", "playoff")),
      newDoc().setTags(newArrayList("offshore", "playoff")));

    SearchResponse result = call("off", 2);

    assertThat(result.getTagsList()).containsOnly("offshore", "official");
  }

  @Test
  public void search_in_lexical_order() {
    index(newDoc().setTags(newArrayList("offshore", "official", "Playoff")));

    SearchResponse result = call(null, null);

    assertThat(result.getTagsList()).containsExactly("Playoff", "official", "offshore");
  }

  @Test
  public void definition() {
    WebService.Action definition = ws.getDef();

    assertThat(definition.key()).isEqualTo("search");
    assertThat(definition.isInternal()).isFalse();
    assertThat(definition.isPost()).isFalse();
    assertThat(definition.responseExampleAsString()).isNotEmpty();
    assertThat(definition.since()).isEqualTo("6.4");
    assertThat(definition.params()).extracting(WebService.Param::key).containsOnly("q", "ps");
  }

  private void index(ProjectMeasuresDoc... docs) {
    es.putDocuments(INDEX_TYPE_PROJECT_MEASURES, docs);
    for (ProjectMeasuresDoc doc : docs) {
      PermissionIndexerDao.Dto access = new PermissionIndexerDao.Dto(doc.getId(), Qualifiers.PROJECT);
      access.allowAnyone();
      authorizationIndexerTester.allow(access);
    }
  }

  private static ProjectMeasuresDoc newDoc(ComponentDto project) {
    return new ProjectMeasuresDoc()
      .setOrganizationUuid(project.getOrganizationUuid())
      .setId(project.uuid())
      .setKey(project.getDbKey())
      .setName(project.name());
  }

  private static ProjectMeasuresDoc newDoc() {
    return newDoc(ComponentTesting.newPrivateProjectDto(ORG));
  }

  private SearchResponse call(@Nullable String textQuery, @Nullable Integer pageSize) {
    TestRequest request = ws.newRequest();
    setNullable(textQuery, s -> request.setParam("q", s));
    setNullable(pageSize, ps -> request.setParam("ps", ps.toString()));

    return request.executeProtobuf(SearchResponse.class);
  }
}
