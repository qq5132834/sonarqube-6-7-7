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
package org.sonar.server.bugtotal;

import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.server.computation.queue.ReportSubmitter;
import org.sonar.server.organization.DefaultOrganizationProvider;

public class BugtotalSubmitAction implements BugtotalWsAction {

  private static final String PARAM_ORGANIZATION_KEY = "organization";
  private static final String PARAM_PROJECT_KEY = "projectKey";
  private static final String PARAM_PROJECT_BRANCH = "projectBranch";
  private static final String PARAM_PROJECT_NAME = "projectName";
  private static final String PARAM_REPORT_DATA = "report";
  private static final String PARAM_ANALYSIS_CHARACTERISTIC = "characteristic";

  private final ReportSubmitter reportSubmitter;
  private final DefaultOrganizationProvider defaultOrganizationProvider;

  public BugtotalSubmitAction(ReportSubmitter reportSubmitter, DefaultOrganizationProvider defaultOrganizationProvider) {
    this.reportSubmitter = reportSubmitter;
    this.defaultOrganizationProvider = defaultOrganizationProvider;
  }

  @Override
  public void define(WebService.NewController controller) {
    WebService.NewAction action = controller.createAction("submit")
      .setDescription("Submits a scanner report to the queue. Report is processed asynchronously. Requires analysis permission. " +
        "If the project does not exist, then the provisioning permission is also required.")
      .setPost(true)
      .setInternal(true)
      .setSince("5.2")
      .setHandler(this)
      .setResponseExample(getClass().getResource("submit-example.json"));

    action.createParam(PARAM_ORGANIZATION_KEY)
      .setDescription("Key of the organization the project belongs to")
      .setExampleValue("my-org")
      .setSince("6.3")
      .setInternal(true);

  }

  @Override
  public void handle(Request wsRequest, Response wsResponse) throws Exception {

  }


}
