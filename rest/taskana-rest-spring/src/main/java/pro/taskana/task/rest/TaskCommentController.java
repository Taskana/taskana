package pro.taskana.task.rest;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.util.QueryParamsValidator;
import pro.taskana.task.api.TaskCommentQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.MismatchedTaskCommentCreatorException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.rest.assembler.TaskCommentRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskCommentCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;

/** Controller for all {@link TaskComment} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class TaskCommentController {

  private final TaskService taskService;
  private final TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler;

  /**
   * This endpoint retrieves a Task Comment.
   *
   * @title Get a single Task Comment
   * @param taskCommentId the Id of the Task Comment
   * @return the Task Comment
   * @throws MismatchedWorkbasketPermissionException if the user is not authorized for the requested
   *     Task Comment
   * @throws TaskNotFoundException TODO: this is never thrown
   * @throws TaskCommentNotFoundException if the requested Task Comment is not found
   * @throws InvalidArgumentException if the requested Id is null or empty
   */
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> getTaskComment(
      @PathVariable String taskCommentId)
      throws TaskNotFoundException, TaskCommentNotFoundException, InvalidArgumentException,
          MismatchedWorkbasketPermissionException {
    TaskComment taskComment = taskService.getTaskComment(taskCommentId);

    TaskCommentRepresentationModel taskCommentRepresentationModel =
        taskCommentRepresentationModelAssembler.toModel(taskComment);

    return ResponseEntity.ok(taskCommentRepresentationModel);
  }

  /**
   * This endpoint retrieves all Task Comments for a specific Task. Further filters can be applied.
   *
   * @param taskId the Id of the Task whose comments are requested
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return a list of Task Comments
   * @title Get a list of all Task Comments for a specific Task
   */
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentCollectionRepresentationModel> getTaskComments(
      @PathVariable String taskId,
      HttpServletRequest request,
      TaskCommentQueryFilterParameter filterParameter,
      TaskCommentQuerySortParameter sortParameter,
      QueryPagingParameter<TaskComment, TaskCommentQuery> pagingParameter) {

    QueryParamsValidator.validateParams(
        request,
        TaskCommentQueryFilterParameter.class,
        QuerySortParameter.class,
        QueryPagingParameter.class);

    TaskCommentQuery query = taskService.createTaskCommentQuery();

    query.taskIdIn(taskId);
    filterParameter.apply(query);
    sortParameter.apply(query);

    List<TaskComment> taskComments = pagingParameter.apply(query);

    TaskCommentCollectionRepresentationModel taskCommentListResource =
        taskCommentRepresentationModelAssembler.toTaskanaCollectionModel(taskComments);

    return ResponseEntity.ok(taskCommentListResource);
  }

  /**
   * This endpoint deletes a given Task Comment.
   *
   * @title Delete a Task Comment
   * @param taskCommentId the Id of the Task Comment which should be deleted
   * @return no content, if everything went well.
   * @throws MismatchedRoleException if the current user is not authorized to delete a Task Comment
   * @throws TaskNotFoundException If the given Task Id in the Task Comment does not refer to an
   *     existing task.
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the requested Task Comment Id is null or empty
   * @throws MismatchedWorkbasketPermissionException if the current user has not correct permissions
   * @throws MismatchedTaskCommentCreatorException if the current user has not correct permissions
   */
  @DeleteMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> deleteTaskComment(
      @PathVariable String taskCommentId)
      throws TaskNotFoundException, TaskCommentNotFoundException, InvalidArgumentException,
          MismatchedTaskCommentCreatorException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {
    taskService.deleteTaskComment(taskCommentId);

    return ResponseEntity.noContent().build();
  }

  /**
   * This endpoint updates a given Task Comment.
   *
   * @title Update a Task Comment
   * @param taskCommentId the Task Comment which should be updated.
   * @param taskCommentRepresentationModel the new comment for the requested id.
   * @return the updated Task Comment
   * @throws MismatchedRoleException if the current user does not have access to the Task Comment
   * @throws TaskNotFoundException if the referenced Task within the Task Comment does not exist
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the Id in the path and in the request body does not match
   * @throws ConcurrencyException if the requested Task Comment has been updated in the meantime by
   *     a different process.
   * @throws MismatchedTaskCommentCreatorException if the current user has not correct permissions
   * @throws MismatchedWorkbasketPermissionException if the current user has not correct permissions
   */
  @PutMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> updateTaskComment(
      @PathVariable String taskCommentId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws TaskNotFoundException, TaskCommentNotFoundException, InvalidArgumentException,
          ConcurrencyException, MismatchedTaskCommentCreatorException, MismatchedRoleException,
          MismatchedWorkbasketPermissionException {
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

    return ResponseEntity.ok(taskCommentRepresentationModelAssembler.toModel(taskComment));
  }

  /**
   * This endpoint creates a Task Comment.
   *
   * @title Create a new Task Comment
   * @param taskId the Id of the Task where a Task Comment should be created.
   * @param taskCommentRepresentationModel the Task Comment to create.
   * @return the created Task Comment
   * @throws MismatchedWorkbasketPermissionException if the current user is not authorized to create
   *     a Task Comment
   * @throws InvalidArgumentException if the Task Comment Id is null or empty
   * @throws TaskNotFoundException if the requested task does not exist
   */
  @PostMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> createTaskComment(
      @PathVariable String taskId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws InvalidArgumentException, TaskNotFoundException,
          MismatchedWorkbasketPermissionException {
    taskCommentRepresentationModel.setTaskId(taskId);

    TaskComment taskCommentFromResource =
        taskCommentRepresentationModelAssembler.toEntityModel(taskCommentRepresentationModel);
    TaskComment createdTaskComment = taskService.createTaskComment(taskCommentFromResource);

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(taskCommentRepresentationModelAssembler.toModel(createdTaskComment));
  }

  public enum TaskCommentQuerySortBy implements QuerySortBy<TaskCommentQuery> {
    CREATED(TaskCommentQuery::orderByCreated),
    MODIFIED(TaskCommentQuery::orderByModified);

    private final BiConsumer<TaskCommentQuery, SortDirection> consumer;

    TaskCommentQuerySortBy(BiConsumer<TaskCommentQuery, SortDirection> consumer) {
      this.consumer = consumer;
    }

    @Override
    public void applySortByForQuery(TaskCommentQuery query, SortDirection sortDirection) {
      consumer.accept(query, sortDirection);
    }
  }

  public static class TaskCommentQuerySortParameter
      extends QuerySortParameter<TaskCommentQuery, TaskCommentQuerySortBy> {

    @ConstructorProperties({"sort-by", "order"})
    public TaskCommentQuerySortParameter(
        List<TaskCommentQuerySortBy> sortBy, List<SortDirection> order)
        throws InvalidArgumentException {
      super(sortBy, order);
    }

    // this getter is necessary for the documentation!
    @Override
    public List<TaskCommentQuerySortBy> getSortBy() {
      return super.getSortBy();
    }
  }
}
