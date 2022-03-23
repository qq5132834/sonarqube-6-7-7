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
package org.sonarqube.ws.client.system;

import org.sonarqube.ws.WsSystem;
import org.sonarqube.ws.client.BaseService;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.PostRequest;
import org.sonarqube.ws.client.WsConnector;
import org.sonarqube.ws.client.WsResponse;

public class SystemService extends BaseService {
  public SystemService(WsConnector wsConnector) {
    super(wsConnector, "api/system");
  }

  public WsSystem.HealthResponse health() {
    return call(new GetRequest(path("health")), WsSystem.HealthResponse.parser());
  }

  public void restart() {
    call(new PostRequest(path("restart")));
  }

  public WsSystem.StatusResponse status() {
    return call(new GetRequest(path("status")), WsSystem.StatusResponse.parser());
  }

  public void changeLogLevel(String level) {
    call(new PostRequest(path("change_log_level")).setParam("level", level));
  }

  public WsResponse info() {
    return call(new GetRequest(path("info")));
  }
}
