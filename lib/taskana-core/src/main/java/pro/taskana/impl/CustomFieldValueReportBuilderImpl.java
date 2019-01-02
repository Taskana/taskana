package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CustomFieldValueReport;

/**
 * The implementation of CustomFieldValueReportBuilder.
 */
public class CustomFieldValueReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<CustomFieldValueReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CustomFieldValueReport.Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFieldValueReportBuilderImpl.class);

    private CustomField customField;

    CustomFieldValueReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper,
        CustomField customField) {
        super(taskanaEngine, taskMonitorMapper);
        this.customField = customField;
    }

    @Override
    protected CustomFieldValueReport.Builder _this() {
        return this;
    }

    @Override
    protected String determineGroupedBy() {
        return customField.name();
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

}
