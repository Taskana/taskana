package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskState;
import pro.taskana.TaskStatusReportBuilder;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.TaskQueryItem;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The implementation of TaskStatusReportBuilder.
 */
public class TaskStatusReportBuilderImpl implements TaskStatusReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusReportBuilderImpl.class);
    private TaskanaEngineImpl taskanaEngine;
    private TaskMonitorMapper taskMonitorMapper;
    private List<String> domains;
    private List<TaskState> states;

    public TaskStatusReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public TaskStatusReportBuilderImpl stateIn(List<TaskState> states) {
        this.states = states;
        return this;
    }

    @Override
    public TaskStatusReportBuilderImpl domainIn(List<String> domains) {
        this.domains = domains;
        return this;
    }

    @Override
    public TaskStatusReport buildReport() throws NotAuthorizedException {
        LOGGER.debug("entry to buildReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
        try {
            this.taskanaEngine.openConnection();
            List<TaskQueryItem> tasks = this.taskMonitorMapper.getTasksCountByState(this.domains, this.states);
            TaskStatusReport report = new TaskStatusReport(this.states);
            report.addItems(tasks);
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildReport().");
        }
    }

}
