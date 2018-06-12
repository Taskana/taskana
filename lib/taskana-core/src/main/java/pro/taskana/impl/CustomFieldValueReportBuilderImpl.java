package pro.taskana.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.CustomFieldValueReportBuilder;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The implementation of CustomFieldValueReportBuilder.
 */
public class CustomFieldValueReportBuilderImpl extends ReportBuilder implements CustomFieldValueReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFieldValueReportBuilderImpl.class);

    public CustomField customField;

    public CustomFieldValueReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper,
        CustomField customField) {
        super(taskanaEngine, taskMonitorMapper);
        this.customField = customField;
    }

    @Override
    public CustomFieldValueReportBuilderImpl withColumnHeaders(List<TimeIntervalColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl inWorkingDays() {
        this.inWorkingDays = true;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl workbasketIdIn(List<String> workbasketIds) {
        this.workbasketIds = workbasketIds;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl stateIn(List<TaskState> states) {
        this.states = states;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl categoryIn(List<String> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl domainIn(List<String> domains) {
        this.domains = domains;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl classificationIdIn(List<String> classificationIds) {
        this.classificationIds = classificationIds;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = excludedClassificationIds;
        return this;
    }

    @Override
    public CustomFieldValueReportBuilderImpl customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = customAttributeFilter;
        return this;
    }

    @Override
    public CustomFieldValueReport buildReport()
        throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to buildReport(customField = {}), this = {}", this.customField, this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            CustomFieldValueReport report = new CustomFieldValueReport(this.columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = this.taskMonitorMapper.getTaskCountOfCustomFieldValues(
                this.customField, this.workbasketIds, this.states, this.categories, this.domains,
                this.classificationIds,
                this.excludedClassificationIds,
                this.customAttributeFilter);

            report.addItems(monitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildReport().");
        }
    }

    @Override
    public CustomField getCustomField() {
        return customField;
    }

}
