package pro.taskana.loghistory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

@Slf4j
public class LogfileHistoryServiceImpl implements TaskanaHistory {

  public static final String TASKANA_HISTORY_LOGGER_NAME = "taskana.historylogger.name";
  private static final String JSON_EXCEPTION =
      "Caught exception while serializing history event to JSON ";
  private ObjectMapper objectMapper;
  private Logger historyLogger;

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {

    objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    String historyLoggerName =
        taskanaEngine.getConfiguration().getProperties().get(TASKANA_HISTORY_LOGGER_NAME);

    if (historyLoggerName != null) {
      historyLogger = LoggerFactory.getLogger(historyLoggerName);
    } else {
      historyLogger = log;
    }

    if (log.isDebugEnabled()) {
      log.debug(
          "LogfileHistoryServiceProvider initialized with name: {} ", historyLogger.getName());
    }
  }

  @Override
  public void create(TaskHistoryEvent event) {

    try {
      if (historyLogger.isInfoEnabled()) {
        historyLogger.info(objectMapper.writeValueAsString(event));
      }
    } catch (JsonProcessingException e) {
      throw new SystemException(JSON_EXCEPTION, e);
    }
  }

  @Override
  public void create(WorkbasketHistoryEvent event) {

    try {
      if (historyLogger.isInfoEnabled()) {
        historyLogger.info(objectMapper.writeValueAsString(event));
      }
    } catch (JsonProcessingException e) {
      throw new SystemException(JSON_EXCEPTION, e);
    }
  }

  @Override
  public void create(ClassificationHistoryEvent event) {

    try {
      if (historyLogger.isInfoEnabled()) {
        historyLogger.info(objectMapper.writeValueAsString(event));
      }
    } catch (JsonProcessingException e) {
      throw new SystemException(JSON_EXCEPTION, e);
    }
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds) {
    throw new UnsupportedOperationException("HistoryLogger is not supposed to delete events");
  }
}
