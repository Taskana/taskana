package io.kadai.task.rest;

import static java.util.function.Predicate.not;

import io.kadai.classification.api.exceptions.ClassificationNotFoundException;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.rest.QueryPagingParameter;
import io.kadai.common.rest.QuerySortBy;
import io.kadai.common.rest.QuerySortParameter;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.common.rest.util.QueryParamsValidator;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskQuery;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.exceptions.AttachmentPersistenceException;
import io.kadai.task.api.exceptions.InvalidCallbackStateException;
import io.kadai.task.api.exceptions.InvalidOwnerException;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.exceptions.ObjectReferencePersistenceException;
import io.kadai.task.api.exceptions.TaskAlreadyExistException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskSummary;
import io.kadai.task.rest.assembler.BulkOperationResultsRepresentationModelAssembler;
import io.kadai.task.rest.assembler.TaskRepresentationModelAssembler;
import io.kadai.task.rest.assembler.TaskSummaryRepresentationModelAssembler;
import io.kadai.task.rest.models.BulkOperationResultsRepresentationModel;
import io.kadai.task.rest.models.IsReadRepresentationModel;
import io.kadai.task.rest.models.TaskRepresentationModel;
import io.kadai.task.rest.models.TaskSummaryCollectionRepresentationModel;
import io.kadai.task.rest.models.TaskSummaryPagedRepresentationModel;
import io.kadai.task.rest.models.TransferTaskRepresentationModel;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.exceptions.WorkbasketNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for all {@link Task} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskController {

  private final TaskService taskService;
  private final TaskRepresentationModelAssembler taskRepresentationModelAssembler;
  private final TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler;
  private final BulkOperationResultsRepresentationModelAssembler
      bulkOperationResultsRepresentationModelAssembler;

  @Autowired
  TaskController(
      TaskService taskService,
      TaskRepresentationModelAssembler taskRepresentationModelAssembler,
      TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler,
      BulkOperationResultsRepresentationModelAssembler
          bulkOperationResultsRepresentationModelAssembler) {
    this.taskService = taskService;
    this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
    this.taskSummaryRepresentationModelAssembler = taskSummaryRepresentationModelAssembler;
    this.bulkOperationResultsRepresentationModelAssembler =
        bulkOperationResultsRepresentationModelAssembler;
  }

  // region CREATE

  /**
   * This endpoint creates a persistent Task.
   *
   * @param taskRepresentationModel the Task which should be created.
   * @return the created Task
   * @throws WorkbasketNotFoundException if the referenced Workbasket does not exist
   * @throws ClassificationNotFoundException if the referenced Classification does not exist
   * @throws NotAuthorizedOnWorkbasketException if the current user is not authorized to append a
   *     Task to the referenced Workbasket
   * @throws TaskAlreadyExistException if the requested Task already exists.
   * @throws InvalidArgumentException if any input is semantically wrong.
   * @throws AttachmentPersistenceException if an Attachment with ID will be added multiple times
   *     without using the task-methods
   * @throws ObjectReferencePersistenceException if an ObjectReference with ID will be added
   *     multiple times without using the task-methods
   * @title Create a new Task
   */
  @Operation(
      summary = "Create a new Task",
      description = "This endpoint creates a persistent Task.",
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the Task which should be created.",
              content =
                  @Content(
                      mediaType = MediaTypes.HAL_JSON_VALUE,
                      schema = @Schema(implementation = TaskRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{"
                                      + "\"priority\" : 0,"
                                      + "\"manualPriority\" : -1,"
                                      + "\"classificationSummary\" : {"
                                      + "\"key\" : \"L11010\","
                                      + "\"priority\" : 0"
                                      + "},"
                                      + "\"workbasketSummary\" : {"
                                      + "\"workbasketId\" : "
                                      + "\"WBI:100000000000000000000000000000000004\","
                                      + "\"markedForDeletion\" : false"
                                      + "},"
                                      + "\"primaryObjRef\" : {"
                                      + "\"company\" : \"MyCompany1\","
                                      + "\"system\" : \"MySystem1\","
                                      + "\"systemInstance\" : \"MyInstance1\","
                                      + "\"type\" : \"MyType1\","
                                      + "\"value\" : \"00000001\""
                                      + "},"
                                      + "\"secondaryObjectReferences\" : [ {"
                                      + "\"company\" : \"company\","
                                      + "\"system\" : \"system\","
                                      + "\"systemInstance\" : \"systemInstance\","
                                      + "\"type\" : \"type\","
                                      + "\"value\" : \"value\""
                                      + "} ],"
                                      + "\"customAttributes\" : [ ],"
                                      + "\"callbackInfo\" : [ ],"
                                      + "\"attachments\" : [ ],"
                                      + "\"read\" : false,"
                                      + "\"transferred\" : false"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "the created Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            })
      })
  @PostMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> createTask(
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws WorkbasketNotFoundException,
          ClassificationNotFoundException,
          TaskAlreadyExistException,
          InvalidArgumentException,
          AttachmentPersistenceException,
          ObjectReferencePersistenceException,
          NotAuthorizedOnWorkbasketException {

    if (!taskRepresentationModel.getAttachments().stream()
        .filter(att -> Objects.nonNull(att.getTaskId()))
        .filter(att -> !att.getTaskId().equals(taskRepresentationModel.getTaskId()))
        .toList()
        .isEmpty()) {
      throw new InvalidArgumentException(
          "An attachments' taskId must be empty or equal to the id of the task it belongs to");
    }

    Task fromResource = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
    Task createdTask = taskService.createTask(fromResource);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(taskRepresentationModelAssembler.toModel(createdTask));
  }

  // endregion

  // region READ

  /**
   * This endpoint retrieves a list of existing Tasks. Filters can be applied.
   *
   * @title Get a list of all Tasks
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param filterCustomFields the filter parameters regarding TaskCustomFields
   * @param filterCustomIntFields the filter parameters regarding TaskCustomIntFields * @param
   * @param groupByParameter the group by parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Tasks with the given filter, sort and paging options.
   * @throws InvalidArgumentException if the query parameter "owner-is-null" has values
   */
  @Operation(
      summary = "Get a list of all Tasks",
      description = "This endpoint retrieves a list of existing Tasks. Filters can be applied.",
      parameters = {
        @Parameter(name = "por-type", example = "VNR"),
        @Parameter(name = "por-value", example = "22334455"),
        @Parameter(name = "sort-by", example = "NAME")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the Tasks with the given filter, sort and paging options.",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskSummaryPagedRepresentationModel.class))
            })
      })
  @GetMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryPagedRepresentationModel> getTasks(
      HttpServletRequest request,
      @ParameterObject TaskQueryFilterParameter filterParameter,
      @ParameterObject TaskQueryFilterCustomFields filterCustomFields,
      @ParameterObject TaskQueryFilterCustomIntFields filterCustomIntFields,
      @ParameterObject TaskQueryGroupByParameter groupByParameter,
      @ParameterObject TaskQuerySortParameter sortParameter,
      @ParameterObject QueryPagingParameter<TaskSummary, TaskQuery> pagingParameter) {
    QueryParamsValidator.validateParams(
        request,
        TaskQueryFilterParameter.class,
        TaskQueryFilterCustomFields.class,
        TaskQueryFilterCustomIntFields.class,
        TaskQueryGroupByParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    if (QueryParamsValidator.hasQueryParameterValuesOrIsNotTrue(request, "owner-is-null")) {
      throw new InvalidArgumentException(
          "It is prohibited to use the param owner-is-null with values.");
    }

    TaskQuery query = taskService.createTaskQuery();

    filterParameter.apply(query);
    filterCustomFields.apply(query);
    filterCustomIntFields.apply(query);
    groupByParameter.apply(query);
    sortParameter.apply(query);

    List<TaskSummary> taskSummaries = pagingParameter.apply(query);

    TaskSummaryPagedRepresentationModel pagedModels =
        taskSummaryRepresentationModelAssembler.toPagedModel(
            taskSummaries, pagingParameter.getPageMetadata());
    return ResponseEntity.ok(pagedModels);
  }

  /**
   * This endpoint retrieves a specific Task.
   *
   * @param taskId the Id of the requested Task
   * @return the requested Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws NotAuthorizedOnWorkbasketException if the current user is not authorized to get the
   *     requested Task.
   * @title Get a single Task
   */
  @Operation(
      summary = "Get a single Task",
      description = "This endpoint retrieves a specific Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            required = true,
            description = "the Id of the requested Task",
            example = "TKI:100000000000000000000000000000000000")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the requested Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            })
      })
  @GetMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> getTask(@PathVariable("taskId") String taskId)
      throws TaskNotFoundException, NotAuthorizedOnWorkbasketException {
    Task task = taskService.getTask(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  // endregion

  // region UPDATE

  /**
   * This endpoint claims a Task if possible.
   *
   * @param taskId the Id of the Task which should be claimed
   * @param userName TODO: this is currently not used
   * @return the claimed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the state of the requested Task is not READY.
   * @throws InvalidOwnerException if the Task is already claimed by someone else.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permissions for the
   *     requested Task.
   * @title Claim a Task
   */
  @Operation(
      summary = "Claim a Task",
      description = "This endpoint claims a Task if possible.",
      parameters = {
        @Parameter(
            name = "taskId",
            required = true,
            description = "the Id of the Task which should be claimed",
            example = "TKI:000000000000000000000000000000000003")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the claimed Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            })
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> claimTask(
      @PathVariable("taskId") String taskId, @RequestBody(required = false) String userName)
      throws TaskNotFoundException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    // TODO verify user
    Task updatedTask = taskService.claim(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint force claims a Task if possible even if it is already claimed by someone else.
   *
   * @param taskId the Id of the Task which should be force claimed
   * @param userName TODO: this is currently not used
   * @return the force claimed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the state of Task with taskId is in an END_STATE.
   * @throws InvalidOwnerException cannot be thrown.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permissions for the
   *     requested Task.
   * @title Force claim a Task
   */
  @Operation(
      summary = "Force claim a Task",
      description =
          "This endpoint force claims a Task if possible even if it is already claimed by someone "
              + "else.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task which should be force claimed",
            required = true,
            example = "TKI:000000000000000000000000000000000003")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the force claimed Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            })
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceClaimTask(
      @PathVariable("taskId") String taskId, @RequestBody(required = false) String userName)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    // TODO verify user
    Task updatedTask = taskService.forceClaim(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint selects the first Task returned by the Task Query and claims it.
   *
   * @param filterParameter the filter parameters
   * @param filterCustomFields the filter parameters regarding TaskCustomFields
   * @param filterCustomIntFields the filter parameters regarding TaskCustomIntFields
   * @param sortParameter the sort parameters
   * @return the claimed Task or 404 if no Task is found
   * @throws InvalidOwnerException if the Task is already claimed by someone else
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Select and claim a Task
   */
  @Operation(
      summary = "Select and claim a Task",
      description =
          "This endpoint selects the first Task returned by the Task Query and claims it.",
      parameters = {@Parameter(name = "custom14", example = "abc")},
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the claimed Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            }),
        @ApiResponse(
            responseCode = "404",
            description = "if no Task is found",
            content = {@Content(schema = @Schema())})
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> selectAndClaimTask(
      @ParameterObject TaskQueryFilterParameter filterParameter,
      @ParameterObject TaskQueryFilterCustomFields filterCustomFields,
      @ParameterObject TaskQueryFilterCustomIntFields filterCustomIntFields,
      @ParameterObject TaskQuerySortParameter sortParameter)
      throws InvalidOwnerException, NotAuthorizedOnWorkbasketException {
    TaskQuery query = taskService.createTaskQuery();

    filterParameter.apply(query);
    filterCustomFields.apply(query);
    filterCustomIntFields.apply(query);
    sortParameter.apply(query);

    Optional<Task> selectedAndClaimedTask = taskService.selectAndClaim(query);

    return selectedAndClaimedTask
        .map(task -> ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * This endpoint cancels the claim of an existing Task if it was claimed by the current user
   * before.
   *
   * @param taskId the Id of the requested Task.
   * @param keepOwner flag whether or not to keep the owner despite the cancel claim
   * @return the unclaimed Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the Task is already in an end state.
   * @throws InvalidOwnerException if the Task is claimed by a different user.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Cancel a claimed Task
   */
  @Operation(
      summary = "Cancel a claimed Task",
      description =
          "This endpoint cancels the claim of an existing Task if it was claimed by the current "
              + "user before.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the requested Task.",
            required = true,
            example = "TKI:000000000000000000000000000000000002"),
        @Parameter(
            name = "keepOwner",
            description = "flag whether or not to keep the owner despite the cancel claim")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the unclaimed Task",
            content = {
              @Content(
                  mediaType = MediaTypes.HAL_JSON_VALUE,
                  schema = @Schema(implementation = TaskRepresentationModel.class))
            })
      })
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelClaimTask(
      @PathVariable("taskId") String taskId,
      @RequestParam(value = "keepOwner", defaultValue = "false") boolean keepOwner)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task updatedTask = taskService.cancelClaim(taskId, keepOwner);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint force cancels the claim of an existing Task.
   *
   * @param taskId the Id of the requested Task.
   * @param keepOwner flag whether or not to keep the owner despite the cancel claim
   * @return the unclaimed Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the Task is already in an end state.
   * @throws InvalidOwnerException if the Task is claimed by a different user.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Force cancel a claimed Task
   */
  @Operation(
      summary = "Force cancel a claimed Task",
      description = "This endpoint force cancels the claim of an existing Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the requested Task.",
            required = true,
            example = "TKI:000000000000000000000000000000000002"),
        @Parameter(
            name = "keepOwner",
            description = "flag whether or not to keep the owner despite the cancel claim.")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the unclaimed Task.",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceCancelClaimTask(
      @PathVariable("taskId") String taskId,
      @RequestParam(value = "keepOwner", defaultValue = "false") boolean keepOwner)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task updatedTask = taskService.forceCancelClaim(taskId, keepOwner);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint request a review on the specified Task.
   *
   * @param taskId taskId the id of the relevant Task
   * @return the Task after a review has been requested
   * @throws InvalidTaskStateException if the state of the Task with taskId is not CLAIMED
   * @throws TaskNotFoundException if the Task with taskId wasn't found
   * @throws InvalidOwnerException if the Task is claimed by another user
   * @throws NotAuthorizedOnWorkbasketException if the current user has no READ permissions for the
   *     Workbasket the Task is in
   * @title Request a review on a Task
   */
  @Operation(
      summary = "Request a review on a Task",
      description = "This endpoint requests a review on the specified Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the id of the relevant Task",
            required = true,
            example = "TKI:000000000000000000000000000000000032")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the Task after a review has been requested",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class))),
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> requestReview(
      @PathVariable("taskId") String taskId)
      throws InvalidTaskStateException,
          TaskNotFoundException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task task = taskService.requestReview(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  /**
   * This endpoint force request a review on the specified Task.
   *
   * @param taskId taskId the id of the relevant Task
   * @return the Task after a review has been requested
   * @throws InvalidTaskStateException if the state of the Task with taskId is not CLAIMED
   * @throws TaskNotFoundException if the Task with taskId wasn't found
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedOnWorkbasketException if the current user has no READ permissions for the
   *     Workbasket the Task is in
   * @title Force request a review on a Task
   */
  @Operation(
      summary = "Force request a review on a Task",
      description = "This endpoint force requests a review on the specified Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the id of the relevant Task",
            required = true,
            example = "TKI:000000000000000000000000000000000101")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the Task after a review has been requested",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceRequestReview(
      @PathVariable("taskId") String taskId)
      throws InvalidTaskStateException,
          TaskNotFoundException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task task = taskService.forceRequestReview(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  /**
   * This endpoint request changes on the specified Task.
   *
   * @param taskId the id of the relevant Task
   * @return the Task after changes have been requested
   * @throws InvalidTaskStateException if the state of the Task with taskId is not IN_REVIEW
   * @throws TaskNotFoundException if the Task with taskId wasn't found
   * @throws InvalidOwnerException if the Task is claimed by another user
   * @throws NotAuthorizedOnWorkbasketException if the current user has no READ permissions for the
   *     Workbasket the Task is in
   * @title Request changes on a Task
   */
  @Operation(
      summary = "Request changes on a Task",
      description = "This endpoint requests changes on the specified Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the id of the relevant Task",
            required = true,
            example = "TKI:000000000000000000000000000000000136")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "Changes requested successfully",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class))),
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> requestChanges(
      @PathVariable("taskId") String taskId)
      throws InvalidTaskStateException,
          TaskNotFoundException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task task = taskService.requestChanges(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  /**
   * This endpoint force requests changes on a Task.
   *
   * @param taskId the Id of the Task on which a review should be requested
   * @return the change requested Task
   * @throws InvalidTaskStateException if the Task with taskId is in an end state
   * @throws TaskNotFoundException if the Task with taskId wasn't found
   * @throws InvalidOwnerException cannot be thrown
   * @throws NotAuthorizedOnWorkbasketException if the current user has no READ permissions for the
   *     Workbasket the Task is in
   * @title Force request changes on a Task
   */
  @Operation(
      summary = "Force request changes on a Task",
      description = "This endpoint force requests changes on the specified Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task on which a review should be requested",
            required = true,
            example = "TKI:000000000000000000000000000000000100")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the change requested Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class))),
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceRequestChanges(
      @PathVariable("taskId") String taskId)
      throws InvalidTaskStateException,
          TaskNotFoundException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task task = taskService.forceRequestChanges(taskId);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  /**
   * This endpoint completes a Task.
   *
   * @param taskId Id of the requested Task to complete.
   * @return the completed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidOwnerException if current user is not the owner of the Task or an administrator.
   * @throws InvalidTaskStateException if Task wasn't claimed previously.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Complete a Task
   */
  @Operation(
      summary = "Complete a Task",
      description = "This endpoint completes a Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "Id of the requested Task to complete.",
            example = "TKI:000000000000000000000000000000000003",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the completed Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> completeTask(@PathVariable("taskId") String taskId)
      throws TaskNotFoundException,
          InvalidOwnerException,
          InvalidTaskStateException,
          NotAuthorizedOnWorkbasketException {

    Task updatedTask = taskService.completeTask(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint force completes a Task.
   *
   * @param taskId Id of the requested Task to force complete.
   * @return the force completed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidOwnerException cannot be thrown.
   * @throws InvalidTaskStateException if the state of the Task with taskId is TERMINATED or
   *     CANCELED
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Force complete a Task
   */
  @Operation(
      summary = "Force complete a Task",
      description = "This endpoint force completes a Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "Id of the requested Task to force complete.",
            example = "TKI:000000000000000000000000000000000003",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the force completed Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceCompleteTask(
      @PathVariable("taskId") String taskId)
      throws TaskNotFoundException,
          InvalidOwnerException,
          InvalidTaskStateException,
          NotAuthorizedOnWorkbasketException {

    Task updatedTask = taskService.forceCompleteTask(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint cancels a Task. Cancellation marks a Task as obsolete. The actual work the Task
   * was referring to is no longer required
   *
   * @param taskId Id of the requested Task to cancel.
   * @return the cancelled Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the task is not in state READY or CLAIMED
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Cancel a Task
   */
  @Operation(
      summary = "Cancel a Task",
      description =
          "This endpoint cancels a Task. Cancellation marks a Task as obsolete. The actual work "
              + "the Task was referring to is no longer required",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "Id of the requested Task to cancel.",
            example = "TKI:000000000000000000000000000000000026",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the cancelled Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CANCEL)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelTask(@PathVariable("taskId") String taskId)
      throws TaskNotFoundException, NotAuthorizedOnWorkbasketException, InvalidTaskStateException {

    Task cancelledTask = taskService.cancelTask(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(cancelledTask));
  }

  /**
   * This endpoint terminates a Task. Termination is an administrative action to complete a Task.
   *
   * @param taskId Id of the requested Task to terminate.
   * @return the terminated Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the task is not in state READY or CLAIMED
   * @throws NotAuthorizedException if the current user isn't an administrator (ADMIN/TASKADMIN)
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   * @title Terminate a Task
   */
  @Operation(
      summary = "Terminate a Task",
      description =
          "This endpoint terminates a Task. Termination is an administrative action to complete a "
              + "Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "Id of the requested Task to terminate.",
            required = true,
            example = "TKI:000000000000000000000000000000000000")
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the terminated Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TERMINATE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> terminateTask(
      @PathVariable("taskId") String taskId)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {

    Task terminatedTask = taskService.terminateTask(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(terminatedTask));
  }

  /**
   * This endpoint transfers a given Task to a given Workbasket, if possible.
   *
   * @title Transfer a Task to another Workbasket
   * @param taskId the Id of the Task which should be transferred
   * @param workbasketId the Id of the destination Workbasket
   * @param transferTaskRepresentationModel sets the transfer flag of the Task (default: true) and
   *     owner of the task
   * @return the successfully transferred Task.
   * @throws TaskNotFoundException if the requested Task does not exist
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist
   * @throws NotAuthorizedOnWorkbasketException if the current user has no authorization to transfer
   *     the Task.
   * @throws InvalidTaskStateException if the Task is in a state which does not allow transferring.
   */
  @Operation(
      summary = "Transfer a Task to another Workbasket",
      description = "This endpoint transfers a Task to a given Workbasket, if possible.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task which should be transferred",
            example = "TKI:000000000000000000000000000000000004",
            required = true),
        @Parameter(
            name = "workbasketId",
            description = "the Id of the destination Workbasket",
            example = "WBI:100000000000000000000000000000000001",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "sets the tansfer flag of the task (default: true)",
              content =
                  @Content(
                      schema = @Schema(implementation = TaskRepresentationModel.class),
                      examples = @ExampleObject(value = "{\"setTransferFlag\": false}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the successfully transferred Task.",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> transferTask(
      @PathVariable("taskId") String taskId,
      @PathVariable("workbasketId") String workbasketId,
      @RequestBody(required = false)
          TransferTaskRepresentationModel transferTaskRepresentationModel)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    Task updatedTask;
    if (transferTaskRepresentationModel == null) {
      updatedTask = taskService.transfer(taskId, workbasketId);
    } else {
      updatedTask =
          taskService.transferWithOwner(
              taskId,
              workbasketId,
              transferTaskRepresentationModel.getOwner(),
              transferTaskRepresentationModel.getSetTransferFlag());
    }

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint transfers a list of Tasks listed in the body to a given Workbasket, if possible.
   * Tasks that can be transfered without throwing an exception get transferred independent of other
   * Tasks. If the transfer of a Task throws an exception, then the Task will remain in the old
   * Workbasket.
   *
   * @title Transfer Tasks to another Workbasket
   * @param workbasketId the Id of the destination Workbasket
   * @param transferTaskRepresentationModel JSON formatted request body containing the TaskIds,
   *     owner and setTransferFlag of tasks to be transferred; owner and setTransferFlag are
   *     optional, while the TaskIds are mandatory
   * @return the taskIds and corresponding ErrorCode of tasks failed to be transferred
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist
   * @throws NotAuthorizedOnWorkbasketException if the current user has no authorization to transfer
   *     the Task
   */
  @PostMapping(path = RestEndpoints.URL_TRANSFER_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<BulkOperationResultsRepresentationModel> transferTasks(
      @PathVariable("workbasketId") String workbasketId,
      @RequestBody TransferTaskRepresentationModel transferTaskRepresentationModel)
      throws NotAuthorizedOnWorkbasketException, WorkbasketNotFoundException {
    List<String> taskIds = transferTaskRepresentationModel.getTaskIds();
    BulkOperationResults<String, KadaiException> result =
        taskService.transferTasksWithOwner(
            workbasketId,
            taskIds,
            transferTaskRepresentationModel.getOwner(),
            transferTaskRepresentationModel.getSetTransferFlag());

    BulkOperationResultsRepresentationModel repModel =
        bulkOperationResultsRepresentationModelAssembler.toModel(result);

    return ResponseEntity.ok(repModel);
  }

  /**
   * This endpoint updates a requested Task.
   *
   * @param taskId the Id of the Task which should be updated
   * @param taskRepresentationModel the new Task for the requested id.
   * @return the updated Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws ClassificationNotFoundException if the updated Classification does not exist.
   * @throws InvalidArgumentException if any semantically invalid parameter was provided
   * @throws ConcurrencyException if the Task has been updated by a different process in the
   *     meantime
   * @throws NotAuthorizedOnWorkbasketException if the current user is not authorized.
   * @throws AttachmentPersistenceException if the modified Task contains two attachments with the
   *     same id.
   * @throws ObjectReferencePersistenceException if the modified Task contains two object references
   *     with the same id.
   * @throws InvalidTaskStateException if an attempt is made to change the owner of the Task and the
   *     Task is not in state READY.
   * @title Update a Task
   */
  @Operation(
      summary = "Update a Task",
      description = "This endpoint updates a requested Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task which should be updated",
            example = "TKI:000000000000000000000000000000000003",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "the new Task for the requested id.",
              content =
                  @Content(
                      schema = @Schema(implementation = TaskRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"taskId\": "
                                      + "\"TKI:000000000000000000000000000000000003\",\n"
                                      + "  \"externalId\": "
                                      + "\"ETI:000000000000000000000000000000000003\",\n"
                                      + "  \"created\": \"2018-02-01T12:00:00.000Z\",\n"
                                      + "  \"modified\": \"2018-02-01T12:00:00.000Z\",\n"
                                      + "  \"planned\": \"2024-05-27T15:27:56.595Z\",\n"
                                      + "  \"received\": \"2024-05-29T15:27:56.595Z\",\n"
                                      + "  \"due\": \"2024-05-29T15:27:56.595Z\",\n"
                                      + "  \"name\": \"Widerruf\",\n"
                                      + "  \"creator\": \"creator_user_id\",\n"
                                      + "  \"description\": \"new description\",\n"
                                      + "  \"priority\": 2,\n"
                                      + "  \"manualPriority\": -1,\n"
                                      + "  \"state\": \"READY\",\n"
                                      + "  \"classificationSummary\": {\n"
                                      + "    \"classificationId\": "
                                      + "\"CLI:100000000000000000000000000000000003\",\n"
                                      + "    \"key\": \"L1050\",\n"
                                      + "    \"applicationEntryPoint\": \"\",\n"
                                      + "    \"category\": \"EXTERNAL\",\n"
                                      + "    \"domain\": \"DOMAIN_A\",\n"
                                      + "    \"name\": \"Widerruf\",\n"
                                      + "    \"parentId\": \"\",\n"
                                      + "    \"parentKey\": \"\",\n"
                                      + "    \"priority\": 1,\n"
                                      + "    \"serviceLevel\": \"P13D\",\n"
                                      + "    \"type\": \"TASK\",\n"
                                      + "    \"custom1\": \"VNR,RVNR,KOLVNR\",\n"
                                      + "    \"custom2\": \"\",\n"
                                      + "    \"custom3\": \"\",\n"
                                      + "    \"custom4\": \"\",\n"
                                      + "    \"custom5\": \"\",\n"
                                      + "    \"custom6\": \"\",\n"
                                      + "    \"custom7\": \"\",\n"
                                      + "    \"custom8\": \"\"\n"
                                      + "  },\n"
                                      + "  \"workbasketSummary\": {\n"
                                      + "    \"workbasketId\": "
                                      + "\"WBI:100000000000000000000000000000000001\",\n"
                                      + "    \"key\": \"GPK_KSC\",\n"
                                      + "    \"name\": \"Gruppenpostkorb KSC\",\n"
                                      + "    \"domain\": \"DOMAIN_A\",\n"
                                      + "    \"type\": \"GROUP\",\n"
                                      + "    \"description\": \"Gruppenpostkorb KSC\",\n"
                                      + "    \"owner\": \"teamlead-1\",\n"
                                      + "    \"custom1\": \"ABCQVW\",\n"
                                      + "    \"custom2\": \"\",\n"
                                      + "    \"custom3\": \"xyz4\",\n"
                                      + "    \"custom4\": \"\",\n"
                                      + "    \"custom5\": \"\",\n"
                                      + "    \"custom6\": \"\",\n"
                                      + "    \"custom7\": \"\",\n"
                                      + "    \"custom8\": \"\",\n"
                                      + "    \"orgLevel1\": \"\",\n"
                                      + "    \"orgLevel2\": \"\",\n"
                                      + "    \"orgLevel3\": \"\",\n"
                                      + "    \"orgLevel4\": \"\",\n"
                                      + "    \"markedForDeletion\": false\n"
                                      + "  },\n"
                                      + "  \"businessProcessId\": \"PI_0000000000003\",\n"
                                      + "  \"parentBusinessProcessId\": "
                                      + "\"DOC_0000000000000000003\",\n"
                                      + "  \"primaryObjRef\": {\n"
                                      + "    \"company\": \"00\",\n"
                                      + "    \"system\": \"PASystem\",\n"
                                      + "    \"systemInstance\": \"00\",\n"
                                      + "    \"type\": \"VNR\",\n"
                                      + "    \"value\": \"11223344\"\n"
                                      + "  },\n"
                                      + "  \"custom1\": \"efg\",\n"
                                      + "  \"custom14\": \"abc\",\n"
                                      + "  \"customInt1\": 1,\n"
                                      + "  \"customInt2\": 2,\n"
                                      + "  \"customInt3\": 3,\n"
                                      + "  \"customInt4\": 4,\n"
                                      + "  \"customInt5\": 5,\n"
                                      + "  \"customInt6\": 6,\n"
                                      + "  \"customInt7\": 7,\n"
                                      + "  \"customInt8\": 8,\n"
                                      + "  \"secondaryObjectReferences\": [],\n"
                                      + "  \"customAttributes\": [],\n"
                                      + "  \"callbackInfo\": [],\n"
                                      + "  \"attachments\": [],\n"
                                      + "  \"read\": false,\n"
                                      + "  \"transferred\": false\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the updated Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PutMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> updateTask(
      @PathVariable("taskId") String taskId,
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws TaskNotFoundException,
          ClassificationNotFoundException,
          InvalidArgumentException,
          ConcurrencyException,
          NotAuthorizedOnWorkbasketException,
          AttachmentPersistenceException,
          InvalidTaskStateException,
          ObjectReferencePersistenceException {
    if (!taskId.equals(taskRepresentationModel.getTaskId())) {
      throw new InvalidArgumentException(
          String.format(
              "TaskId ('%s') is not identical with the taskId of to "
                  + "object in the payload which should be updated. ID=('%s')",
              taskId, taskRepresentationModel.getTaskId()));
    }

    if (!taskRepresentationModel.getAttachments().stream()
        .filter(att -> Objects.nonNull(att.getTaskId()))
        .filter(att -> !att.getTaskId().equals(taskRepresentationModel.getTaskId()))
        .toList()
        .isEmpty()) {
      throw new InvalidArgumentException(
          "An attachments' taskId must be empty or equal to the id of the task it belongs to");
    }

    Task task = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
    task = taskService.updateTask(task);
    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
  }

  /**
   * This endpoint sets the 'isRead' property of a Task.
   *
   * @param taskId Id of the requested Task to set read or unread.
   * @param isRead if true, the Task property isRead is set to true, else it's set to false
   * @return the updated Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Set a Task read or unread
   */
  @Operation(
      summary = "Set a Task read or unread",
      description = "This endpoint sets the 'isRead' property of a Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "Id of the requested Task to set read or unread.",
            example = "TKI:000000000000000000000000000000000025",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description =
                  "if true, the Task property isRead is set to true, else it's set to false",
              content =
                  @Content(
                      schema = @Schema(implementation = IsReadRepresentationModel.class),
                      examples = @ExampleObject(value = "{\"is-read\": true}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the updated Task",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SET_READ)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> setTaskRead(
      @PathVariable("taskId") String taskId, @RequestBody IsReadRepresentationModel isRead)
      throws TaskNotFoundException, NotAuthorizedOnWorkbasketException {

    Task updatedTask = taskService.setTaskRead(taskId, isRead.getIsRead());

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  // endregion

  // region DELETE

  /**
   * This endpoint deletes a Task.
   *
   * @title Delete a Task
   * @param taskId the Id of the Task which should be deleted.
   * @return the deleted Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException If the Task is not in an END_STATE
   * @throws NotAuthorizedException if the current user isn't an administrator (ADMIN)
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   * @throws InvalidCallbackStateException some comment
   */
  @Operation(
      summary = "Delete a Task",
      description = "This endpoint deletes a Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task which should be deleted.",
            required = true,
            example = "TKI:000000000000000000000000000000000039")
      },
      responses = {@ApiResponse(responseCode = "204", content = @Content(schema = @Schema()))})
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> deleteTask(@PathVariable("taskId") String taskId)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException,
          InvalidCallbackStateException {
    taskService.deleteTask(taskId);

    return ResponseEntity.noContent().build();
  }

  /**
   * This endpoint force deletes a Task even if it's not completed.
   *
   * @title Force delete a Task
   * @param taskId the Id of the Task which should be force deleted.
   * @return the force deleted Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException If the Task is not TERMINATED or CANCELLED and the Callback
   *     state of the Task is CALLBACK_PROCESSING_REQUIRED
   * @throws NotAuthorizedException if the current user isn't an administrator (ADMIN) Task.
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct
   * @throws InvalidCallbackStateException some comment
   */
  @Operation(
      summary = "Force delete a Task",
      description = "This endpoint force deletes a Task.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "the Id of the Task which should be force deleted.",
            example = "TKI:000000000000000000000000000000000005",
            required = true)
      },
      responses = {@ApiResponse(responseCode = "204", content = @Content(schema = @Schema()))})
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceDeleteTask(
      @PathVariable("taskId") String taskId)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException,
          InvalidCallbackStateException {
    taskService.forceDeleteTask(taskId);

    return ResponseEntity.noContent().build();
  }

  /**
   * This endpoint deletes an aggregation of Tasks and returns the deleted Tasks. Filters can be
   * applied.
   *
   * @title Delete multiple Tasks
   * @param filterParameter the filter parameters
   * @param filterCustomFields the filter parameters regarding TaskCustomFields
   * @param filterCustomIntFields the filter parameters regarding TaskCustomIntFields
   * @return the deleted task summaries
   * @throws InvalidArgumentException TODO: this is never thrown
   * @throws NotAuthorizedException if the current user is not authorized to delete the requested
   *     Tasks.
   */
  @Operation(
      summary = "Delete multiple Tasks",
      description =
          "This endpoint deletes an aggregation of Tasks and returns the deleted Tasks. Filters "
              + "can be applied.",
      parameters = {
        @Parameter(
            name = "task-id",
            examples = {
              @ExampleObject(value = "TKI:000000000000000000000000000000000036"),
              @ExampleObject(value = "TKI:000000000000000000000000000000000037"),
              @ExampleObject(value = "TKI:000000000000000000000000000000000038")
            })
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the deleted task summaries",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema =
                        @Schema(implementation = TaskSummaryCollectionRepresentationModel.class)))
      })
  @DeleteMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryCollectionRepresentationModel> deleteTasks(
      @ParameterObject TaskQueryFilterParameter filterParameter,
      @ParameterObject TaskQueryFilterCustomFields filterCustomFields,
      @ParameterObject TaskQueryFilterCustomIntFields filterCustomIntFields)
      throws InvalidArgumentException, NotAuthorizedException {
    TaskQuery query = taskService.createTaskQuery();
    filterParameter.apply(query);
    filterCustomFields.apply(query);
    filterCustomIntFields.apply(query);

    List<TaskSummary> taskSummaries = query.list();

    List<String> taskIdsToDelete = taskSummaries.stream().map(TaskSummary::getId).toList();

    BulkOperationResults<String, KadaiException> result = taskService.deleteTasks(taskIdsToDelete);

    Set<String> failedIds = new HashSet<>(result.getFailedIds());
    List<TaskSummary> successfullyDeletedTaskSummaries =
        taskSummaries.stream().filter(not(summary -> failedIds.contains(summary.getId()))).toList();

    return ResponseEntity.ok(
        taskSummaryRepresentationModelAssembler.toKadaiCollectionModel(
            successfullyDeletedTaskSummaries));
  }

  // endregion

  // region TaskQuery

  public enum TaskQuerySortBy implements QuerySortBy<TaskQuery> {
    CLASSIFICATION_KEY(TaskQuery::orderByClassificationKey),
    CLASSIFICATION_NAME(TaskQuery::orderByClassificationName),
    POR_TYPE(TaskQuery::orderByPrimaryObjectReferenceType),
    POR_VALUE(TaskQuery::orderByPrimaryObjectReferenceValue),
    POR_COMPANY(TaskQuery::orderByPrimaryObjectReferenceCompany),
    POR_SYSTEM(TaskQuery::orderByPrimaryObjectReferenceSystem),
    POR_SYSTEM_INSTANCE(TaskQuery::orderByPrimaryObjectReferenceSystemInstance),
    STATE(TaskQuery::orderByState),
    NAME(TaskQuery::orderByName),
    DUE(TaskQuery::orderByDue),
    PLANNED(TaskQuery::orderByPlanned),
    RECEIVED(TaskQuery::orderByReceived),
    PRIORITY(TaskQuery::orderByPriority),
    CREATED(TaskQuery::orderByCreated),
    CLAIMED(TaskQuery::orderByClaimed),
    DOMAIN(TaskQuery::orderByDomain),
    TASK_ID(TaskQuery::orderByTaskId),
    MODIFIED(TaskQuery::orderByModified),
    CREATOR(TaskQuery::orderByCreator),
    NOTE(TaskQuery::orderByNote),
    OWNER(TaskQuery::orderByOwner),
    OWNER_LONG_NAME(TaskQuery::orderByOwnerLongName),
    BUSINESS_PROCESS_ID(TaskQuery::orderByBusinessProcessId),
    PARENT_BUSINESS_PROCESS_ID(TaskQuery::orderByParentBusinessProcessId),
    WORKBASKET_KEY(TaskQuery::orderByWorkbasketKey),
    CUSTOM_1((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_1, sort)),
    CUSTOM_2((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_2, sort)),
    CUSTOM_3((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_3, sort)),
    CUSTOM_4((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_4, sort)),
    CUSTOM_5((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_5, sort)),
    CUSTOM_6((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_6, sort)),
    CUSTOM_7((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_7, sort)),
    CUSTOM_8((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_8, sort)),
    CUSTOM_9((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_9, sort)),
    CUSTOM_10((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_10, sort)),
    CUSTOM_11((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_11, sort)),
    CUSTOM_12((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_12, sort)),
    CUSTOM_13((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_13, sort)),
    CUSTOM_14((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_14, sort)),
    CUSTOM_15((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_15, sort)),
    CUSTOM_16((query, sort) -> query.orderByCustomAttribute(TaskCustomField.CUSTOM_16, sort)),
    WORKBASKET_ID(TaskQuery::orderByWorkbasketId),
    WORKBASKET_NAME(TaskQuery::orderByWorkbasketName),
    ATTACHMENT_CLASSIFICATION_KEY(TaskQuery::orderByAttachmentClassificationKey),
    ATTACHMENT_CLASSIFICATION_NAME(TaskQuery::orderByAttachmentClassificationName),
    ATTACHMENT_CLASSIFICATION_ID(TaskQuery::orderByAttachmentClassificationId),
    ATTACHMENT_CHANNEL(TaskQuery::orderByAttachmentChannel),
    ATTACHMENT_REFERENCE(TaskQuery::orderByAttachmentReference),
    ATTACHMENT_RECEIVED(TaskQuery::orderByAttachmentReceived),
    COMPLETED(TaskQuery::orderByCompleted);

    private final BiConsumer<TaskQuery, SortDirection> consumer;

    TaskQuerySortBy(BiConsumer<TaskQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(TaskQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  // Unfortunately this class is necessary, since spring can not inject the generic 'sort-by'
  // parameter from the super class.
  public static class TaskQuerySortParameter
      extends QuerySortParameter<TaskQuery, TaskQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public TaskQuerySortParameter(List<TaskQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<TaskQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }

  // endregion

}
