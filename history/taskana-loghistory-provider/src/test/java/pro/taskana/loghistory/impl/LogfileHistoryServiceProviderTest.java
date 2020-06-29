package pro.taskana.loghistory.impl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

class LogfileHistoryServiceProviderTest {

  static ObjectMapper objectMapper = new ObjectMapper();
  LogfileHistoryServiceProvider logfileHistoryServiceProvider = new LogfileHistoryServiceProvider();
  TestLogger logger = TestLoggerFactory.getTestLogger("AUDIT");
  @Mock TaskanaEngine taskanaEngine;

  @AfterAll
  public static void clearLoggers() {
    TestLoggerFactory.clear();
  }

  @BeforeAll
  public static void setupObjectMapper() {
    objectMapper.registerModule(new JavaTimeModule());
  }

  @Test
  void should_LogEventAsJson_When_CreateIsCalled() throws Exception {

    logfileHistoryServiceProvider.initialize(taskanaEngine);
    TaskanaHistoryEvent eventToBeLogged = new TaskanaHistoryEvent();
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

    logfileHistoryServiceProvider.create(eventToBeLogged);

    String logMessage = logger.getLoggingEvents().asList().get(0).getMessage();

    TaskanaHistoryEvent deserializedEventFromLogMessage =
        objectMapper.readValue(logMessage, TaskanaHistoryEvent.class);

    assertThat(eventToBeLogged).isEqualTo(deserializedEventFromLogMessage);
  }
}
