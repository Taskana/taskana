package pro.taskana.loghistory.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEventType;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;

class LogfileHistoryServiceImplTest {

  static TaskanaEngine taskanaEngineMock;
  private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
  private final LogfileHistoryServiceImpl logfileHistoryServiceImpl =
      new LogfileHistoryServiceImpl();
  private final TestLogger logger = TestLoggerFactory.getTestLogger("AUDIT");

  @AfterEach
  public void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @BeforeAll
  public static void setupObjectMapper() {
    TaskanaConfiguration taskanaConfiguration = Mockito.mock(TaskanaConfiguration.class);
    taskanaEngineMock = Mockito.mock(TaskanaEngine.class);
    Mockito.when(taskanaEngineMock.getConfiguration()).thenReturn(taskanaConfiguration);
    Mockito.when(taskanaConfiguration.getLogHistoryLoggerName()).thenReturn("AUDIT");
  }

  @Test
  void should_LogTaskEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceImpl.initialize(taskanaEngineMock);
    TaskHistoryEvent eventToBeLogged = new TaskHistoryEvent();
    eventToBeLogged.setId("someId");
    eventToBeLogged.setUserId("someUser");
    eventToBeLogged.setEventType(TaskHistoryEventType.CREATED.getName());
    eventToBeLogged.setDomain("DOMAIN_A");
    eventToBeLogged.setCreated(Instant.now());
    eventToBeLogged.setNewValue("someNewValue");
    eventToBeLogged.setOldValue("someOldValue");
    eventToBeLogged.setBusinessProcessId("someBusinessProcessId");
    eventToBeLogged.setWorkbasketKey("someWorkbasketKey");
    eventToBeLogged.setTaskClassificationKey("someTaskClassificationKey");
    eventToBeLogged.setTaskClassificationCategory("someTaskClassificationCategory");
    eventToBeLogged.setDetails("someDetails");

    logfileHistoryServiceImpl.create(eventToBeLogged);

    String logMessage = logger.getLoggingEvents().get(0).getMessage();

    TaskHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, TaskHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }

  @Test
  void should_LogWorkbasketEventAsJson_When_CreateIsCalled() throws Exception {
    logfileHistoryServiceImpl.initialize(taskanaEngineMock);
    WorkbasketHistoryEvent eventToBeLogged = new WorkbasketHistoryEvent();
    eventToBeLogged.setId("someId");
    eventToBeLogged.setUserId("someUser");
    eventToBeLogged.setEventType(WorkbasketHistoryEventType.CREATED.getName());
    eventToBeLogged.setDomain("DOMAIN_A");
    eventToBeLogged.setCreated(Instant.now());
    eventToBeLogged.setKey("someWorkbasketKey");
    eventToBeLogged.setDetails("someDetails");

    logfileHistoryServiceImpl.create(eventToBeLogged);

    String logMessage = logger.getLoggingEvents().get(0).getMessage();

    WorkbasketHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, WorkbasketHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }

  @Test
  void should_LogClassificationEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceImpl.initialize(taskanaEngineMock);
    ClassificationHistoryEvent eventToBeLogged = new ClassificationHistoryEvent();
    eventToBeLogged.setId("someId");
    eventToBeLogged.setUserId("someUser");
    eventToBeLogged.setEventType(ClassificationHistoryEventType.CREATED.getName());
    eventToBeLogged.setDomain("DOMAIN_A");
    eventToBeLogged.setCreated(Instant.now());
    eventToBeLogged.setKey("someClassificationKey");
    eventToBeLogged.setDetails("someDetails");

    logfileHistoryServiceImpl.create(eventToBeLogged);

    String logMessage = logger.getLoggingEvents().get(0).getMessage();

    ClassificationHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, ClassificationHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }
}
