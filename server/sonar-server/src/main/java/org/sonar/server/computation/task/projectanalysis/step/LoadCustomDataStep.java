package org.sonar.server.computation.task.projectanalysis.step;

import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.CloseableIterator;
import org.sonar.server.computation.task.projectanalysis.batch.BatchReportReader;
import org.sonar.server.computation.task.step.ComputationStep;

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
        CloseableIterator<String> closeableIterator = this.reportReader.readCustomData();
        if(closeableIterator != null){
            LOGGER.info("LoadCustomDataStep.execute.自定义数据:{}", closeableIterator.next());
        }
    }

    @Override
    public String getDescription() {
        return "加载自定义数据";
    }
}
