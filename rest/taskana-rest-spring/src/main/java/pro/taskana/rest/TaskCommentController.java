package pro.taskana.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.rest.resource.TaskCommentListResource;
import pro.taskana.rest.resource.TaskCommentResource;
import pro.taskana.rest.resource.TaskCommentResourceAssembler;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;

/** Controller for all {@link TaskComment} related endpoints. */
@RestController
@EnableHypermediaSupport(type = HypermediaType.HAL)
public class TaskCommentController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentController.class);

  private TaskService taskService;
  private TaskCommentResourceAssembler taskCommentResourceAssembler;

  TaskCommentController(
      TaskService taskService, TaskCommentResourceAssembler taskCommentResourceAssembler) {
    this.taskService = taskService;
    this.taskCommentResourceAssembler = taskCommentResourceAssembler;
  }

  @GetMapping(path = Mapping.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentResource> getTaskComment(
      @PathVariable String taskId, @PathVariable String taskCommentId)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskComment(taskId= {}, taskCommentId= {})", taskId, taskCommentId);
    }

    TaskComment taskComment = taskService.getTaskComment(taskId, taskCommentId);

    TaskCommentResource taskCommentResource = taskCommentResourceAssembler.toResource(taskComment);

    ResponseEntity<TaskCommentResource> response = ResponseEntity.ok(taskCommentResource);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskComment(), returning {}", response);
    }

    return response;
  }

  @GetMapping(path = Mapping.URL_TASK_COMMENTS)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentListResource> getTaskComments(@PathVariable String taskId)
      throws NotAuthorizedException, TaskNotFoundException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Entry to getTaskComments(taskId= {})", taskId);
    }

    List<TaskComment> taskComments = taskService.getTaskComments(taskId);

    TaskCommentListResource taskCommentListResource =
        taskCommentResourceAssembler.toListResource(taskComments);

    ResponseEntity<TaskCommentListResource> response = ResponseEntity.ok(taskCommentListResource);

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from getTaskComments(), returning {}", response);
    }

    return response;
  }

  @DeleteMapping(path = Mapping.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentResource> deleteTaskComment(
      @PathVariable String taskId, @PathVariable String taskCommentId)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to deleteTaskComment(taskId= {}, taskCommentId= {})", taskId, taskCommentId);
    }

    taskService.deleteTaskComment(taskId, taskCommentId);

    ResponseEntity<TaskCommentResource> result = ResponseEntity.noContent().build();

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from deleteTaskComment(), returning {}", result);
    }

    return result;
  }

  @PutMapping(path = Mapping.URL_TASK_COMMENT)
  @Transactional(readOnly = true, rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentResource> updateTaskComment(
      @PathVariable String taskId,
      @PathVariable String taskCommentId,
      @RequestBody TaskCommentResource taskCommentResource)
      throws NotAuthorizedException, TaskNotFoundException, TaskCommentNotFoundException,
          InvalidArgumentException, ConcurrencyException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to updateTaskComment(taskId= {}, taskCommentId= {}, taskCommentResource= {})",
          taskId,
          taskCommentId,
          taskCommentResource);
    }

    ResponseEntity<TaskCommentResource> result;

    if ((taskCommentId.equals(taskCommentResource.getTaskCommentId())
        && (taskId.equals(taskCommentResource.getTaskId())))) {

      TaskComment taskComment = taskCommentResourceAssembler.toModel(taskCommentResource);

      taskComment = taskService.updateTaskComment(taskComment);
      result = ResponseEntity.ok(taskCommentResourceAssembler.toResource(taskComment));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskCommentId ('%s') or TaskId ('%s') are not identical with the ids"
                  + " of object in the payload which should be updated",
              taskCommentId, taskId));
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from updateTaskComment(), returning {}", result);
    }

    return result;
  }

  @PostMapping(path = Mapping.URL_TASK_COMMENTS)
  @Transactional(rollbackFor = Exception.class)
  public ResponseEntity<TaskCommentResource> createTaskComment(
      @PathVariable String taskId, @RequestBody TaskCommentResource taskCommentResource)
      throws NotAuthorizedException, InvalidArgumentException, TaskNotFoundException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Entry to createTaskComment(taskId = {}, taskCommentResource= {})",
          taskId,
          taskCommentResource);
    }

    ResponseEntity<TaskCommentResource> result;

    if (taskId.equals(taskCommentResource.getTaskId())) {

      TaskComment taskCommentFromResource =
          taskCommentResourceAssembler.toModel(taskCommentResource);
      TaskComment createdTaskComment = taskService.createTaskComment(taskCommentFromResource);

      result =
          ResponseEntity.status(HttpStatus.CREATED)
              .body(taskCommentResourceAssembler.toResource(createdTaskComment));
    } else {
      throw new InvalidArgumentException(
          String.format(
              "TaskId ('%s') is not identical with the taskId of the"
                  + "object in the payload which should be updated.",
              taskCommentResource.getTaskId()));
    }
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Exit from createTaskComment(), returning {}", result);
    }

    return result;
  }
}
