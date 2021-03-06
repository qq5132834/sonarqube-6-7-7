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
package org.sonar.server.qualitygate.ws;

import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.organization.OrganizationDto;
import org.sonar.db.qualitygate.QualityGateConditionDto;
import org.sonar.server.exceptions.NotFoundException;
import org.sonar.server.organization.DefaultOrganizationProvider;

import static java.lang.String.format;
import static org.sonar.server.ws.WsUtils.checkFound;

public class QualityGatesWsSupport {
  private final DbClient dbClient;
  private final DefaultOrganizationProvider organizationProvider;

  public QualityGatesWsSupport(DbClient dbClient, DefaultOrganizationProvider organizationProvider) {
    this.dbClient = dbClient;
    this.organizationProvider = organizationProvider;
  }

  QualityGateConditionDto getCondition(DbSession dbSession, long id) {
    return checkFound(dbClient.gateConditionDao().selectById(id, dbSession), "No quality gate condition with id '%d'", id);
  }

  OrganizationDto getOrganization(DbSession dbSession) {
    String organizationKey = organizationProvider.get().getKey();
    return dbClient.organizationDao().selectByKey(dbSession, organizationKey)
      .orElseThrow(() -> new NotFoundException(format("No organization with key '%s'", organizationKey)));
  }
}
