package pro.taskana.task.rest;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import pro.taskana.task.api.TaskQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.AttachmentPersistenceException;
import pro.taskana.task.api.exceptions.InvalidOwnerException;
import pro.taskana.task.api.exceptions.InvalidStateException;
import pro.taskana.task.api.exceptions.TaskAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.assembler.TaskSummaryRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryPagedRepresentationModel;
import pro.taskana.workbasket.api.exceptions.WorkbasketNotFoundException;

/** Controller for all {@link Task} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

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

  /**
   * This endpoint retrieves a list of existing Tasks. Filters can be applied.
   *
   * @title Get a list of all Tasks
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return the Tasks with the given filter, sort and paging options.
   */
  @GetMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryPagedRepresentationModel> getTasks(
      TaskQueryFilterParameter filterParameter,
      TaskQuerySortParameter sortParameter,
      QueryPagingParameter<TaskSummary, TaskQuery> pagingParameter) {

    TaskQuery query = taskService.createTaskQuery();

    filterParameter.applyToQuery(query);
    sortParameter.applyToQuery(query);

    List<TaskSummary> taskSummaries = pagingParameter.applyToQuery(query);

    TaskSummaryPagedRepresentationModel pagedModels =
        taskSummaryRepresentationModelAssembler.toPagedModel(
            taskSummaries, pagingParameter.getPageMetadata());
    ResponseEntity<TaskSummaryPagedRepresentationModel> response = ResponseEntity.ok(pagedModels);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTasks(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint deletes an aggregation of Tasks and returns the deleted Tasks. Filters can be
   * applied.
   *
   * @title Delete multiple Tasks
   * @param filterParameter the filter parameters.
   * @return the deleted task summaries.
   * @throws InvalidArgumentException TODO: this is never thrown
   * @throws NotAuthorizedException if the current user is not authorized to delete the requested
   *     Tasks.
   */
  @DeleteMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskSummaryCollectionRepresentationModel> deleteTasks(
      TaskQueryFilterParameter filterParameter)
      throws InvalidArgumentException, NotAuthorizedException {
    TaskQuery query = taskService.createTaskQuery();
    filterParameter.applyToQuery(query);

    List<TaskSummary> taskSummaries = query.list();

    List<String> taskIdsToDelete =
        taskSummaries.stream().map(TaskSummary::getId).collect(Collectors.toList());

    BulkOperationResults<String, TaskanaException> result =
        taskService.deleteTasks(taskIdsToDelete);

    List<TaskSummary> successfullyDeletedTaskSummaries =
        taskSummaries.stream()
            .filter(summary -> !result.getFailedIds().contains(summary.getId()))
            .collect(Collectors.toList());

    ResponseEntity<TaskSummaryCollectionRepresentationModel> response =
        ResponseEntity.ok(
            taskSummaryRepresentationModelAssembler.toTaskanaCollectionModel(
                successfullyDeletedTaskSummaries));

    LOGGER.debug("Exit from deleteTasks(), returning {}", response);

    return response;
  }

  /**
   * This endpoint retrieves a specific Task.
   *
   * @title Get a single Task
   * @param taskId the id of the requested Task
   * @return the requested Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws NotAuthorizedException if the current user is not authorized to get the requested
   *     Task.
   */
  @GetMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> getTask(@PathVariable String taskId)
      throws TaskNotFoundException, NotAuthorizedException {
    LOGGER.debug("Entry to getTask(taskId= {})", taskId);
    Task task = taskService.getTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTask(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint claims a Task if possible.
   *
   * @title Claim a Task
   * @param taskId the requested Task which should be claimed
   * @param userName TODO: this is currently not used
   * @return the claimed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidStateException then the state of the requested Task is not READY.
   * @throws InvalidOwnerException if the Task is already claimed by someone else.
   * @throws NotAuthorizedException if the current user has no read permissions for the requested
   *     Task.
   */
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> claimTask(
      @PathVariable String taskId, @RequestBody(required = false) String userName)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {
    LOGGER.debug("Entry to claimTask(taskId= {}, userName= {})", taskId, userName);
    // TODO verify user
    taskService.claim(taskId);
    Task updatedTask = taskService.getTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from claimTask(), returning {}", result);
    }

    return result;
  }

  /**
   * Selects the first Task returned by the Task Query and claims it.
   *
   * @title Select and claim a Task
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @return the claimed Task
   * @throws InvalidOwnerException If the Task is already claimed by someone else
   * @throws NotAuthorizedException if the current user has no read permission for the Workbasket
   *     the Task is in
   */
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> selectAndClaimTask(
      TaskQueryFilterParameter filterParameter, TaskQuerySortParameter sortParameter)
      throws InvalidOwnerException, NotAuthorizedException {
    LOGGER.debug("Entry to selectAndClaimTask");

    TaskQuery query = taskService.createTaskQuery();

    filterParameter.applyToQuery(query);
    sortParameter.applyToQuery(query);

    Task selectedAndClaimedTask = taskService.selectAndClaim(query);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(selectedAndClaimedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from selectAndClaimTask(), returning {}", result);
    }

    return result;
  }

  /**
   * Cancel the claim of an existing Task if it was claimed by the current user before.
   *
   * @title Cancel a claimed Task
   * @param taskId the id of the requested Task.
   * @return the unclaimed Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidStateException if the Task is already in a final state.
   * @throws InvalidOwnerException if the Task is claimed by a different user.
   * @throws NotAuthorizedException if the current user has no read permission for the Workbasket
   *     the Task is in
   */
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID_CLAIM)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> cancelClaimTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidStateException, InvalidOwnerException,
          NotAuthorizedException {

    LOGGER.debug("Entry to cancelClaimTask(taskId= {}", taskId);

    Task updatedTask = taskService.cancelClaim(taskId);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from cancelClaimTask(), returning {}", result);
    }
    return result;
  }

  /**
   * This endpoint completes a Task.
   *
   * @title Complete a Task
   * @param taskId the requested Task.
   * @return the completed Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidOwnerException if current user is not the owner of the Task or an administrator.
   * @throws InvalidStateException if Task wasn't claimed before.
   * @throws NotAuthorizedException if the current user has no read permission for the Workbasket
   *     the Task is in
   */
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_COMPLETE)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> completeTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidOwnerException, InvalidStateException,
          NotAuthorizedException {
    LOGGER.debug("Entry to completeTask(taskId= {})", taskId);

    Task updatedTask = taskService.forceCompleteTask(taskId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from completeTask(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint deletes a Task.
   *
   * @title Delete a Task
   * @param taskId the id of the Task which should be deleted.
   * @return the deleted Task.
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws InvalidStateException TODO: this is never thrown
   * @throws NotAuthorizedException if the current user is not authorized to delete the requested
   *     Task.
   */
  @DeleteMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> deleteTask(@PathVariable String taskId)
      throws TaskNotFoundException, InvalidStateException, NotAuthorizedException {
    LOGGER.debug("Entry to deleteTask(taskId= {})", taskId);
    taskService.forceDeleteTask(taskId);
    ResponseEntity<TaskRepresentationModel> result = ResponseEntity.noContent().build();
    LOGGER.debug("Exit from deleteTask(), returning {}", result);
    return result;
  }

  /**
   * This endpoint creates a persistent Task.
   *
   * @title Create a new Task
   * @param taskRepresentationModel the Task which should be created.
   * @return the created Task
   * @throws WorkbasketNotFoundException if the referenced Workbasket does not exist
   * @throws ClassificationNotFoundException if the referenced Classification does not exist
   * @throws NotAuthorizedException if the current user is not authorized to append a Task in the
   *     referenced Workbasket
   * @throws TaskAlreadyExistException if the requested Task already exists.
   * @throws InvalidArgumentException if any input is semantically wrong.
   */
  @PostMapping(path = RestEndpoints.URL_TASKS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> createTask(
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws WorkbasketNotFoundException, ClassificationNotFoundException, NotAuthorizedException,
          TaskAlreadyExistException, InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to createTask(params= {})", taskRepresentationModel);
    }

    Task fromResource = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
    Task createdTask = taskService.createTask(fromResource);

    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(taskRepresentationModelAssembler.toModel(createdTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createTask(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint transfers a given Task to a given Workbasket, if possible.
   *
   * @title Transfer a Task to another Workbasket
   * @param taskId the id of the Task which should be transferred
   * @param workbasketId the id of the destination Workbasket
   * @return the successfully transferred Task.
   * @throws TaskNotFoundException if the requested Task does not exist
   * @throws WorkbasketNotFoundException if the requested Workbasket does not exist
   * @throws NotAuthorizedException if the current user has no authorization to transfer the Task.
   * @throws InvalidStateException if the Task is in a state which does not allow transferring.
   */
  @PostMapping(path = RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> transferTask(
      @PathVariable String taskId, @PathVariable String workbasketId)
      throws TaskNotFoundException, WorkbasketNotFoundException, NotAuthorizedException,
          InvalidStateException {
    LOGGER.debug("Entry to transferTask(taskId= {}, workbasketId= {})", taskId, workbasketId);
    Task updatedTask = taskService.transfer(taskId, workbasketId);
    ResponseEntity<TaskRepresentationModel> result =
        ResponseEntity.ok(taskRepresentationModelAssembler.toModel(updatedTask));
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from transferTask(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint updates a requested Task.
   *
   * @title Update a Task
   * @param taskId the id of the Task which should be updated
   * @param taskRepresentationModel the new Task
   * @return the updated Task
   * @throws TaskNotFoundException if the requested Task does not exist.
   * @throws ClassificationNotFoundException if the updated Classification does not exist.
   * @throws InvalidArgumentException if any semantically invalid parameter was provided
   * @throws ConcurrencyException if the Task has been updated by a different process in the
   *     meantime
   * @throws NotAuthorizedException if the current user is not authorized.
   * @throws AttachmentPersistenceException if the Task contains two attachments with the same id.
   * @throws InvalidStateException if an attempt is made to change the owner of the Task and the
   *     Task is not in state READY.
   */
  @PutMapping(path = RestEndpoints.URL_TASKS_ID)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskRepresentationModel> updateTask(
      @PathVariable(value = "taskId") String taskId,
      @RequestBody TaskRepresentationModel taskRepresentationModel)
      throws TaskNotFoundException, ClassificationNotFoundException, InvalidArgumentException,
          ConcurrencyException, NotAuthorizedException, AttachmentPersistenceException,
          InvalidStateException {
    LOGGER.debug(
        "Entry to updateTask(taskId= {}, taskResource= {})", taskId, taskRepresentationModel);
    ResponseEntity<TaskRepresentationModel> result;
    if (taskId.equals(taskRepresentationModel.getTaskId())) {
      Task task = taskRepresentationModelAssembler.toEntityModel(taskRepresentationModel);
      task = taskService.updateTask(task);
      result = ResponseEntity.ok(taskRepresentationModelAssembler.toModel(task));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskId ('%s') is not identical with the taskId of to "
                  + "object in the payload which should be updated. ID=('%s')",
              taskId, taskRepresentationModel.getTaskId()));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTask(), returning {}", result);
    }

    return result;
  }

  public enum TaskQuerySortBy implements QuerySortBy<TaskQuery> {
    CLASSIFICATION_KEY(TaskQuery::orderByClassificationKey),
    POR_TYPE(TaskQuery::orderByPrimaryObjectReferenceType),
    POR_VALUE(TaskQuery::orderByPrimaryObjectReferenceValue),
    STATE(TaskQuery::orderByState),
    NAME(TaskQuery::orderByName),
    DUE(TaskQuery::orderByDue),
    PLANNED(TaskQuery::orderByPlanned),
    PRIORITY(TaskQuery::orderByPriority);

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
}
