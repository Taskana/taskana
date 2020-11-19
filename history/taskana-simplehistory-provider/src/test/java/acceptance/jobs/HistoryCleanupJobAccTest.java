package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.ScheduledJob;
import pro.taskana.common.api.ScheduledJob.Type;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.jobs.HistoryCleanupJob;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;

@ExtendWith(JaasExtension.class)
class HistoryCleanupJobAccTest extends AbstractAccTest {

  @BeforeEach
  void before() throws Exception {
    resetDb(getSchemaName());
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CleanHistoryEventsUntilDate_When_SameParentBusinessTrueAndEventsQualified()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);

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

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(19);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEventsUntilDate_When_MinimumAgeNotReached() throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);

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

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(15);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(15);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEvents_When_SameParentBusinessTrueAndActiveTaskInParentBusiness()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);

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

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(16);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(16);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEvents_When_MinimumAgeOfOtherEndtstateEventInParentBusinessNotReached()
      throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);

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

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(17);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CleanHistoryEventsUntilDate_When_SameParentBusinessFalse() throws Exception {

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(13);

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

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(19);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createTaskHistoryQuery().count()).isEqualTo(15);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteOldHistoryCleanupJobs_When_InitializingSchedule() throws Exception {

    for (int i = 0; i < 10; i++) {
      ScheduledJob job = new ScheduledJob();
      job.setType(ScheduledJob.Type.HISTORYCLEANUPJOB);
      taskanaEngine.getJobService().createJob(job);
      job.setType(Type.UPDATETASKSJOB);
      taskanaEngine.getJobService().createJob(job);
      job.setType(Type.CLASSIFICATIONCHANGEDJOB);
      taskanaEngine.getJobService().createJob(job);
    }

    List<ScheduledJob> jobsToRun = getJobMapper().findJobsToRun();

    assertThat(jobsToRun).hasSize(30);

    List<ScheduledJob> historyCleanupJobs =
        jobsToRun.stream()
            .filter(scheduledJob -> scheduledJob.getType().equals(Type.HISTORYCLEANUPJOB))
            .collect(Collectors.toList());

    HistoryCleanupJob.initializeSchedule(taskanaEngine);

    jobsToRun = getJobMapper().findJobsToRun();

    assertThat(jobsToRun).doesNotContainAnyElementsOf(historyCleanupJobs);
  }
}
