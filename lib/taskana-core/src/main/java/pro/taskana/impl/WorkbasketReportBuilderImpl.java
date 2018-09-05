package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.CombinedClassificationFilter;
import pro.taskana.impl.report.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.MonitorQueryItem;
import pro.taskana.impl.report.TimeIntervalColumnHeader;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.WorkbasketReport;

/**
 * The implementation of WorkbasketReportBuilder.
 */
public class WorkbasketReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<WorkbasketReport.Builder, TimeIntervalColumnHeader>
    implements WorkbasketReport.Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketReportBuilderImpl.class);
    private List<CombinedClassificationFilter> combinedClassificationFilter;

    WorkbasketReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    protected WorkbasketReport.Builder _this() {
        return this;
    }

    @Override
    protected String determineDimension() {
        return "WORKBASKET_KEY";
    }

    @Override
    public WorkbasketReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to buildReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            WorkbasketReport report = new WorkbasketReport(this.columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = this.taskMonitorMapper.getTaskCountOfWorkbaskets(
                this.workbasketIds, this.states, this.categories, this.domains, this.classificationIds,
                this.excludedClassificationIds, this.customAttributeFilter, this.combinedClassificationFilter);
            report.addItems(monitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildReport().");
        }
    }

    @Override
    public WorkbasketReport.Builder combinedClassificationFilterIn(
        List<CombinedClassificationFilter> combinedClassificationFilter) {
        this.combinedClassificationFilter = combinedClassificationFilter;
        return this;
    }
}
