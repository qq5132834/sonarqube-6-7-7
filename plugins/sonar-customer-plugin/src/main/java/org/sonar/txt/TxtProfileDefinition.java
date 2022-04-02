package org.sonar.txt;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.ValidationMessages;

import java.util.ArrayList;
import java.util.List;

public class TxtProfileDefinition extends ProfileDefinition {

    public static final String PROFILE = "txt";

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        RulesProfile rulesProfile = RulesProfile.create();
        rulesProfile.setName(PROFILE);
        rulesProfile.setLanguage(TxtLanguage.KEY);
        rulesProfile.setActiveRules(this.getActiveRuleList());
        rulesProfile.setDefaultProfile(false);
        return rulesProfile;
    }

    private List<ActiveRule> getActiveRuleList(){

        Rule rule = Rule.create(TxtContainRuleDefinition.TXT_REPOSITORY, TxtSensor.RULE_KEY);
        //rule.setStatus("");

        List<ActiveRule> activeRuleList = new ArrayList<>();
        ActiveRule activeRule = new ActiveRule();
        activeRule.setRule(rule);
        activeRule.setPriority(RulePriority.MAJOR);
        return activeRuleList;
    }


//        profile.activateRule(Rule.create(XooRulesDefinition.XOO2_REPOSITORY, HasTagSensor.RULE_KEY), RulePriority.MAJOR);
//        profile.activateRule(Rule.create(XooRulesDefinition.XOO_REPOSITORY, HasTagSensor.RULE_KEY), RulePriority.MAJOR);
//        profile.activateRule(Rule.create(XooRulesDefinition.XOO_REPOSITORY, OneIssuePerLineSensor.RULE_KEY), RulePriority.INFO);
//        profile.activateRule(Rule.create(XooRulesDefinition.XOO_REPOSITORY, OneIssuePerFileSensor.RULE_KEY), RulePriority.CRITICAL);

}
