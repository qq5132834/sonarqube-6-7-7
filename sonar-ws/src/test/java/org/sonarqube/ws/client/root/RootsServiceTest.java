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
package org.sonarqube.ws.client.root;

import org.junit.Rule;
import org.junit.Test;
import org.sonarqube.ws.client.ServiceTester;
import org.sonarqube.ws.client.WsConnector;

import static org.mockito.Mockito.mock;

public class RootsServiceTest {
  private static final String SOME_LOGIN = "johnDoe";

  @Rule
  public ServiceTester<RootsService> serviceTester = new ServiceTester<>(new RootsService(mock(WsConnector.class)));

  private RootsService underTest = serviceTester.getInstanceUnderTest();

  @Test
  public void search() {
    underTest.search();

    serviceTester.assertThat(serviceTester.getGetRequest())
      .hasPath("search")
      .andNoOtherParam();
  }

  @Test
  public void setRoot() {
    underTest.setRoot(SOME_LOGIN);

    serviceTester.assertThat(serviceTester.getPostRequest())
      .hasPath("set_root")
      .hasParam("login", SOME_LOGIN)
      .andNoOtherParam();
  }

  @Test
  public void unsetRoot() {
    underTest.unsetRoot(SOME_LOGIN);

    serviceTester.assertThat(serviceTester.getPostRequest())
      .hasPath("unset_root")
      .hasParam("login", SOME_LOGIN)
      .andNoOtherParam();
  }
}
