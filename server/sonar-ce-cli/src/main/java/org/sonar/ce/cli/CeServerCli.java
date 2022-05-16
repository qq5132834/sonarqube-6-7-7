package org.sonar.ce.cli;

import org.sonar.ce.ComputeEngine;
import org.sonar.ce.ComputeEngineImpl;
import org.sonar.ce.container.ComputeEngineContainerImpl;
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
        this.computeEngine = new ComputeEngineImpl(props, new ComputeEngineContainerImpl());
    }

    public static void main(String[] args) {
        args = new String[]{
                "C:\\Users\\51328\\Desktop\\sonarqube-6.7.7\\sonarqube-6.7.7\\server\\sonar-ce\\src\\main\\resources\\sq-process-properties"
        };
        CeServerCli ceServerCli = new CeServerCli(args);
        ceServerCli.computeEngine.startup();
    }
}
