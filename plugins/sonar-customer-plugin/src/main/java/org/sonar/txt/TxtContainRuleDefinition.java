package org.sonar.txt;

import org.sonar.api.server.rule.RulesDefinition;

public class TxtContainRuleDefinition implements RulesDefinition {

    public static final String TXT_REPOSITORY = "txt";

    @Override
    public void define(RulesDefinition.Context context) {
        NewRepository repo = context.createRepository(TXT_REPOSITORY, TxtLanguage.KEY).setName("txt");
        NewRule containHello = repo.createRule(TxtSensor.RULE_KEY)
                            .setActivatedByDefault(true)
                            .setName("txtFileContainHello")
                            .setHtmlDescription("查询文本文件是否含有hello字符串"); //查询文件的hello单词
        repo.done();
    }

}
