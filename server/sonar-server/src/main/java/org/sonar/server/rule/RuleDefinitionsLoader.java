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
package org.sonar.server.rule;

import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.server.plugins.ServerPluginRepository;

/**
 * Loads all instances of {@link RulesDefinition}. Used during server startup
 * and restore of debt model backup.
 */
public class RuleDefinitionsLoader {
  private final static Logger LOGGER = Loggers.get(RuleDefinitionsLoader.class);
  private final DeprecatedRulesDefinitionLoader deprecatedDefConverter;
  private final CommonRuleDefinitions coreCommonDefs;
  private final RulesDefinition[] pluginDefs;
  private final ServerPluginRepository serverPluginRepository;

  public RuleDefinitionsLoader(DeprecatedRulesDefinitionLoader deprecatedDefConverter,
    CommonRuleDefinitions coreCommonDefs, ServerPluginRepository serverPluginRepository, RulesDefinition[] pluginDefs) {
    this.deprecatedDefConverter = deprecatedDefConverter;
    this.coreCommonDefs = coreCommonDefs;
    this.serverPluginRepository = serverPluginRepository;
    this.pluginDefs = pluginDefs;
  }

  /**
   * Used when no definitions at all.
   */
  public RuleDefinitionsLoader(DeprecatedRulesDefinitionLoader converter,
    CommonRuleDefinitions coreCommonDefs, ServerPluginRepository serverPluginRepository) {
    this(converter, coreCommonDefs, serverPluginRepository, new RulesDefinition[0]);
  }

  public RulesDefinition.Context load() {
    LOGGER.info("加载规则定义-RuleDefinitionsLoader.load。注，pluginDefs 数据的初始化是在 ServerExtensionInstaller 中完成容器的注入。");
    RulesDefinition.Context context = new RulesDefinition.Context();
    for (RulesDefinition pluginDefinition : pluginDefs) {
      String pluginKey = serverPluginRepository.getPluginKey(pluginDefinition);
      context.setCurrentPluginKey(pluginKey);
      pluginDefinition.define(context); //在这里，在context中存入插件中定义的全部规则内容。可以直接debug到Oxx插件的RuleDefintion查看过程。
    }
    deprecatedDefConverter.complete(context); //规则债务
    context.setCurrentPluginKey(null);
    coreCommonDefs.define(context);  //添加sonarqube自带通用规则
    return context;
  }
}
