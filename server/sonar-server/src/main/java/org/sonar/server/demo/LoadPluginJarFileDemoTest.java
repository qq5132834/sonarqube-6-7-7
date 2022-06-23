package org.sonar.server.demo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.gson.internal.$Gson$Preconditions;
import org.apache.commons.io.FileUtils;
import org.picocontainer.MutablePicoContainer;
import org.sonar.api.*;
import org.sonar.api.config.Configuration;
import org.sonar.api.config.Settings;
import org.sonar.api.internal.ApiVersion;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.XMLProfileParser;
import org.sonar.api.resources.Language;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleRepository;
import org.sonar.api.server.ServerSide;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinitionXmlLoader;
import org.sonar.api.utils.AnnotationUtils;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.Version;
import org.sonar.api.utils.internal.DefaultTempFolder;
import org.sonar.core.platform.*;
import org.sonar.db.DbClient;
import org.sonar.db.rule.RuleDefinitionDto;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.platform.ServerFileSystemImpl;
import org.sonar.server.plugins.ServerPluginJarExploder;
import org.sonar.server.rule.WebServerRuleFinderImpl;
import org.sonar.server.settings.ChildSettings;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.*;

import static java.lang.String.format;
import static org.sonar.core.util.FileUtils.deleteQuietly;

/***
 * 加载plugin-jar文件
 *
 * 参考：
 * 1. ServerPluginRepository.java
 *      loadPreInstalledPlugins()
 *      loadInstances()
 * 2、RegisterRules.java
 *      start()
 * 3. RuleDefinitionsLoader.java
 *      load()
 * 4. ServerExtensionInstaller.java  将插件中plugin接口实现类中addExtension的对象注入容器，比如：RulesDefinition[]
 *      installExtensions()
 * 5. RuleDefinitionsLoader.java 将注入的RulesDefinition[] 加载到数据库中
 *      load()
 * 6. RegisterRules.java
 *      start()
 *
 */
public class LoadPluginJarFileDemoTest {

    private Map<String, PluginInfo> pluginInfosByKeys = new HashMap<>();
    private Map<String, Plugin> pluginInstancesByKeys = new HashMap<>();
    private Map<ClassLoader, String> keysByClassLoader = new HashMap<>();
    public static void main(String[] args) {

//    RulesDefinitionXmlLoader rulesDefinitionXmlLoader
//            = container.getComponentByType(RulesDefinitionXmlLoader.class);

        RuleFinder finder = new WebServerRuleFinderImpl(new DbClient(null, null, null), null);
        ComponentContainer myComponentContainer = new ComponentContainer();
        myComponentContainer.add(new RulesDefinitionXmlLoader());
        myComponentContainer.add(new XMLProfileParser(finder));
        myComponentContainer.add(finder);
        myComponentContainer.add(new Settings(){

            @Override
            protected Optional<String> get(String key) {
                return Optional.empty();
            }

            @Override
            protected void set(String key, String value) {

            }

            @Override
            protected void remove(String key) {

            }

            @Override
            public Map<String, String> getProperties() {
                return null;
            }
        });
//        myComponentContainer.add(new RulesDefinitionXmlLoader());

        String dir = "C:\\Users\\51328\\Desktop\\sonarqube-6-7-7-application\\sonarqube-6.7.7\\sonarqube-6.7.7\\extensions\\plugins";
        LoadPluginJarFileDemoTest loadPluginJarFile = new LoadPluginJarFileDemoTest();
        loadPluginJarFile.loadPreInstalledPlugins(new File(dir));
        loadPluginJarFile.installExtensions(myComponentContainer);
        System.out.println();

    }

    /***
     * 参考
     * RuleDefinitionsLoader.java
     *      load()
     */
    public RulesDefinition.Context load(List<RulesDefinition> pluginDefs) {
        RulesDefinition.Context context = new RulesDefinition.Context();
        for (RulesDefinition pluginDefinition : pluginDefs) {
//            String pluginKey = serverPluginRepository.getPluginKey(pluginDefinition);
            String pluginKey = keysByClassLoader.get(pluginDefinition.getClass().getClassLoader());
            context.setCurrentPluginKey(pluginKey);
            pluginDefinition.define(context); //在这里，在context中存入插件中定义的全部规则内容。可以直接debug到Oxx插件的RuleDefintion查看过程。
        }
//        deprecatedDefConverter.complete(context); //规则债务
        context.setCurrentPluginKey(null);
//        coreCommonDefs.define(context);  //添加sonarqube自带通用规则
        return context;
    }


    /***
     * 参考：
     *  RegisterRules.java
     *      getRepositories()方法
     */
    private List<RulesDefinition.ExtendedRepository> getRepositories(RulesDefinition.Context context) {
        List<RulesDefinition.ExtendedRepository> repositories = new ArrayList<>();
        for (RulesDefinition.Repository repoDef : context.repositories()) {
            repositories.add(repoDef);
        }
        for (RulesDefinition.ExtendedRepository extendedRepoDef : context.extendedRepositories()) {
            if (context.repository(extendedRepoDef.key()) == null) {
               // LOG.warn(format("Extension is ignored, repository %s does not exist", extendedRepoDef.key()));
            } else {
                repositories.add(extendedRepoDef);
            }
        }
        return repositories;
    }

