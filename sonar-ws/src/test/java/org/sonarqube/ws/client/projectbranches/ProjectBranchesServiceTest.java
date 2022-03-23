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
package org.sonarqube.ws.client.projectbranches;

import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.ws.WsBranches.ListWsResponse;
import org.sonarqube.ws.WsBranches.ShowWsResponse;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.PostRequest;
import org.sonarqube.ws.client.ServiceTester;
import org.sonarqube.ws.client.WsConnector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.sonarqube.ws.client.projectbranches.ProjectBranchesParameters.PARAM_BRANCH;
import static org.sonarqube.ws.client.projectbranches.ProjectBranchesParameters.PARAM_NAME;
import static org.sonarqube.ws.client.projectbranches.ProjectBranchesParameters.PARAM_PROJECT;

public class ProjectBranchesServiceTest {

  @Rule
  public ServiceTester<ProjectBranchesService> serviceTester = new ServiceTester<>(new ProjectBranchesService(mock(WsConnector.class)));

  private ProjectBranchesService underTest = serviceTester.getInstanceUnderTest();

  @Test
  public void list() {
    underTest.list("projectKey");

    assertThat(serviceTester.getGetParser()).isSameAs(ListWsResponse.parser());

    GetRequest getRequest = serviceTester.getGetRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("list")
      .hasParam(PARAM_PROJECT, "projectKey")
      .andNoOtherParam();
  }

  @Test
  public void show() {
    underTest.show("projectKey", "my_branch");

    assertThat(serviceTester.getGetParser()).isSameAs(ShowWsResponse.parser());

    GetRequest getRequest = serviceTester.getGetRequest();
    serviceTester.assertThat(getRequest)
      .hasPath("show")
      .hasParam(PARAM_PROJECT, "projectKey")
      .hasParam(PARAM_BRANCH, "my_branch")
      .andNoOtherParam();
  }

  @Test
  public void delete() {
    underTest.delete("projectKey", "my_branch");

    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("delete")
      .hasParam(PARAM_PROJECT, "projectKey")
      .hasParam(PARAM_BRANCH, "my_branch")
      .andNoOtherParam();
  }

  @Test
  public void rename() {
    underTest.rename("projectKey", "my_branch");

    PostRequest postRequest = serviceTester.getPostRequest();
    serviceTester.assertThat(postRequest)
      .hasPath("rename")
      .hasParam(PARAM_PROJECT, "projectKey")
      .hasParam(PARAM_NAME, "my_branch")
      .andNoOtherParam();
  }

}
