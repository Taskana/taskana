package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.TaskMonitorService;
import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.rest.resource.ReportAssembler;
import pro.taskana.rest.resource.ReportResource;

/**
 * Controller for all monitoring endpoints.
 */
@RestController
@RequestMapping(path = "/v1/monitor", produces = "application/hal+json")
public class MonitorController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private TaskMonitorService taskMonitorService;

    @Autowired
    private ReportAssembler reportAssembler;

    @GetMapping(path = "/tasks-status-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getTasksStatusReport(@RequestParam(required = false) List<String> domains,
        @RequestParam(required = false) List<TaskState> states) throws NotAuthorizedException,
        InvalidArgumentException {
        LOGGER.debug("Entry to getTasksStatusReport()");
        ResponseEntity<ReportResource> response = new ResponseEntity<>(reportAssembler.toResource(
            taskMonitorService.createTaskStatusReportBuilder().stateIn(states).domainIn(domains).buildReport(),
            domains, states), HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getTasksStatusReport(), returning {}", response);
        }

        return response;
    }

    @GetMapping(path = "/tasks-workbasket-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<?> getTasksWorkbasketReport(
        @RequestParam(value = "daysInPast") int daysInPast,
        @RequestParam(value = "states") List<TaskState> states)
        throws NotAuthorizedException, InvalidArgumentException {
        LOGGER.debug("Entry to getTasksWorkbasketReport()");
        ResponseEntity<?> response = new ResponseEntity<>(reportAssembler.toResource(
            taskMonitorService.createWorkbasketReportBuilder()
                .stateIn(states)
                .withColumnHeaders(getTasksWorkbasketsTimeInterval(daysInPast)).buildReport(), daysInPast, states),
            HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getTasksWorkbasketReport(), returning {}", response);
        }

        return response;
    }

    @GetMapping(path = "/tasks-classification-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getTasksClassificationReport()
        throws NotAuthorizedException, InvalidArgumentException {
        LOGGER.debug("Entry to getTasksClassificationReport()");
        ResponseEntity<ReportResource> response = new ResponseEntity<>(reportAssembler.toResource(
            taskMonitorService.createClassificationReportBuilder()
                .withColumnHeaders(getTaskClassificationTimeInterval())
                .buildReport()), HttpStatus.OK);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Exit from getTasksClassificationReport(), returning {}", response);
        }

        return response;
    }

    @GetMapping(path = "/daily-entry-exit-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getDailyEntryExitReport()
        throws NotAuthorizedException, InvalidArgumentException {
        List<TimeIntervalColumnHeader.Date> columnHeaders = IntStream.range(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.OK)
            .body(reportAssembler.toResource(
                taskMonitorService.createDailyEntryExitReportBuilder()
                    .withColumnHeaders(columnHeaders)
                    .buildReport()));
    }

    private List<TimeIntervalColumnHeader> getTaskClassificationTimeInterval() {
        return Stream.concat(Stream.concat(
            Stream.of(new TimeIntervalColumnHeader.Range(Integer.MIN_VALUE, -10),
                new TimeIntervalColumnHeader.Range(-10, -5)
            ),
            Stream.of(-4, -3, -2, -1, 0, 1, 2, 3, 4)
                .map(TimeIntervalColumnHeader.Range::new)
            ),
            Stream.of(new TimeIntervalColumnHeader.Range(5, 10),
                new TimeIntervalColumnHeader.Range(10, Integer.MAX_VALUE)
            ))
            .collect(Collectors.toList());
    }

    private List<TimeIntervalColumnHeader> getTasksWorkbasketsTimeInterval(int daysInPast) {

        List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
        for (int i = 0; i <= daysInPast; i++) {
            columnHeaders.add(new TimeIntervalColumnHeader.Date(i - daysInPast));
        }
        return columnHeaders;
    }
}
