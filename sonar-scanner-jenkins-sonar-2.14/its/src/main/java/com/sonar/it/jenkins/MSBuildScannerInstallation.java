/*
 * Jenkins :: Integration Tests
 * Copyright (C) 2013-2021 SonarSource SA
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
package com.sonar.it.jenkins;

import org.jenkinsci.test.acceptance.po.Jenkins;
import org.jenkinsci.test.acceptance.po.ToolInstallation;
import org.jenkinsci.test.acceptance.po.ToolInstallationPageObject;

@ToolInstallationPageObject(installer = "hudson.plugins.sonar.MsBuildSonarQubeRunnerInstaller", name = "SonarScanner for MSBuild")
public class MSBuildScannerInstallation extends ToolInstallation {

  public MSBuildScannerInstallation(Jenkins jenkins, String path) {
    super(jenkins, path);
  }

  public static void install(final Jenkins jenkins, final String version, boolean isDotnetCore) {
    installTool(jenkins, MSBuildScannerInstallation.class, getInstallName(version, isDotnetCore), version + (isDotnetCore ? "-netcore" : ""));
  }

  public static String getInstallName(final String version, boolean isDotnetCore) {
    return "SonarScanner for MSBuild" + version + (isDotnetCore ? " - .NET Core" : " - .NET Fwk");
  }

}
