package org.sonar.server.demo;

import org.apache.commons.io.FileUtils;
import org.sonar.api.Plugin;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.internal.DefaultTempFolder;
import org.sonar.core.platform.PluginClassloaderFactory;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginJarExploder;
import org.sonar.core.platform.PluginLoader;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.platform.ServerFileSystemImpl;
import org.sonar.server.plugins.ServerPluginJarExploder;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.sonar.core.util.FileUtils.deleteQuietly;

/***
 * 加载plugin-jar文件
 *
 * 参考：
 * 1. ServerPluginRepository.java
 *      loadPreInstalledPlugins()
 *      loadInstances()
 * 2.
 */
public class LoadPluginJarFileDemoTest {

    private Map<String, PluginInfo> pluginInfosByKeys = new HashMap<>();
    private  Map<String, Plugin> pluginInstancesByKeys = new HashMap<>();


    public static void main(String[] args) {

        String dir = "C:\\Users\\51328\\Desktop\\sonarqube-6-7-7-application\\sonarqube-6.7.7\\sonarqube-6.7.7\\extensions\\plugins";

        LoadPluginJarFileDemoTest loadPluginJarFile = new LoadPluginJarFileDemoTest();
        loadPluginJarFile.loadPreInstalledPlugins(new File(dir));

    }

    private void loadPreInstalledPlugins(File pluginDir){
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

        Map<ClassLoader, String> keysByClassLoader = new HashMap<>();

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
