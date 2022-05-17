package org.sonar.ce.cli;

import org.sonar.ce.CeServerCliComputeEngineImpl;
import org.sonar.ce.ComputeEngine;
import org.sonar.ce.container.CeServerCliComputeEngineContainerImpl;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.process.ProcessEntryPoint;
import org.sonar.process.Props;

/***
 * 一个任务对应一个jar进程。任务执行完成即jar结束
 */
public class CeServerCli {

    private final ComputeEngine computeEngine;

    public CeServerCli(String[] args) {
        ProcessEntryPoint entryPoint = ProcessEntryPoint.createForArguments(args);
        Props props = entryPoint.getProps();
        CeQueueDto ceQueueDto = new CeQueueDto();
        ceQueueDto.setComponentUuid("AYDRm03B6pOU71bJfQ3f");
        ceQueueDto.setUuid("AYDRm05Z6pOU71bJfQ3l");
        ceQueueDto.setTaskType("REPORT");
        ceQueueDto.setSubmitterLogin("admin");
        this.computeEngine = new CeServerCliComputeEngineImpl(props, new CeServerCliComputeEngineContainerImpl(ceQueueDto));
    }

    public static void main(String[] args) {
        args = new String[]{
                "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\server\\sonar-ce\\src\\main\\resources\\sq-process-properties"
        };
        CeServerCli ceServerCli = new CeServerCli(args);
        ceServerCli.computeEngine.startup();
    }
}
