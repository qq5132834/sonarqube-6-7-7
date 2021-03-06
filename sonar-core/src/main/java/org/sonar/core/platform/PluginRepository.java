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
package org.sonar.core.platform;

import java.util.Collection;
import org.sonar.api.Plugin;
import org.sonar.api.batch.ScannerSide;
import org.sonar.api.ce.ComputeEngineSide;
import org.sonar.api.server.ServerSide;

/**
 * Provides information about the plugins installed in the dependency injection container
 */
@ScannerSide
@ServerSide
@ComputeEngineSide
public interface PluginRepository {

  Collection<PluginInfo> getPluginInfos();

  /**
   * Never returns {@code null}, a runtime exception is thrown
   * if no plugins have the specified key.
   */
  PluginInfo getPluginInfo(String key);

  /**
   * @return the instance of {@link Plugin} for the given plugin key. Never return null.
   */
  Plugin getPluginInstance(String key);

  boolean hasPlugin(String key);
}
