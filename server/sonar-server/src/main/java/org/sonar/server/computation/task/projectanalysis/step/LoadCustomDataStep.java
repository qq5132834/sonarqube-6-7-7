package org.sonar.server.computation.task.projectanalysis.step;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.measure.MeasureDao;
import org.sonar.db.measure.MeasureDto;
import org.sonar.server.computation.task.projectanalysis.batch.BatchReportReader;
import org.sonar.server.computation.task.projectanalysis.component.Component;
import org.sonar.server.computation.task.projectanalysis.component.TreeRootHolder;
import org.sonar.server.computation.task.projectanalysis.measure.Measure;
import org.sonar.server.computation.task.projectanalysis.measure.MeasureRepository;
import org.sonar.server.computation.task.projectanalysis.measure.MeasureToMeasureDto;
import org.sonar.server.computation.task.projectanalysis.metric.Metric;
import org.sonar.server.computation.task.projectanalysis.metric.MetricRepository;
import org.sonar.server.computation.task.step.ComputationStep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/***
 * 加载自定义custom_data.json数据
 */
public class LoadCustomDataStep implements ComputationStep {

    private final BatchReportReader reportReader;

    private final static Logger LOGGER = Loggers.get(LoadCustomDataStep.class);

    private final TreeRootHolder treeRootHolder;
    private final MetricRepository metricRepository;
    private final MeasureToMeasureDto measureToMeasureDto;
    private final MeasureRepository measureRepository;
    private final DbClient dbClient;

    public LoadCustomDataStep(BatchReportReader reportReader, TreeRootHolder treeRootHolder, MetricRepository metricRepository, MeasureToMeasureDto measureToMeasureDto, MeasureRepository measureRepository, DbClient dbClient) {
        this.reportReader = reportReader;
        this.treeRootHolder = treeRootHolder;
        this.metricRepository = metricRepository;
        this.measureToMeasureDto = measureToMeasureDto;
        this.measureRepository = measureRepository;
        this.dbClient = dbClient;
    }

    @Override
    public void execute() {

        //读取zip中的自定义文件数据
        final String[] customDataFiles = new String[]{"custom_data.json"};
        List<File> fileList = this.reportReader.readCustomData(customDataFiles);
        Map<String, File> map = new HashMap<>();
        if(fileList != null && fileList.size() > 0){
            fileList.stream().forEach(e->{
                map.put(e.getName(), e);
            });
            //
            fileList.stream().forEach(file->{
                LOGGER.info("自定义文件路径:{}", file.getAbsolutePath());
                try{
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line = null;
                    while ((line = bufferedReader.readLine()) != null){
                        stringBuilder.append(line);
                        stringBuilder.append("\n");
                    }
                    bufferedReader.close();
                    LOGGER.info("文件内容:{}", stringBuilder.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        //随机写入50以内的sca值
        try (DbSession dbSession = dbClient.openSession(false)) {
            Component component = this.treeRootHolder.getRoot();
            MeasureDao measureDao = dbClient.measureDao();
            Measure measure = Measure.newMeasureBuilder().create(new Random().nextInt(50), String.valueOf(new Random().nextInt(50)));
            Metric metric = this.metricRepository.getByKey(CoreMetrics.SCA_KEY);
            MeasureDto measureDto = this.measureToMeasureDto.toMeasureDto(measure, metric, component);
            measureDao.insert(dbSession, measureDto);
            dbSession.commit();
        }


    }

    @Override
    public String getDescription() {
        return "加载自定义数据";
    }
}
