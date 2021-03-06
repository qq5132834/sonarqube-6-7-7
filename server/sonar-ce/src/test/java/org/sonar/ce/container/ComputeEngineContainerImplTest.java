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
package org.sonar.ce.container;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.sonar.api.CoreProperties;
import org.sonar.api.database.DatabaseProperties;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.utils.System2;
import org.sonar.ce.CeDistributedInformationImpl;
import org.sonar.ce.StandaloneCeDistributedInformation;
import org.sonar.db.DbTester;
import org.sonar.db.property.PropertyDto;
import org.sonar.process.ProcessId;
import org.sonar.process.ProcessProperties;
import org.sonar.process.Props;
import org.sonar.server.property.InternalProperties;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.sonar.process.ProcessEntryPoint.PROPERTY_PROCESS_INDEX;
import static org.sonar.process.ProcessEntryPoint.PROPERTY_SHARED_PATH;
import static org.sonar.process.ProcessProperties.PATH_DATA;
import static org.sonar.process.ProcessProperties.PATH_HOME;
import static org.sonar.process.ProcessProperties.PATH_TEMP;

public class ComputeEngineContainerImplTest {
  private static final int CONTAINER_ITSELF = 1;
  private static final int COMPONENTS_IN_LEVEL_1_AT_CONSTRUCTION = CONTAINER_ITSELF + 1;

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();
  @Rule
  public DbTester db = DbTester.create(System2.INSTANCE);

  private ComputeEngineContainerImpl underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new ComputeEngineContainerImpl();
    underTest.setComputeEngineStatus(mock(ComputeEngineStatus.class));
  }

  @Test
  public void constructor_does_not_create_container() {
    assertThat(underTest.getComponentContainer()).isNull();
  }

  @Test
  public void test_real_start() throws IOException {
    Properties properties = getProperties();

    // required persisted properties
    insertProperty(CoreProperties.SERVER_ID, "a_server_id");
    insertProperty(CoreProperties.SERVER_STARTTIME, DateUtils.formatDateTime(new Date()));
    insertInternalProperty(InternalProperties.SERVER_ID_CHECKSUM, DigestUtils.sha256Hex("a_server_id|" + cleanJdbcUrl()));

    underTest
            .setProps(new Props(properties))
      .start();

    MutablePicoContainer picoContainer = underTest.getComponentContainer().getPicoContainer();
    assertThat(picoContainer.getComponentAdapters())
      .hasSize(
        CONTAINER_ITSELF
          + 76 // level 4
          + 6 // content of CeConfigurationModule
          + 4 // content of CeQueueModule
          + 4 // content of CeHttpModule
          + 3 // content of CeTaskCommonsModule
          + 4 // content of ProjectAnalysisTaskModule
          + 7 // content of CeTaskProcessorModule
          + 4 // content of ReportAnalysisFailureNotificationModule
          + 3 // CeCleaningModule + its content
          + 1 // CeDistributedInformation
    );
    assertThat(picoContainer.getParent().getComponentAdapters()).hasSize(
      CONTAINER_ITSELF
        + 5 // level 3
        + 4 // content of ServerIdModule
    );
    assertThat(picoContainer.getParent().getParent().getComponentAdapters()).hasSize(
      CONTAINER_ITSELF
        + 13 // MigrationConfigurationModule
        + 17 // level 2
    );
    assertThat(picoContainer.getParent().getParent().getParent().getComponentAdapters()).hasSize(
      COMPONENTS_IN_LEVEL_1_AT_CONSTRUCTION
        + 26 // level 1
        + 52 // content of DaoModule
        + 3 // content of EsSearchModule
        + 68 // content of CorePropertyDefinitions
        + 1 // StopFlagContainer
    );
    assertThat(
      picoContainer.getComponentAdapters().stream()
        .map(ComponentAdapter::getComponentImplementation)
        .collect(Collectors.toList())).doesNotContain(
          (Class) CeDistributedInformationImpl.class).contains(
            (Class) StandaloneCeDistributedInformation.class);
    assertThat(picoContainer.getParent().getParent().getParent().getParent()).isNull();
    underTest.stop();

    assertThat(picoContainer.getLifecycleState().isStarted()).isFalse();
    assertThat(picoContainer.getLifecycleState().isStopped()).isFalse();
    assertThat(picoContainer.getLifecycleState().isDisposed()).isTrue();
  }

  private String cleanJdbcUrl() {
    return StringUtils.lowerCase(StringUtils.substringBefore(db.getUrl(), "?"), Locale.ENGLISH);
  }

  private Properties getProperties() throws IOException {
    Properties properties = ProcessProperties.defaults();
    File homeDir = tempFolder.newFolder();
    File dataDir = new File(homeDir, "data");
    File tmpDir = new File(homeDir, "tmp");
    properties.setProperty(PATH_HOME, homeDir.getAbsolutePath());
    properties.setProperty(PATH_DATA, dataDir.getAbsolutePath());
    properties.setProperty(PATH_TEMP, tmpDir.getAbsolutePath());
    properties.setProperty(PROPERTY_PROCESS_INDEX, valueOf(ProcessId.COMPUTE_ENGINE.getIpcIndex()));
    properties.setProperty(PROPERTY_SHARED_PATH, tmpDir.getAbsolutePath());
    properties.setProperty(DatabaseProperties.PROP_URL, db.getUrl());
    properties.setProperty(DatabaseProperties.PROP_USER, "sonar");
    properties.setProperty(DatabaseProperties.PROP_PASSWORD, "sonar");
    return properties;
  }

  private void insertProperty(String key, String value) {
    PropertyDto dto = new PropertyDto().setKey(key).setValue(value);
    db.getDbClient().propertiesDao().saveProperty(db.getSession(), dto);
    db.commit();
  }

  private void insertInternalProperty(String key, String value) {
    db.getDbClient().internalPropertiesDao().save(db.getSession(), key, value);
    db.commit();
  }
}
