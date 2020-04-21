package pro.taskana.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.rest.resource.ReportResource;
import pro.taskana.rest.resource.ReportResourceAssembler;
import pro.taskana.task.api.TaskState;

/** Controller for all monitoring endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class MonitorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

  private MonitorService monitorService;

  private ReportResourceAssembler reportResourceAssembler;

  MonitorController(
      MonitorService monitorService, ReportResourceAssembler reportResourceAssembler) {
    this.monitorService = monitorService;
    this.reportResourceAssembler = reportResourceAssembler;
  }

  @GetMapping(path = Mapping.URL_MONITOR_TASKSSTATUS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportResource> getTasksStatusReport(
      @RequestParam(required = false) List<String> domains,
      @RequestParam(required = false) List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksStatusReport()");
    ResponseEntity<ReportResource> response =
        ResponseEntity.ok(
            reportResourceAssembler.toModel(
                monitorService
                    .createTaskStatusReportBuilder()
                    .stateIn(states)
                    .domainIn(domains)
                    .buildReport(),
                domains,
                states));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksStatusReport(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_MONITOR_TASKSWORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<?> getTasksWorkbasketReport(
      @RequestParam(value = "states") List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksWorkbasketReport()");

    ReportResource report =
        reportResourceAssembler.toModel(
            monitorService
                .createWorkbasketReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .buildReport(),
            states);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksWorkbasketReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = Mapping.URL_MONITOR_TASKSWORKBASKETPLANNED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<?> getTasksWorkbasketPlannedDateReport(
      @RequestParam(value = "daysInPast") int daysInPast,
      @RequestParam(value = "states") List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksWorkbasketPlannedDateReport()");

    ReportResource report =
        reportResourceAssembler.toModel(
            monitorService
                .createWorkbasketReportBuilder()
                .stateIn(states)
                .withColumnHeaders(getDateTimeInterval(daysInPast))
                .buildPlannedDateBasedReport(),
            daysInPast,
            states);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksWorkbasketPlannedDateReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = Mapping.URL_MONITOR_TASKSCLASSIFICATION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportResource> getTasksClassificationReport()
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksClassificationReport()");

    ReportResource report =
        reportResourceAssembler.toModel(
            monitorService
                .createClassificationReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .buildReport());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksClassificationReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = Mapping.URL_MONITOR_TIMESTAMP)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportResource> getDailyEntryExitReport()
      throws NotAuthorizedException, InvalidArgumentException {
    List<TimeIntervalColumnHeader> columnHeaders =
        IntStream.range(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            reportResourceAssembler.toModel(
                monitorService
                    .createTimestampReportBuilder()
                    .withColumnHeaders(columnHeaders)
                    .buildReport()));
  }

  private List<TimeIntervalColumnHeader> getRangeTimeInterval() {
    return Stream.concat(
            Stream.concat(
                Stream.of(
                    new TimeIntervalColumnHeader.Range(Integer.MIN_VALUE, -10),
                    new TimeIntervalColumnHeader.Range(-10, -5)),
                Stream.of(-4, -3, -2, -1, 0, 1, 2, 3, 4).map(TimeIntervalColumnHeader.Range::new)),
            Stream.of(
                new TimeIntervalColumnHeader.Range(5, 10),
                new TimeIntervalColumnHeader.Range(10, Integer.MAX_VALUE)))
        .collect(Collectors.toList());
  }

  private List<TimeIntervalColumnHeader> getDateTimeInterval(int daysInPast) {

    List<TimeIntervalColumnHeader> columnHeaders = new ArrayList<>();
    for (int i = 0; i <= daysInPast; i++) {
      columnHeaders.add(new TimeIntervalColumnHeader.Date(i - daysInPast));
    }
    return columnHeaders;
  }
}
