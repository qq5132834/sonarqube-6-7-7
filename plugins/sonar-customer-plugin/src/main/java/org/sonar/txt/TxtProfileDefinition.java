package org.sonar.txt;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RulePriority;
import org.sonar.api.utils.ValidationMessages;

import java.util.ArrayList;
import java.util.List;

import static org.sonar.api.rules.RulePriority.*;

public class TxtProfileDefinition extends ProfileDefinition {

    public static final String PROFILE = "txt";

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        //return this.getRulesProfile();
        return this.getActiveRulesProfile();
    }

    //添加激活规则
    private RulesProfile getActiveRulesProfile(){
        RulesProfile profile = RulesProfile.create(PROFILE, TxtLanguage.KEY);
        profile.activateRule(Rule.create(TxtContainRuleDefinition.TXT_REPOSITORY, TxtSensor.RULE_KEY), BLOCKER);
        return profile;
    }

    //添加未激活规则
    private RulesProfile getRulesProfile(){
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

//    public RulesProfile createProfile(ValidationMessages validation) {
//        RulesProfile profile = RulesProfile.create("FooLint Rules", FooLanguage.KEY);
//        profile.activateRule(Rule.create(REPO_KEY, "ExampleRule1"), BLOCKER);
//        profile.activateRule(Rule.create(REPO_KEY, "ExampleRule2"), MAJOR);
//        profile.activateRule(Rule.create(REPO_KEY, "ExampleRule3"), CRITICAL);
//        return profile;
//    }

}