    /***
     * 参考：ServerExtensionInstaller.installExtensions()， 这个类的主要作用是将插件中plugin接口的实现类中 addExtension
     */
    public void installExtensions(ComponentContainer container){

        ListMultimap<PluginInfo, Object> installedExtensionsByPlugin = ArrayListMultimap.create();
        for (PluginInfo pluginInfo : this.pluginInfosByKeys.values()) {
            String pluginKey = pluginInfo.getKey();
            Plugin plugin = this.pluginInstancesByKeys.get(pluginKey);
            container.addExtension(pluginInfo, plugin);
            System.out.println("---安装pluginKey:" + pluginKey + ";plugin:" + plugin.getClass().getName());
            Plugin.Context context = new Plugin.Context(this.createSonarRuntime());
            plugin.define(context);
            for (Object extension : context.getExtensions()) {
                if (installExtension(container, pluginInfo, extension, true) != null) {  //这里注入容器
                    System.out.println("------extension.class:" + extension.getClass().getName() + ", toString:" + extension.toString());
                    installedExtensionsByPlugin.put(pluginInfo, extension);  //extension实例保存在以pluginInfo为key的map列表中
                } else {
                    System.out.println("------declareExtension.class:" + extension.getClass().getName() + ", toString:" + extension.toString());
                    container.declareExtension(pluginInfo, extension);
                }
            }
        }

        List<RulesDefinition> rulesDefinitionList = container.getComponentsByType(RulesDefinition.class);//这个在
        rulesDefinitionList.stream().forEach(e->{System.out.println(e.getClass().getSimpleName());});
        List<ProfileDefinition> profileDefinitionList = container.getComponentsByType(ProfileDefinition.class);
        profileDefinitionList.stream().forEach(e->{System.out.println(e.getClass().getSimpleName());});
        List<RuleRepository> ruleRepositoryList = container.getComponentsByType(RuleRepository.class);
        ruleRepositoryList.stream().forEach(e->{System.out.println(e.getClass().getSimpleName());});
        List<Language> languageList = container.getComponentsByType(Language.class);
        languageList.stream().forEach(e->System.out.println(e.getKey() + "/" + e.getName() + "/" + e.getClass().getSimpleName()));

        System.out.println("\n\n\n");

        //
        RulesDefinition.Context context = this.load(rulesDefinitionList);
        for (RulesDefinition.ExtendedRepository repoDef : getRepositories(context)){
            for (RulesDefinition.Rule ruleDef : repoDef.rules()) {
                //将插件中的规则信息写入到ruleDef中
                RuleKey ruleKey = RuleKey.of(ruleDef.repository().key(), ruleDef.key());
            }
        }
    }

    private Object installExtension(ComponentContainer container, PluginInfo pluginInfo, Object extension, boolean acceptProvider) {
        List<Class<? extends Annotation>> supportedAnnotationTypes = new ArrayList<>();
        supportedAnnotationTypes.add(ServerSide.class); //将含有 ServerSide.class 注解的类注入
        for (Class<? extends Annotation> supportedAnnotationType : supportedAnnotationTypes) {
            if (AnnotationUtils.getAnnotation(extension, supportedAnnotationType) != null) {
                if (!acceptProvider && isExtensionProvider(extension)) {
                    throw new IllegalStateException("ExtensionProvider can not include providers itself: " + extension);
                }
                container.addExtension(pluginInfo, extension); //注入容器
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

    private SonarRuntime createSonarRuntime(){
        Version apiVersion = ApiVersion.load(System2.INSTANCE);;
        SonarQubeSide sonarQubeSide = SonarQubeSide.SERVER;
        SonarRuntime sonarRuntime = SonarRuntimeImpl.forSonarQube(apiVersion, sonarQubeSide);
        return sonarRuntime;
    }

    public void loadPreInstalledPlugins(File pluginDir){
        this.listJarFiles(pluginDir).stream().forEach(f->{
            System.out.println(f.getName());
            PluginInfo info = PluginInfo.create(f);
            pluginInfosByKeys.put(info.getKey(), info);
        });

        pluginInfosByKeys.entrySet().stream().forEach(e->{
            System.out.println(e.getKey());
        });

        this.loadInstances(pluginInfosByKeys);

    }

    private void loadInstances(Map<String, PluginInfo> pluginInfosByKeys) {

        PluginJarExploder jarExploder = new LoadPluginJarExploderDemoTest();
        PluginClassloaderFactory classloaderFactory = new PluginClassloaderFactory(new DefaultTempFolder(new File("D:\\temp")));
        PluginLoader loader = new PluginLoader(jarExploder, classloaderFactory);

        pluginInstancesByKeys.putAll(loader.load(pluginInfosByKeys));

        for (Map.Entry<String, Plugin> e : pluginInstancesByKeys.entrySet()) {
            keysByClassLoader.put(e.getValue().getClass().getClassLoader(), e.getKey());
        }
    }

    private Collection<File> listJarFiles(File dir) {
        if (dir.exists()) {
            final String[] JAR_FILE_EXTENSIONS = new String[] {"jar"};
            return FileUtils.listFiles(dir, JAR_FILE_EXTENSIONS, false);
        }
        return Collections.emptyList();
    }


}
