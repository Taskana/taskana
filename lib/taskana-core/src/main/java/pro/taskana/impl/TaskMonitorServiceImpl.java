package pro.taskana.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
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
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories) {
        return getWorkbasketLevelReport(workbaskets, states, categories, null, false);
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getWorkbasketLevelReport(workbaskets, states, categories, reportLineItemDefinitions, true);
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getWorkbasketLevelReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {},"
                    + " inWorkingDays = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfWorkbasketsByWorkbasketsAndStates(workbaskets, states, categories);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketLevelReport().");

        }
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories) {
        return getCategoryReport(workbaskets, states, categories, null, false);
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCategoryReport(workbaskets, states, categories, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states, List<String> categories,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCategoryReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {},"
                    + " inWorkingDays = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCategoriesByWorkbasketsAndStates(workbaskets, states, categories);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCategoryReport().");
        }
    }

    @Override
    public ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories) {
        return getClassificationReport(workbaskets, states, categories, null, false);
    }

    @Override
    public ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getClassificationReport(workbaskets, states, categories, reportLineItemDefinitions, true);
    }

    @Override
    public ClassificationReport getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getClassificationReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {},"
                    + " inWorkingDays = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            ClassificationReport report = new ClassificationReport();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfClassificationsByWorkbasketsAndStates(workbaskets, states, categories);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassificationReport().");
        }
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets,
        List<TaskState> states, List<String> categories) {
        return getDetailedClassificationReport(workbaskets, states, categories, null, false);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets,
        List<TaskState> states, List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getDetailedClassificationReport(workbaskets, states, categories, reportLineItemDefinitions, true);
    }

    @Override
    public DetailedClassificationReport getDetailedClassificationReport(List<Workbasket> workbaskets,
        List<TaskState> states, List<String> categories, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getDetailedClassificationReport(workbaskets = {}, states = {}, customField = {}, "
                    + "reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            DetailedClassificationReport report = new DetailedClassificationReport();
            List<DetailedMonitorQueryItem> detailedMonitorQueryItems = taskMonitorMapper
                .getTaskCountOfDetailedClassificationsByWorkbasketsAndStates(workbaskets, states, categories);
            report.addDetailedMonitoringQueryItems(detailedMonitorQueryItems, reportLineItemDefinitions,
                inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getDetailedClassificationReport().");
        }
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, CustomField customField) {
        return getCustomFieldValueReport(workbaskets, states, categories, customField, null, false);
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCustomFieldValueReport(workbaskets, states, categories, customField, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<String> categories, CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions,
        boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCustomFieldValueReport(workbaskets = {}, states = {}, customField = {}, "
                    + "reportLineItemDefinitions = {}, inWorkingDays = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states), customField,
                LoggerUtils.listToString(reportLineItemDefinitions), inWorkingDays);
        }
        try {
            taskanaEngineImpl.openConnection();

            Report report = new Report();
            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(workbaskets, states, categories,
                    customField);
            report.addMonitoringQueryItems(monitorQueryItems, reportLineItemDefinitions, inWorkingDays);
            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomFieldValueReport().");
        }
    }

}
