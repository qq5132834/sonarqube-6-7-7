package org.sonar.txt;

import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.InputModuleHierarchy;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.highlighting.TypeOfText;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.config.Configuration;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonarqube.ws.client.DefaultWsClient;
import org.sonarqube.ws.client.HttpConnector;
import org.sonarqube.ws.client.WsClient;
import org.sonarqube.ws.client.WsClientFactories;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class TxtSensor implements Sensor {

    private final ActiveRules activeRules;
    private final static Logger LOGGER = Loggers.get(TxtSensor.class);
    public static final String RULE_KEY = "txt";
    public final InputModuleHierarchy inputModuleHierarchy;
    public final Configuration configuration;

    public TxtSensor(ActiveRules activeRules, InputModuleHierarchy inputModuleHierarchy, Configuration configuration) {
        this.activeRules = activeRules;
        this.inputModuleHierarchy = inputModuleHierarchy;
        this.configuration = configuration;
    }

   public ActiveRules getActiveRules(){
        return this.activeRules;
    }

    protected String getRuleKey() {
        return RULE_KEY;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name(getClass().getName());
//        descriptor.onlyOnLanguage(TxtLanguage.KEY)
//                .createIssuesForRuleRepository(TxtContainRuleDefinition.TXT_REPOSITORY);
    }

    @Override
    public void execute(SensorContext context) {
        doAnalyse(context, TxtLanguage.KEY);
    }

    private void doAnalyse(SensorContext context, String languageKey) {
        FileSystem fs = context.fileSystem();
        RuleKey ruleKey = RuleKey.of(TxtContainRuleDefinition.TXT_REPOSITORY, getRuleKey());
//        if (activeRules.find(ruleKey) == null) {
//            return;
//        }

        LOGGER.info("TxtSensor.sonar.password:{}", this.configuration.get("sonar.password").get());
        LOGGER.info("TxtSensor.sonar.login:{}", this.configuration.get("sonar.login").get());
        LOGGER.info("TxtSensor.sonar.host.url:{}", this.configuration.get("sonar.host.url").get());

        //在scanner-report.zip文件中创建一个custom_data.json文件，并在其中写入数据data
        context.newCustomData()
                .setFileName("custom_data.json")
                .setData("hallo, this is my custom data JSON.")
                .save();

        //
        this.writeData("cutom_data.txt", "hello，this is my custom data txt.");

        //在插件中通过http接口向服务器提交数据
//        HttpConnector connector = HttpConnector.newBuilder()
//                                                .url(this.configuration.get("sonar.host.url").get())
//                                                .token("token")
//                                                .credentials("login", "password")
//                                                .build();
//        WsClient wsClient = WsClientFactories.getDefault().newClient(connector);
//        DefaultWsClient defaultWsClient = (DefaultWsClient) wsClient;
//        defaultWsClient.projects().create();

        for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage(languageKey))) {
            String fileName = inputFile.filename();
            boolean isFile = inputFile.isFile();
            File file = inputFile.file();
            LOGGER.info("fileName:{},isFile:{},path:{}", fileName, isFile, file.getAbsolutePath());
            processFile(inputFile, context, ruleKey, languageKey);
        }
    }

    private void writeData(String filename, String filedata){
        Path reportDir = this.inputModuleHierarchy.root().getWorkDir().resolve("scanner-report");
        File file = new File(reportDir.toFile(), filename);
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(filedata);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        catch (Exception e){
            e.printStackTrace();
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
            AtomicBoolean st = new AtomicBoolean(false);
            bufferedReader.lines().forEachOrdered(line -> {
                if(!st.get()){
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
                    st.set(true);

                    //整行高亮，注意同文件不能执行两次save，所以同一个文件的highlight高亮部分，必须写在一起。
                    context.newHighlighting()
                            .onFile(inputFile)
                            .highlight(inputFile.selectLine(1), TypeOfText.KEYWORD)
                            .highlight(inputFile.selectLine(2), TypeOfText.CONSTANT)
                            .highlight(3, 1, 3, 5, TypeOfText.STRING )
                            .highlight(4, 7, 4, 11, TypeOfText.KEYWORD )
                    .save();

                    //覆盖率
                    context.newCoverage()
                            .onFile(inputFile)
                            .conditions(5,2,3)
                            .lineHits(5, 2)
                            .save();

                    context.newAnalysisError()
                            .onFile(inputFile)
                            .at(new DefaultTextPointer(6, 5))
                            .message("分析错误")
                            .save();


                    context.newCpdTokens()
                            .onFile(inputFile)
                            .addToken(7, 1, 7, 5,"https://ns-strategy.cdn.bcebos.com/ns-strategy/upload/fc_big_pic/part-00305-1258.jpg")
                            .save();

                    //
//                    Metric<Integer> FILENAME_SIZE = new Metric.Builder("filename_size", "Filename Size", Metric.ValueType.INT)
//                            .setDescription("Number of characters of file names")
//                            .setDirection(Metric.DIRECTION_BETTER)
//                            .setQualitative(false)
//                            .setDomain(CoreMetrics.DOMAIN_GENERAL)
//                            .create();
//                    context.<Integer>newMeasure()
//                            .forMetric(FILENAME_SIZE)
//                            .on(inputFile)
//                            .withValue(inputFile.file().getName().length())
//                            .save();


//                    context.newSymbolTable()
//                            .onFile(inputFile)
//                            .newSymbol(7, 1, 7, 5)
//                            .save();





                }

            });
        } catch (Exception e) {
            throw new IllegalStateException("Fail to process " + inputFile, e);
        }

    }
}
