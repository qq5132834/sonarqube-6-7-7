/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonarsource.plugins.example.settings;

import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.utils.log.Loggers;

import java.util.Collection;

public class SayHelloFromScanner implements Sensor {

  private final ActiveRules activeRules;

  public SayHelloFromScanner(ActiveRules activeRules) {
    this.activeRules = activeRules;
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name(getClass().getName());
  }

  @Override
  public void execute(SensorContext context) {
    Collection<ActiveRule> activeRules = this.activeRules.findAll();
    Loggers.get(getClass()).info("Hello World!");

//    if (context.settings().getBoolean(HelloWorldProperties.HELLO_KEY)) {
//      // print log only if property is set to true
//      Loggers.get(getClass()).info("Hello World!");
//    }
  }

}
