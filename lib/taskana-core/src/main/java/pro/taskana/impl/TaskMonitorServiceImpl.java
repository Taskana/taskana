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
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states) {
        return getWorkbasketLevelReport(workbaskets, states, null, false);
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getWorkbasketLevelReport(workbaskets, states, reportLineItemDefinitions, true);
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getWorkbasketLevelReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfWorkbasketsByWorkbasketsAndStates(workbaskets, states);

            return createReport(reportLineItemDefinitions, inWorkingDays, monitorQueryItems);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getWorkbasketLevelReport().");

        }
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states) {
        return getCategoryReport(workbaskets, states, null, false);
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCategoryReport(workbaskets, states, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCategoryReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCategoriesByWorkbasketsAndStates(workbaskets, states);

            return createReport(reportLineItemDefinitions, inWorkingDays, monitorQueryItems);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCategoryReport().");
        }
    }

    @Override
    public Report getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states) {
        return getClassificationReport(workbaskets, states, null, false);
    }

    @Override
    public Report getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getClassificationReport(workbaskets, states, reportLineItemDefinitions, true);
    }

    @Override
    public Report getClassificationReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getClassificationReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfClassificationsByWorkbasketsAndStates(workbaskets, states);

            return createReport(reportLineItemDefinitions, inWorkingDays, monitorQueryItems);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getClassificationReport().");
        }
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        CustomField customField) {
        return getCustomFieldValueReport(workbaskets, states, customField, null, false);
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions) {
        return getCustomFieldValueReport(workbaskets, states, customField, reportLineItemDefinitions, true);
    }

    @Override
    public Report getCustomFieldValueReport(List<Workbasket> workbaskets, List<TaskState> states,
        CustomField customField, List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCustomFieldValueReport(workbaskets = {}, states = {}, customField = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states), customField,
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCustomFieldValuesByWorkbasketsAndStatesAndCustomField(workbaskets, states, customField);

            return createReport(reportLineItemDefinitions, inWorkingDays, monitorQueryItems);

        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getCustomFieldValueReport().");
        }
    }

    private Report createReport(List<ReportLineItemDefinition> reportLineItemDefinitions, boolean inWorkingDays,
        List<MonitorQueryItem> monitorQueryItems) {
        Report report = new Report();

        DaysToWorkingDaysConverter instance = null;
        if (reportLineItemDefinitions != null && inWorkingDays) {
            instance = DaysToWorkingDaysConverter.initialize(reportLineItemDefinitions);
        }

        for (MonitorQueryItem item : monitorQueryItems) {
            if (instance != null) {
                item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
            }
            if (!report.getDetailLines().containsKey(item.getKey())) {
                report.getDetailLines().put(item.getKey(), createEmptyReportLine(reportLineItemDefinitions));
            }
            report.getDetailLines().get(item.getKey()).addNumberOfTasks(item);
        }

        report.generateSumLine(createEmptyReportLine(reportLineItemDefinitions));
        return report;
    }

    private ReportLine createEmptyReportLine(List<ReportLineItemDefinition> reportLineItemDefinitions) {
        ReportLine reportLine = new ReportLine();
        if (reportLineItemDefinitions != null) {
            for (ReportLineItemDefinition reportLineItemDefinition : reportLineItemDefinitions) {
                ReportLineItem reportLineItem = new ReportLineItem();
                reportLineItem.setReportLineItemDefinition(reportLineItemDefinition);
                reportLine.getLineItems().add(reportLineItem);
            }
        }
        return reportLine;
    }

}
