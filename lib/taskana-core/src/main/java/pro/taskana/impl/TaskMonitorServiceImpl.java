package pro.taskana.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.ClassificationReport;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.MonitorQueryItem;
import pro.taskana.impl.report.impl.TaskQueryItem;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.impl.report.impl.WorkbasketLevelReport;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.mappings.TaskMonitorMapper;

/**
 * This is the implementation of TaskMonitorService.
 */
public class TaskMonitorServiceImpl implements TaskMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitorServiceImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskMonitorMapper taskMonitorMapper;

    TaskMonitorServiceImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException, NotAuthorizedException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException, NotAuthorizedException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getWorkbasketLevelReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            WorkbasketLevelReport report = new WorkbasketLevelReport(columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfWorkbaskets(
                workbasketIds, states, categories, domains, customField, customFieldValues);

            report.addItems(monitorQueryItems, new DaysToWorkingDaysPreProcessor<>(columnHeaders, inWorkingDays));

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketLevelReport().");
        }
    }

    @Override
    public CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException, NotAuthorizedException {
        return getCategoryReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(),
            false);
    }

    @Override
    public CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException, NotAuthorizedException {
        return getCategoryReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCategoryReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, customField = {}, customFieldValues = {}, reportLineItemDefinitions = {}, "
                + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            CategoryReport report = new CategoryReport(columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfCategories(
                workbasketIds, states, categories, domains, customField, customFieldValues);

            report.addItems(monitorQueryItems, new DaysToWorkingDaysPreProcessor<>(columnHeaders, inWorkingDays));

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCategoryReport().");
        }
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException, NotAuthorizedException {
        return getClassificationReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException, NotAuthorizedException {
        return getClassificationReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getClassificationReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            ClassificationReport report = new ClassificationReport(columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfClassifications(
                workbasketIds, states, categories, domains, customField, customFieldValues);

            report.addItems(monitorQueryItems, new DaysToWorkingDaysPreProcessor<>(columnHeaders, inWorkingDays));

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassificationReport().");
        }
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains, CustomField customField,
        List<String> customFieldValues) throws InvalidArgumentException, NotAuthorizedException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, customField,
            customFieldValues, Collections.emptyList(), false);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains, CustomField customField,
        List<String> customFieldValues, List<TimeIntervalColumnHeader> columnHeaders)
        throws InvalidArgumentException, NotAuthorizedException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, customField,
            customFieldValues, columnHeaders, true);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains, CustomField customField,
        List<String> customFieldValues, List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException, NotAuthorizedException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getDetailedClassificationReport(workbasketIds = {}, states = {}, "
                + "categories = {}, domains = {}, customField = {}, customFieldValues = {}, "
                + "columnHeaders = {}, inWorkingDays = {})", LoggerUtils.listToString(workbasketIds),
                LoggerUtils.listToString(states), LoggerUtils.listToString(categories),
                LoggerUtils.listToString(domains), customField, LoggerUtils.listToString(customFieldValues),
                LoggerUtils.listToString(columnHeaders), inWorkingDays);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            DetailedClassificationReport report = new DetailedClassificationReport(columnHeaders);
            List<DetailedMonitorQueryItem> detailedMonitorQueryItems = taskMonitorMapper
                .getTaskCountOfDetailedClassifications(workbasketIds, states, categories, domains, customField,
                    customFieldValues);

            report.addItems(detailedMonitorQueryItems,
                new DaysToWorkingDaysPreProcessor<>(columnHeaders, inWorkingDays));

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getDetailedClassificationReport().");
        }
    }

    @Override
    public CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues)
        throws InvalidArgumentException, NotAuthorizedException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException, NotAuthorizedException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCustomFieldValueReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            if (customField == null) {
                throw new InvalidArgumentException("CustomField can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            CustomFieldValueReport report = new CustomFieldValueReport(columnHeaders);
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfCustomFieldValues(
                workbasketIds, states, categories, domains, customField, customFieldValues);

            report.addItems(monitorQueryItems, new DaysToWorkingDaysPreProcessor<>(columnHeaders, inWorkingDays));

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomFieldValueReport().");
        }
    }

    @Override
    public List<String> getCustomAttributeValuesForReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<String> classificationIds,
        List<String> excludedClassificationIds, Map<String, String> customAttributeFilter,
        String customAttributeName) throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCustomAttributeValuesForReport(workbasketIds = {}, states = {}, "
                + "categories = {}, domains = {}, classificationIds = {}, excludedClassificationIds = {}, customAttributeName = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(classificationIds), LoggerUtils.listToString(excludedClassificationIds),
                customAttributeName);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();

            if (customAttributeName == null || customAttributeName.isEmpty()) {
                throw new InvalidArgumentException("customAttributeName must not be null.");
            }

            List<String> customAttributeValues = taskMonitorMapper.getCustomAttributeValuesForReport(workbasketIds,
                states,
                categories, domains, classificationIds, excludedClassificationIds, customAttributeFilter,
                "CUSTOM_" + customAttributeName);

            return customAttributeValues;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomAttributeValuesForReport().");
        }
    }

    @Override
    public List<String> getTaskIdsForSelectedItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<String> classificationIds,
        List<String> excludedClassificationIds, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays,
        List<SelectedItem> selectedItems, String dimension) throws InvalidArgumentException, NotAuthorizedException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskIdsForSelectedItems(workbasketIds = {}, states = {}, "
                + "categories = {}, domains = {}, customField = {}, customFieldValues = {}, "
                + "columnHeaders = {}, inWorkingDays = {}, selectedItems = {}, dimension = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(classificationIds), LoggerUtils.listToString(excludedClassificationIds),
                customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays, LoggerUtils.listToString(selectedItems), dimension);
        }
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR);
        try {
            taskanaEngineImpl.openConnection();
            if (columnHeaders == null) {
                throw new InvalidArgumentException("ColumnHeader must not be null.");
            }
            if (selectedItems == null || selectedItems.size() == 0) {
                throw new InvalidArgumentException(
                    "SelectedItems must not be null or empty.");
            }
            boolean joinWithAttachments = subKeyIsSet(selectedItems);
            if (joinWithAttachments && !TaskMonitorService.DIMENSION_CLASSIFICATION_KEY.equals(dimension)) {
                throw new InvalidArgumentException("SubKeys are supported for dimension CLASSIFICATION_KEY only.");
            }

            configureDaysToWorkingDaysConverter();

            if (inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, columnHeaders);
            }

            List<String> taskIds = taskMonitorMapper.getTaskIdsForSelectedItems(workbasketIds, states,
                categories, domains, classificationIds, excludedClassificationIds, customField, customFieldValues,
                dimension, selectedItems, joinWithAttachments);

            return taskIds;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskIdsForSelectedItems().");
        }
    }

    @Override
    public TaskStatusReport getTaskStatusReport() throws NotAuthorizedException {
        return getTaskStatusReport(null, null);
    }

    @Override
    public TaskStatusReport getTaskStatusReport(List<String> domains) throws NotAuthorizedException {
        return getTaskStatusReport(domains, null);
    }

    @Override
    public TaskStatusReport getTaskStatusReport(List<String> domains, List<TaskState> states)
        throws NotAuthorizedException {
        taskanaEngineImpl.checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
        try {
            taskanaEngineImpl.openConnection();

            List<TaskQueryItem> tasks = taskMonitorMapper.getTasksCountByState(domains, states);
            TaskStatusReport report = new TaskStatusReport(states);
            report.addItems(tasks);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
        }
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

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(taskanaEngineImpl.getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngineImpl.getConfiguration().isGermanPublicHolidaysEnabled());
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
