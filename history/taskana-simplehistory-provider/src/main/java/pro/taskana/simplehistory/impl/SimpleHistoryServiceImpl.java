package pro.taskana.simplehistory.impl;

import java.sql.SQLException;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.history.api.TaskanaHistory;
import pro.taskana.history.api.events.TaskanaHistoryEvent;
import pro.taskana.simplehistory.impl.mappings.HistoryEventMapper;
import pro.taskana.simplehistory.impl.mappings.HistoryQueryMapper;
import pro.taskana.simplehistory.query.HistoryQuery;

/** This is the implementation of TaskanaHistory. */
public class SimpleHistoryServiceImpl implements TaskanaHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHistoryServiceImpl.class);
  private TaskanaHistoryEngineImpl taskanaHistoryEngine;
  private HistoryEventMapper historyEventMapper;
  private HistoryQueryMapper historyQueryMapper;

  @Override
  public void initialize(TaskanaEngineConfiguration taskanaEngineConfiguration) {
    try {
      this.taskanaHistoryEngine =
          TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngineConfiguration);
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug(
            "Simple history service implementation initialized with schemaName: {} ",
            taskanaEngineConfiguration.getSchemaName());
      }
    } catch (SQLException e) {
      LOGGER.error("There was an error creating Taskana history engine", e);
      e.printStackTrace();
    }
    this.historyEventMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(HistoryEventMapper.class);
    this.historyQueryMapper =
        this.taskanaHistoryEngine.getSqlSession().getMapper(HistoryQueryMapper.class);
  }

  @Override
  public void create(TaskanaHistoryEvent event) {
    try {
      taskanaHistoryEngine.openConnection();
      if (event.getCreated() == null) {
        Instant now = Instant.now();
        event.setCreated(now);
      }
      historyEventMapper.insert(event);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      taskanaHistoryEngine.returnConnection();
      LOGGER.debug("exit from create(TaskanaHistoryEvent event). Returning object = {}.", event);
    }
  }

  public HistoryQuery createHistoryQuery() {
    return new HistoryQueryImpl(taskanaHistoryEngine, historyQueryMapper);
  }
}
