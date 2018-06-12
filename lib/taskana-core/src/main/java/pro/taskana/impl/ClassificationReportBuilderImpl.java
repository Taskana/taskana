package pro.taskana.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.ClassificationReportBuilder;
import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.ClassificationReport;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The implementation of ClassificationReportBuilder.
 */
public class ClassificationReportBuilderImpl extends ReportBuilder implements ClassificationReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationReportBuilder.class);

    public ClassificationReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    public ClassificationReportBuilder withColumnHeaders(List<TimeIntervalColumnHeader> columnHeaders) {
        this.columnHeaders = columnHeaders;
        return this;
    }

    @Override
    public ClassificationReportBuilder inWorkingDays() {
        this.inWorkingDays = true;
        return this;
    }

    @Override
    public ClassificationReportBuilder workbasketIdIn(List<String> workbasketIds) {
        this.workbasketIds = workbasketIds;
        return this;
    }

    @Override
    public ClassificationReportBuilder stateIn(List<TaskState> states) {
        this.states = states;
        return this;
    }

    @Override
    public ClassificationReportBuilder categoryIn(List<String> categories) {
        this.categories = categories;
        return this;
    }

    @Override
    public ClassificationReportBuilder domainIn(List<String> domains) {
        this.domains = domains;
        return this;
    }

    @Override
    public ClassificationReportBuilder customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = customAttributeFilter;
        return this;
    }

    @Override
    public ClassificationReportBuilder classificationIdIn(List<String> classificationIds) {
        this.classificationIds = classificationIds;
        return this;
    }

    @Override
    public ClassificationReportBuilder excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = excludedClassificationIds;
        return this;
    }

    @Override
    public ClassificationReport buildReport() throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to buildReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            ClassificationReport report = new ClassificationReport(this.columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = this.taskMonitorMapper.getTaskCountOfClassifications(
                this.workbasketIds, this.states, this.categories, this.domains, this.classificationIds,
                this.excludedClassificationIds, this.customAttributeFilter);
            report.addItems(monitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildReport().");
        }
    }

    @Override
    public DetailedClassificationReport buildDetailedReport() throws InvalidArgumentException, NotAuthorizedException {
        LOGGER.debug("entry to buildDetailedReport(), this = {}", this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            DetailedClassificationReport report = new DetailedClassificationReport(this.columnHeaders);
            List<DetailedMonitorQueryItem> detailedMonitorQueryItems = this.taskMonitorMapper
                .getTaskCountOfDetailedClassifications(this.workbasketIds, this.states, this.categories, this.domains,
                    this.classificationIds, this.excludedClassificationIds, this.customAttributeFilter);

            report.addItems(detailedMonitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));

            return report;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from buildDetailedReport().");
        }
    }

}
