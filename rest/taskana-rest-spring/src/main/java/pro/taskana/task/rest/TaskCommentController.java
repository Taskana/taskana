package pro.taskana.task.rest;

import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.config.EnableHypermediaSupport;
import org.springframework.hateoas.config.EnableHypermediaSupport.HypermediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
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
import pro.taskana.common.rest.QueryHelper;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.rest.assembler.TaskCommentRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;

/** Controller for all {@link TaskComment} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskCommentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentController.class);

  private static final String CREATED = "created";
  private static final String MODIFIED = "modified";

  private final TaskService taskService;
  private final TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler;

  @Autowired
  TaskCommentController(
      TaskService taskService,
      TaskCommentRepresentationModelAssembler taskCommentRepresentationModelAssembler) {
    this.taskService = taskService;
    this.taskCommentRepresentationModelAssembler = taskCommentRepresentationModelAssembler;
  }

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

  @GetMapping(path = RestEndpoints.URL_TASK_COMMENTS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>> getTaskComments(
      @PathVariable String taskId,
      @RequestParam(required = false) MultiValueMap<String, String> params)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskComments(taskId= {})", taskId);
    }

    List<TaskComment> taskComments = taskService.getTaskComments(taskId);

    // TODO Maybe introduce a query for task comments
    applySortingParams(taskComments, params);

    TaskanaPagedModel<TaskCommentRepresentationModel> taskCommentListResource =
        taskCommentRepresentationModelAssembler.toPageModel(taskComments, null);

    ResponseEntity<TaskanaPagedModel<TaskCommentRepresentationModel>> response =
        ResponseEntity.ok(taskCommentListResource);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskComments(), returning {}", response);
    }

    return response;
  }

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

    ResponseEntity<TaskCommentRepresentationModel> result = null;

    if ((taskCommentId.equals(taskCommentRepresentationModel.getTaskCommentId()))) {

      TaskComment taskComment =
          taskCommentRepresentationModelAssembler.toEntityModel(taskCommentRepresentationModel);

      taskComment = taskService.updateTaskComment(taskComment);
      result = ResponseEntity.ok(taskCommentRepresentationModelAssembler.toModel(taskComment));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskCommentId ('%s') is not identical with the id"
                  + " of the object in the payload which should be updated",
              taskCommentId));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTaskComment(), returning {}", result);
    }

    return result;
  }

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

  private void applySortingParams(
      List<TaskComment> taskComments, MultiValueMap<String, String> params)
      throws InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to applySortingParams(taskComments= {}, params= {})", taskComments, params);
    }
    QueryHelper.applyAndRemoveSortingParams(
        params,
        (sortBy, sortDirection) -> {
          Comparator<TaskComment> comparator;
          switch (sortBy) {
            case (CREATED):
              comparator = Comparator.comparing(TaskComment::getCreated);
              break;
            case (MODIFIED):
              comparator = Comparator.comparing(TaskComment::getModified);
              break;
            default:
              throw new InvalidArgumentException("Unknown sort attribute: " + sortBy);
          }
          if (sortDirection == SortDirection.DESCENDING) {
            comparator = comparator.reversed();
          }
          taskComments.sort(comparator);
        });

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from applySortingParams(), returning {}", taskComments);
    }
  }
}
