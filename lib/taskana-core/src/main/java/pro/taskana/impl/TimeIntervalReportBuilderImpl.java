package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskState;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.AgeQueryItem;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.TaskMonitorMapper;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.TimeIntervalReportBuilder;

/**
 * Implementation of {@link TimeIntervalReportBuilder}.
 * @param <B> the true Builder behind this Interface
 * @param <I> the true AgeQueryItem inside the Report
 * @param <H> the column header
 */
abstract class TimeIntervalReportBuilderImpl<B extends TimeIntervalReportBuilder<B, I, H>, I extends AgeQueryItem, H extends TimeIntervalColumnHeader>
    implements TimeIntervalReportBuilder<B, I, H> {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeIntervalReportBuilder.class);

    protected InternalTaskanaEngine taskanaEngine;
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

    TimeIntervalReportBuilderImpl(InternalTaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        this.taskanaEngine = taskanaEngine;
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
        this.workbasketIds = new ArrayList<>(workbasketIds);
        return _this();
    }

    @Override
    public B stateIn(List<TaskState> states) {
        this.states = new ArrayList<>(states);
        return _this();
    }

    @Override
    public B categoryIn(List<String> categories) {
        this.categories = new ArrayList<>(categories);
        return _this();
    }

    @Override
    public B classificationIdIn(List<String> classificationIds) {
        this.classificationIds = new ArrayList<>(classificationIds);
        return _this();
    }

    @Override
    public B excludedClassificationIdIn(List<String> excludedClassificationIds) {
        this.excludedClassificationIds = new ArrayList<>(excludedClassificationIds);
        return _this();
    }

    @Override
    public B domainIn(List<String> domains) {
        this.domains = new ArrayList<>(domains);
        return _this();
    }

    @Override
    public B customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
        this.customAttributeFilter = new HashMap<>(customAttributeFilter);
        return _this();
    }

    @Override
    public List<String> listCustomAttributeValuesForCustomAttributeName(CustomField customField)
        throws NotAuthorizedException {
        LOGGER.debug("entry to listCustomAttributeValuesForCustomAttributeName(customField = {}), this = {}",
            customField, this);
        this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
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
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to listTaskIdsForSelectedItems(selectedItems = {}), this = {}",
                LoggerUtils.listToString(selectedItems), this);
        }

        this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            if (this.columnHeaders == null) {
                throw new InvalidArgumentException("ColumnHeader must not be null.");
            }
            if (selectedItems == null || selectedItems.isEmpty()) {
                throw new InvalidArgumentException("SelectedItems must not be null or empty.");
            }
            boolean joinWithAttachments = subKeyIsSet(selectedItems);
            if (!(this instanceof ClassificationReport.Builder) && joinWithAttachments) {
                throw new InvalidArgumentException("SubKeys are supported for ClassificationReport only.");
            }
            if (this.inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, this.columnHeaders);
            }
            return this.taskMonitorMapper.getTaskIdsForSelectedItems(this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter, determineGroupedBy(), selectedItems, joinWithAttachments);
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from listTaskIdsForSelectedItems().");
        }
    }

    protected abstract String determineGroupedBy();

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(
            this.taskanaEngine.getEngine().getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngine.getEngine().getConfiguration().isGermanPublicHolidaysEnabled());
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

