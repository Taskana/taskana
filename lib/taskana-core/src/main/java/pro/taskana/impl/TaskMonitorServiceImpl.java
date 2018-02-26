package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
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
        List<String> categories, List<String> domains) {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getWorkbasketLevelReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public Report getWorkbasketLevelReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getWorkbasketLevelReport(workbasketIds = {}, states = {}, categories = {}, domains = {}, "
                    + "reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfWorkbasketsByWorkbasketsAndStates(workbasketIds, states, categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketLevelReport().");
        }
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains) {
        return getCategoryReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCategoryReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCategoryReport(List<String> workbasketIds, List<TaskState> states, List<String> categories,
        List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCategoryReport(workbasketIds = {}, states = {}, categories = {}, domains = {}, "
                    + "reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCategoriesByWorkbasketsAndStates(workbasketIds, states, categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCategoryReport().");
        }
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains) {
        return getClassificationReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getClassificationReport(workbasketIds, states, categories, domains, reportLineItemDefinitions, true);
    }

    @Override
    public ClassificationReport getClassificationReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getClassificationReport(workbasketIds = {}, states = {}, categories = {}, domains = {}, "
                    + "reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            ClassificationReport report = new ClassificationReport();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfClassificationsByWorkbasketsAndStates(workbasketIds, states, categories, domains);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassificationReport().");
        }
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains) {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, null, false);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getDetailedClassificationReport(workbasketIds, states, categories, domains, reportLineItemDefinitions,
            true);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<String> workbasketIds,
        List<TaskState> states, List<String> categories, List<String> domains,
        List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getDetailedClassificationReport(workbasketIds = {}, states = {}, categories = {},"
                    + " domains = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            DetailedClassificationReport report = new DetailedClassificationReport();
            List<DetailedMonitorQueryItem> detailedMonitorQueryItems = taskMonitorMapper
                .getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(workbasketIds, states, categories,
                    domains);
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
        List<String> categories, List<String> domains, CustomField customField) {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField, null, false);
    }

    @Override
    public Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCustomFieldValueReport(workbasketIds, states, categories, domains, customField,
            reportLineItemDefinitions, true);
    }

    @Override
    public Report getCustomFieldValueReport(List<String> workbasketIds, List<TaskState> states,
        List<String> categories, List<String> domains, CustomField customField,
        List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCustomFieldValueReport(workbasketIds = {}, states = {}, categories = {}, domains = {},"
                    + " customField = {}, reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbasketIds), LoggerUtils.listToString(states),
                LoggerUtils.listToString(categories), LoggerUtils.listToString(domains), customField,
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            configureDaysToWorkingDaysConverter();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(workbasketIds, states, categories,
                    domains, customField);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomFieldValueReport().");
        }
    }

    private void configureDaysToWorkingDaysConverter() {
        DaysToWorkingDaysConverter.setCustomHolidays(taskanaEngineImpl.getConfiguration().getCustomHolidays());
        DaysToWorkingDaysConverter.setGermanPublicHolidaysEnabled(
            this.taskanaEngineImpl.getConfiguration().isGermanPublicHolidaysEnabled());
    }

}
