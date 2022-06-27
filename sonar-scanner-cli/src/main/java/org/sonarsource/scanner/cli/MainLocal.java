/*
 * SonarQube Scanner
 * Copyright (C) 2011-2019 SonarSource SA
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
package org.sonarsource.scanner.cli;

import java.util.UUID;

public class MainLocal{
  public static void main(String[] args) {
    args = new String[]{
      "-Dsonar.projectKey=" + UUID.randomUUID().toString().replaceAll("-", ""),
      "-Dsonar.projectName=HelloWorld",
      "-X",
      "-Dsonar.sorceEncoding=UTF-8",
      "-Dsonar.login=admin",
      "-Dsonar.password=admin",
      "-Dsonar.host.url=http://127.0.0.1:9000",
//      "-Dsonar.host.url=http://192.168.52.1:9000",
      //"-Dsonar.sources=C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\sonar-scanner-cli\\src\\test\\resources\\org\\sonarsource\\scanner\\cli\\pluginTest",
      "-Dsonar.sources=C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\sonar-scanner-cli\\src\\test\\resources\\org\\sonarsource\\scanner\\cli\\pluginTest" ,
      "-Dsonar.projectBaseDir=C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\sonar-scanner-cli\\src\\test\\resources\\org\\sonarsource\\scanner\\cli\\pluginTest",
      "-Dsonar.java.binaries=C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\sonar-scanner-cli\\target\\classes"
    };
    Main.main(args);
  }
}
