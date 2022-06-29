package org.sonar.txt;

import org.sonar.api.server.rule.RulesDefinition;

public class TxtContainRuleDefinition implements RulesDefinition {

    public static final String TXT_REPOSITORY = "txt_repository";
    public static final String TXT_RULE_KEY = "txt_rule_key";

    @Override
    public void define(RulesDefinition.Context context) {

        /**
         * 在rule_repositories表中记录kee、language、name字段
         */
        NewRepository repo = context.createRepository(TxtContainRuleDefinition.TXT_REPOSITORY, TxtLanguage.LANGUAGE)
                .setName("rule_repositories表的name字段");

        /***
         * 在rules表中记录以下数据
         */
        NewRule containHello = repo.createRule(TXT_RULE_KEY)
                            .setActivatedByDefault(true)
                            .setName("txtFileContainHello")
                            .setHtmlDescription("查询文本文件是否含有hello字符串"); //查询文件的hello单词

        repo.done();
    }

}
