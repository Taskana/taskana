package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskSummary;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByTimeIntervalsAccTest extends AbstractAccTest {

  private static SortDirection asc = SortDirection.ASCENDING;

  QueryTasksByTimeIntervalsAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
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

    assertThat(results.size(), equalTo(40));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertTrue(interval1.contains(cr) || interval2.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getCreated().isAfter(taskSummary.getCreated()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testCreatedBefore() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 = new TimeInterval(null, getInstant("2018-01-29T15:55:17"));

    List<TaskSummary> results =
        taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

    assertThat(results.size(), equalTo(37));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertTrue(interval1.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getCreated().isAfter(taskSummary.getCreated()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testCreatedAfter() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval1 = new TimeInterval(getInstant("2018-01-29T15:55:17"), null);

    List<TaskSummary> results =
        taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

    assertThat(results.size(), equalTo(38));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCreated();
      assertTrue(interval1.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getCreated().isAfter(taskSummary.getCreated()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
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

    assertThat(results.size(), equalTo(25));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getClaimed();
      assertTrue(interval1.contains(cr) || interval2.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getClaimed().isAfter(taskSummary.getClaimed()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testCompletedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T16:55:23"), getInstant("2018-01-30T16:55:25"));
    List<TaskSummary> results =
        taskService.createTaskQuery().completedWithin(interval).orderByCompleted(asc).list();

    assertThat(results.size(), equalTo(5));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getCompleted();
      assertTrue(interval.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getCompleted().isAfter(taskSummary.getCompleted()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testModifiedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-30T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().modifiedWithin(interval).orderByModified(asc).list();

    assertThat(results.size(), equalTo(6));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getModified();
      assertTrue(interval.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getModified().isAfter(taskSummary.getModified()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testPlannedWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().plannedWithin(interval).orderByPlanned(asc).list();

    assertThat(results.size(), equalTo(71));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getPlanned();
      assertTrue(interval.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getPlanned().isAfter(taskSummary.getPlanned()));
      }
      previousSummary = taskSummary;
    }
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testDueWithin() {
    TaskService taskService = taskanaEngine.getTaskService();

    TimeInterval interval =
        new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
    List<TaskSummary> results =
        taskService.createTaskQuery().dueWithin(interval).orderByPlanned(asc).list();

    assertThat(results.size(), equalTo(71));
    TaskSummary previousSummary = null;
    for (TaskSummary taskSummary : results) {
      Instant cr = taskSummary.getDue();
      assertTrue(interval.contains(cr));

      if (previousSummary != null) {
        assertFalse(previousSummary.getPlanned().isAfter(taskSummary.getPlanned()));
      }
      previousSummary = taskSummary;
    }
  }
}
