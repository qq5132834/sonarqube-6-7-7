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
package org.sonar.server.setting.ws;

import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.config.PropertyDefinitions;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.web.UserRole;
import org.sonar.core.util.stream.MoreCollectors;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.server.component.ComponentFinder;
import org.sonar.server.setting.ws.SettingValidations.SettingData;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.client.setting.ResetRequest;

import static java.util.Collections.emptyList;
import static org.sonar.server.ws.KeyExamples.KEY_BRANCH_EXAMPLE_001;
import static org.sonar.server.ws.KeyExamples.KEY_PROJECT_EXAMPLE_001;
import static org.sonarqube.ws.client.setting.SettingsWsParameters.ACTION_RESET;
import static org.sonarqube.ws.client.setting.SettingsWsParameters.PARAM_BRANCH;
import static org.sonarqube.ws.client.setting.SettingsWsParameters.PARAM_COMPONENT;
import static org.sonarqube.ws.client.setting.SettingsWsParameters.PARAM_KEYS;

public class ResetAction implements SettingsWsAction {

  private final DbClient dbClient;
  private final ComponentFinder componentFinder;
  private final SettingsUpdater settingsUpdater;
  private final UserSession userSession;
  private final PropertyDefinitions definitions;
  private final SettingValidations validations;

  public ResetAction(DbClient dbClient, ComponentFinder componentFinder, SettingsUpdater settingsUpdater, UserSession userSession, PropertyDefinitions definitions,
    SettingValidations validations) {
    this.dbClient = dbClient;
    this.settingsUpdater = settingsUpdater;
    this.userSession = userSession;
    this.componentFinder = componentFinder;
    this.definitions = definitions;
    this.validations = validations;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction(ACTION_RESET)
      .setDescription("Remove a setting value.<br>" +
        "Requires one of the following permissions: " +
        "<ul>" +
        "<li>'Administer System'</li>" +
        "<li>'Administer' rights on the specified component</li>" +
        "</ul>")
      .setSince("6.1")
      .setPost(true)
      .setHandler(this);

    action.createParam(PARAM_KEYS)
      .setDescription("Setting keys")
      .setExampleValue("sonar.links.scm,sonar.debt.hoursInDay")
      .setRequired(true);
    action.createParam(PARAM_COMPONENT)
      .setDescription("Component key")
      .setDeprecatedKey("componentKey", "6.3")
      .setExampleValue(KEY_PROJECT_EXAMPLE_001);
    action.createParam(PARAM_BRANCH)
      .setDescription("Branch key")
      .setExampleValue(KEY_BRANCH_EXAMPLE_001)
      .setInternal(true)
      .setSince("6.6");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    try (DbSession dbSession = dbClient.openSession(false)) {
      ResetRequest resetRequest = toWsRequest(request);
      Optional<ComponentDto> component = getComponent(dbSession, resetRequest);
      checkPermissions(component);
      resetRequest.getKeys().forEach(key -> {
        SettingData data = new SettingData(key, emptyList(), component.orElse(null));
        ImmutableList.of(validations.scope(), validations.qualifier())
          .forEach(validation -> validation.accept(data));
      });

      List<String> keys = getKeys(resetRequest);
      if (component.isPresent()) {
        settingsUpdater.deleteComponentSettings(dbSession, component.get(), keys);
      } else {
        settingsUpdater.deleteGlobalSettings(dbSession, keys);
      }
      dbSession.commit();
      response.noContent();
    }
  }

  private List<String> getKeys(ResetRequest request) {
    return new ArrayList<>(request.getKeys().stream()
      .map(key -> {
        PropertyDefinition definition = definitions.get(key);
        return definition != null ? definition.key() : key;
      })
      .collect(MoreCollectors.toSet()));
  }

  private static ResetRequest toWsRequest(Request request) {
    return ResetRequest.builder()
      .setKeys(request.paramAsStrings(PARAM_KEYS))
      .setComponent(request.param(PARAM_COMPONENT))
      .setBranch(request.param(PARAM_BRANCH))
      .build();
  }

  private Optional<ComponentDto> getComponent(DbSession dbSession, ResetRequest request) {
    String componentKey = request.getComponent();
    if (componentKey == null) {
      return Optional.empty();
    }
    return Optional.of(componentFinder.getByKeyAndOptionalBranch(dbSession, componentKey, request.getBranch()));
  }

  private void checkPermissions(Optional<ComponentDto> component) {
    if (component.isPresent()) {
      userSession.checkComponentPermission(UserRole.ADMIN, component.get());
    } else {
      userSession.checkIsSystemAdministrator();
    }
  }
}
