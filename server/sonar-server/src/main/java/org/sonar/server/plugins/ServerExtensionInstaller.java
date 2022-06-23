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
package org.sonar.server.plugins;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.sonar.api.ExtensionProvider;
import org.sonar.api.Plugin;
import org.sonar.api.SonarRuntime;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.platform.ComponentContainer;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginRepository;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

/**
 * Loads the plugins server extensions and injects them to DI container
 */
public abstract class ServerExtensionInstaller {
  private final static Logger LOGGER = Loggers.get(ServerExtensionInstaller.class);
  private final SonarRuntime sonarRuntime;
  private final PluginRepository pluginRepository;
  private final Set<Class<? extends Annotation>> supportedAnnotationTypes;

  protected ServerExtensionInstaller(SonarRuntime sonarRuntime, PluginRepository pluginRepository,
    @Nullable Collection<Class<? extends Annotation>> supportedAnnotationTypes) {
    checkArgument(supportedAnnotationTypes != null && !supportedAnnotationTypes.isEmpty(),
      "At least one supported annotation type must be specified");
    this.sonarRuntime = sonarRuntime;
    this.pluginRepository = pluginRepository;
    this.supportedAnnotationTypes = ImmutableSet.copyOf(supportedAnnotationTypes);

  }

  protected ServerExtensionInstaller(SonarRuntime sonarRuntime, PluginRepository pluginRepository,
    Class<? extends Annotation>... supportedAnnotationTypes) {
    requireNonNull(supportedAnnotationTypes, "At least one supported annotation type must be specified");
    this.sonarRuntime = sonarRuntime;
    this.pluginRepository = pluginRepository;
    this.supportedAnnotationTypes = ImmutableSet.copyOf(supportedAnnotationTypes);
  }

  public void installExtensions(ComponentContainer container) {
    ListMultimap<PluginInfo, Object> installedExtensionsByPlugin = ArrayListMultimap.create();
    LOGGER.info("安装插件中Context中添加的extension，并将extension注入到pico容器中，ServerExtensionInstaller.installExtensions()");
    for (PluginInfo pluginInfo : pluginRepository.getPluginInfos()) {
      try {
        String pluginKey = pluginInfo.getKey();
        Plugin plugin = pluginRepository.getPluginInstance(pluginKey);
        container.addExtension(pluginInfo, plugin);
        LOGGER.info("---安装pluginKey:{};plugin:{}", pluginKey, plugin.getClass().getName());
        Plugin.Context context = new Plugin.Context(sonarRuntime);
        plugin.define(context);
        for (Object extension : context.getExtensions()) {
          if (installExtension(container, pluginInfo, extension, true) != null) {  //这里注入容器
            LOGGER.info("------extension.class:{}, toString:{}", extension.getClass().getName(), extension.toString());
            installedExtensionsByPlugin.put(pluginInfo, extension);  //extension实例保存在以pluginInfo为key的map列表中
          } else {
            System.out.println("------declareExtension.class:" + extension.getClass().getName() + ", toString:" + extension.toString());
            container.declareExtension(pluginInfo, extension);
          }
        }
      } catch (Throwable e) {
        // catch Throwable because we want to catch Error too (IncompatibleClassChangeError, ...)
        throw new IllegalStateException(String.format("Fail to load plugin %s [%s]", pluginInfo.getName(), pluginInfo.getKey()), e);
      }
    }
    //从容器中获取plugin中定义的规则定义实例
    LOGGER.info("从容器中获取plugin中定义的规则定义实例，如：RulesDefinition、ProfileDefinition....");
    List<RulesDefinition> rulesDefinitionList = container.getComponentsByType(RulesDefinition.class);//这个在
    rulesDefinitionList.stream().forEach(e->{LOGGER.info(e.getClass().getSimpleName());});
    List<ProfileDefinition> profileDefinitionList = container.getComponentsByType(ProfileDefinition.class);
    profileDefinitionList.stream().forEach(e->{LOGGER.info(e.getClass().getSimpleName());});
    List<RuleRepository> ruleRepositoryList = container.getComponentsByType(RuleRepository.class);
    ruleRepositoryList.stream().forEach(e->{LOGGER.info(e.getClass().getSimpleName());});

    for (Map.Entry<PluginInfo, Object> entry : installedExtensionsByPlugin.entries()) {
      PluginInfo pluginInfo = entry.getKey();
      try {
        Object extension = entry.getValue();
        if (isExtensionProvider(extension)) {
          ExtensionProvider provider = (ExtensionProvider) container.getComponentByKey(extension);
          installProvider(container, pluginInfo, provider);
        }
      } catch (Throwable e) {
        // catch Throwable because we want to catch Error too (IncompatibleClassChangeError, ...)
        throw new IllegalStateException(String.format("Fail to load plugin %s [%s]", pluginInfo.getName(), pluginInfo.getKey()), e);
      }
    }
  }

  private void installProvider(ComponentContainer container, PluginInfo pluginInfo, ExtensionProvider provider) {
    Object obj = provider.provide();
    if (obj != null) {
      if (obj instanceof Iterable) {
        for (Object ext : (Iterable) obj) {
          installExtension(container, pluginInfo, ext, false);
        }
      } else {
        installExtension(container, pluginInfo, obj, false);
      }
    }
  }

  Object installExtension(ComponentContainer container, PluginInfo pluginInfo, Object extension, boolean acceptProvider) {
    for (Class<? extends Annotation> supportedAnnotationType : supportedAnnotationTypes) {
      if (AnnotationUtils.getAnnotation(extension, supportedAnnotationType) != null) {
        if (!acceptProvider && isExtensionProvider(extension)) {
          throw new IllegalStateException("ExtensionProvider can not include providers itself: " + extension);
        }
        container.addExtension(pluginInfo, extension);  //注入容器
        return extension;
      }
    }
    return null;
  }

  static boolean isExtensionProvider(Object extension) {
    return isType(extension, ExtensionProvider.class) || extension instanceof ExtensionProvider;
  }

  static boolean isType(Object extension, Class extensionClass) {
    Class clazz = extension instanceof Class ? (Class) extension : extension.getClass();
    return extensionClass.isAssignableFrom(clazz);
  }
}
