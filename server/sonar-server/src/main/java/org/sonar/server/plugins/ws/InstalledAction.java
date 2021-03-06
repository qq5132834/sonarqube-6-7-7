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
package org.sonar.server.plugins.ws;

import com.google.common.io.Resources;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.function.Function;
import org.sonar.api.server.ws.Change;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.text.JsonWriter;
import org.sonar.core.platform.PluginInfo;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.plugin.PluginDto;
import org.sonar.server.plugins.ServerPluginRepository;
import org.sonar.server.plugins.UpdateCenterMatrixFactory;
import org.sonar.updatecenter.common.Plugin;

import static com.google.common.collect.ImmutableSortedSet.copyOf;
import static java.lang.String.format;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;
import static org.sonar.server.plugins.ws.PluginWSCommons.NAME_KEY_PLUGIN_METADATA_COMPARATOR;
import static org.sonar.server.plugins.ws.PluginWSCommons.compatiblePluginsByKey;

/**
 * Implementation of the {@code installed} action for the Plugins WebService.
 */
public class InstalledAction implements PluginsWsAction {
  private static final String ARRAY_PLUGINS = "plugins";
  private static final String FIELD_CATEGORY = "category";

  private final ServerPluginRepository pluginRepository;
  private final PluginWSCommons pluginWSCommons;
  private final UpdateCenterMatrixFactory updateCenterMatrixFactory;
  private final DbClient dbClient;

  public InstalledAction(ServerPluginRepository pluginRepository, PluginWSCommons pluginWSCommons, UpdateCenterMatrixFactory updateCenterMatrixFactory, DbClient dbClient) {
    this.pluginRepository = pluginRepository;
    this.pluginWSCommons = pluginWSCommons;
    this.updateCenterMatrixFactory = updateCenterMatrixFactory;
    this.dbClient = dbClient;
  }

  @Override
  public void define(WebService.NewController controller) {
    WebService.NewAction action = controller.createAction("installed")
      .setDescription("Get the list of all the plugins installed on the SonarQube instance, sorted by plugin name.")
      .setSince("5.2")
      .setChangelog(
        new Change("6.6", "The 'filename' field is added"),
        new Change("6.6", "The 'fileHash' field is added"),
        new Change("6.6", "The 'sonarLintSupported' field is added"),
        new Change("6.6", "The 'updatedAt' field is added"))
      .setHandler(this)
      .setResponseExample(Resources.getResource(this.getClass(), "example-installed_plugins.json"));

    action.createFieldsParam(singleton("category"))
      .setDescription(format("Comma-separated list of the additional fields to be returned in response. No additional field is returned by default. Possible values are:" +
        "<ul>" +
        "<li>%s - category as defined in the Update Center. A connection to the Update Center is needed</li>" +
        "</lu>", FIELD_CATEGORY))
      .setSince("5.6");
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    Collection<PluginInfo> pluginInfoList = searchPluginInfoList();
    Map<String, PluginDto> pluginDtosByKey;
    try (DbSession dbSession = dbClient.openSession(false)) {
      pluginDtosByKey = dbClient.pluginDao().selectAll(dbSession).stream().collect(toMap(PluginDto::getKee, Function.identity()));
    }

    JsonWriter jsonWriter = response.newJsonWriter();
    jsonWriter.setSerializeEmptys(false);
    jsonWriter.beginObject();

    List<String> additionalFields = request.paramAsStrings(WebService.Param.FIELDS);
    writePluginInfoList(jsonWriter, pluginInfoList, additionalFields == null ? Collections.<String>emptyList() : additionalFields, pluginDtosByKey);

    jsonWriter.endObject();
    jsonWriter.close();
  }

  private SortedSet<PluginInfo> searchPluginInfoList() {
    return copyOf(NAME_KEY_PLUGIN_METADATA_COMPARATOR, pluginRepository.getPluginInfos());
  }

  private void writePluginInfoList(JsonWriter jsonWriter, Collection<PluginInfo> pluginInfoList, List<String> additionalFields, Map<String, PluginDto> pluginDtos) {
    Map<String, Plugin> compatiblesPluginsFromUpdateCenter = additionalFields.isEmpty()
      ? Collections.<String, Plugin>emptyMap()
      : compatiblePluginsByKey(updateCenterMatrixFactory);
    pluginWSCommons.writePluginInfoList(jsonWriter, pluginInfoList, compatiblesPluginsFromUpdateCenter, ARRAY_PLUGINS, pluginDtos);
  }
}
