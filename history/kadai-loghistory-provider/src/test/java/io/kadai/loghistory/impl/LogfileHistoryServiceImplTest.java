package io.kadai.loghistory.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEventType;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LogfileHistoryServiceImplTest {

  static KadaiEngine kadaiEngineMock;
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
    KadaiConfiguration kadaiConfiguration = Mockito.mock(KadaiConfiguration.class);
    kadaiEngineMock = Mockito.mock(KadaiEngine.class);
    Mockito.when(kadaiEngineMock.getConfiguration()).thenReturn(kadaiConfiguration);
    Mockito.when(kadaiConfiguration.getLogHistoryLoggerName()).thenReturn("AUDIT");
  }

  @Test
  void should_LogTaskEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceImpl.initialize(kadaiEngineMock);
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
    logfileHistoryServiceImpl.initialize(kadaiEngineMock);
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

    logfileHistoryServiceImpl.initialize(kadaiEngineMock);
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
