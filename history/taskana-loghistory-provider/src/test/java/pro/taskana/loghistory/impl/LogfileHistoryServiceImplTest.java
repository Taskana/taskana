package pro.taskana.loghistory.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

class LogfileHistoryServiceImplTest {

  static ObjectMapper objectMapper = new ObjectMapper();
  LogfileHistoryServiceImpl logfileHistoryServiceImpl = new LogfileHistoryServiceImpl();
  TestLogger logger = TestLoggerFactory.getTestLogger("AUDIT");
  @Mock TaskanaEngine taskanaEngine;

  @AfterEach
  public void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @BeforeAll
  public static void setupObjectMapper() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void should_LogTaskEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceImpl.initialize(taskanaEngine);
    TaskHistoryEvent eventToBeLogged = new TaskHistoryEvent();
    eventToBeLogged.setId("someId");
    eventToBeLogged.setUserId("someUser");
    eventToBeLogged.setEventType("TASK_CREATED");
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

    String logMessage = logger.getLoggingEvents().asList().get(0).getMessage();

    TaskHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, TaskHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }

  @Test
  void should_LogWorkbasketEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceImpl.initialize(taskanaEngine);
    WorkbasketHistoryEvent eventToBeLogged = new WorkbasketHistoryEvent();
    eventToBeLogged.setId("someId");
    eventToBeLogged.setUserId("someUser");
    eventToBeLogged.setEventType("TASK_CREATED");
    eventToBeLogged.setDomain("DOMAIN_A");
    eventToBeLogged.setCreated(Instant.now());
    eventToBeLogged.setKey("someWorkbasketKey");
    eventToBeLogged.setDetails("someDetails");

    logfileHistoryServiceImpl.create(eventToBeLogged);

    String logMessage = logger.getLoggingEvents().asList().get(0).getMessage();

    WorkbasketHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, WorkbasketHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }
}
