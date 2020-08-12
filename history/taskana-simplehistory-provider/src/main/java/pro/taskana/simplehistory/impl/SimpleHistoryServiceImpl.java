package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
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

/** This is the implementation of TaskanaHistory. */
public class SimpleHistoryServiceImpl implements TaskanaHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHistoryServiceImpl.class);
  private TaskanaHistoryEngineImpl taskanaHistoryEngine;
  private TaskHistoryEventMapper taskHistoryEventMapper;
  private WorkbasketHistoryEventMapper workbasketHistoryEventMapper;
  private ClassificationHistoryEventMapper classificationHistoryEventMapper;

  public void initialize(TaskanaEngine taskanaEngine) {

    this.taskanaHistoryEngine = getTaskanaEngine(taskanaEngine.getConfiguration());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "Simple history service implementation initialized with schemaName: {} ",
          taskanaEngine.getConfiguration().getSchemaName());
    }

    this.taskHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(TaskHistoryEventMapper.class);
    this.workbasketHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(WorkbasketHistoryEventMapper.class);
    this.classificationHistoryEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(ClassificationHistoryEventMapper.class);
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
      LOGGER.error("Error while inserting task history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
      LOGGER.debug("Exit from create(TaskHistoryEvent event). Returning object = {}.", event);
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
      LOGGER.error("Error while inserting workbasket history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
      LOGGER.debug("Exit from create(WorkbasketHistoryEvent event). Returning object = {}.", event);
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
      LOGGER.error("Error while inserting classification history event into database", e);
    } finally {
      taskanaHistoryEngine.returnConnection();
      LOGGER.debug(
          "Exit from create(ClassificationHistoryEvent event). Returning object = {}.", event);
    }
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds)
      throws InvalidArgumentException, NotAuthorizedException {

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to deleteHistoryEventsByTaskIds(taskIds = {})", taskIds);
    }

    taskanaHistoryEngine.checkRoleMembership(TaskanaRole.ADMIN);

    if (taskIds == null) {
      throw new InvalidArgumentException("List of taskIds must not be null.");
    }

    try {
      taskanaHistoryEngine.openConnection();

      taskHistoryEventMapper.deleteMultipleByTaskIds(taskIds);

    } catch (SQLException e) {
      LOGGER.error("Caught exception while trying to delete history events", e);
    } finally {
      LOGGER.debug("exit from deleteHistoryEventsByTaskIds()");
      taskanaHistoryEngine.returnConnection();
    }
  }

  public TaskHistoryEvent getTaskHistoryEvent(String historyEventId)
      throws TaskanaHistoryEventNotFoundException {
    LOGGER.debug("entry to getTaskHistoryEvent (id = {})", historyEventId);
    TaskHistoryEvent resultEvent = null;
    try {
      taskanaHistoryEngine.openConnection();
      resultEvent = taskHistoryEventMapper.findById(historyEventId);

      if (resultEvent == null) {
        throw new TaskanaHistoryEventNotFoundException(
            historyEventId,
            String.format("TaskHistoryEvent for id %s was not found", historyEventId));
      }

      return resultEvent;

    } catch (SQLException e) {
      LOGGER.error("Caught exception while trying to retrieve a history event", e);
      return resultEvent;
    } finally {
      taskanaHistoryEngine.returnConnection();
      LOGGER.debug("exit from getHistoryEvent(). Returning result {} ", resultEvent);
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
  TaskanaHistoryEngineImpl getTaskanaEngine(TaskanaEngineConfiguration taskanaEngineConfiguration) {
    return TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineConfiguration);
  }
}
