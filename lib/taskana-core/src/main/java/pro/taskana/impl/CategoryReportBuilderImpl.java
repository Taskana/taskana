package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.impl.report.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.CategoryReport;

/**
 * The implementation of CategoryReportBuilder.
 */
public class CategoryReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<CategoryReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CategoryReport.Builder {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryReport.Builder.class);

    CategoryReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super(taskanaEngine, taskMonitorMapper);
    }

    @Override
    protected CategoryReport.Builder _this() {
        return this;
    }

    @Override
    protected String determineGroupedBy() {
        return "CLASSIFICATION_CATEGORY";
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
