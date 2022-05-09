package org.sonar.server.computation.task.projectanalysis.step;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.CloseableIterator;
import org.sonar.server.computation.task.projectanalysis.batch.BatchReportReader;
import org.sonar.server.computation.task.step.ComputationStep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 加载自定义custom_data.json数据
 */
public class LoadCustomDataStep implements ComputationStep {

    private final BatchReportReader reportReader;

    private final static Logger LOGGER = Loggers.get(LoadCustomDataStep.class);

    public LoadCustomDataStep(BatchReportReader reportReader) {
        this.reportReader = reportReader;
    }

    @Override
    public void execute() {
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

    }

    @Override
    public String getDescription() {
        return "加载自定义数据";
    }
}
