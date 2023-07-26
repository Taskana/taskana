package pro.taskana.task.rest;

import static java.util.function.Predicate.not;

import java.beans.ConstructorProperties;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.util.QueryParamsValidator;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidCallbackStateException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.ObjectReferencePersistenceException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.assembler.TaskSummaryRepresentationModelAssembler;
import pro.taskana.task.rest.models.IsReadRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryPagedRepresentationModel;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Controller for all {@link Task} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskController {

  private final TaskService taskService;
  private final TaskRepresentationModelAssembler taskRepresentationModelAssembler;
  private final TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler;

  @Autowired
  TaskController(
      TaskService taskService,
      TaskRepresentationModelAssembler taskRepresentationModelAssembler,
      TaskSummaryRepresentationModelAssembler taskSummaryRepresentationModelAssembler) {
    this.taskService = taskService;
    this.taskRepresentationModelAssembler = taskRepresentationModelAssembler;
    this.taskSummaryRepresentationModelAssembler = taskSummaryRepresentationModelAssembler;
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
   */
  @GetMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryPagedRepresentationModel> getTasks(
      HttpServletRequest request,
      TaskQueryFilterParameter filterParameter,
      TaskQueryFilterCustomFields filterCustomFields,
      TaskQueryFilterCustomIntFields filterCustomIntFields,
      TaskQueryGroupByParameter groupByParameter,
      TaskQuerySortParameter sortParameter,
      QueryPagingParameter<TaskSummary, TaskQuery> pagingParameter) {
    QueryParamsValidator.validateParams(
        request,
        TaskQueryFilterParameter.class,
        TaskQueryFilterCustomFields.class,
        TaskQueryFilterCustomIntFields.class,
        TaskQueryGroupByParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);
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
  @GetMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> getTask(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> claimTask(
      @PathVariable String taskId, @RequestBody(required = false) String userName)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceClaimTask(
      @PathVariable String taskId, @RequestBody(required = false) String userName)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> selectAndClaimTask(
      TaskQueryFilterParameter filterParameter,
      TaskQueryFilterCustomFields filterCustomFields,
      TaskQueryFilterCustomIntFields filterCustomIntFields,
      TaskQuerySortParameter sortParameter)
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
   * @return the unclaimed Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the Task is already in an end state.
   * @throws InvalidOwnerException if the Task is claimed by a different user.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Cancel a claimed Task
   */
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelClaimTask(@PathVariable String taskId)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task updatedTask = taskService.cancelClaim(taskId);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
  }

  /**
   * This endpoint force cancels the claim of an existing Task.
   *
   * @param taskId the Id of the requested Task.
   * @return the unclaimed Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidTaskStateException if the Task is already in an end state.
   * @throws InvalidOwnerException if the Task is claimed by a different user.
   * @throws NotAuthorizedOnWorkbasketException if the current user has no read permission for the
   *     Workbasket the Task is in
   * @title Force cancel a claimed Task
   */
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceCancelClaimTask(@PathVariable String taskId)
      throws TaskNotFoundException,
          InvalidTaskStateException,
          InvalidOwnerException,
          NotAuthorizedOnWorkbasketException {
    Task updatedTask = taskService.forceCancelClaim(taskId);
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> requestReview(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceRequestReview(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> requestChanges(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceRequestChanges(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> completeTask(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceCompleteTask(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CANCEL)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelTask(@PathVariable String taskId)
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TERMINATE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> terminateTask(@PathVariable String taskId)
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
   * @param setTransferFlag sets the tansfer flag of the task (default: true)
   * @return the successfully transferred Task.
   * @throws TaskNotFoundException if the requested Task does not exist
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist
   * @throws NotAuthorizedOnWorkbasketException if the current user has no authorization to transfer
   *     the Task.
   * @throws InvalidTaskStateException if the Task is in a state which does not allow transferring.
   */
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> transferTask(
      @PathVariable String taskId,
      @PathVariable String workbasketId,
      @RequestBody(required = false) Boolean setTransferFlag)
      throws TaskNotFoundException,
          WorkbasketNotFoundException,
          NotAuthorizedOnWorkbasketException,
          InvalidTaskStateException {
    Task updatedTask =
        taskService.transfer(taskId, workbasketId, setTransferFlag == null || setTransferFlag);

    return ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
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
  @PutMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> updateTask(
      @PathVariable(value = "taskId") String taskId,
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
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SET_READ)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> setTaskRead(
      @PathVariable String taskId, @RequestBody IsReadRepresentationModel isRead)
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
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> deleteTask(@PathVariable String taskId)
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
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_FORCE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> forceDeleteTask(@PathVariable String taskId)
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
  @DeleteMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryCollectionRepresentationModel> deleteTasks(
      TaskQueryFilterParameter filterParameter,
      TaskQueryFilterCustomFields filterCustomFields,
      TaskQueryFilterCustomIntFields filterCustomIntFields)
      throws InvalidArgumentException, NotAuthorizedException {
    TaskQuery query = taskService.createTaskQuery();
    filterParameter.apply(query);
    filterCustomFields.apply(query);
    filterCustomIntFields.apply(query);

    List<TaskSummary> taskSummaries = query.list();

    List<String> taskIdsToDelete =
        taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());

    BulkOperationResults<String, TaskanaException> result =
        taskService.deleteTasks(taskIdsToDelete);

    Set<String> failedIds = new HashSet<>(result.getFailedIds());
    List<TaskSummary> successfullyDeletedTaskSummaries =
        taskSummaries.stream()
            .filter(not(summary -> failedIds.contains(summary.getId())))
            .collect(Collectors.toList());

    return ResponseEntity.ok(
        taskSummaryRepresentationModelAssembler.toTaskanaCollectionModel(
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
