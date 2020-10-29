package pro.taskana.monitor.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.rest.assembler.ReportRepresentationModelAssembler;
import pro.taskana.monitor.rest.models.ReportRepresentationModel;
import pro.taskana.task.api.TaskState;

/** Controller for all monitoring endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class MonitorController {

  private static final Logger LOGGER = LoggerFactory.getLogger(MonitorController.class);

  private final MonitorService monitorService;

  private final ReportRepresentationModelAssembler reportRepresentationModelAssembler;

  @Autowired
  MonitorController(
      MonitorService monitorService,
      ReportRepresentationModelAssembler reportRepresentationModelAssembler) {
    this.monitorService = monitorService;
    this.reportRepresentationModelAssembler = reportRepresentationModelAssembler;
  }

  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_STATUS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTasksStatusReport(
      @RequestParam(required = false) List<String> domains,
      @RequestParam(required = false) List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksStatusReport(), states to include {}", states);
    ResponseEntity<ReportRepresentationModel> response =
        ResponseEntity.ok(
            reportRepresentationModelAssembler.toModel(
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

  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_WORKBASKET)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTasksWorkbasketReport(
      @RequestParam(value = "states") List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksWorkbasketReport(), states to include {}", states);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            monitorService
                .createWorkbasketReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .stateIn(states)
                .buildReport(),
            states);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksWorkbasketReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_WORKBASKET_PLANNED)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTasksWorkbasketPlannedDateReport(
      @RequestParam(value = "daysInPast") int daysInPast,
      @RequestParam(value = "states") List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug(
        "Entry to getTasksWorkbasketPlannedDateReport(), "
            + "upto {} days in the past, states to include {}",
        daysInPast,
        states);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            monitorService
                .createWorkbasketReportBuilder()
                .stateIn(states)
                .withColumnHeaders(getDateTimeInterval(daysInPast))
                .buildReport(TaskTimestamp.PLANNED),
            daysInPast,
            states);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksWorkbasketPlannedDateReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_CLASSIFICATION)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTasksClassificationReport()
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksClassificationReport()");

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            monitorService
                .createClassificationReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .buildReport());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksClassificationReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = RestEndpoints.URL_MONITOR_TIMESTAMP)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getDailyEntryExitReport()
      throws NotAuthorizedException, InvalidArgumentException {
    List<TimeIntervalColumnHeader> columnHeaders =
        IntStream.range(-14, 0)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            reportRepresentationModelAssembler.toModel(
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
