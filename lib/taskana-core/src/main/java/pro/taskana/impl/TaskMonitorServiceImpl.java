package pro.taskana.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskanaEngine;
import pro.taskana.Workbasket;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.model.DueWorkbasketCounter;
import pro.taskana.model.MonitorQueryItem;
import pro.taskana.model.Report;
import pro.taskana.model.ReportLine;
import pro.taskana.model.ReportLineItem;
import pro.taskana.model.ReportLineItemDefinition;
import pro.taskana.model.TaskState;
import pro.taskana.model.TaskStateCounter;
import pro.taskana.model.mappings.TaskMonitorMapper;

/**
 * This is the implementation of TaskMonitorService.
 */
public class TaskMonitorServiceImpl implements TaskMonitorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskMonitorServiceImpl.class);
    private TaskanaEngineImpl taskanaEngineImpl;
    private TaskMonitorMapper taskMonitorMapper;

    public TaskMonitorServiceImpl(TaskanaEngine taskanaEngine, TaskMonitorMapper taskMonitorMapper) {
        super();
        this.taskanaEngineImpl = (TaskanaEngineImpl) taskanaEngine;
        this.taskMonitorMapper = taskMonitorMapper;
    }

    @Override
    public List<TaskStateCounter> getTaskCountForState(List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskCountForState(states = {})", LoggerUtils.listToString(states));
        }
        List<TaskStateCounter> result = null;
        try {
            taskanaEngineImpl.openConnection();
            result = taskMonitorMapper.getTaskCountForState(states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug("exit from getTaskCountForState(). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public long getTaskCountForWorkbasketByDaysInPastAndState(String workbasketId, long daysInPast,
        List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getTaskCountForWorkbasketByDaysInPastAndState(workbasketId {}, daysInPast={}, states = {})",
                workbasketId, daysInPast, LoggerUtils.listToString(states));
        }
        long result = -1;
        try {
            taskanaEngineImpl.openConnection();
            Instant fromDate = Instant.now().minus(Duration.ofDays(daysInPast));
            result = taskMonitorMapper.getTaskCountForWorkbasketByDaysInPastAndState(workbasketId, fromDate, states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            LOGGER.debug("exit from getTaskCountForWorkbasketByDaysInPastAndState(). Returning result {} ", result);
        }
    }

    @Override
    public List<DueWorkbasketCounter> getTaskCountByWorkbasketAndDaysInPastAndState(long daysInPast,
        List<TaskState> states) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("entry to getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast = {}, states = {})",
                daysInPast, LoggerUtils.listToString(states));
        }
        List<DueWorkbasketCounter> result = null;
        try {
            taskanaEngineImpl.openConnection();
            Instant fromDate = Instant.now().minus(Duration.ofDays(daysInPast));
            result = taskMonitorMapper.getTaskCountByWorkbasketIdAndDaysInPastAndState(fromDate, states);
            return result;
        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                int numberOfResultObjects = result == null ? 0 : result.size();
                LOGGER.debug(
                    "exit from getTaskCountByWorkbasketAndDaysInPastAndState(daysInPast,states). Returning {} resulting Objects: {} ",
                    numberOfResultObjects, LoggerUtils.listToString(result));
            }
        }
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states) {
        return getWorkbasketLevelReport(workbaskets, states, null);
    }

    @Override
    public Report getWorkbasketLevelReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getWorkbasketLevelReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            Report report = new Report();
            report.setDetailLines(createEmptyDetailLinesForWorkbaskets(workbaskets, reportLineItemDefinitions));

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfWorkbasketsByWorkbasketsAndStates(workbaskets, states);

            for (MonitorQueryItem item : monitorQueryItems) {
                report.getDetailLines().get(item.getKey()).addNumberOfTasks(item);
            }
            report.setSumLine(createEmptyReportLine(reportLineItemDefinitions));
            report.generateSumLine();

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("exit from getWorkbasketLevelReport().");
            }
        }
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states) {
        return getCategoryReport(workbaskets, states, null);
    }

    @Override
    public Report getCategoryReport(List<Workbasket> workbaskets, List<TaskState> states,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(
                "entry to getCategoryReport(workbaskets = {}, states = {}, reportLineItemDefinitions = {})",
                LoggerUtils.listToString(workbaskets), LoggerUtils.listToString(states),
                LoggerUtils.listToString(reportLineItemDefinitions));
        }
        try {
            taskanaEngineImpl.openConnection();

            Report report = new Report();

            List<MonitorQueryItem> monitorQueryItems = taskMonitorMapper
                .getTaskCountOfCategoriesByWorkbasketsAndStates(workbaskets, states);
            for (MonitorQueryItem item : monitorQueryItems) {
                if (!report.getDetailLines().containsKey(item.getKey())) {
                    report.getDetailLines().put(item.getKey(), createEmptyReportLine(reportLineItemDefinitions));
                }
                report.getDetailLines().get(item.getKey()).addNumberOfTasks(item);
            }
            report.setSumLine(createEmptyReportLine(reportLineItemDefinitions));
            report.generateSumLine();

            return report;

        } finally {
            taskanaEngineImpl.returnConnection();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("exit from getCategoryReport().");
            }
        }
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

    private Map<String, ReportLine> createEmptyDetailLinesForWorkbaskets(List<Workbasket> workbaskets,
        List<ReportLineItemDefinition> reportLineItemDefinitions) {
        Map<String, ReportLine> detailLines = new LinkedHashMap<>();
        for (Workbasket workbasket : workbaskets) {
            ReportLine reportLine = createEmptyReportLine(reportLineItemDefinitions);
            detailLines.put(workbasket.getKey(), reportLine);
        }
        return detailLines;
    }

}
