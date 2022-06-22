package org.sonar.server.demo;

import org.apache.commons.io.FileUtils;
import org.sonar.core.platform.PluginInfo;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/***
 * 加载jar插件文件
 */
public class LoadPluginJarFile {

    public static void main(String[] args) {
        String dir = "C:\\Users\\51328\\Desktop\\sonarqube-6-7-7-application\\sonarqube-6.7.7\\sonarqube-6.7.7\\extensions\\plugins";
        File pluginDir = new File(dir);
        LoadPluginJarFile loadPluginJarFile = new LoadPluginJarFile();
        loadPluginJarFile.listJarFiles(pluginDir).stream().forEach(f->{
            System.out.println(f.getName());
            PluginInfo info = PluginInfo.create(f);
            System.out.println(info.getKey());
        });
    }


    private Collection<File> listJarFiles(File dir) {
        if (dir.exists()) {
            final String[] JAR_FILE_EXTENSIONS = new String[] {"jar"};
            return FileUtils.listFiles(dir, JAR_FILE_EXTENSIONS, false);
        }
        return Collections.emptyList();
    }


}
