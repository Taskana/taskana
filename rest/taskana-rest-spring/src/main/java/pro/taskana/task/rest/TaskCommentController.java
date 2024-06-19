package pro.taskana.task.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.function.BiConsumer;
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
import org.springframework.web.bind.annotation.RestController;
import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.rest.QueryPagingParameter;
import pro.taskana.common.rest.QuerySortBy;
import pro.taskana.common.rest.QuerySortParameter;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.util.QueryParamsValidator;
import pro.taskana.task.api.TaskCommentQuery;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.rest.assembler.TaskCommentRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskCommentCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;

/** Controller for all {@link TaskComment} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskCommentController {

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
   * This endpoint retrieves a Task Comment.
   *
   * @title Get a single Task Comment
   * @param taskCommentId the Id of the Task Comment
   * @return the Task Comment
   * @throws NotAuthorizedOnWorkbasketException if the user is not authorized for the requested Task
   *     Comment
   * @throws TaskNotFoundException TODO: this is never thrown
   * @throws TaskCommentNotFoundException if the requested Task Comment is not found
   * @throws InvalidArgumentException if the requested Id is null or empty
   */
  @Operation(
      summary = "Get a single Task Comment",
      description = "This endpoint retrieves a Task Comment.",
      parameters = {
        @Parameter(
            name = "taskCommentId",
            description = "The Id of the Task Comment",
            example = "TCI:000000000000000000000000000000000000",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the Task Comment",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskCommentRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> getTaskComment(
      @PathVariable("taskCommentId") String taskCommentId)
      throws TaskNotFoundException,
          TaskCommentNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {
    TaskComment taskComment = taskService.getTaskComment(taskCommentId);

    TaskCommentRepresentationModel taskCommentRepresentationModel =
        taskCommentRepresentationModelAssembler.toModel(taskComment);

    return ResponseEntity.ok(taskCommentRepresentationModel);
  }

  /**
   * This endpoint retrieves all Task Comments for a specific Task. Further filters can be applied.
   *
   * @title Get a list of all Task Comments for a specific Task
   * @param taskId the Id of the Task whose comments are requested
   * @param request the HTTP request
   * @param filterParameter the filter parameters
   * @param sortParameter the sort parameters
   * @param pagingParameter the paging parameters
   * @return a list of Task Comments
   */
  @Operation(
      summary = "Get a list of all Task Comments for a specific Task",
      description =
          "This endpoint retrieves all Task Comments for a specific Task. Further filters can be "
              + "applied.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "The Id of the Task whose comments are requested",
            example = "TKI:000000000000000000000000000000000000",
            required = true)
      },
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "a list of Task Comments",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema =
                        @Schema(implementation = TaskCommentCollectionRepresentationModel.class)))
      })
  @GetMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentCollectionRepresentationModel> getTaskComments(
      @PathVariable("taskId") String taskId,
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
   * @throws NotAuthorizedException if the current user is not authorized to delete a Task Comment
   * @throws TaskNotFoundException If the given Task Id in the Task Comment does not refer to an
   *     existing task.
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the requested Task Comment Id is null or empty
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   * @throws NotAuthorizedOnTaskCommentException if the current user has not correct permissions
   */
  @Operation(
      summary = "Delete a Task Comment",
      description = "This endpoint deletes a given Task Comment.",
      parameters = {
        @Parameter(
            name = "taskCommentId",
            description = "The Id of the Task Comment which should be deleted",
            example = "TCI:000000000000000000000000000000000001",
            required = true)
      },
      responses = {@ApiResponse(responseCode = "204", content = @Content(schema = @Schema()))})
  @DeleteMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> deleteTaskComment(
      @PathVariable("taskCommentId") String taskCommentId)
      throws TaskNotFoundException,
          TaskCommentNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnTaskCommentException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {
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
   * @throws NotAuthorizedException if the current user does not have access to the Task Comment
   * @throws TaskNotFoundException if the referenced Task within the Task Comment does not exist
   * @throws TaskCommentNotFoundException if the requested Task Comment does not exist
   * @throws InvalidArgumentException if the Id in the path and in the request body does not match
   * @throws ConcurrencyException if the requested Task Comment has been updated in the meantime by
   *     a different process.
   * @throws NotAuthorizedOnTaskCommentException if the current user has not correct permissions
   * @throws NotAuthorizedOnWorkbasketException if the current user has not correct permissions
   */
  @Operation(
      summary = "Update a Task Comment",
      description = "This endpoint updates a given Task Comment.",
      parameters = {
        @Parameter(
            name = "taskCommentId",
            description = "The Id of the Task Comment which should be updated",
            example = "TCI:000000000000000000000000000000000000",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The new comment for the requested id",
              content =
                  @Content(
                      schema = @Schema(implementation = TaskCommentRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"taskCommentId\": "
                                      + "\"TCI:000000000000000000000000000000000000\",\n"
                                      + "  \"taskId\": "
                                      + "\"TKI:000000000000000000000000000000000000\",\n"
                                      + "  \"textField\": \"updated text in textfield\",\n"
                                      + "  \"creator\": \"user-1-1\",\n"
                                      + "  \"creatorFullName\": \"Mustermann, Max\",\n"
                                      + "  \"created\": \"2017-01-29T15:55:00Z\",\n"
                                      + "  \"modified\": \"2018-01-30T15:55:00Z\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "200",
            description = "the updated Task Comment",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskCommentRepresentationModel.class)))
      })
  @PutMapping(path = RestEndpoints.URL_TASK_COMMENT)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> updateTaskComment(
      @PathVariable("taskCommentId") String taskCommentId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws TaskNotFoundException,
          TaskCommentNotFoundException,
          InvalidArgumentException,
          ConcurrencyException,
          NotAuthorizedOnTaskCommentException,
          NotAuthorizedException,
          NotAuthorizedOnWorkbasketException {
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
   * @throws NotAuthorizedOnWorkbasketException if the current user is not authorized to create a
   *     Task Comment
   * @throws InvalidArgumentException if the Task Comment Id is null or empty
   * @throws TaskNotFoundException if the requested task does not exist
   */
  @Operation(
      summary = "Create a new Task Comment",
      description = "This endpoint creates a Task Comment.",
      parameters = {
        @Parameter(
            name = "taskId",
            description = "The Id of the Task where a Task Comment should be created",
            example = "TKI:000000000000000000000000000000000000",
            required = true)
      },
      requestBody =
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "The Task Comment to create",
              content =
                  @Content(
                      schema = @Schema(implementation = TaskCommentRepresentationModel.class),
                      examples =
                          @ExampleObject(
                              value =
                                  "{\n"
                                      + "  \"taskId\": "
                                      + "\"TKI:000000000000000000000000000000000000\",\n"
                                      + "  \"textField\": \"some text in textfield\"\n"
                                      + "}"))),
      responses = {
        @ApiResponse(
            responseCode = "201",
            description = "the created Task Comment",
            content =
                @Content(
                    mediaType = MediaTypes.HAL_JSON_VALUE,
                    schema = @Schema(implementation = TaskCommentRepresentationModel.class)))
      })
  @PostMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentRepresentationModel> createTaskComment(
      @PathVariable("taskId") String taskId,
      @RequestBody TaskCommentRepresentationModel taskCommentRepresentationModel)
      throws InvalidArgumentException, TaskNotFoundException, NotAuthorizedOnWorkbasketException {
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
