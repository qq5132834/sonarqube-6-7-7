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
package org.sonar.server.computation.task.projectanalysis.step;

import java.util.ArrayList;
import java.util.List;
import org.sonar.core.util.UuidFactory;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.component.AnalysisPropertyDto;
import org.sonar.server.computation.task.projectanalysis.analysis.AnalysisMetadataHolder;
import org.sonar.server.computation.task.projectanalysis.batch.BatchReportReader;
import org.sonar.server.computation.task.step.ComputationStep;

/**
 * Persist analysis properties
 * Only properties starting with "sonar.analysis" will be persisted in database
 */
public class PersistAnalysisPropertiesStep implements ComputationStep {

  private static final String SONAR_ANALYSIS = "sonar.analysis.";

  private final DbClient dbClient;
  private final AnalysisMetadataHolder analysisMetadataHolder;
  private final BatchReportReader reportReader;
  private final UuidFactory uuidFactory;

  public PersistAnalysisPropertiesStep(DbClient dbClient, AnalysisMetadataHolder analysisMetadataHolder,
    BatchReportReader reportReader, UuidFactory uuidFactory) {
    this.dbClient = dbClient;
    this.analysisMetadataHolder = analysisMetadataHolder;
    this.reportReader = reportReader;
    this.uuidFactory = uuidFactory;
  }

  @Override
  public void execute() {
    final List<AnalysisPropertyDto> analysisPropertyDtos = new ArrayList<>();
    reportReader.readContextProperties().forEachRemaining(
      contextProperty -> {
        if (contextProperty.getKey().startsWith(SONAR_ANALYSIS)) {
          analysisPropertyDtos.add(new AnalysisPropertyDto()
            .setUuid(uuidFactory.create())
            .setKey(contextProperty.getKey())
            .setValue(contextProperty.getValue())
            .setSnapshotUuid(analysisMetadataHolder.getUuid()));
        }
      });

    if (analysisPropertyDtos.isEmpty()) {
      return;
    }

    try (DbSession dbSession = dbClient.openSession(false)) {
      dbClient.analysisPropertiesDao().insert(dbSession, analysisPropertyDtos);
      dbSession.commit();
    }
  }

  @Override
  public String getDescription() {
    return "Persist analysis properties";
  }
}
