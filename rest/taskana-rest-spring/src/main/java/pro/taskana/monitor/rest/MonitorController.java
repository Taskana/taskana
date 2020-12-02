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

  /**
   * This endpoints generates a Task Status Report.
   *
   * <p>A Task Status Report contains the total number of tasks, clustered in their Task States and
   * grouped by Workbaskets. Each row represents a Workbasket while each column represents a Task
   * State.
   *
   * @param domains Filter the report values by domains.
   * @param states Filter the report values by task states.
   * @return the computed TaskStatusReport
   * @throws NotAuthorizedException if the current user is not authenticated to compute the report
   * @title Get a Task Status Report
   */
  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_STATUS_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTaskStatusReport(
      @RequestParam(required = false) List<String> domains,
      @RequestParam(required = false) List<TaskState> states)
      throws NotAuthorizedException {
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

  /**
   * This endpoints generates a Workbasket Report.
   *
   * <p>A WorkbasketReport contains the total numbers of tasks, clustered by the a Task Timestamp
   * date range and grouped by Workbaskets. Each row represents a Workbasket while each column
   * represents a date range.
   *
   * @param states Filter the report by task states
   * @param taskTimestamp determine which task timestamp should be used for comparison
   * @return the computed report
   * @throws NotAuthorizedException if the current user is not authenticated to compute the report
   * @throws InvalidArgumentException TODO: this is never thrown ...
   * @title Get a Workbasket Report
   */
  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_WORKBASKET_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getWorkbasketReport(
      @RequestParam List<TaskState> states,
      @RequestParam(required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getTasksWorkbasketReport(), states to include {}", states);
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            monitorService
                .createWorkbasketReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .stateIn(states)
                .buildReport(taskTimestamp),
            states,
            taskTimestamp);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasksWorkbasketReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_WORKBASKET_PLANNED_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  // TODO: remove this endpoint and replace with general endpoint.
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

  /**
   * This endpoints generates a Classification Report.
   *
   * <p>A Classification Report contains the total numbers of tasks, clustered by the a Task
   * Timestamp date range and grouped by Classifications. Each row represents a Classification while
   * each column represents a date range.
   *
   * @title Get a Classification Report
   * @return the computed report
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @throws NotAuthorizedException if the current user is not authenticated to compute the report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @GetMapping(path = RestEndpoints.URL_MONITOR_TASKS_CLASSIFICATION_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getClassificationReport(
      @RequestParam(required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    LOGGER.debug("Entry to getClassificationReport()");
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            monitorService
                .createClassificationReportBuilder()
                .withColumnHeaders(getRangeTimeInterval())
                .buildReport(taskTimestamp),
            taskTimestamp);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getClassificationReport(), returning {}", report);
    }

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoints generates a Timestamp Report.
   *
   * <p>A Timestamp Report contains the total numbers of tasks, clustered by date range and grouped
   * by its Task Status. Each row represents a Task Status while each column represents a date
   * range. Each row can be expanded to further group the tasks by their Org Level (1-4)
   *
   * @title Get a Timestamp Report
   * @return the computed report
   * @throws NotAuthorizedException if the current user is not authenticated to compute the report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @GetMapping(path = RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> getTimestampReport()
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
