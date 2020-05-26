package acceptance.jobs;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.HistoryCleanupJob;
import pro.taskana.simplehistory.impl.HistoryEventImpl;

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

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(13);

    HistoryEventImpl eventToBeCleaned =
        createHistoryEvent(
            "wbKey1", "taskId1", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    HistoryEventImpl eventToBeCleaned2 =
        createHistoryEvent(
            "wbKey1", "taskId2", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(15);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(13);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEventsUntilDate_When_SameParentBusinessTrueAndEventsNotQualified()
      throws Exception {

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(13);

    HistoryEventImpl eventToBeCleaned =
        createHistoryEvent(
            "wbKey1", "taskId1", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    HistoryEventImpl eventToBeCleaned2 =
        createHistoryEvent(
            "wbKey1", "taskId2", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(1, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    HistoryEventImpl eventToBeCleaned3 =
        createHistoryEvent(
            "wbKey1",
            "TKI:000000000000000000000000000000000001",
            "TASK_COMPLETED",
            "wbKey2",
            "someUserId",
            "someDetails");
    eventToBeCleaned3.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned3.setParentBusinessProcessId("PBPI21");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);
    getHistoryService().create(eventToBeCleaned3);

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(16);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(true);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(16);
  }

  @Test
  @WithAccessId(user = "admin")
  void should_CleanHistoryEventsUntilDate_When_SameParentBusinessFalse() throws Exception {

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(13);

    HistoryEventImpl eventToBeCleaned =
        createHistoryEvent(
            "wbKey1", "taskId1", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(20, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    HistoryEventImpl eventToBeCleaned2 =
        createHistoryEvent(
            "wbKey1", "taskId2", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned2.setCreated(Instant.now().minus(1, ChronoUnit.DAYS));
    eventToBeCleaned2.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);
    getHistoryService().create(eventToBeCleaned2);

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(15);

    taskanaEngine.getConfiguration().setTaskCleanupJobAllCompletedSameParentBusiness(false);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(14);
    assertThat(getHistoryService().getHistoryEvent(eventToBeCleaned2.getId())).isNotNull();
  }

  @Test
  @WithAccessId(user = "admin")
  void should_NotCleanHistoryEvents_When_MinimumAgeNotReached() throws Exception {

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(13);

    HistoryEventImpl eventToBeCleaned =
        createHistoryEvent(
            "wbKey1", "taskId1", "TASK_COMPLETED", "wbKey2", "someUserId", "someDetails");
    eventToBeCleaned.setCreated(Instant.now().minus(1, ChronoUnit.DAYS));
    eventToBeCleaned.setParentBusinessProcessId("sameParentId");

    getHistoryService().create(eventToBeCleaned);

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(14);

    HistoryCleanupJob job = new HistoryCleanupJob(taskanaEngine, null, null);
    job.run();

    assertThat(getHistoryService().createHistoryQuery().count()).isEqualTo(14);
  }
}
