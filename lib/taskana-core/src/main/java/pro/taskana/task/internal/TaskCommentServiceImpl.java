package pro.taskana.task.internal;

import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.UserMapper;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;

class TaskCommentServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentServiceImpl.class);

  private final InternalTaskanaEngine taskanaEngine;
  private final TaskServiceImpl taskService;
  private final TaskCommentMapper taskCommentMapper;
  private final UserMapper userMapper;

  TaskCommentServiceImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskCommentMapper taskCommentMapper,
      UserMapper userMapper,
      TaskServiceImpl taskService) {
    this.taskanaEngine = taskanaEngine;
    this.taskService = taskService;
    this.taskCommentMapper = taskCommentMapper;
    this.userMapper = userMapper;
  }

  TaskComment newTaskComment(String taskId) {

    TaskCommentImpl taskComment = new TaskCommentImpl();
    taskComment.setTaskId(taskId);

    return taskComment;
  }

  TaskComment updateTaskComment(TaskComment taskCommentToUpdate)
      throws ConcurrencyException,
          TaskCommentNotFoundException,
          TaskNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnTaskCommentException,
          NotAuthorizedOnWorkbasketException {

    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();

    TaskCommentImpl taskCommentImplToUpdate = (TaskCommentImpl) taskCommentToUpdate;

    try {

      taskanaEngine.openConnection();

      TaskComment originalTaskComment = getTaskComment(taskCommentImplToUpdate.getId());

      if (originalTaskComment.getCreator().equals(userId)
              && taskCommentImplToUpdate.getCreator().equals(originalTaskComment.getCreator())
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.TASK_ADMIN)) {

        checkModifiedHasNotChanged(originalTaskComment, taskCommentImplToUpdate);

        taskCommentImplToUpdate.setModified(Instant.now());

        taskCommentMapper.update(taskCommentImplToUpdate);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug(
              "Method updateTaskComment() updated taskComment '{}' for user '{}'.",
              taskCommentImplToUpdate.getId(),
              userId);
        }

      } else {
        throw new NotAuthorizedOnTaskCommentException(userId, taskCommentImplToUpdate.getId());
      }
    } finally {
      taskanaEngine.returnConnection();
    }

    return taskCommentImplToUpdate;
  }

  TaskComment createTaskComment(TaskComment taskCommentToCreate)
      throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedOnWorkbasketException {

    TaskCommentImpl taskCommentImplToCreate = (TaskCommentImpl) taskCommentToCreate;

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskCommentImplToCreate.getTaskId());

      validateNoneExistingTaskCommentId(taskCommentImplToCreate.getId());

      initDefaultTaskCommentValues(taskCommentImplToCreate);

      taskCommentMapper.insert(taskCommentImplToCreate);

    } finally {
      taskanaEngine.returnConnection();
    }

    return taskCommentImplToCreate;
  }

  void deleteTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException,
          TaskNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnTaskCommentException,
          NotAuthorizedOnWorkbasketException {

    String userId = taskanaEngine.getEngine().getCurrentUserContext().getUserid();

    try {

      taskanaEngine.openConnection();

      TaskComment taskCommentToDelete = getTaskComment(taskCommentId);

      if (taskCommentToDelete.getCreator().equals(userId)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.ADMIN)
          || taskanaEngine.getEngine().isUserInRole(TaskanaRole.TASK_ADMIN)) {

        taskCommentMapper.delete(taskCommentId);

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("taskComment {} deleted", taskCommentToDelete.getId());
        }

      } else {
        throw new NotAuthorizedOnTaskCommentException(userId, taskCommentToDelete.getId());
      }

    } finally {
      taskanaEngine.returnConnection();
    }
  }

  List<TaskComment> getTaskComments(String taskId)
      throws TaskNotFoundException, NotAuthorizedOnWorkbasketException {

    try {

      taskanaEngine.openConnection();

      taskService.getTask(taskId);

      List<TaskComment> taskComments = taskService.createTaskCommentQuery().taskIdIn(taskId).list();

      if (taskComments.isEmpty() && LOGGER.isDebugEnabled()) {
        LOGGER.debug("getTaskComments() found no comments for the provided taskId");
      }

      return taskComments;

    } finally {
      taskanaEngine.returnConnection();
    }
  }

  TaskComment getTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException,
          TaskNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnWorkbasketException {

    TaskCommentImpl result;

    verifyTaskCommentIdIsNotNullOrEmpty(taskCommentId);

    try {

      taskanaEngine.openConnection();

      result = taskCommentMapper.findById(taskCommentId);

      if (result == null) {
        throw new TaskCommentNotFoundException(taskCommentId);
      }

      taskService.getTask(result.getTaskId());

      if (taskanaEngine.getEngine().getConfiguration().isAddAdditionalUserInfo()) {
        User creator = userMapper.findById(result.getCreator());
        if (creator != null) {
          result.setCreatorFullName(creator.getFullName());
        }
      }

      return result;

    } finally {
      taskanaEngine.returnConnection();
    }
  }

  private void checkModifiedHasNotChanged(
      TaskComment oldTaskComment, TaskComment taskCommentImplToUpdate) throws ConcurrencyException {

    if (!oldTaskComment.getModified().equals(taskCommentImplToUpdate.getModified())) {
      throw new ConcurrencyException(taskCommentImplToUpdate.getId());
    }
  }

  private void initDefaultTaskCommentValues(TaskCommentImpl taskCommentImplToCreate) {

    Instant now = Instant.now();

    taskCommentImplToCreate.setId(
        IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_COMMENT));
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
