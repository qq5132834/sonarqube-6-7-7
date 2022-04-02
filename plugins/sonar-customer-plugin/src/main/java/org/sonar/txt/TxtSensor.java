package org.sonar.txt;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.rule.RuleKey;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public abstract class TxtSensor implements Sensor {

    private final FileSystem fs;
    private final ActiveRules activeRules;
    public static final String RULE_KEY = "txt";

    public TxtSensor(FileSystem fs, ActiveRules activeRules) {
        this.fs = fs;
        this.activeRules = activeRules;
    }

    public ActiveRules getActiveRules(){
        return this.activeRules;
    }

    protected String getRuleKey() {
        return RULE_KEY;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.onlyOnLanguage(TxtLanguage.KEY)
                .createIssuesForRuleRepository(TxtContainRuleDefinition.TXT_REPOSITORY);
    }

    @Override
    public void execute(SensorContext context) {
        doAnalyse(context, TxtLanguage.KEY);
    }

    private void doAnalyse(SensorContext context, String languageKey) {
        RuleKey ruleKey = RuleKey.of(TxtContainRuleDefinition.TXT_REPOSITORY, getRuleKey());
        if (activeRules.find(ruleKey) == null) {
            return;
        }
        for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage(languageKey))) {
            processFile(inputFile, context, ruleKey, languageKey);
        }
    }

    protected void processFile(InputFile inputFile, SensorContext context, RuleKey ruleKey, String languageKey) {
        ActiveRule activeRule = this.getActiveRules().find(ruleKey);
        if(activeRule == null){
            return;
        }
        try {
            InputStreamReader isr = new InputStreamReader(inputFile.inputStream(), inputFile.charset());
            BufferedReader bufferedReader = new BufferedReader(isr);
            bufferedReader.lines().forEachOrdered(line -> {
                NewIssue newIssue = context.newIssue();
                int startline = 1;
                int startIndex = 2;
                int endLine = 1;
                int endLineOffset = 5;
                newIssue.forRule(ruleKey)
                        .at(newIssue.newLocation()
                                .on(inputFile)
                                .at(inputFile.newRange(startline, startIndex, endLine, endLineOffset)))
                        .save();
            });
        } catch (Exception e) {
            throw new IllegalStateException("Fail to process " + inputFile, e);
        }

    }
}