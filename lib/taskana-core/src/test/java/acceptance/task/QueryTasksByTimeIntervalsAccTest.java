package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import helper.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByTimeIntervalsAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;

  QueryTasksByTimeIntervalsAccTest() {
    super();
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreatedWithin2Intervals() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 =
        new TimeInterval(getInstant("2018-01-29T15:55:10"), getInstant("2018-01-29T15:55:17"));
    TimeInterval interval2 =
        new TimeInterval(getInstant("2018-01-29T15:55:23"), getInstant("2018-01-29T15:55:25"));

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .createdWithin(interval1, interval2)
            .orderByCreated(asc)
            .list();

    assertThat(results).hasSize(53);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertThat(interval1.contains(cr) || interval2.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getCreated().isAfter(taskSummary.getCreated())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreatedBefore() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 = new TimeInterval(null, getInstant("2018-01-29T15:55:17"));

    List<TaskSummary> results =
        taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

    assertThat(results).hasSize(38);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertThat(interval1.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getCreated().isAfter(taskSummary.getCreated())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testCreatedAfter() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 = new TimeInterval(getInstant("2018-01-29T15:55:17"), null);

    List<TaskSummary> results =
        taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

    assertThat(results).hasSize(51);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertThat(interval1.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getCreated().isAfter(taskSummary.getCreated())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testClaimedWithin2Intervals() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 =
        new TimeInterval(getInstant("2018-01-30T15:55:00"), getInstant("2018-01-30T15:55:10"));
    TimeInterval interval2 =
        new TimeInterval(getInstant("2018-01-30T15:55:23"), getInstant("2018-01-30T15:55:25"));

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .claimedWithin(interval1, interval2)
            .orderByCreated(asc)
            .list();

    assertThat(results).hasSize(38);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getClaimed();
      assertThat(interval1.contains(cr) || interval2.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getClaimed().isAfter(taskSummary.getClaimed())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testCompletedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T16:55:23"), getInstant("2018-01-30T16:55:25"));
    List<TaskSummary> results =
        taskService.createTaskQuery().completedWithin(interval).orderByCompleted(asc).list();

    assertThat(results).hasSize(18);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCompleted();
      assertThat(interval.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getCompleted().isAfter(taskSummary.getCompleted())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testModifiedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().modifiedWithin(interval).orderByModified(asc).list();

    assertThat(results).hasSize(7);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getModified();
      assertThat(interval.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getModified().isAfter(taskSummary.getModified())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testPlannedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().plannedWithin(interval).orderByPlanned(asc).list();

    assertThat(results).hasSize(85);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getPlanned();
      assertThat(interval.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getPlanned().isAfter(taskSummary.getPlanned())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void testDueWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().dueWithin(interval).orderByPlanned(asc).list();

    assertThat(results).hasSize(85);
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getDue();
      assertThat(interval.contains(cr)).isTrue();

      if (previousSummary != null) {
        assertThat(previousSummary.getPlanned().isAfter(taskSummary.getPlanned())).isFalse();
      }
      previousSummary = taskSummary;
    }
  }
}
