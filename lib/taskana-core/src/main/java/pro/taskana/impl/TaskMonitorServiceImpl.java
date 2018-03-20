package pro.taskana.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.report.impl.CategoryReport;
import pro.taskana.impl.report.impl.ClassificationReport;
import pro.taskana.impl.report.impl.CustomFieldValueReport;
import pro.taskana.impl.report.impl.DaysToWorkingDaysPreProcessor;
import pro.taskana.impl.report.impl.DetailedClassificationReport;
import pro.taskana.impl.report.impl.DetailedMonitorQueryItem;
import pro.taskana.impl.report.impl.MonitorQueryItem;
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
        throws InvalidArgumentException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public WorkbasketLevelReport getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getWorkbasketLevelReport(workbasketIds = {}, states = {}, categories = {}, "
                    + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                    + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
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
        List<String> domains, CustomField customField, List<String> customFieldValues) throws InvalidArgumentException {
        return getCategoryReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(),
            false);
    }

    @Override
    public CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
        return getCategoryReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public CategoryReport getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCategoryReport(workbasketIds = {}, states = {}, categories = {}, "
                    + "domains = {}, customField = {}, customFieldValues = {}, reportLineItemDefinitions = {}, "
                    + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
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
        throws InvalidArgumentException {
        return getClassificationReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
        return getClassificationReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getClassificationReport(workbasketIds = {}, states = {}, categories = {}, "
                    + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                    + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
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
        List<String> customFieldValues) throws InvalidArgumentException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, customField,
            customFieldValues, Collections.emptyList(), false);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains, CustomField customField,
        List<String> customFieldValues, List<TimeIntervalColumnHeader> columnHeaders)
        throws InvalidArgumentException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, customField,
            customFieldValues, columnHeaders, true);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains, CustomField customField,
        List<String> customFieldValues, List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getDetailedClassificationReport(workbasketIds = {}, states = {}, "
                    + "categories = {}, domains = {}, customField = {}, customFieldValues = {}, "
                    + "columnHeaders = {}, inWorkingDays = {})", LoggerUtils.listToString(workbasketIds),
                LoggerUtils.listToString(states), LoggerUtils.listToString(categories),
                LoggerUtils.listToString(domains), customField, LoggerUtils.listToString(customFieldValues),
                LoggerUtils.listToString(columnHeaders), inWorkingDays);
        }
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
        throws InvalidArgumentException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            Collections.emptyList(), false);
    }

    @Override
    public CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders) throws InvalidArgumentException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, customFieldValues,
            columnHeaders, true);
    }

    @Override
    public CustomFieldValueReport getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCustomFieldValueReport(workbasketIds = {}, states = {}, categories = {}, "
                    + "domains = {}, customField = {}, customFieldValues = {}, columnHeaders = {}, "
                    + "inWorkingDays = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays);
        }
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
    public List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, List<SelectedItem> selectedItems)
        throws InvalidArgumentException {
        return getTaskIdsOfCategoryReportLineItems(workbasketIds, states, categories, domains, customField,
            customFieldValues, columnHeaders, true, selectedItems);
    }

    @Override
    public List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField, List<String> customFieldValues,
        List<TimeIntervalColumnHeader> columnHeaders, boolean inWorkingDays,
        List<SelectedItem> selectedItems) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskIdsOfCategoryReportLineItems(workbasketIds = {}, states = {}, "
                    + "categories = {}, domains = {}, customField = {}, customFieldValues = {}, "
                    + "columnHeaders = {}, inWorkingDays = {}, selectedItems = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(customFieldValues), LoggerUtils.listToString(columnHeaders),
                inWorkingDays, LoggerUtils.listToString(selectedItems));
        }
        try {
            taskanaEngineImpl.openConnection();

            if (columnHeaders == null) {
                throw new InvalidArgumentException("ReportLineItemDefinitions can´t be used as NULL-Parameter");
            }
            if (selectedItems == null || selectedItems.size() == 0) {
                throw new InvalidArgumentException(
                    "SelectedItems can´t be used as NULL-Parameter and should not be empty");
            }

            configureDaysToWorkingDaysConverter();

            if (inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, columnHeaders);
            }

            List<String> taskIds = taskMonitorMapper.getTaskIdsOfCategoriesBySelectedItems(workbasketIds, states,
                categories, domains, customField, customFieldValues, selectedItems);

            return taskIds;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskIdsOfCategoryReportLineItems().");
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

}
