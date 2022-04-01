package org.sonar.txt;

import org.sonar.api.resources.AbstractLanguage;

public class TxtLanguage extends AbstractLanguage {

    public static final String KEY = "txt";
    public static final String NAME = "txt";

    public TxtLanguage(String key, String name) {
        super(KEY, NAME);
    }

    @Override
    public String[] getFileSuffixes() {
        //获取文件后缀
        return new String[]{"txt"};
    }

}
