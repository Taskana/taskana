package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.MismatchedRoleException;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryQuery;
import pro.taskana.simplehistory.impl.task.TaskHistoryEventMapper;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;
import pro.taskana.user.api.models.User;
import pro.taskana.user.internal.UserMapper;

/** This is the implementation of TaskanaHistory. */
@Slf4j
public class SimpleHistoryServiceImpl implements TaskanaHistory {

  private TaskanaHistoryEngineImpl taskanaHistoryEngine;
  private TaskHistoryEventMapper taskHistoryEventMapper;
  private WorkbasketHistoryEventMapper workbasketHistoryEventMapper;
  private ClassificationHistoryEventMapper classificationHistoryEventMapper;
  private UserMapper userMapper;

  public void initialize(TaskanaEngine taskanaEngine) {

    this.taskanaHistoryEngine = getTaskanaEngine(taskanaEngine);

    if (log.isDebugEnabled()) {
      log.debug(
          "Simple history service implementation initialized with schemaName: {} ",
          taskanaEngine.getConfiguration().getSchemaName());
    }

    this.taskHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(TaskHistoryEventMapper.class);
    this.workbasketHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(WorkbasketHistoryEventMapper.class);
    this.classificationHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(ClassificationHistoryEventMapper.class);
    this.userMapper = taskanaHistoryEngine.getSqlSession().getMapper(UserMapper.class);
  }

  @Override
  public void create(TaskHistoryEvent event) {
    try {
      taskanaHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      taskHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      log.error("Error while inserting task history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public void create(WorkbasketHistoryEvent event) {
    try {
      taskanaHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      workbasketHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      log.error("Error while inserting workbasket history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public void create(ClassificationHistoryEvent event) {
    try {
      taskanaHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      classificationHistoryEventMapper.insert(event);
    } catch (SQLException e) {
      log.error("Error while inserting classification history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, MismatchedRoleException {
    taskanaHistoryEngine.checkRoleMembership(TaskanaRole.ADMIN);

    if (taskIds == null) {
      throw new InvalidArgumentException("List of taskIds must not be null.");
    }

    try {
      taskanaHistoryEngine.openConnection();
      taskHistoryEventMapper.deleteMultipleByTaskIds(taskIds);
    } catch (SQLException e) {
      log.error("Caught exception while trying to delete history events", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  public TaskHistoryEvent getTaskHistoryEvent(String historyEventId)
      throws TaskanaHistoryEventNotFoundException {
    TaskHistoryEvent resultEvent = null;
    try {
      taskanaHistoryEngine.openConnection();
      resultEvent = taskHistoryEventMapper.findById(historyEventId);

      if (resultEvent == null) {
        throw new TaskanaHistoryEventNotFoundException(historyEventId);
      }

      if (taskanaHistoryEngine.getConfiguration().isAddAdditionalUserInfo()) {
        User user = userMapper.findById(resultEvent.getUserId());
        if (user != null) {
          resultEvent.setUserLongName(user.getLongName());
        }
      }
      return resultEvent;

    } catch (SQLException e) {
      log.error("Caught exception while trying to retrieve a history event", e);
      return resultEvent;
    } finally {
      taskanaHistoryEngine.returnConnection();
    }
  }

  public TaskHistoryQuery createTaskHistoryQuery() {
    return new TaskHistoryQueryImpl(taskanaHistoryEngine);
  }

  public WorkbasketHistoryQuery createWorkbasketHistoryQuery() {
    return new WorkbasketHistoryQueryImpl(taskanaHistoryEngine);
  }

  public ClassificationHistoryQuery createClassificationHistoryQuery() {
    return new ClassificationHistoryQueryImpl(taskanaHistoryEngine);
  }

  /*
   * ATTENTION: This method exists for testing purposes.
   */
  TaskanaHistoryEngineImpl getTaskanaEngine(TaskanaEngine taskanaEngine) {
    return TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngine);
  }
}
