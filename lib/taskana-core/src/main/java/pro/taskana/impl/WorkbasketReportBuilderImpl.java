package pro.taskana.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.WorkbasketReportBuilder;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CombinedClassificationFilter;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketReport;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The implementation of WorkbasketReportBuilder.
 */
public class WorkbasketReportBuilderImpl extends ReportBuilder implements WorkbasketReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkbasketReportBuilderImpl.class);
    private List<CombinedClassificationFilter> combinedClassificationFilter;

    public WorkbasketReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public WorkbasketReportBuilderImpl withColumnHeaders(List<TimeIntervalColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl inWorkingDays() {
        this.inWorkingDays = true;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl workbasketIdIn(List<String> workbasketIds) {
        this.workbasketIds = workbasketIds;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl stateIn(List<TaskState> states) {
        this.states = states;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl categoryIn(List<String> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl domainIn(List<String> domains) {
        this.domains = domains;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl classificationIdIn(List<String> classificationIds) {
        this.classificationIds = classificationIds;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = excludedClassificationIds;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = customAttributeFilter;
        return this;
    }

    @Override
    public WorkbasketReportBuilderImpl combinedClassificationFilterIn(
        List<CombinedClassificationFilter> combinedClassificationFilter) {
        this.combinedClassificationFilter = combinedClassificationFilter;
        return this;
    }

    public List<CombinedClassificationFilter> getCombinedClassificationFilterIn() {
        if (combinedClassificationFilter == null) {
            combinedClassificationFilter = new ArrayList<>();
        }
        return this.combinedClassificationFilter;
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

}
