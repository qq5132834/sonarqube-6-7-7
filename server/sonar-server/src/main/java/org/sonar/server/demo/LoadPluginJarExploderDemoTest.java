package org.sonar.server.demo;

import org.apache.commons.io.FileUtils;
import org.sonar.api.utils.ZipUtils;
import org.sonar.core.platform.ExplodedPlugin;
import org.sonar.core.platform.PluginInfo;
import org.sonar.core.platform.PluginJarExploder;

import java.io.File;

import static org.apache.commons.io.FileUtils.forceMkdir;

public class LoadPluginJarExploderDemoTest extends PluginJarExploder {

    public LoadPluginJarExploderDemoTest(){}

    @Override
    public ExplodedPlugin explode(PluginInfo pluginInfo) {
        String dirPath = pluginInfo.getJarFile().getParentFile().getAbsolutePath() + File.separator + pluginInfo.getKey();
        File toDir = new File(dirPath);
         pluginInfo.getKey();
        try {
            forceMkdir(toDir);
            org.sonar.core.util.FileUtils.cleanDirectory(toDir);

            File jarSource = pluginInfo.getNonNullJarFile();
            File jarTarget = new File(toDir, jarSource.getName());
            FileUtils.copyFile(jarSource, jarTarget);
            ZipUtils.unzip(jarSource, toDir, newLibFilter());
            return explodeFromUnzippedDir(pluginInfo.getKey(), jarTarget, toDir);
        } catch (Exception e) {
            throw new IllegalStateException(String.format(
                    "Fail to unzip plugin [%s] %s to %s", pluginInfo.getKey(), pluginInfo.getNonNullJarFile().getAbsolutePath(), toDir.getAbsolutePath()), e);
        }
    }
}
