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
package org.sonar.db.component;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

public class BranchDto {
  public static final String DEFAULT_MAIN_BRANCH_NAME = "master";

  /**
   * Maximum length of column "kee"
   */
  public static final int KEE_MAX_LENGTH = 255;

  /**
   * Branch UUID is the projects.uuid that reference projects, branches or pull requests
   * (projects.qualifier="TRK").
   * Not null.
   * Important - the table project_branches does NOT have its own UUIDs for the time being.
   * All values must exist in projects.uuid.
   */
  private String uuid;

  /**
   * UUID of the project that represents the main branch.
   * On main branches, projectUuid equals uuid.
   * Not null.
   */
  private String projectUuid;

  /**
   * Name of branch, for example "feature/foo".
   */
  private String kee;

  /**
   * Branch type, as provided by {@link BranchType}.
   * Not null.
   */
  private BranchType branchType;

  /**
   * UUID of the branch:
   * - in which the short-lived branch or pull request will be merged into
   * - that is the base of long-lived branch.
   *
   * Can be null if information is not known.
   */
  @Nullable
  private String mergeBranchUuid;

  public String getUuid() {
    return uuid;
  }

  public BranchDto setUuid(String s) {
    this.uuid = s;
    return this;
  }

  public String getProjectUuid() {
    return projectUuid;
  }

  public BranchDto setProjectUuid(String s) {
    this.projectUuid = s;
    return this;
  }

  public boolean isMain() {
    return projectUuid.equals(uuid);
  }

  /**
   * This is the getter used by MyBatis mapper.
   */
  private String getKee() {
    return kee;
  }

  public String getKey() {
    return kee;
  }

  /**
   * This is the setter used by MyBatis mapper.
   */
  private void setKee(String s) {
    this.kee = s;
  }

  public BranchDto setKey(String s) {
    checkArgument(s.length() <= KEE_MAX_LENGTH, "Maximum length of branch name or pull request id is %s: %s", KEE_MAX_LENGTH, s);
    setKee(s);
    return this;
  }

  public BranchType getBranchType() {
    return branchType;
  }

  public BranchDto setBranchType(@Nullable BranchType b) {
    this.branchType = b;
    return this;
  }

  @Nullable
  public String getMergeBranchUuid() {
    return mergeBranchUuid;
  }

  public BranchDto setMergeBranchUuid(@Nullable String s) {
    this.mergeBranchUuid = s;
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("BranchDto{");
    sb.append("uuid='").append(uuid).append('\'');
    sb.append(", projectUuid='").append(projectUuid).append('\'');
    sb.append(", kee='").append(kee).append('\'');
    sb.append(", branchType=").append(branchType);
    sb.append(", mergeBranchUuid='").append(mergeBranchUuid).append('\'');
    sb.append('}');
    return sb.toString();
  }
}
