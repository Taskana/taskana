package pro.taskana.loghistory.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

public class LogfileHistoryServiceImpl implements TaskanaHistory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogfileHistoryServiceImpl.class);
  private static final String TASKANA_PROPERTIES = "/taskana.properties";
  private static final String TASKANA_HISTORYLOGGER_NAME = "taskana.historylogger.name";
  private static Logger historyLogger;
  ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void initialize(TaskanaEngine taskanaEngine) {

    objectMapper.registerModule(new JavaTimeModule());

    String historyLoggerName =
        readPropertiesFromFile(TASKANA_PROPERTIES).getProperty(TASKANA_HISTORYLOGGER_NAME);

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
  public void deleteHistoryEventsByTaskIds(List<String> taskIds) {

    throw new UnsupportedOperationException("HistoryLogger is not supposed to delete events");
  }

  private Properties readPropertiesFromFile(String propertiesFile) {
    java.util.Properties props = new Properties();
    boolean loadFromClasspath = loadFromClasspath(propertiesFile);
    try {
      if (loadFromClasspath) {
        InputStream inputStream =
            TaskanaEngineConfiguration.class.getResourceAsStream(propertiesFile);
        if (inputStream == null) {
          LOGGER.error("taskana properties file {} was not found on classpath.", propertiesFile);
        } else {
          props.load(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
          LOGGER.debug("properties were loaded from file {} from classpath.", propertiesFile);
        }
      } else {
        props.load(new FileInputStream(propertiesFile));
        LOGGER.debug("properties were loaded from file {}.", propertiesFile);
      }
    } catch (IOException e) {
      LOGGER.error("caught IOException when processing properties file {}.", propertiesFile);
      throw new SystemException(
          "internal System error when processing properties file " + propertiesFile, e.getCause());
    }
    return props;
  }

  private boolean loadFromClasspath(String propertiesFile) {
    boolean loadFromClasspath = true;
    File f = new File(propertiesFile);
    if (f.exists() && !f.isDirectory()) {
      loadFromClasspath = false;
    }
    return loadFromClasspath;
  }
}
