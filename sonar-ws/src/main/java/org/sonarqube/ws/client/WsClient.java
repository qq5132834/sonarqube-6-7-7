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
package org.sonarqube.ws.client;

import org.sonarqube.ws.client.ce.CeService;
import org.sonarqube.ws.client.component.ComponentsService;
import org.sonarqube.ws.client.favorite.FavoritesService;
import org.sonarqube.ws.client.issue.IssuesService;
import org.sonarqube.ws.client.measure.MeasuresService;
import org.sonarqube.ws.client.organization.OrganizationService;
import org.sonarqube.ws.client.permission.PermissionsService;
import org.sonarqube.ws.client.project.ProjectsService;
import org.sonarqube.ws.client.projectanalysis.ProjectAnalysisService;
import org.sonarqube.ws.client.projectbranches.ProjectBranchesService;
import org.sonarqube.ws.client.projectlinks.ProjectLinksService;
import org.sonarqube.ws.client.qualitygate.QualityGatesService;
import org.sonarqube.ws.client.qualityprofile.QualityProfilesService;
import org.sonarqube.ws.client.root.RootsService;
import org.sonarqube.ws.client.rule.RulesService;
import org.sonarqube.ws.client.setting.SettingsService;
import org.sonarqube.ws.client.system.SystemService;
import org.sonarqube.ws.client.user.UsersService;
import org.sonarqube.ws.client.usergroup.UserGroupsService;
import org.sonarqube.ws.client.usertoken.UserTokensService;
import org.sonarqube.ws.client.webhook.WebhooksService;

/**
 * Allows to request the web services of SonarQube server. Instance is provided by
 * {@link WsClientFactory}.
 *
 * <p>
 * Usage:
 * <pre>
 *   HttpConnector httpConnector = HttpConnector.newBuilder()
 *     .url("http://localhost:9000")
 *     .credentials("admin", "admin")
 *     .build();
 *   WsClient wsClient = WsClientFactories.getDefault().newClient(httpConnector);
 *   wsClient.issues().search(issueRequest);
 * </pre>
 * </p>
 *
 * @since 5.3
 */
public interface WsClient {
  OrganizationService organizations();

  ComponentsService components();

  FavoritesService favorites();

  IssuesService issues();

  PermissionsService permissions();

  QualityProfilesService qualityProfiles();

  UsersService users();

  UserGroupsService userGroups();

  UserTokensService userTokens();

  QualityGatesService qualityGates();

  MeasuresService measures();

  SystemService system();

  CeService ce();

  RulesService rules();

  WsConnector wsConnector();

  /**
   * @since 5.5
   */
  ProjectsService projects();

  /**
   * @since 6.1
   */
  ProjectLinksService projectLinks();

  /**
   * @since 6.1
   */
  SettingsService settings();

  /**
   * @since 6.2
   */
  RootsService roots();

  /**
   * @since 6.2
   */
  WebhooksService webhooks();

  /**
   * @since 6.3
   */
  ProjectAnalysisService projectAnalysis();

  /**
   * @since 6.6>
   */
  ProjectBranchesService projectBranches();
}
