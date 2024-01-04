package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;
import pro.taskana.TaskanaConfiguration.Builder;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.util.IdGenerator;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.testapi.TaskanaConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.security.WithAccessId;

@WithServiceProvider(
    serviceProviderInterface = TaskanaHistory.class,
    serviceProviders = SimpleHistoryServiceImpl.class)
@TaskanaIntegrationTest
class HistoryCleanupJobAccTest {

  @TaskanaInject TaskanaEngine taskanaEngine;

  SimpleHistoryServiceImpl historyService;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(taskanaEngine);
  }

  @WithAccessId(user = "admin")
  @AfterEach
  void after() throws Exception {
    List<TaskHistoryEvent> taskHistoryEventList = historyService.createTaskHistoryQuery().list();
    List<String> taskIds = taskHistoryEventList.stream().map(TaskHistoryEvent::getTaskId).toList();
    if (!taskIds.isEmpty()) {
      historyService.deleteHistoryEventsByTaskIds(taskIds);
    }
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEventsUntilDate_When_MinimumAgeNotReached() throws Exception {
    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails",
            Instant.now().minus(20, ChronoUnit.DAYS),
            "sameParentId");
    historyService.create(eventToBeCleaned);
    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails",
            Instant.now().minus(5, ChronoUnit.DAYS),
            "sameParentId");
    historyService.create(eventToBeCleaned2);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(2);
  }

  private TaskHistoryEvent createTaskHistoryEvent(
      String workbasketKey,
      String taskId,
      String type,
      String previousWorkbasketId,
      String userid,
      String details,
      Instant created,
      String parentBusinessProcessId) {
    TaskHistoryEvent historyEvent = new TaskHistoryEvent();
    historyEvent.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT));
    historyEvent.setUserId(userid);
    historyEvent.setDetails(details);
    historyEvent.setWorkbasketKey(workbasketKey);
    historyEvent.setTaskId(taskId);
    historyEvent.setEventType(type);
    historyEvent.setOldValue(previousWorkbasketId);
    historyEvent.setCreated(created);
    historyEvent.setParentBusinessProcessId(parentBusinessProcessId);
    return historyEvent;
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class SimpleHistoryCleanupJobAllCompletedSameParentBusinessIsTrue
      implements TaskanaConfigurationModifier {
    @TaskanaInject TaskanaEngine taskanaEngine;
    SimpleHistoryServiceImpl historyService;

    @Override
    public Builder modify(Builder builder) {
      return builder.simpleHistoryCleanupJobAllCompletedSameParentBusiness(true);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(taskanaEngine);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_CleanHistoryEventsUntilDate_When_SameParentBusinessTrueAndEventsQualified()
        throws Exception {

      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CANCELLED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned4);

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isZero();
    }

    @Test
    @WithAccessId(user = "admin")
    void should_NotCleanHistoryEvents_When_SameParentBusinessTrueAndActiveTaskInParentBusiness()
        throws Exception {

      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(1, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(3);
    }

    @Test
    @WithAccessId(user = "admin")
    void
        should_NotCleanHistoryEvents_When_MinimumAgeOfOtherEndStateEventInParentBusinessNotReached()
            throws Exception {
      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(3, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CANCELLED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(5, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned4);

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(4);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_NotCleanEvents_When_NoCreatedEventsForParentBusinessProcessIdExist()
        throws Exception {
      TaskHistoryEvent toBeIgnored1 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.CANCELLED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored1");
      historyService.create(toBeIgnored1);
      TaskHistoryEvent toBeIgnored2 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.COMPLETED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored2");
      historyService.create(toBeIgnored2);
      TaskHistoryEvent toBeIgnored3 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId3",
              TaskHistoryEventType.TERMINATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored3");
      historyService.create(toBeIgnored3);

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(3);
    }

    @WithAccessId(user = "admin")
    @TestFactory
    Stream<DynamicTest>
        should_CleanTaskHistoryEventsWithParentProcessIdEmptyOrNull_When_TaskCompleted()
            throws SQLException {
      Iterator<String> iterator = Arrays.asList("", null).iterator();
      String taskId1 = "taskId1";
      String taskId2 = "taskId2";

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);

      ThrowingConsumer<String> test =
          parentBusinessId -> {
            List<TaskHistoryEvent> events =
                Stream.of(
                        Pair.of(taskId1, TaskHistoryEventType.CREATED),
                        Pair.of(taskId1, TaskHistoryEventType.COMPLETED),
                        Pair.of(taskId2, TaskHistoryEventType.CREATED))
                    .map(
                        pair ->
                            createTaskHistoryEvent(
                                "wbKey1",
                                pair.getLeft(),
                                pair.getRight().getName(),
                                "wbKey2",
                                "someUserId",
                                "someDetails",
                                Instant.now().minus(20, ChronoUnit.DAYS),
                                parentBusinessId))
                    .toList();
            historyService.deleteHistoryEventsByTaskIds(List.of(taskId1, taskId2));
            events.forEach(x -> x.setCreated(Instant.now().minus(20, ChronoUnit.DAYS)));
            events.forEach(x -> x.setParentBusinessProcessId(parentBusinessId));
            events.forEach(x -> historyService.create(x));

            job.run();
            List<TaskHistoryEvent> eventsAfterCleanup =
                historyService.createTaskHistoryQuery().taskIdIn(taskId1, taskId2).list();

            assertThat(eventsAfterCleanup)
                .extracting(TaskHistoryEvent::getTaskId)
                .containsExactly(taskId2);
          };

      return DynamicTest.stream(iterator, c -> "for parentBusinessProcessId = '" + c + "'", test);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class SimpleHistoryCleanupJobAllCompletedSameParentBusinessIsFalse
      implements TaskanaConfigurationModifier {
    @TaskanaInject TaskanaEngine taskanaEngine;
    SimpleHistoryServiceImpl historyService;

    @Override
    public Builder modify(Builder builder) {
      return builder.simpleHistoryCleanupJobAllCompletedSameParentBusiness(false);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(taskanaEngine);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_CleanHistoryEventsUntilDate_When_SameParentBusinessFalse() throws Exception {
      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "wbKey1",
              "taskId2",
              TaskHistoryEventType.TERMINATED.getName(),
              "wbKey2",
              "someUserId",
              "someDetails",
              Instant.now().minus(5, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned4);

      HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(2);
    }
  }
}
