package pro.taskana.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.TimeIntervalColumnHeader;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.TimeIntervalReportBuilder;

/**
 * Implementation of {@link TimeIntervalReportBuilder}.
 * @param <B> the true Builder behind this Interface
 * @param <H> the column header
 */
abstract class TimeIntervalReportBuilderImpl<B extends TimeIntervalReportBuilder, H extends TimeIntervalColumnHeader>
    implements TimeIntervalReportBuilder<B, H> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeIntervalReportBuilder.class);

    protected TaskanaEngineImpl taskanaEngine;
    protected TaskMonitorMapper taskMonitorMapper;
    protected List<H> columnHeaders;
    protected boolean inWorkingDays;
    protected List<String> workbasketIds;
    protected List<TaskState> states;
    protected List<String> categories;
    protected List<String> domains;
    protected List<String> classificationIds;
    protected List<String> excludedClassificationIds;
    protected Map<CustomField, String> customAttributeFilter;

    TimeIntervalReportBuilderImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
        this.columnHeaders = Collections.emptyList();
        configureDaysToWorkingDaysConverter();
    }

    protected abstract B _this();

    @Override
    public B withColumnHeaders(List<H> columnHeaders) {
        this.columnHeaders = columnHeaders;
        return _this();
    }

    @Override
    public B inWorkingDays() {
        this.inWorkingDays = true;
        return _this();
    }

    @Override
    public B workbasketIdIn(List<String> workbasketIds) {
        this.workbasketIds = workbasketIds;
        return _this();
    }

    @Override
    public B stateIn(List<TaskState> states) {
        this.states = states;
        return _this();
    }

    @Override
    public B categoryIn(List<String> categories) {
        this.categories = categories;
        return _this();
    }

    @Override
    public B classificationIdIn(List<String> classificationIds) {
        this.classificationIds = classificationIds;
        return _this();
    }

    @Override
    public B excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = excludedClassificationIds;
        return _this();
    }

    @Override
    public B domainIn(List<String> domains) {
        this.domains = domains;
        return _this();
    }

    @Override
    public B customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = customAttributeFilter;
        return _this();
    }

    @Override
    public List<String> listCustomAttributeValuesForCustomAttributeName(CustomField customField)
        throws NotAuthorizedException {
        LOGGER.debug("entry to listCustomAttributeValuesForCustomAttributeName(customField = {}), this = {}",
            customField, this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            return taskMonitorMapper.getCustomAttributeValuesForReport(this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter, customField);
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from listCustomAttributeValuesForCustomAttributeName().");
        }
    }

    @Override
    public List<String> listTaskIdsForSelectedItems(List<SelectedItem> selectedItems)
        throws NotAuthorizedException, InvalidArgumentException {
        LOGGER.debug("entry to listTaskIdsForSelectedItems(selectedItems = {}), this = {}",
            LoggerUtils.listToString(selectedItems), this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            if (this.columnHeaders == null) {
                throw new InvalidArgumentException("ColumnHeader must not be null.");
            }
            if (selectedItems == null || selectedItems.size() == 0) {
                throw new InvalidArgumentException("SelectedItems must not be null or empty.");
            }
            boolean joinWithAttachments = subKeyIsSet(selectedItems);
            if (!(this instanceof ClassificationReport.Builder) && joinWithAttachments) {
                throw new InvalidArgumentException("SubKeys are supported for ClassificationReport only.");
            }
            String dimension = determineDimension();
            if (this.inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, this.columnHeaders);
            }
            return this.taskMonitorMapper.getTaskIdsForSelectedItems(this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter, dimension, selectedItems, joinWithAttachments);
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from listTaskIdsForSelectedItems().");
        }
    }

    protected abstract String determineDimension();

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(this.taskanaEngine.getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngine.getConfiguration().isGermanPublicHolidaysEnabled());
    }

    private List<SelectedItem> convertWorkingDaysToDays(List<SelectedItem> selectedItems,
        List<H> columnHeaders) throws InvalidArgumentException {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter.initialize(columnHeaders);
        for (SelectedItem selectedItem : selectedItems) {
            selectedItem
                .setLowerAgeLimit(Collections.min(instance.convertWorkingDaysToDays(selectedItem.getLowerAgeLimit())));
            selectedItem
                .setUpperAgeLimit(Collections.max(instance.convertWorkingDaysToDays(selectedItem.getUpperAgeLimit())));
        }
        return selectedItems;
    }

    private boolean subKeyIsSet(List<SelectedItem> selectedItems) {
        for (SelectedItem selectedItem : selectedItems) {
            if (selectedItem.getSubKey() != null && !selectedItem.getSubKey().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}

