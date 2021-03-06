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
package org.sonarqube.ws.client.projectlinks;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class CreateWsRequest {
  private String projectId;
  private String projectKey;
  private String name;
  private String url;

  @CheckForNull
  public String getProjectId() {
    return projectId;
  }

  public CreateWsRequest setProjectId(@Nullable String projectId) {
    this.projectId = projectId;
    return this;
  }

  @CheckForNull
  public String getProjectKey() {
    return projectKey;
  }

  public CreateWsRequest setProjectKey(@Nullable String projectKey) {
    this.projectKey = projectKey;
    return this;
  }

  public String getName() {
    return name;
  }

  public CreateWsRequest setName(String name) {
    this.name = name;
    return this;
  }

  public String getUrl() {
    return url;
  }

  public CreateWsRequest setUrl(String url) {
    this.url = url;
    return this;
  }
}
