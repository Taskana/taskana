package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration.Builder;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.util.IdGenerator;
import io.kadai.common.internal.util.Pair;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.jobs.HistoryCleanupJob;
import io.kadai.spi.history.api.KadaiHistory;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import io.kadai.testapi.KadaiConfigurationModifier;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.WithServiceProvider;
import io.kadai.testapi.security.WithAccessId;
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

@WithServiceProvider(
    serviceProviderInterface = KadaiHistory.class,
    serviceProviders = SimpleHistoryServiceImpl.class)
@KadaiIntegrationTest
class HistoryCleanupJobAccTest {

  @KadaiInject KadaiEngine kadaiEngine;

  SimpleHistoryServiceImpl historyService;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(kadaiEngine);
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
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "someUserId",
            "someDetails",
            Instant.now().minus(20, ChronoUnit.DAYS),
            "sameParentId");
    historyService.create(eventToBeCleaned);
    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "someUserId",
            "someDetails",
            Instant.now().minus(5, ChronoUnit.DAYS),
            "sameParentId");
    historyService.create(eventToBeCleaned2);

    HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
    job.run();

    assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(2);
  }

  private TaskHistoryEvent createTaskHistoryEvent(
      String taskId,
      String type,
      String userid,
      String details,
      Instant created,
      String parentBusinessProcessId) {
    TaskHistoryEvent historyEvent = new TaskHistoryEvent();
    historyEvent.setId(IdGenerator.generateWithPrefix(IdGenerator.ID_PREFIX_TASK_HISTORY_EVENT));
    historyEvent.setUserId(userid);
    historyEvent.setDetails(details);
    historyEvent.setTaskId(taskId);
    historyEvent.setEventType(type);
    historyEvent.setCreated(created);
    historyEvent.setParentBusinessProcessId(parentBusinessProcessId);
    return historyEvent;
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class SimpleHistoryCleanupJobAllCompletedSameParentBusinessIsTrue
      implements KadaiConfigurationModifier {
    @KadaiInject KadaiEngine kadaiEngine;
    SimpleHistoryServiceImpl historyService;

    @Override
    public Builder modify(Builder builder) {
      return builder.simpleHistoryCleanupJobAllCompletedSameParentBusiness(true);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(kadaiEngine);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_CleanHistoryEventsUntilDate_When_SameParentBusinessTrueAndEventsQualified()
        throws Exception {

      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CANCELLED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned4);
      TaskHistoryEvent eventToBeCleaned5 =
          createTaskHistoryEvent(
              "taskId3",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned5);
      TaskHistoryEvent eventToBeCleaned6 =
          createTaskHistoryEvent(
              "taskId3",
              TaskHistoryEventType.DELETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned6);
      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isZero();
    }

    @Test
    @WithAccessId(user = "admin")
    void should_NotCleanHistoryEvents_When_SameParentBusinessTrueAndActiveTaskInParentBusiness()
        throws Exception {
      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(1, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);

      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
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
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(3, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CANCELLED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(5, ChronoUnit.DAYS),
              "sameParentId");
      historyService.create(eventToBeCleaned4);

      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(4);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_NotCleanEvents_When_NoCreatedEventsForParentBusinessProcessIdExist()
        throws Exception {
      TaskHistoryEvent toBeIgnored1 =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.CANCELLED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored1");
      historyService.create(toBeIgnored1);
      TaskHistoryEvent toBeIgnored2 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.COMPLETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored2");
      historyService.create(toBeIgnored2);
      TaskHistoryEvent toBeIgnored3 =
          createTaskHistoryEvent(
              "taskId3",
              TaskHistoryEventType.TERMINATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "toBeIgnored3");
      historyService.create(toBeIgnored3);

      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
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

      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);

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
                                pair.getLeft(),
                                pair.getRight().getName(),
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
      implements KadaiConfigurationModifier {
    @KadaiInject KadaiEngine kadaiEngine;
    SimpleHistoryServiceImpl historyService;

    @Override
    public Builder modify(Builder builder) {
      return builder.simpleHistoryCleanupJobAllCompletedSameParentBusiness(false);
    }

    @WithAccessId(user = "businessadmin")
    @BeforeAll
    void setup() throws Exception {
      historyService = new SimpleHistoryServiceImpl();
      historyService.initialize(kadaiEngine);
    }

    @Test
    @WithAccessId(user = "admin")
    void should_CleanHistoryEventsUntilDate_When_SameParentBusinessFalse() throws Exception {
      TaskHistoryEvent eventToBeCleaned =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "someParentId1");
      historyService.create(eventToBeCleaned);
      TaskHistoryEvent eventToBeCleaned2 =
          createTaskHistoryEvent(
              "taskId1",
              TaskHistoryEventType.COMPLETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "someParentId1");
      historyService.create(eventToBeCleaned2);
      TaskHistoryEvent eventToBeCleaned3 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "someParentId2");
      historyService.create(eventToBeCleaned3);
      TaskHistoryEvent eventToBeCleaned4 =
          createTaskHistoryEvent(
              "taskId2",
              TaskHistoryEventType.TERMINATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(5, ChronoUnit.DAYS),
              "someParentId2");
      historyService.create(eventToBeCleaned4);
      TaskHistoryEvent eventToBeCleaned5 =
          createTaskHistoryEvent(
              "taskId3",
              TaskHistoryEventType.CREATED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "someParentId3");
      historyService.create(eventToBeCleaned5);
      TaskHistoryEvent eventToBeCleaned6 =
          createTaskHistoryEvent(
              "taskId3",
              TaskHistoryEventType.DELETED.getName(),
              "someUserId",
              "someDetails",
              Instant.now().minus(20, ChronoUnit.DAYS),
              "someParentId3");
      historyService.create(eventToBeCleaned6);
      HistoryCleanupJob job = new HistoryCleanupJob(kadaiEngine, null, null);
      job.run();

      assertThat(historyService.createTaskHistoryQuery().count()).isEqualTo(2);
    }
  }
}
