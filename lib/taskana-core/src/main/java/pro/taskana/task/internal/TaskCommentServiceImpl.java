package pro.taskana.task.internal;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
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

  TaskComment newTaskComment(String taskId) {

    LOGGER.debug("entry to newTaskComment (taskId = {})", taskId);

    TaskCommentImpl taskComment = new TaskCommentImpl();
    taskComment.setTaskId(taskId);

    LOGGER.debug("exit from newTaskComment(), returning {}", taskComment);

    return taskComment;
  }

  TaskComment updateTaskComment(TaskComment taskCommentToUpdate)
      throws NotAuthorizedException, ConcurrencyException, TaskCommentNotFoundException,
          TaskNotFoundException, InvalidArgumentException {

    LOGGER.debug("entry to updateTaskComment (taskComment = {})", taskCommentToUpdate);

    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();

    TaskCommentImpl taskCommentImplToUpdate = (TaskCommentImpl) taskCommentToUpdate;

    try {

      taskanaEngine.openConnection();

      TaskComment originalTaskComment = getTaskComment(taskCommentImplToUpdate.getId());

      if (originalTaskComment.getCreator().equals(userId)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.TASK_ADMIN)) {

        checkModifiedHasNotChanged(originalTaskComment, taskCommentImplToUpdate);

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

    return taskCommentImplToUpdate;
  }

  TaskComment createTaskComment(TaskComment taskCommentToCreate)
      throws NotAuthorizedException, TaskNotFoundException, InvalidArgumentException {

    LOGGER.debug("entry to setTaskComment (taskCommentToCreate = {})", taskCommentToCreate);

    TaskCommentImpl taskCommentImplToCreate = (TaskCommentImpl) taskCommentToCreate;

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskCommentImplToCreate.getTaskId());

      validateNoneExistingTaskCommentId(taskCommentImplToCreate.getId());

      initDefaultTaskCommentValues(taskCommentImplToCreate);

      taskCommentMapper.insert(taskCommentImplToCreate);

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from setTaskComment()");
    }

    return taskCommentImplToCreate;
  }

  void deleteTaskComment(String taskCommentId)
      throws NotAuthorizedException, TaskCommentNotFoundException, TaskNotFoundException,
          InvalidArgumentException {

    LOGGER.debug("entry to deleteTaskComment (taskComment = {}", taskCommentId);

    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();

    try {

      taskanaEngine.openConnection();

      TaskComment taskCommentToDelete = getTaskComment(taskCommentId);

      if (taskCommentToDelete.getCreator().equals(userId)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.TASK_ADMIN)) {

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

  List<TaskComment> getTaskComments(String taskId)
      throws NotAuthorizedException, TaskNotFoundException {

    LOGGER.debug("entry to getTaskComments (taskId = {})", taskId);

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskId);

      List<TaskComment> taskComments = new ArrayList<>(taskCommentMapper.findByTaskId(taskId));

      if (taskComments.isEmpty()) {
        LOGGER.debug("getTaskComments() found no comments for the provided taskId");
      }

      return taskComments;

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from getTaskComments()");
    }
  }

  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException {

    LOGGER.debug("entry to getTaskComment (taskCommentId = {})", taskCommentId);

    TaskCommentImpl result;

    verifyTaskCommentIdIsNotNullOrEmpty(taskCommentId);

    try {

      taskanaEngine.openConnection();

      result = taskCommentMapper.findById(taskCommentId);

      if (result == null) {
        throw new TaskCommentNotFoundException(
            taskCommentId,
            String.format("TaskComment for taskCommentId '%s' was not found", taskCommentId));
      }

      taskService.getTask(result.getTaskId());

      return result;

    } finally {

      taskanaEngine.returnConnection();

      LOGGER.debug("exit from getTaskComment()");
    }
  }

  private void checkModifiedHasNotChanged(
      TaskComment oldTaskComment, TaskComment taskCommentImplToUpdate) throws ConcurrencyException {

    if (!oldTaskComment.getModified().equals(taskCommentImplToUpdate.getModified())) {

      throw new ConcurrencyException(
          "The current TaskComment has been modified while editing. "
              + "The values can not be updated. TaskComment "
              + taskCommentImplToUpdate.toString());
    }
  }

  private void initDefaultTaskCommentValues(TaskCommentImpl taskCommentImplToCreate) {

    Instant now = Instant.now();

    taskCommentImplToCreate.setId(IdGenerator.generateWithPrefix(ID_PREFIX_TASK_COMMENT));
    taskCommentImplToCreate.setModified(now);
    taskCommentImplToCreate.setCreated(now);

    String creator = taskanaEngine.getEngine().getCurrentUserContext().getUserid();
    if (taskanaEngine.getEngine().getConfiguration().isSecurityEnabled() && creator == null) {
      throw new SystemException(
          "TaskanaSecurity is enabled, but the current UserId is"
              + " NULL while creating a TaskComment.");
    }
    taskCommentImplToCreate.setCreator(creator);
  }

  private void validateNoneExistingTaskCommentId(String taskCommentId)
      throws InvalidArgumentException {

    if (taskCommentId != null && !taskCommentId.equals("")) {
      throw new InvalidArgumentException(
          String.format(
              "taskCommentId must be null/empty for creation, but found %s", taskCommentId));
    }
  }

  private void verifyTaskCommentIdIsNotNullOrEmpty(String taskCommentId)
      throws InvalidArgumentException {

    if (taskCommentId == null || taskCommentId.isEmpty()) {
      throw new InvalidArgumentException(
          "taskCommentId must not be null/empty for retrieval/deletion");
    }
  }
}
