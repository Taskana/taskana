package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import pro.taskana.impl.report.impl.TimeIntervalColumnHeader;
import pro.taskana.rest.resource.ReportResource;
import pro.taskana.rest.resource.assembler.ReportAssembler;

/**
 * Controller for all monitoring endpoints.
 */
@RestController
@RequestMapping(path = "/v1/monitor", produces = "application/hal+json")
public class MonitorController {

    @Autowired
    private TaskMonitorService taskMonitorService;

    @Autowired
    private ReportAssembler reportAssembler;

    @GetMapping(path = "/tasks-status-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getTasksStatusReport(@RequestParam(required = false) List<String> domains,
        @RequestParam(required = false) List<TaskState> states) throws NotAuthorizedException {
        return ResponseEntity.status(HttpStatus.OK)
            .body(reportAssembler.toResource(
                taskMonitorService.createTaskStatusReportBuilder().stateIn(states).domainIn(domains).buildReport(),
                domains, states));
    }

    @GetMapping(path = "/tasks-workbasket-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<?> getTasksWorkbasketReport(
        @RequestParam(value = "daysInPast") int daysInPast,
        @RequestParam(value = "states") List<TaskState> states)
        throws NotAuthorizedException, InvalidArgumentException {
        return ResponseEntity.status(HttpStatus.OK)
            .body(reportAssembler.toResource(
                taskMonitorService.createWorkbasketReportBuilder()
                    .stateIn(states)
                    .withColumnHeaders(getTasksWorkbasketsTimeInterval(daysInPast)).buildReport(), daysInPast, states));

    }

    @GetMapping(path = "/tasks-classification-report")
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public ResponseEntity<ReportResource> getTasksClassificationReport()
        throws NotAuthorizedException, InvalidArgumentException {

        return ResponseEntity.status(HttpStatus.OK)
            .body(reportAssembler.toResource(
                taskMonitorService.createClassificationReportBuilder()
                    .withColumnHeaders(getTaskClassificationTimeInterval())
                    .buildReport()));
    }

    private List<TimeIntervalColumnHeader> getTaskClassificationTimeInterval() {
        return Stream.concat(Stream.concat(
            Stream.of(new TimeIntervalColumnHeader(Integer.MIN_VALUE, -10),
                new TimeIntervalColumnHeader(-10, -5)
            ),
            Stream.of(-4, -3, -2, -1, 0, 1, 2, 3, 4)
                .map(TimeIntervalColumnHeader::new)
            ),
            Stream.of(new TimeIntervalColumnHeader(5, 10),
                new TimeIntervalColumnHeader(10, Integer.MAX_VALUE)
            ))
            .collect(Collectors.toList());
    }

    private List<TimeIntervalColumnHeader> getTasksWorkbasketsTimeInterval(int daysInPast) {

        List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
        for (int i = 0; i <= daysInPast; i++) {
            columnHeaders.add(new TimeIntervalColumnHeader(i - daysInPast));
        }
        return columnHeaders;
    }
}
