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
package org.sonarqube.ws.client.component;

import java.util.List;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class TreeWsRequest {
  private String baseComponentId;
  private String baseComponentKey;
  private String component;
  private String branch;
  private String strategy;
  private List<String> qualifiers;
  private String query;
  private List<String> sort;
  private Boolean asc;
  private Integer page;
  private Integer pageSize;

  /**
   * @deprecated since 6.4, please use {@link #getComponent()} instead
   */
  @Deprecated
  @CheckForNull
  public String getBaseComponentId() {
    return baseComponentId;
  }

  /**
   * @deprecated since 6.4, please use {@link #setComponent(String)} instead
   */
  @Deprecated
  public TreeWsRequest setBaseComponentId(@Nullable String baseComponentId) {
    this.baseComponentId = baseComponentId;
    return this;
  }

  /**
   * @deprecated since 6.4, please use {@link #getComponent()} instead
   */
  @Deprecated
  @CheckForNull
  public String getBaseComponentKey() {
    return baseComponentKey;
  }

  /**
   * @deprecated since 6.4, please use {@link #setComponent(String)} instead
   */
  @Deprecated
  public TreeWsRequest setBaseComponentKey(@Nullable String baseComponentKey) {
    this.baseComponentKey = baseComponentKey;
    return this;
  }

  public TreeWsRequest setComponent(@Nullable String component) {
    this.component = component;
    return this;
  }

  @CheckForNull
  public String getComponent() {
    return component;
  }

  @CheckForNull
  public String getBranch() {
    return branch;
  }

  public TreeWsRequest setBranch(@Nullable String branch) {
    this.branch = branch;
    return this;
  }

  @CheckForNull
  public String getStrategy() {
    return strategy;
  }

  public TreeWsRequest setStrategy(@Nullable String strategy) {
    this.strategy = strategy;
    return this;
  }

  @CheckForNull
  public List<String> getQualifiers() {
    return qualifiers;
  }

  public TreeWsRequest setQualifiers(@Nullable List<String> qualifiers) {
    this.qualifiers = qualifiers;
    return this;
  }

  @CheckForNull
  public String getQuery() {
    return query;
  }

  public TreeWsRequest setQuery(@Nullable String query) {
    this.query = query;
    return this;
  }

  @CheckForNull
  public List<String> getSort() {
    return sort;
  }

  public TreeWsRequest setSort(@Nullable List<String> sort) {
    this.sort = sort;
    return this;
  }

  public Boolean getAsc() {
    return asc;
  }

  public TreeWsRequest setAsc(@Nullable Boolean asc) {
    this.asc = asc;
    return this;
  }

  @CheckForNull
  public Integer getPage() {
    return page;
  }

  public TreeWsRequest setPage(@Nullable Integer page) {
    this.page = page;
    return this;
  }

  @CheckForNull
  public Integer getPageSize() {
    return pageSize;
  }

  public TreeWsRequest setPageSize(@Nullable Integer pageSize) {
    this.pageSize = pageSize;
    return this;
  }
}
