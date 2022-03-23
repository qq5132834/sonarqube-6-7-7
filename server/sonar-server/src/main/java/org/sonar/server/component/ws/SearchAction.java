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
package org.sonar.server.component.ws;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.sonar.api.i18n.I18n;
import org.sonar.api.resources.Languages;
import org.sonar.api.resources.ResourceTypes;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.Paging;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.server.component.index.ComponentIndex;
import org.sonar.server.component.index.ComponentQuery;
import org.sonar.server.es.SearchIdResult;
import org.sonar.server.es.SearchOptions;
import org.sonar.server.organization.DefaultOrganizationProvider;
import org.sonar.server.ws.WsUtils;
import org.sonarqube.ws.WsComponents;
import org.sonarqube.ws.WsComponents.SearchWsResponse;
import org.sonarqube.ws.client.component.SearchWsRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toMap;
import static org.sonar.core.util.Protobuf.setNullable;
import static org.sonar.core.util.stream.MoreCollectors.toHashSet;
import static org.sonar.server.util.LanguageParamUtils.getExampleValue;
import static org.sonar.server.util.LanguageParamUtils.getLanguageKeys;
import static org.sonar.server.ws.WsParameterBuilder.QualifierParameterContext.newQualifierParameterContext;
import static org.sonar.server.ws.WsParameterBuilder.createQualifiersParameter;
import static org.sonar.server.ws.WsUtils.writeProtobuf;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.ACTION_SEARCH;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_LANGUAGE;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_ORGANIZATION;
import static org.sonarqube.ws.client.component.ComponentsWsParameters.PARAM_QUALIFIERS;

public class SearchAction implements ComponentsWsAction {
  private final ComponentIndex componentIndex;
  private final DbClient dbClient;
  private final ResourceTypes resourceTypes;
  private final I18n i18n;
  private final Languages languages;
  private final DefaultOrganizationProvider defaultOrganizationProvider;

  public SearchAction(ComponentIndex componentIndex, DbClient dbClient, ResourceTypes resourceTypes, I18n i18n, Languages languages,
                      DefaultOrganizationProvider defaultOrganizationProvider) {
    this.componentIndex = componentIndex;
    this.dbClient = dbClient;
    this.resourceTypes = resourceTypes;
    this.i18n = i18n;
    this.languages = languages;
    this.defaultOrganizationProvider = defaultOrganizationProvider;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction(ACTION_SEARCH)
      .setSince("6.3")
      .setDescription("Search for components")
      .addPagingParams(100)
      .setResponseExample(getClass().getResource("search-components-example.json"))
      .setHandler(this);

    action.createParam(Param.TEXT_QUERY)
      .setDescription("Limit search to: <ul>" +
        "<li>component names that contain the supplied string</li>" +
        "<li>component keys that are exactly the same as the supplied string</li>" +
        "</ul>")
      .setExampleValue("sonar");

    action
      .createParam(PARAM_ORGANIZATION)
      .setDescription("Organization key")
      .setRequired(false)
      .setInternal(true)
      .setExampleValue("my-org")
      .setSince("6.3");

    action
      .createParam(PARAM_LANGUAGE)
      .setDescription("Language key. If provided, only components for the given language are returned.")
      .setExampleValue(getExampleValue(languages))
      .setPossibleValues(getLanguageKeys(languages));
    createQualifiersParameter(action, newQualifierParameterContext(i18n, resourceTypes))
      .setRequired(true);
  }

  @Override
  public void handle(Request wsRequest, Response wsResponse) throws Exception {
    SearchWsResponse searchWsResponse = doHandle(toSearchWsRequest(wsRequest));
    writeProtobuf(searchWsResponse, wsRequest, wsResponse);
  }

  private static SearchWsRequest toSearchWsRequest(Request request) {
    return new SearchWsRequest()
      .setOrganization(request.param(PARAM_ORGANIZATION))
      .setQualifiers(request.mandatoryParamAsStrings(PARAM_QUALIFIERS))
      .setLanguage(request.param(PARAM_LANGUAGE))
      .setQuery(request.param(Param.TEXT_QUERY))
      .setPage(request.mandatoryParamAsInt(Param.PAGE))
      .setPageSize(request.mandatoryParamAsInt(Param.PAGE_SIZE));
  }

  private SearchWsResponse doHandle(SearchWsRequest request) {
    try (DbSession dbSession = dbClient.openSession(false)) {
      OrganizationDto organization = getOrganization(dbSession, request);
      ComponentQuery esQuery = buildEsQuery(organization, request);
      SearchIdResult<String> results = componentIndex.search(esQuery, new SearchOptions().setPage(request.getPage(), request.getPageSize()));

      List<ComponentDto> components = dbClient.componentDao().selectByUuids(dbSession, results.getIds());
      Map<String, String> projectKeysByUuids = searchProjectsKeysByUuids(dbSession, components);

      return buildResponse(components, organization, projectKeysByUuids,
        Paging.forPageIndex(request.getPage()).withPageSize(request.getPageSize()).andTotal((int) results.getTotal()));
    }
  }

  private Map<String, String> searchProjectsKeysByUuids(DbSession dbSession, List<ComponentDto> components) {
    Set<String> projectUuidsToSearch = components.stream()
      .map(ComponentDto::projectUuid)
      .collect(toHashSet());
    List<ComponentDto> projects = dbClient.componentDao().selectByUuids(dbSession, projectUuidsToSearch);
    return projects.stream().collect(toMap(ComponentDto::uuid, ComponentDto::getDbKey));
  }

  private OrganizationDto getOrganization(DbSession dbSession, SearchWsRequest request) {
    String organizationKey = Optional.ofNullable(request.getOrganization())
      .orElseGet(defaultOrganizationProvider.get()::getKey);
    return WsUtils.checkFoundWithOptional(
      dbClient.organizationDao().selectByKey(dbSession, organizationKey),
      "No organizationDto with key '%s'", organizationKey);
  }

  private static ComponentQuery buildEsQuery(OrganizationDto organization, SearchWsRequest request) {
    return ComponentQuery.builder()
      .setQuery(request.getQuery())
      .setOrganization(organization.getUuid())
      .setLanguage(request.getLanguage())
      .setQualifiers(request.getQualifiers())
      .build();
  }

  private static SearchWsResponse buildResponse(List<ComponentDto> components, OrganizationDto organization, Map<String, String> projectKeysByUuids, Paging paging) {
    SearchWsResponse.Builder responseBuilder = SearchWsResponse.newBuilder();
    responseBuilder.getPagingBuilder()
      .setPageIndex(paging.pageIndex())
      .setPageSize(paging.pageSize())
      .setTotal(paging.total())
      .build();

    components.stream()
      .map(dto -> dtoToComponent(organization, dto, projectKeysByUuids.get(dto.projectUuid())))
      .forEach(responseBuilder::addComponents);

    return responseBuilder.build();
  }

  private static WsComponents.Component dtoToComponent(OrganizationDto organization, ComponentDto dto, String projectKey) {
    checkArgument(
      organization.getUuid().equals(dto.getOrganizationUuid()),
      "No Organization found for uuid '%s'",
      dto.getOrganizationUuid());

    WsComponents.Component.Builder builder = WsComponents.Component.newBuilder()
      .setOrganization(organization.getKey())
      .setId(dto.uuid())
      .setKey(dto.getDbKey())
      .setProject(projectKey)
      .setName(dto.name())
      .setQualifier(dto.qualifier());
    setNullable(dto.language(), builder::setLanguage);
    return builder.build();
  }

}
