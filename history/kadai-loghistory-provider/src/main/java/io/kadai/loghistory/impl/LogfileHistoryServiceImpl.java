package io.kadai.loghistory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogfileHistoryServiceImpl implements KadaiHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogfileHistoryServiceImpl.class);
  private ObjectMapper objectMapper;
  private Logger historyLogger;

  @Override
  public void initialize(KadaiEngine kadaiEngine) {

    objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    String historyLoggerName = kadaiEngine.getConfiguration().getLogHistoryLoggerName();

    if (historyLoggerName != null) {
      historyLogger = LoggerFactory.getLogger(historyLoggerName);
    } else {
      historyLogger = LOGGER;
    }

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
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
      throw new SystemException("Caught exception while serializing history event to JSON ", e);
    }
  }

  @Override
  public void create(WorkbasketHistoryEvent event) {

    try {
      if (historyLogger.isInfoEnabled()) {
        historyLogger.info(objectMapper.writeValueAsString(event));
      }
    } catch (JsonProcessingException e) {
      throw new SystemException("Caught exception while serializing history event to JSON ", e);
    }
  }

  @Override
  public void create(ClassificationHistoryEvent event) {

    try {
      if (historyLogger.isInfoEnabled()) {
        historyLogger.info(objectMapper.writeValueAsString(event));
      }
    } catch (JsonProcessingException e) {
      throw new SystemException("Caught exception while serializing history event to JSON ", e);
    }
  }

  @Override
  public void deleteHistoryEventsByTaskIds(List<String> taskIds) {
    throw new UnsupportedOperationException("HistoryLogger is not supposed to delete events");
  }
}
