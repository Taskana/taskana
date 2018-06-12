package pro.taskana.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CategoryReportBuilder;
import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The implementation of CategoryReportBuilder.
 */
public class CategoryReportBuilderImpl extends ReportBuilder implements CategoryReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryReportBuilder.class);

    public CategoryReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public CategoryReportBuilder withColumnHeaders(List<TimeIntervalColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
        return this;
    }

    @Override
    public CategoryReportBuilder inWorkingDays() {
        this.inWorkingDays = true;
        return this;
    }

    @Override
    public CategoryReportBuilder workbasketIdIn(List<String> workbasketIds) {
        this.workbasketIds = workbasketIds;
        return this;
    }

    @Override
    public CategoryReportBuilder stateIn(List<TaskState> states) {
        this.states = states;
        return this;
    }

    @Override
    public CategoryReportBuilder categoryIn(List<String> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public CategoryReportBuilder classificationIdIn(List<String> classificationIds) {
        this.classificationIds = classificationIds;
        return this;
    }

    @Override
    public CategoryReportBuilder excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = excludedClassificationIds;
        return this;
    }

    @Override
    public CategoryReportBuilder domainIn(List<String> domains) {
        this.domains = domains;
        return this;
    }

    @Override
    public CategoryReportBuilder customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = customAttributeFilter;
        return this;
    }

    @Override
    public CategoryReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to buildReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            CategoryReport report = new CategoryReport(this.columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = this.taskMonitorMapper.getTaskCountOfCategories(
                this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter);
            report.addItems(monitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildReport().");
        }
    }

}
