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
package org.sonar.server.permission.ws.template;

import org.sonar.api.i18n.I18n;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.organization.DefaultTemplates;
import org.sonar.db.organization.OrganizationDao;
import org.sonar.db.permission.template.PermissionTemplateDto;
import org.sonar.server.permission.ws.PermissionWsSupport;
import org.sonar.server.permission.ws.PermissionsWsAction;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.client.permission.SetDefaultTemplateWsRequest;

import static org.sonar.server.permission.PermissionPrivilegeChecker.checkGlobalAdmin;
import static org.sonar.server.permission.ws.PermissionRequestValidator.validateQualifier;
import static org.sonar.server.permission.ws.PermissionsWsParametersBuilder.createTemplateParameters;
import static org.sonar.server.permission.ws.template.WsTemplateRef.newTemplateRef;
import static org.sonar.server.ws.WsParameterBuilder.QualifierParameterContext.newQualifierParameterContext;
import static org.sonar.server.ws.WsParameterBuilder.createDefaultTemplateQualifierParameter;
import static org.sonar.server.ws.WsUtils.checkFoundWithOptional;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_ORGANIZATION;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_QUALIFIER;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_NAME;

public class SetDefaultTemplateAction implements PermissionsWsAction {
  private final DbClient dbClient;
  private final PermissionWsSupport wsSupport;
  private final ResourceTypes resourceTypes;
  private final UserSession userSession;
  private final I18n i18n;

  public SetDefaultTemplateAction(DbClient dbClient, PermissionWsSupport wsSupport, ResourceTypes resourceTypes,
    UserSession userSession, I18n i18n) {
    this.dbClient = dbClient;
    this.wsSupport = wsSupport;
    this.resourceTypes = resourceTypes;
    this.userSession = userSession;
    this.i18n = i18n;
  }

  private static SetDefaultTemplateWsRequest toSetDefaultTemplateWsRequest(Request request) {
    return new SetDefaultTemplateWsRequest()
      .setQualifier(request.param(PARAM_QUALIFIER))
      .setTemplateId(request.param(PARAM_TEMPLATE_ID))
      .setOrganization(request.param(PARAM_ORGANIZATION))
      .setTemplateName(request.param(PARAM_TEMPLATE_NAME));
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("set_default_template")
      .setDescription("Set a permission template as default.<br />" +
        "Requires the following permission: 'Administer System'.")
      .setPost(true)
      .setSince("5.2")
      .setHandler(this);

    createTemplateParameters(action);
    createDefaultTemplateQualifierParameter(action, newQualifierParameterContext(i18n, resourceTypes))
      .setDefaultValue(Qualifiers.PROJECT);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    doHandle(toSetDefaultTemplateWsRequest(request));
    response.noContent();
  }

  private void doHandle(SetDefaultTemplateWsRequest request) {
    try (DbSession dbSession = dbClient.openSession(false)) {
      String qualifier = request.getQualifier();
      PermissionTemplateDto template = findTemplate(dbSession, request);
      checkGlobalAdmin(userSession, template.getOrganizationUuid());
      validateQualifier(qualifier, resourceTypes);
      setDefaultTemplateUuid(dbSession, template, qualifier);
      dbSession.commit();
    }
  }

  private PermissionTemplateDto findTemplate(DbSession dbSession, SetDefaultTemplateWsRequest request) {
    return wsSupport.findTemplate(dbSession, newTemplateRef(request.getTemplateId(),
      request.getOrganization(), request.getTemplateName()));
  }

  private void setDefaultTemplateUuid(DbSession dbSession, PermissionTemplateDto permissionTemplateDto, String qualifier) {
    String organizationUuid = permissionTemplateDto.getOrganizationUuid();
    OrganizationDao organizationDao = dbClient.organizationDao();

    DefaultTemplates defaultTemplates = checkFoundWithOptional(
      organizationDao.getDefaultTemplates(dbSession, organizationUuid),
      "No Default templates for organization with uuid '%s'", organizationUuid);
    if (Qualifiers.PROJECT.equals(qualifier)) {
      defaultTemplates.setProjectUuid(permissionTemplateDto.getUuid());
    } else if (Qualifiers.VIEW.equals(qualifier)) {
      defaultTemplates.setViewUuid(permissionTemplateDto.getUuid());
    }
    organizationDao.setDefaultTemplates(dbSession, organizationUuid, defaultTemplates);
  }
}
