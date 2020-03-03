package pro.taskana.task.internal;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.security.CurrentUserContext;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.task.api.exceptions.TaskCommentAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;

class TaskCommentServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentServiceImpl.class);

  private static final String NOT_AUTHORIZED =
      " Not authorized, TaskComment creator and current user must match. TaskComment creator is ";
  private static final String BUT_CURRENT_USER_IS = " but current user is ";
  private static final String ID_PREFIX_TASK_COMMENT = "TCI";
  private InternalTaskanaEngine taskanaEngine;
  private TaskServiceImpl taskService;
  private TaskCommentMapper taskCommentMapper;

  TaskCommentServiceImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskCommentMapper taskCommentMapper,
      TaskServiceImpl taskService) {
    super();
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.taskCommentMapper = taskCommentMapper;
  }

  TaskComment updateTaskComment(TaskComment taskCommentToUpdate)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException {

    LOGGER.debug("entry to updateTaskComment (taskComment = {})", taskCommentToUpdate);

    String userId = CurrentUserContext.getUserid();

    TaskCommentImpl taskCommentImplToUpdate = (TaskCommentImpl) taskCommentToUpdate;

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskCommentImplToUpdate.getTaskId());

      if (taskCommentToUpdate.getCreator().equals(userId)) {

        TaskCommentImpl oldTaskComment = getTaskComment(taskCommentImplToUpdate.getId());

        checkModifiedHasNotChanged(oldTaskComment, taskCommentImplToUpdate);

        taskCommentImplToUpdate.setModified(Instant.now());

        taskCommentMapper.update(taskCommentImplToUpdate);

        LOGGER.debug(
            "Method updateTaskComment() updated taskComment '{}' for user '{}'.",
            taskCommentImplToUpdate.getId(),
            userId);

      } else {
        throw new NotAuthorizedException(
            NOT_AUTHORIZED + taskCommentImplToUpdate.getCreator() + BUT_CURRENT_USER_IS + userId,
            userId);
      }
    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from updateTaskComment()");
    }

    return taskCommentToUpdate;
  }

  TaskComment createTaskComment(TaskComment taskCommentToCreate)
      throws NotAuthorizedException, TaskCommentAlreadyExistException, TaskNotFoundException {

    LOGGER.debug("entry to setTaskComment (taskCommentToCreate = {})", taskCommentToCreate);

    TaskCommentImpl taskCommentImplToCreate = (TaskCommentImpl) taskCommentToCreate;

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskCommentImplToCreate.getTaskId());

      if (isTaskCommentAlreadyExisting(taskCommentImplToCreate.getId())) {
        throw new TaskCommentAlreadyExistException(taskCommentImplToCreate.getId());
      }

      initDefaultTaskCommentValues(taskCommentImplToCreate);

      taskCommentMapper.insert(taskCommentImplToCreate);

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from setTaskComment()");
    }

    return taskCommentToCreate;
  }

  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException {

    LOGGER.debug("entry to deleteTaskComment (taskComment = {}", taskCommentId);

    String userId = CurrentUserContext.getUserid();

    validateTaskCommentId(taskCommentId);

    try {

      taskanaEngine.openConnection();

      TaskComment taskCommentToDelete = getTaskComment(taskCommentId);

      if (taskCommentToDelete != null) {

        taskService.getTask(taskCommentToDelete.getTaskId());

      } else {
        throw new TaskCommentNotFoundException(
            taskCommentId, String.format("The TaskComment with ID %s wasn't found", taskCommentId));
      }

      if (taskCommentToDelete.getCreator().equals(userId)) {

        taskCommentMapper.delete(taskCommentId);

        LOGGER.debug("taskComment {} deleted", taskCommentToDelete.getId());

      } else {
        throw new NotAuthorizedException(
            NOT_AUTHORIZED + taskCommentToDelete.getCreator() + BUT_CURRENT_USER_IS + userId,
            userId);
      }

    } finally {
      taskanaEngine.returnConnection();
      LOGGER.debug("exit from deleteTaskComment()");
    }
  }

  List<TaskCommentImpl> getTaskComments(String taskId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    LOGGER.debug("entry to getTaskComments (taskId = {})", taskId);

    List<TaskCommentImpl> result = null;

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskId);

      result = taskCommentMapper.findByTaskId(taskId);

      if (result == null || result.isEmpty()) {
        throw new TaskCommentNotFoundException(
            taskId, "TaskComments for TaskId " + taskId + " were not found");
      }

      return result;

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from getTaskComments()");
    }
  }

  TaskCommentImpl getTaskComment(String taskCommentId) throws TaskCommentNotFoundException {

    LOGGER.debug("entry to getTaskComment (taskCommentId = {})", taskCommentId);

    TaskCommentImpl result = null;

    try {

      taskanaEngine.openConnection();

      result = taskCommentMapper.findById(taskCommentId);

      if (result == null) {
        throw new TaskCommentNotFoundException(
            taskCommentId, "TaskComment for id " + taskCommentId + " was not found");
      }

      return result;

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from getTaskComment()");
    }
  }

  private void checkModifiedHasNotChanged(
      TaskCommentImpl oldTaskComment, TaskCommentImpl taskCommentImplToUpdate)
      throws ConcurrencyException {

    if (!oldTaskComment.getModified().equals(taskCommentImplToUpdate.getModified())) {

      throw new ConcurrencyException(
          "The current TaskComment has been modified while editing. "
              + "The values can not be updated. TaskComment "
              + taskCommentImplToUpdate.toString());
    }
  }

  private void initDefaultTaskCommentValues(TaskCommentImpl taskCommentImplToCreate) {

    Instant now = Instant.now();

    if (taskCommentImplToCreate.getId() == null || "".equals(taskCommentImplToCreate.getId())) {
      taskCommentImplToCreate.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK_COMMENT));
    }

    if (taskCommentImplToCreate.getModified() == null) {
      taskCommentImplToCreate.setModified(now);
    }

    if (taskCommentImplToCreate.getCreated() == null) {
      taskCommentImplToCreate.setCreated(now);
    }

    String creator = CurrentUserContext.getUserid();
    if (taskanaEngine.getEngine().getConfiguration().isSecurityEnabled() && creator == null) {
      throw new SystemException(
          "TaskanaSecurity is enabled, but the current UserId is"
              + " NULL while creating a TaskComment.");
    }
    taskCommentImplToCreate.setCreator(creator);
  }

  private boolean isTaskCommentAlreadyExisting(String taskCommentId) {

    boolean isExisting = false;

    try {
      if (getTaskComment(taskCommentId) != null) {
        isExisting = true;
      }
    } catch (Exception ex) {
      LOGGER.warn(
          "TaskComment-Service threw Exception while calling "
              + "mapper and searching for TaskComment.",
          ex);
    }
    return isExisting;
  }

  private void validateTaskCommentId(String taskCommentId) throws InvalidArgumentException {

    if (taskCommentId == null || taskCommentId.isEmpty()) {
      throw new InvalidArgumentException("TaskId must not be null/empty for deletion");
    }
  }
}
