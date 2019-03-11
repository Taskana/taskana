package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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
import pro.taskana.impl.report.TimeIntervalColumnHeader;
import pro.taskana.monitor.ClassificationTimeIntervalColumnHeader;
import pro.taskana.monitor.WorkbasketTimeIntervalColumnHeader;
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

    private List<TimeIntervalColumnHeader> getTaskClassificationTimeInterval() {
        return Stream.concat(Stream.concat(
            Stream.of(new ClassificationTimeIntervalColumnHeader(Integer.MIN_VALUE, -10),
                new ClassificationTimeIntervalColumnHeader(-10, -5)
            ),
            Stream.of(-4, -3, -2, -1, 0, 1, 2, 3, 4)
                .map(ClassificationTimeIntervalColumnHeader::new)
            ),
            Stream.of(new ClassificationTimeIntervalColumnHeader(5, 10),
                new ClassificationTimeIntervalColumnHeader(10, Integer.MAX_VALUE)
            ))
            .collect(Collectors.toList());
    }

    private List<TimeIntervalColumnHeader> getTasksWorkbasketsTimeInterval(int daysInPast) {

        List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
        for (int i = 0; i <= daysInPast; i++) {
            columnHeaders.add(new WorkbasketTimeIntervalColumnHeader(i - daysInPast));
        }
        return columnHeaders;
    }
}
