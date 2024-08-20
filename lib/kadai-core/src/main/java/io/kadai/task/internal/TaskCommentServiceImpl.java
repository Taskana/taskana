package io.kadai.task.internal;

import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.ConcurrencyException;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.InternalKadaiEngine;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import io.kadai.task.api.exceptions.TaskCommentNotFoundException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.TaskComment;
import io.kadai.task.internal.models.TaskCommentImpl;
import io.kadai.user.api.models.User;
import io.kadai.user.internal.UserMapper;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TaskCommentServiceImpl {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskCommentServiceImpl.class);

  private final InternalKadaiEngine kadaiEngine;
  private final TaskServiceImpl taskService;
  private final TaskCommentMapper taskCommentMapper;
  private final TaskMapper taskMapper;
  private final UserMapper userMapper;

  TaskCommentServiceImpl(
      InternalKadaiEngine kadaiEngine,
      TaskCommentMapper taskCommentMapper,
      UserMapper userMapper,
      TaskMapper taskMapper,
      TaskServiceImpl taskService) {
    this.kadaiEngine = kadaiEngine;
    this.taskService = taskService;
    this.taskCommentMapper = taskCommentMapper;
    this.userMapper = userMapper;
    this.taskMapper = taskMapper;
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

    String userId = kadaiEngine.getEngine().getCurrentUserContext().getUserid();

    TaskCommentImpl taskCommentImplToUpdate = (TaskCommentImpl) taskCommentToUpdate;

    try {

      kadaiEngine.openConnection();

      TaskComment originalTaskComment = getTaskComment(taskCommentImplToUpdate.getId());

      if (originalTaskComment.getCreator().equals(userId)
              && taskCommentImplToUpdate.getCreator().equals(originalTaskComment.getCreator())
          || kadaiEngine.getEngine().isUserInRole(KadaiRole.ADMIN)
          || kadaiEngine.getEngine().isUserInRole(KadaiRole.TASK_ADMIN)) {

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
      kadaiEngine.returnConnection();
    }

    return taskCommentImplToUpdate;
  }

  TaskComment createTaskComment(TaskComment taskCommentToCreate)
      throws TaskNotFoundException, InvalidArgumentException, NotAuthorizedOnWorkbasketException {

    TaskCommentImpl taskCommentImplToCreate = (TaskCommentImpl) taskCommentToCreate;

    try {

      kadaiEngine.openConnection();

      taskService.getTask(taskCommentImplToCreate.getTaskId());

      validateNoneExistingTaskCommentId(taskCommentImplToCreate.getId());

      initDefaultTaskCommentValues(taskCommentImplToCreate);

      taskCommentMapper.insert(taskCommentImplToCreate);

      taskMapper.incrementNumberOfComments(taskCommentImplToCreate.getTaskId(), Instant.now());

    } finally {
      kadaiEngine.returnConnection();
    }

    return taskCommentImplToCreate;
  }

  void deleteTaskComment(String taskCommentId)
      throws TaskCommentNotFoundException,
          TaskNotFoundException,
          InvalidArgumentException,
          NotAuthorizedOnTaskCommentException,
          NotAuthorizedOnWorkbasketException {

    String userId = kadaiEngine.getEngine().getCurrentUserContext().getUserid();

    try {

      kadaiEngine.openConnection();

      TaskComment taskCommentToDelete = getTaskComment(taskCommentId);

      if (taskCommentToDelete.getCreator().equals(userId)
          || kadaiEngine.getEngine().isUserInRole(KadaiRole.ADMIN)
          || kadaiEngine.getEngine().isUserInRole(KadaiRole.TASK_ADMIN)) {

        taskCommentMapper.delete(taskCommentId);
        taskMapper.decrementNumberOfComments(taskCommentToDelete.getTaskId(), Instant.now());

        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("taskComment {} deleted", taskCommentToDelete.getId());
        }

      } else {
        throw new NotAuthorizedOnTaskCommentException(userId, taskCommentToDelete.getId());
      }

    } finally {
      kadaiEngine.returnConnection();
    }
  }

  List<TaskComment> getTaskComments(String taskId)
      throws TaskNotFoundException, NotAuthorizedOnWorkbasketException {

    try {

      kadaiEngine.openConnection();

      taskService.getTask(taskId);

      List<TaskComment> taskComments = taskService.createTaskCommentQuery().taskIdIn(taskId).list();

      if (taskComments.isEmpty() && LOGGER.isDebugEnabled()) {
        LOGGER.debug("getTaskComments() found no comments for the provided taskId");
      }

      return taskComments;

    } finally {
      kadaiEngine.returnConnection();
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

      kadaiEngine.openConnection();

      result = taskCommentMapper.findById(taskCommentId);

      if (result == null) {
        throw new TaskCommentNotFoundException(taskCommentId);
      }

      taskService.getTask(result.getTaskId());

      if (kadaiEngine.getEngine().getConfiguration().isAddAdditionalUserInfo()) {
        User creator = userMapper.findById(result.getCreator());
        if (creator != null) {
          result.setCreatorFullName(creator.getFullName());
        }
      }

      return result;

    } finally {
      kadaiEngine.returnConnection();
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

    String creator = kadaiEngine.getEngine().getCurrentUserContext().getUserid();
    if (kadaiEngine.getEngine().getConfiguration().isSecurityEnabled() && creator == null) {
      throw new SystemException(
          "KadaiSecurity is enabled, but the current UserId is"
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
