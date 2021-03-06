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
package org.sonar.server.projectanalysis.ws;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Consumer;
import org.sonar.db.event.EventDto;
import org.sonarqube.ws.client.projectanalysis.EventCategory;

import static com.google.common.base.Preconditions.checkArgument;
import static org.sonarqube.ws.client.projectanalysis.EventCategory.OTHER;
import static org.sonarqube.ws.client.projectanalysis.EventCategory.VERSION;
import static org.sonarqube.ws.client.projectanalysis.EventCategory.fromLabel;

class EventValidator {
  private static final Set<String> AUTHORIZED_CATEGORIES = ImmutableSet.of(VERSION.name(), OTHER.name());
  private static final String AUTHORIZED_CATEGORIES_INLINED = Joiner.on(", ").join(AUTHORIZED_CATEGORIES);

  private EventValidator() {
    // prevent instantiation
  }

  static Consumer<EventDto> checkModifiable() {
    return event -> checkArgument(AUTHORIZED_CATEGORIES.contains(fromLabel(event.getCategory()).name()),
      "Event of category '%s' cannot be modified. Authorized categories: %s",
      EventCategory.fromLabel(event.getCategory()), AUTHORIZED_CATEGORIES_INLINED);
  }
}
