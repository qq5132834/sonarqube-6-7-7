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
package org.sonar.txt;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Settings;
import org.sonar.api.resources.AbstractLanguage;

import java.util.ArrayList;
import java.util.List;

public final class TxtLanguage extends AbstractLanguage {

  public static final String LANGUAGE = "txt";
  public static final String KEY = LANGUAGE;
  public static final String NAME = "txt_name";

  private final Settings settings;

  public TxtLanguage(Settings settings) {
    super(KEY, NAME);
    this.settings = settings;
  }

  @Override
  public String[] getFileSuffixes() {
    String[] suffixes = filterEmptyStrings(settings.getStringArray(TxtLanguageProperties.FILE_SUFFIXES_KEY));
    if (suffixes.length == 0) {
      suffixes = StringUtils.split(TxtLanguageProperties.FILE_SUFFIXES_DEFAULT_VALUE, ",");
    }
    return suffixes;
  }

  private String[] filterEmptyStrings(String[] stringArray) {
    List<String> nonEmptyStrings = new ArrayList<>();
    for (String string : stringArray) {
      if (StringUtils.isNotBlank(string.trim())) {
        nonEmptyStrings.add(string.trim());
      }
    }
    return nonEmptyStrings.toArray(new String[nonEmptyStrings.size()]);
  }

}
