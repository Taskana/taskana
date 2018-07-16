package pro.taskana.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CategoryReportBuilder;
import pro.taskana.ClassificationReportBuilder;
import pro.taskana.CustomField;
import pro.taskana.CustomFieldValueReportBuilder;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * The super class of the different report builders.
 */
public abstract class ReportBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportBuilder.class);

    private static final String DIMENSION_CLASSIFICATION_CATEGORY = "CLASSIFICATION_CATEGORY";
    private static final String DIMENSION_CLASSIFICATION_KEY = "CLASSIFICATION_KEY";
    private static final String DIMENSION_WORKBASKET_KEY = "WORKBASKET_KEY";

    protected TaskanaEngineImpl taskanaEngine;
    protected TaskMonitorMapper taskMonitorMapper;
    protected List<TimeIntervalColumnHeader> columnHeaders;
    protected boolean inWorkingDays;
    protected List<String> workbasketIds;
    protected List<TaskState> states;
    protected List<String> categories;
    protected List<String> domains;
    protected List<String> classificationIds;
    protected List<String> excludedClassificationIds;
    protected Map<CustomField, String> customAttributeFilter;

    public ReportBuilder(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        this.taskanaEngine = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
        this.columnHeaders = Collections.emptyList();
        configureDaysToWorkingDaysConverter();
    }

    public List<TimeIntervalColumnHeader> getColumnHeaders() {
        if (columnHeaders == null) {
            columnHeaders = new ArrayList<>();
        }
        return this.columnHeaders;
    }

    public boolean isInWorkingDays() {
        return this.inWorkingDays;
    }

    public List<String> getWorkbasketIdIn() {
        if (workbasketIds == null) {
            workbasketIds = new ArrayList<>();
        }
        return this.workbasketIds;
    }

    public List<TaskState> getStateIn() {
        if (states == null) {
            states = new ArrayList<>();
        }
        return this.states;
    }

    public List<String> getCategoryIn() {
        if (categories == null) {
            categories = new ArrayList<>();
        }
        return this.categories;
    }

    public List<String> getDomainIn() {
        if (domains == null) {
            domains = new ArrayList<>();
        }
        return this.domains;
    }

    public List<String> getClassificationIdsIn() {
        if (classificationIds == null) {
            classificationIds = new ArrayList<>();
        }
        return this.classificationIds;
    }

    public List<String> getExcludedClassificationIdsIn() {
        if (excludedClassificationIds == null) {
            excludedClassificationIds = new ArrayList<>();
        }
        return this.excludedClassificationIds;
    }

    public Map<CustomField, String> getCustomAttributeFilter() {
        if (customAttributeFilter == null) {
            customAttributeFilter = new HashMap<>();
        }
        return this.customAttributeFilter;
    }

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
            if (!(this instanceof ClassificationReportBuilder) && joinWithAttachments) {
                throw new InvalidArgumentException("SubKeys are supported for ClassificationReport only.");
            }
            String dimension = determineDimension();
            if (this.inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, this.columnHeaders);
            }
            List<String> taskIds = this.taskMonitorMapper.getTaskIdsForSelectedItems(this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter, dimension, selectedItems, joinWithAttachments);
            return taskIds;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from listTaskIdsForSelectedItems().");
        }
    }

    private String determineDimension() {
        String dimension = null;
        if (this instanceof CategoryReportBuilder) {
            dimension = DIMENSION_CLASSIFICATION_CATEGORY;
        } else if (this instanceof WorkbasketReportBuilderImpl) {
            dimension = DIMENSION_WORKBASKET_KEY;
        } else if (this instanceof ClassificationReportBuilder) {
            dimension = DIMENSION_CLASSIFICATION_KEY;
        } else if (this instanceof CustomFieldValueReportBuilder) {
            dimension = ((CustomFieldValueReportBuilder) this).getCustomField().toString();
        } else {
            throw new SystemException("Internal error. listTaskIdsForSelectedItems() does not support " + this);
        }
        return dimension;
    }

    public List<String> listCustomAttributeValuesForCustomAttributeName(CustomField customField)
        throws NotAuthorizedException {
        LOGGER.debug("entry to listCustomAttributeValuesForCustomAttributeName(customField = {}), this = {}",
            customField, this);
        this.taskanaEngine.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            this.taskanaEngine.openConnection();
            List<String> customAttributeValues = taskMonitorMapper.getCustomAttributeValuesForReport(this.workbasketIds,
                this.states, this.categories, this.domains, this.classificationIds, this.excludedClassificationIds,
                this.customAttributeFilter, customField);
            return customAttributeValues;
        } finally {
            this.taskanaEngine.returnConnection();
            LOGGER.debug("exit from listCustomAttributeValuesForCustomAttributeName().");
        }
    }

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(this.taskanaEngine.getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngine.getConfiguration().isGermanPublicHolidaysEnabled());
    }

    private List<SelectedItem> convertWorkingDaysToDays(List<SelectedItem> selectedItems,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter.initialize(columnHeaders);
        for (SelectedItem selectedItem : selectedItems) {
            selectedItem
                .setLowerAgeLimit(Collections.min(instance.convertWorkingDaysToDays(selectedItem.getLowerAgeLimit())));
            selectedItem
                .setUpperAgeLimit(Collections.max(instance.convertWorkingDaysToDays(selectedItem.getUpperAgeLimit())));
        }
        return selectedItems;
    }

    protected boolean subKeyIsSet(List<SelectedItem> selectedItems) {
        for (SelectedItem selectedItem : selectedItems) {
            if (selectedItem.getSubKey() != null && !selectedItem.getSubKey().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
