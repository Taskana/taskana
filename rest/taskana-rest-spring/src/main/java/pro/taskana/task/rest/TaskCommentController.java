package pro.taskana.task.rest;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.rest.assembler.TaskCommentRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskCommentCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Controller for all {@link TaskComment} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskCommentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentController.class);

  private final TaskService taskService;
  private final TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler;

  @Autowired
  TaskCommentController(
      TaskService taskService,
      TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler) {
    this.taskService = taskService;
    this.taskCommentRepresentationModelAssembler = taskCommentRepresentationModelAssembler;
  }

  /**
   * This endpoint fetches a Task Comment.
   *
   * @title Get a single Task Comment
   * @param taskCommentId the id of the Task Comment
   * @return the Task Comment
   * @throws NotAuthorizedException if the user is not authorized for the requested Task Comment
   * @throws TaskNotFoundException TODO: this is never thrown
   * @throws TaskCommentNotFoundException if the requested Task Comment is not found
   * @throws InvalidArgumentException if the requested id is null or empty
   */
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> getTaskComment(
      @PathVariable String taskCommentId)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskComment(taskCommentId= {})", taskCommentId);
    }

    TaskComment taskComment = taskService.getTaskComment(taskCommentId);

    TaskCommentRepresentationModel taskCommentRepresentationModel =
        taskCommentRepresentationModelAssembler.toModel(taskComment);

    ResponseEntity<TaskCommentRepresentationModel> response =
        ResponseEntity.ok(taskCommentRepresentationModel);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskComment(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint retrieves all Task Comments for a specific Task. Further filters can be applied.
   *
   * @param taskId the id of the Task whose comments are requested
   * @param sortBy Sort the result by a given field. Multiple sort values can be declared. When the
   *     primary sort value is the same, the second one will be used.
   * @param order The order direction for each sort value. This value requires the use of 'sort-by'.
   *     The amount of sort-by and order declarations have to match. Alternatively the value can be
   *     omitted. If done so the default sort order (ASCENDING) will be applied to every sort-by
   *     value.
   * @return a list of Task Comments
   * @throws NotAuthorizedException If the current user has no authorization to retrieve a Task
   *     Comment from a certain Task or is not authorized to access the Task.
   * @throws TaskNotFoundException If the given task id in the Task Comment does not refer to an
   *     existing Task
   * @throws InvalidArgumentException if some parameters were not supplied correctly
   * @title Get a list of all Task Comments for a specific Task
   */
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentCollectionRepresentationModel> getTaskComments(
      @PathVariable String taskId,
      @RequestParam(name = "sort-by", required = false) List<TaskCommentsSortBy> sortBy,
      @RequestParam(required = false) List<SortDirection> order)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskComments(taskId= {})", taskId);
    }

    Optional<Comparator<TaskComment>> comparator = getTaskCommentComparator(sortBy, order);
    List<TaskComment> taskComments = taskService.getTaskComments(taskId);
    comparator.ifPresent(taskComments::sort);

    TaskCommentCollectionRepresentationModel taskCommentListResource =
        taskCommentRepresentationModelAssembler.toTaskanaCollectionModel(taskComments);

    ResponseEntity<TaskCommentCollectionRepresentationModel> response =
        ResponseEntity.ok(taskCommentListResource);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskComments(), returning {}", response);
    }

    return response;
  }

  /**
   * This endpoint deletes a given Task Comment.
   *
   * @title Delete a Task Comment
   * @param taskCommentId the id of the Task Comment which should be deleted
   * @return no content, if everything went well.
   * @throws NotAuthorizedException if the current user is not authorized to delete a Task Comment
   * @throws TaskNotFoundException If the given task id in the Task Comment does not refer to an
   *     existing task.
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the requested Task Comment id is null or empty
   */
  @DeleteMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> deleteTaskComment(
      @PathVariable String taskCommentId)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to deleteTaskComment(taskCommentId= {})", taskCommentId);
    }

    taskService.deleteTaskComment(taskCommentId);

    ResponseEntity<TaskCommentRepresentationModel> result = ResponseEntity.noContent().build();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from deleteTaskComment(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint updates a given Task Comment.
   *
   * @title Update a Task Comment
   * @param taskCommentId the Task Comment which should be updated.
   * @param taskCommentRepresentationModel the new comment for the requested id.
   * @return the updated Task Comment
   * @throws NotAuthorizedException if the current user does not have access to the Task Comment
   * @throws TaskNotFoundException if the referenced Task within the Task Comment does not exist
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the id in the path and in the the request body does not
   *     match
   * @throws ConcurrencyException if the requested Task Comment has been updated in the meantime
   *     by a different process.
   */
  @PutMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> updateTaskComment(
      @PathVariable String taskCommentId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException, ConcurrencyException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to updateTaskComment(taskCommentId= {}, taskCommentResource= {})",
          taskCommentId,
          taskCommentRepresentationModel);
    }
    if (!taskCommentId.equals(taskCommentRepresentationModel.getTaskCommentId())) {
      throw new InvalidArgumentException(
          String.format(
              "TaskCommentId ('%s') is not identical with the id"
                  + " of the object in the payload which should be updated",
              taskCommentId));
    }

    TaskComment taskComment =
        taskCommentRepresentationModelAssembler.toEntityModel(taskCommentRepresentationModel);

    taskComment = taskService.updateTaskComment(taskComment);
    ResponseEntity<TaskCommentRepresentationModel> result =
        ResponseEntity.ok(taskCommentRepresentationModelAssembler.toModel(taskComment));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTaskComment(), returning {}", result);
    }

    return result;
  }

  /**
   * This endpoint creates a Task Comment.
   *
   * @title Create a new Task Comment
   * @param taskId the id of the Task where a Task Comment should be created.
   * @param taskCommentRepresentationModel the body of the Task Comment
   * @return the created Task Comment
   * @throws NotAuthorizedException if the current user is not authorized to create a Task Comment
   * @throws InvalidArgumentException if the Task Comment id is null or empty
   * @throws TaskNotFoundException if the requested task does not exist
   */
  @PostMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> createTaskComment(
      @PathVariable String taskId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws NotAuthorizedException, InvalidArgumentException, TaskNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to createTaskComment(taskId= {}, taskCommentResource= {})",
          taskId,
          taskCommentRepresentationModel);
    }

    taskCommentRepresentationModel.setTaskId(taskId);

    TaskComment taskCommentFromResource =
        taskCommentRepresentationModelAssembler.toEntityModel(taskCommentRepresentationModel);
    TaskComment createdTaskComment = taskService.createTaskComment(taskCommentFromResource);

    ResponseEntity<TaskCommentRepresentationModel> result;

    result =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(taskCommentRepresentationModelAssembler.toModel(createdTaskComment));

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createTaskComment(), returning {}", result);
    }

    return result;
  }

  private Optional<Comparator<TaskComment>> getTaskCommentComparator(
      List<TaskCommentsSortBy> sortBy, List<SortDirection> order) throws InvalidArgumentException {
    QuerySortParameter.verifyNotOnlyOrderByExists(sortBy, order);
    QuerySortParameter.verifyAmountOfSortByAndOrderByMatches(sortBy, order);
    Comparator<TaskComment> comparator = null;
    if (sortBy != null) {
      for (int i = 0; i < sortBy.size(); i++) {
        SortDirection sortDirection = order == null ? SortDirection.ASCENDING : order.get(i);
        Comparator<TaskComment> temp;
        switch (sortBy.get(i)) {
          case CREATED:
            temp = Comparator.comparing(TaskComment::getCreated);
            break;
          case MODIFIED:
            temp = Comparator.comparing(TaskComment::getModified);
            break;
          default:
            throw new InvalidArgumentException(
                String.format("Unknown sort-by '%s'", sortBy.get(i)));
        }
        if (sortDirection == SortDirection.DESCENDING) {
          temp = temp.reversed();
        }
        comparator = comparator == null ? temp : comparator.thenComparing(temp);
      }
    }
    return Optional.ofNullable(comparator);
  }

  enum TaskCommentsSortBy {
    CREATED,
    MODIFIED
  }
}
