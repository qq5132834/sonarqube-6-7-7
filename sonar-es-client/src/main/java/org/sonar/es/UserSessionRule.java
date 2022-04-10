///*
// * SonarQube
// * Copyright (C) 2009-2018 SonarSource SA
// * mailto:info AT sonarsource DOT com
// *
// * This program is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 3 of the License, or (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with this program; if not, write to the Free Software Foundation,
// * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
// */
//package org.sonar.es;
//
//import org.sonar.db.component.ComponentDto;
//import org.sonar.db.organization.OrganizationDto;
//import org.sonar.db.permission.OrganizationPermission;
//import org.sonar.db.user.GroupDto;
//import org.sonar.server.user.UserSession;
//
//import java.util.Collection;
//import java.util.List;
//
//public class UserSessionRule implements UserSession {
//
//  public String getLogin() {
//    return "admin";
//  }
//
//  public String getName() {
//    return "Administrator";
//  }
//
//  public Integer getUserId() {
//    return 1;
//  }
//
//  public Collection<GroupDto> getGroups() {
//    return null;
//  }
//
//  public boolean isLoggedIn() {
//    return true;
//  }
//
//  public boolean isRoot() {
//    return false;
//  }
//
//  public UserSession checkIsRoot() {
//    return null;
//  }
//
//  public UserSession checkLoggedIn() {
//    return null;
//  }
//
//  public boolean hasPermission(OrganizationPermission permission, OrganizationDto organization) {
//    return false;
//  }
//
//  public boolean hasPermission(OrganizationPermission permission, String organizationUuid) {
//    return false;
//  }
//
//  public UserSession checkPermission(OrganizationPermission permission, OrganizationDto organization) {
//    return null;
//  }
//
//  public UserSession checkPermission(OrganizationPermission permission, String organizationUuid) {
//    return null;
//  }
//
//  public boolean hasComponentPermission(String permission, ComponentDto component) {
//    return false;
//  }
//
//  public boolean hasComponentUuidPermission(String permission, String componentUuid) {
//    return false;
//  }
//
//  public List<ComponentDto> keepAuthorizedComponents(String permission, Collection<ComponentDto> components) {
//    return null;
//  }
//
//  public UserSession checkComponentPermission(String projectPermission, ComponentDto component) {
//    return null;
//  }
//
//  public UserSession checkComponentUuidPermission(String permission, String componentUuid) {
//    return null;
//  }
//
//  public boolean isSystemAdministrator() {
//    return false;
//  }
//
//  public UserSession checkIsSystemAdministrator() {
//    return null;
//  }
//}
