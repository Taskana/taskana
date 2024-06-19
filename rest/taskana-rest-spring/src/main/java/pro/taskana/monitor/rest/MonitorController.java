package pro.taskana.monitor.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
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
import pro.taskana.monitor.api.reports.ClassificationCategoryReport;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.TaskCustomFieldValueReport;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.rest.assembler.PriorityColumnHeaderRepresentationModelAssembler;
import pro.taskana.monitor.rest.assembler.ReportRepresentationModelAssembler;
import pro.taskana.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import pro.taskana.monitor.rest.models.ReportRepresentationModel;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketType;

/** Controller for all monitoring endpoints. */
@RestController
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class MonitorController {

  private final MonitorService monitorService;

  private final ReportRepresentationModelAssembler reportRepresentationModelAssembler;
  private final PriorityColumnHeaderRepresentationModelAssembler
      priorityColumnHeaderRepresentationModelAssembler;

  @Autowired
  MonitorController(
      MonitorService monitorService,
      ReportRepresentationModelAssembler reportRepresentationModelAssembler,
      PriorityColumnHeaderRepresentationModelAssembler
          priorityColumnHeaderRepresentationModelAssembler) {
    this.monitorService = monitorService;
    this.reportRepresentationModelAssembler = reportRepresentationModelAssembler;
    this.priorityColumnHeaderRepresentationModelAssembler =
        priorityColumnHeaderRepresentationModelAssembler;
  }

  /**
   * This endpoint generates a Workbasket Report.
   *
   * <p>Each Row represents a Workbasket.
   *
   * <p>Each Column Header represents a Time Interval.
   *
   * @title Compute a Workbasket Report
   * @param filterParameter the filter parameters
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException TODO: this is never thrown ...
   */
  @Operation(
      summary = "Compute a Workbasket Report",
      description =
          "This endpoint generates a Workbasket Report.<p>Each Row represents a Workbasket.<p>Each "
              + "Column Header represents a Time Interval.",
      parameters = {
        @Parameter(
            name = "task-timestamp",
            description = "Determine which Task Timestamp should be used for comparison"),
        @Parameter(
            name = "state",
            examples = {
              @ExampleObject(value = "READY"),
              @ExampleObject(value = "CLAIMED"),
              @ExampleObject(value = "COMPLETED")
            })
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class))),
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_WORKBASKET_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeWorkbasketReport(
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    WorkbasketReport.Builder builder = monitorService.createWorkbasketReportBuilder();
    filterParameter.apply(builder);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(taskTimestamp), filterParameter, taskTimestamp);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Workbasket Priority Report.
   *
   * <p>Each Row represents a Workbasket.
   *
   * <p>Each Column Header represents a priority range.
   *
   * @title Compute a Workbasket Priority Report
   * @param filterParameter the filter parameters
   * @param workbasketTypes determine the WorkbasketTypes to include in the report
   * @param columnHeaders the column headers for the report
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException if topicWorkbaskets or useDefaultValues are false
   */
  @Operation(
      summary = "Compute a Workbasket Priority Report",
      description =
          "This endpoint generates a Workbasket Priority Report.<p>Each Row represents a "
              + "Workbasket.<p>Each Column Header represents a priority range.",
      parameters = {
        @Parameter(
            name = "workbasket-type",
            description = "Determine the WorkbasketTypes to include in the report",
            example = "GROUP"),
        @Parameter(name = "columnHeader", description = "The column headers for the report")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computePriorityWorkbasketReport(
      @ParameterObject PriorityReportFilterParameter filterParameter,
      @RequestParam(name = "workbasket-type", required = false) WorkbasketType[] workbasketTypes,
      @RequestParam(name = "columnHeader", required = false)
          PriorityColumnHeaderRepresentationModel[] columnHeaders)
      throws NotAuthorizedException, InvalidArgumentException {

    WorkbasketPriorityReport.Builder builder =
        monitorService.createWorkbasketPriorityReportBuilder().workbasketTypeIn(workbasketTypes);
    filterParameter.apply(builder);

    if (columnHeaders != null) {
      List<PriorityColumnHeader> priorityColumnHeaders =
          Arrays.stream(columnHeaders)
              .map(priorityColumnHeaderRepresentationModelAssembler::toEntityModel)
              .toList();
      builder.withColumnHeaders(priorityColumnHeaders);
    }

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(), filterParameter, workbasketTypes, columnHeaders);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Classification Category Report
   *
   * <p>Each Row represents a Classification category.
   *
   * <p>Each Column Header represents a Time Interval.
   *
   * @title Compute a Classification Category Report
   * @param filterParameter the filter parameters
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException TODO: this is never thrown ...
   */
  @Operation(
      summary = "Compute a Classification Category Report",
      description =
          "This endpoint generates a Classification Category Report.<p>Each Row represents a "
              + "Classification category.<p>Each Column Header represents a Time Interval.",
      parameters = {
        @Parameter(
            name = "task-timestamp",
            description = "Determine which Task Timestamp should be used for comparison")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_CLASSIFICATION_CATEGORY_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeClassificationCategoryReport(
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp taskTimestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    ClassificationCategoryReport.Builder builder =
        monitorService.createClassificationCategoryReportBuilder();
    filterParameter.apply(builder);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(taskTimestamp), filterParameter, taskTimestamp);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Classification Report.
   *
   * <p>Each Row represents a Classification.
   *
   * <p>Each Column Header represents a Time Interval.
   *
   * @title Compute a Classification Report
   * @param filterParameter the filter parameters
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @Operation(
      summary = "Compute a Classification Report",
      description =
          "This endpoint generates a Classification Report.<p>Each Row represents a Classification."
              + "<p>Each Column Header represents a Time Interval.",
      parameters = {
        @Parameter(
            name = "task-timestamp",
            description = "Determine which Task Timestamp should be used for comparison")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_CLASSIFICATION_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeClassificationReport(
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    ClassificationReport.Builder builder = monitorService.createClassificationReportBuilder();
    filterParameter.apply(builder);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(taskTimestamp), filterParameter, taskTimestamp);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Detailed Classification Report.
   *
   * <p>Each Foldable Row represents a Classification and can be expanded to show the Classification
   * of Attachments.
   *
   * <p>Each Column Header represents a Time Interval.
   *
   * @title Compute a Detailed Classification Report
   * @param filterParameter the filter parameters
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @Operation(
      summary = "Compute a Detailed Classification Report",
      description =
          "This endpoint generates a Detailed Classification Report.<p>Each Foldable Row represents"
              + " a Classification and can be expanded to show the Classification of Attachments."
              + "<p>Each Column Header represents a Time Interval.",
      parameters = {
        @Parameter(
            name = "task-timestamp",
            description = "Determine which Task Timestamp should be used for comparison")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_DETAILED_CLASSIFICATION_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeDetailedClassificationReport(
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    ClassificationReport.Builder builder = monitorService.createClassificationReportBuilder();
    filterParameter.apply(builder);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildDetailedReport(taskTimestamp), filterParameter, taskTimestamp);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Task Custom Field Value Report.
   *
   * <p>Each Row represents a value of the requested Task Custom Field.
   *
   * <p>Each Column Header represents a Time Interval
   *
   * @title Compute a Detailed Classification Report
   * @param customField the Task Custom Field whose values are of interest
   * @param filterParameter the filter parameters
   * @param taskTimestamp determine which Task Timestamp should be used for comparison
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @Operation(
      summary = "Compute a Detailed Classification Report",
      description =
          "This endpoint generates a Task Custom Field Value Report.<p>Each Row represents a value "
              + "of the requested Task Custom Field.<p>Each Column Header represents a Time "
              + "Interval.",
      parameters = {
        @Parameter(
            name = "custom-field",
            description = "The Task Custom Field whose values are of interest",
            example = "CUSTOM_14",
            required = true),
        @Parameter(
            name = "task-timestamp",
            description = "Determine which Task Timestamp should be used for comparison")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_TASK_CUSTOM_FIELD_VALUE_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeTaskCustomFieldValueReport(
      @RequestParam(name = "custom-field") TaskCustomField customField,
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    if (taskTimestamp == null) {
      taskTimestamp = TaskTimestamp.DUE;
    }

    TaskCustomFieldValueReport.Builder builder =
        monitorService.createTaskCustomFieldValueReportBuilder(customField);
    filterParameter.apply(builder);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(taskTimestamp), customField, filterParameter, taskTimestamp);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }

  /**
   * This endpoint generates a Task Status Report.
   *
   * <p>Each Row represents a Workbasket.
   *
   * <p>Each Column Header represents a Task State
   *
   * @title Compute a Task Status Report
   * @param domains Filter the report values by domains.
   * @param states Filter the report values by Task states.
   * @param workbasketIds Filter the report values by Workbasket Ids.
   * @param priorityMinimum Filter the report values by a minimum priority.
   * @return the computed Report
   * @throws NotAuthorizedException if the current user is not authorized to compute the Report
   */
  @Operation(
      summary = "Compute a Task Status Report",
      description =
          "This endpoint generates a Task Status Report.<p>Each Row represents a Workbasket.<p>"
              + "Each Column Header represents a Task State.",
      parameters = {
        @Parameter(
            name = "domain",
            description = "Filter the report values by domains",
            required = false),
        @Parameter(
            name = "state",
            description = "Filter the report values by Task states",
            required = false),
        @Parameter(
            name = "workbasket-id",
            description = "Filter the report values by Workbasket Ids",
            required = false),
        @Parameter(
            name = "priority-minimum",
            description = "Filter the report values by a minimum priority",
            required = false)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeTaskStatusReport(
      @RequestParam(name = "domain", required = false) List<String> domains,
      @RequestParam(name = "state", required = false) List<TaskState> states,
      @RequestParam(name = "workbasket-id", required = false) List<String> workbasketIds,
      @RequestParam(name = "priority-minimum", required = false) Integer priorityMinimum)
      throws NotAuthorizedException {

    TaskStatusReport.Builder builder = monitorService.createTaskStatusReportBuilder();
    if (states != null && !states.isEmpty()) {
      builder = builder.stateIn(states);
    }
    if (domains != null && !domains.isEmpty()) {
      builder.domainIn(domains);
    }
    if (workbasketIds != null && !workbasketIds.isEmpty()) {
      builder.workbasketIdsIn(workbasketIds);
    }
    if (priorityMinimum != null) {
      builder.priorityMinimum(priorityMinimum);
    }

    return ResponseEntity.ok(
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(), domains, states, workbasketIds, priorityMinimum));
  }

  /**
   * This endpoint generates a Timestamp Report.
   *
   * <p>Each Foldable Row represents a TaskTimestamp and can be expanded to display the four
   * organization levels of the corresponding Workbasket.
   *
   * <p>Each Column Header represents a TimeInterval.
   *
   * @title Compute a Timestamp Report
   * @param filterParameter the filter parameter
   * @param timestamps Filter by the Task Timestamp of the task
   * @return the computed report
   * @throws NotAuthorizedException if the current user is not authorized to compute the report
   * @throws InvalidArgumentException TODO: this is never thrown
   */
  @Operation(
      summary = "Compute a Timestamp Report",
      description =
          "This endpoint generates a Timestamp Report.<p>Each Foldable Row represents a "
              + "TaskTimestamp and can be expanded to display the four organization levels of the "
              + "corresponding Workbasket.<p>Each Column Header represents a TimeInterval.",
      parameters = {
        @Parameter(
            name = "task-timestamp",
            description = "Filter by the Task Timestamp of the task")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "The computed Report",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = ReportRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<ReportRepresentationModel> computeTimestampReport(
      @ParameterObject TimeIntervalReportFilterParameter filterParameter,
      @RequestParam(name = "task-timestamp", required = false) TaskTimestamp[] timestamps)
      throws NotAuthorizedException, InvalidArgumentException {

    TimestampReport.Builder builder = monitorService.createTimestampReportBuilder();
    filterParameter.apply(builder);
    Optional.ofNullable(timestamps).map(Arrays::asList).ifPresent(builder::withTimestamps);

    ReportRepresentationModel report =
        reportRepresentationModelAssembler.toModel(
            builder.buildReport(), filterParameter, timestamps);

    return ResponseEntity.status(HttpStatus.OK).body(report);
  }
}
