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
package org.sonarqube.ws.client.usergroup;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class DeleteWsRequest {

  private final Long id;
  private final String name;
  private final String organization;

  private DeleteWsRequest(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.organization = builder.organization;
  }

  @CheckForNull
  public Long getId() {
    return id;
  }

  @CheckForNull
  public String getName() {
    return name;
  }

  @CheckForNull
  public String getOrganization() {
    return organization;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private Long id;
    private String name;
    private String organization;

    private Builder() {
      // enforce factory method use
    }

    public Builder setId(@Nullable Long id) {
      this.id = id;
      return this;
    }

    public Builder setName(@Nullable String name) {
      this.name = name;
      return this;
    }

    public Builder setOrganization(@Nullable String organization) {
      this.organization = organization;
      return this;
    }

    public DeleteWsRequest build() {
      return new DeleteWsRequest(this);
    }
  }
}
