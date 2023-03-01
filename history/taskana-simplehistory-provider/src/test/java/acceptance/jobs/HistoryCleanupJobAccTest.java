package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.internal.jobs.ClassificationChangedJob;
import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.internal.jobs.AbstractTaskanaJob;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.internal.jobs.TaskRefreshJob;

@ExtendWith(JaasExtension.class)
class HistoryCleanupJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    resetDb(DataSourceGenerator.getSchemaName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CleanHistoryEventsUntilDate_When_SameParentBusinessTrueAndEventsQualified()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned3 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned3.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned3.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned4 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CANCELLED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned4.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned4.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned5 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId3",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned5.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned5.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned6 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId3",
            TaskHistoryEventType.TERMINATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned6.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned6.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);
    getHistoryService().create(eventToBeCleaned3);
    getHistoryService().create(eventToBeCleaned4);
    getHistoryService().create(eventToBeCleaned5);
    getHistoryService().create(eventToBeCleaned6);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(20);

    createTaskanaEngineWithNewConfig(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEventsUntilDate_When_MinimumAgeNotReached() throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(5, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(16);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(16);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEvents_When_SameParentBusinessTrueAndActiveTaskInParentBusiness()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned3 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned3.setCreated(Instant.now().minus(1, ChronoUnit.DAYS));
    eventToBeCleaned3.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);
    getHistoryService().create(eventToBeCleaned3);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);

    createTaskanaEngineWithNewConfig(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEvents_When_MinimumAgeOfOtherEndtstateEventInParentBusinessNotReached()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned3 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned3.setCreated(Instant.now().minus(3, ChronoUnit.DAYS));
    eventToBeCleaned3.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned4 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CANCELLED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned4.setCreated(Instant.now().minus(5, ChronoUnit.DAYS));
    eventToBeCleaned4.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);
    getHistoryService().create(eventToBeCleaned3);
    getHistoryService().create(eventToBeCleaned4);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(18);

    createTaskanaEngineWithNewConfig(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(18);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CleanHistoryEventsUntilDate_When_SameParentBusinessFalse() throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent eventToBeCleaned =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned3 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned3.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned3.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned4 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.CANCELLED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned4.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned4.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned5 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId3",
            TaskHistoryEventType.CREATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned5.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned5.setParentBusinessProcessId("sameParentId");

    TaskHistoryEvent eventToBeCleaned6 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId3",
            TaskHistoryEventType.TERMINATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned6.setCreated(Instant.now().minus(5, ChronoUnit.DAYS));
    eventToBeCleaned6.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);
    getHistoryService().create(eventToBeCleaned3);
    getHistoryService().create(eventToBeCleaned4);
    getHistoryService().create(eventToBeCleaned5);
    getHistoryService().create(eventToBeCleaned6);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(20);

    createTaskanaEngineWithNewConfig(false);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(16);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanEvents_When_NoCreatedEventsForParentBusinessProcessIdExist()
      throws Exception {
    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(14);

    TaskHistoryEvent toBeIgnored1 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId1",
            TaskHistoryEventType.CANCELLED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    toBeIgnored1.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    toBeIgnored1.setParentBusinessProcessId("toBeIgnored1");

    TaskHistoryEvent toBeIgnored2 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId2",
            TaskHistoryEventType.COMPLETED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    toBeIgnored2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    toBeIgnored2.setParentBusinessProcessId("toBeIgnored2");

    TaskHistoryEvent toBeIgnored3 =
        createTaskHistoryEvent(
            "wbKey1",
            "taskId3",
            TaskHistoryEventType.TERMINATED.getName(),
            "wbKey2",
            "someUserId",
            "someDetails");
    toBeIgnored3.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    toBeIgnored3.setParentBusinessProcessId("toBeIgnored3");

    getHistoryService().create(toBeIgnored1);
    getHistoryService().create(toBeIgnored2);
    getHistoryService().create(toBeIgnored3);

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);

    createTaskanaEngineWithNewConfig(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteOldHistoryCleanupJobs_When_InitializingSchedule() throws Exception {

    for (int i = 0; i < 10; i++) {
      ScheduledJob job = new ScheduledJob();
      job.setType(HistoryCleanupJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(TaskRefreshJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
      job.setType(ClassificationChangedJob.class.getName());
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper().findJobsToRun(Instant.now());

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> historyCleanupJobs =
        jobsToRun.stream()
            .filter(
                scheduledJob -> scheduledJob.getType().equals(HistoryCleanupJob.class.getName()))
            .collect(Collectors.toList());

    AbstractTaskanaJob.initializeSchedule(taskanaEngine, HistoryCleanupJob.class);

    jobsToRun = getJobMapper().findJobsToRun(Instant.now());

    assertThat(jobsToRun).doesNotContainAnyElementsOf(historyCleanupJobs);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest>
      should_CleanTaskHistoryEventsWithParentProcessIdEmptyOrNull_When_TaskCompleted()
          throws SQLException {
    Iterator<String> iterator = Arrays.asList("", null).iterator();
    String taskId1 = "taskId1";
    String taskId2 = "taskId2";
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
                        "someDetails"))
            .collect(Collectors.toList());

    createTaskanaEngineWithNewConfig(true);
    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);

    ThrowingConsumer<String> test =
        parentBusinessId -> {
          getHistoryService().deleteHistoryEventsByTaskIds(List.of(taskId1, taskId2));
          events.forEach(x -> x.setCreated(Instant.now().minus(20, ChronoUnit.DAYS)));
          events.forEach(x -> x.setParentBusinessProcessId(parentBusinessId));
          events.forEach(x -> getHistoryService().create(x));

          job.run();
          List<TaskHistoryEvent> eventsAfterCleanup =
              getHistoryService().createTaskHistoryQuery().taskIdIn(taskId1, taskId2).list();

          assertThat(eventsAfterCleanup)
              .extracting(TaskHistoryEvent::getTaskId)
              .containsExactly(taskId2);
        };

    return DynamicTest.stream(iterator, c -> "for parentBusinessProcessId = '" + c + "'", test);
  }

  private void createTaskanaEngineWithNewConfig(
      boolean taskCleanupJobAllCompletedSameParentBusiness) throws SQLException {

    TaskanaConfiguration tec =
        new TaskanaConfiguration.Builder(AbstractAccTest.taskanaConfiguration)
            .taskCleanupJobAllCompletedSameParentBusiness(
                taskCleanupJobAllCompletedSameParentBusiness)
            .build();
    initTaskanaEngine(tec);
  }
}
