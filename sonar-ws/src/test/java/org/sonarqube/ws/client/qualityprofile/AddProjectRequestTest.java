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
package org.sonarqube.ws.client.qualityprofile;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class AddProjectRequestTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  AddProjectRequest.Builder underTest = AddProjectRequest.builder();

  @Test
  public void create_request_from_profile_key_and_project_key() {
    AddProjectRequest result = underTest.setKey("SonarWay").setProjectKey("sample").build();

    assertThat(result.getLanguage()).isNull();
    assertThat(result.getKey()).isEqualTo("SonarWay");
    assertThat(result.getQualityProfile()).isNull();
    assertThat(result.getProjectKey()).isEqualTo("sample");
    assertThat(result.getProjectUuid()).isNull();
  }

  @Test
  public void create_request_from_profile_name_and_language_and_project_key() {
    AddProjectRequest result = underTest.setLanguage("xoo").setQualityProfile("Sonar Way").setProjectKey("sample").build();

    assertThat(result.getLanguage()).isEqualTo("xoo");
    assertThat(result.getKey()).isNull();
    assertThat(result.getQualityProfile()).isEqualTo("Sonar Way");
    assertThat(result.getProjectKey()).isEqualTo("sample");
    assertThat(result.getProjectUuid()).isNull();
  }

  @Test
  public void create_request_from_profile_key_and_project_uuid() {
    AddProjectRequest result = underTest.setKey("SonarWay").setProjectUuid("123").build();

    assertThat(result.getLanguage()).isNull();
    assertThat(result.getKey()).isEqualTo("SonarWay");
    assertThat(result.getQualityProfile()).isNull();
    assertThat(result.getProjectKey()).isNull();
    assertThat(result.getProjectUuid()).isEqualTo("123");
  }

}
