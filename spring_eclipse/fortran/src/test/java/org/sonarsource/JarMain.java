package org.sonarsource;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class JarMain {

    private static Set<String> FILE_SET = new HashSet<String>();

    private static void recursionFile(File file){
        if (file.isFile() && file.getName().endsWith(".jar")) {
            try {
                FILE_SET.add(file.getCanonicalPath());
            }catch (Exception e) {}
        }
        if (file.isDirectory()) {
            Arrays.asList(file.listFiles()).stream().forEach(JarMain::recursionFile);
        }
    }

    public static void main(String[] args) {
        final String JAR_PATH = "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\spring_eclipse\\fortran\\lib";
        recursionFile(new File(JAR_PATH));
        AtomicInteger atomicInteger = new AtomicInteger(0);
        StringBuffer stringBuffer = new StringBuffer();
        FILE_SET.stream().forEach(file -> {
            int i = atomicInteger.addAndGet(1);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<dependency>\n");
            stringBuilder.append("    <groupId>ft</groupId>\n");
            stringBuilder.append("    <artifactId>ft" + i + "</artifactId>\n");
            stringBuilder.append("    <version>" + i + "</version>\n");
            stringBuilder.append("    <scope>system</scope>\n");
            stringBuilder.append("    <systemPath>${pom.basedir}/lib/" + new File(file).getName() + "</systemPath>\n");
            stringBuilder.append("</dependency>\n");

            //
            stringBuffer.append(stringBuilder.toString() + "\n");

        });

        System.out.println(stringBuffer.toString());
    }
}
