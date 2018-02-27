package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.CustomField;
import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.TaskanaEngine;
import pro.taskana.exceptions.InvalidArgumentException;
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
    public Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains) throws InvalidArgumentException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions)
        throws InvalidArgumentException {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getWorkbasketLevelReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfWorkbaskets(workbasketIds,
                states, categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketLevelReport().");
        }
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains) throws InvalidArgumentException {
        return getCategoryReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions)
        throws InvalidArgumentException {
        return getCategoryReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCategoryReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {},reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfCategories(workbasketIds, states,
                categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCategoryReport().");
        }
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains) throws InvalidArgumentException {
        return getClassificationReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions)
        throws InvalidArgumentException {
        return getClassificationReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getClassificationReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            ClassificationReport report = new ClassificationReport();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfClassifications(workbasketIds,
                states, categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassificationReport().");
        }
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains) throws InvalidArgumentException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains,
        List<ReportLineItemDefinition> reportLineItemDefinitions) throws InvalidArgumentException {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, reportLineItemDefinitions,
            true);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays)
        throws InvalidArgumentException {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getDetailedClassificationReport(workbasketIds = {}, states = {}, "
                + "categories = {}, domains = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            DetailedClassificationReport report = new DetailedClassificationReport();
            List<DetailedMonitorQueryItem> detailedMonitorQueryItems = taskMonitorMapper
                .getTaskCountOfDetailedClassifications(workbasketIds, states, categories, domains);
            report.addDetailedMonitoringQueryItems(detailedMonitorQueryItems, reportLineItemDefinitions,
                inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getDetailedClassificationReport().");
        }
    }

    @Override
    public Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField) throws InvalidArgumentException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, null, false);
    }

    @Override
    public Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField,
        List<ReportLineItemDefinition> reportLineItemDefinitions) throws InvalidArgumentException {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField,
            reportLineItemDefinitions, true);
    }

    @Override
    public Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays)
        throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getCustomFieldValueReport(workbasketIds = {}, states = {}, categories = {}, "
                + "domains = {}, customField = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }
            if (customField == null) {
                throw new InvalidArgumentException("CustomField can´t be used as NULL-Parameter");
            }

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper.getTaskCountOfCustomFieldValues(workbasketIds,
                states, categories, domains, customField);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomFieldValueReport().");
        }
    }

    @Override
    public List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        List<SelectedItem> selectedItems) throws InvalidArgumentException {
        return getTaskIdsOfCategoryReportLineItems(workbasketIds, states, categories, domains,
            reportLineItemDefinitions, true, selectedItems);
    }

    @Override
    public List<String> getTaskIdsOfCategoryReportLineItems(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays, List<SelectedItem> selectedItems) throws InvalidArgumentException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskIdsOfCategoryReportLineItems(workbasketIds = {}, states = {}, "
                + "categories = {}, domains = {}, reportLineItemDefinitions = {}, inWorkingDays = {}, "
                + "selectedItems = {})", LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays,
                LoggerUtils.listToString(selectedItems));
        }
        try {
            taskanaEngineImpl.openConnection();

            if (workbasketIds == null) {
                throw new InvalidArgumentException("WorkbasketIds can´t be used as NULL-Parameter");
            }
            if (states == null) {
                throw new InvalidArgumentException("States can´t be used as NULL-Parameter");
            }
            if (categories == null) {
                throw new InvalidArgumentException("Categories can´t be used as NULL-Parameter");
            }
            if (domains == null) {
                throw new InvalidArgumentException("Domains can´t be used as NULL-Parameter");
            }
            if (reportLineItemDefinitions == null) {
                throw new InvalidArgumentException("ReportLineItemDefinitions can´t be used as NULL-Parameter");
            }
            if (selectedItems == null || selectedItems.size() == 0) {
                throw new InvalidArgumentException(
                    "SelectedItems can´t be used as NULL-Parameter and should not be empty");
            }

            configureDaysToWorkingDaysConverter();

            if (inWorkingDays) {
                selectedItems = convertWorkingDaysToDays(selectedItems, reportLineItemDefinitions);
            }

            List<String> taskIds = taskMonitorMapper.getTaskIdsOfCategoriesBySelectedItems(workbasketIds, states,
                categories, domains, selectedItems);

            return taskIds;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskIdsOfCategoryReportLineItems().");
        }
    }

    private List<SelectedItem> convertWorkingDaysToDays(List<SelectedItem> selectedItems,
        List<ReportLineItemDefinition> reportLineItemDefinitions) throws InvalidArgumentException {

        DaysToWorkingDaysConverter instance = DaysToWorkingDaysConverter.initialize(reportLineItemDefinitions);
        for (SelectedItem selectedItem : selectedItems) {
            selectedItem.setLowerAgeLimit(instance.convertWorkingDaysToDays(selectedItem.getLowerAgeLimit()));
            selectedItem.setUpperAgeLimit(instance.convertWorkingDaysToDays(selectedItem.getUpperAgeLimit()));
        }
        return selectedItems;
    }

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(taskanaEngineImpl.getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngineImpl.getConfiguration().isGermanPublicHolidaysEnabled());
    }

}
