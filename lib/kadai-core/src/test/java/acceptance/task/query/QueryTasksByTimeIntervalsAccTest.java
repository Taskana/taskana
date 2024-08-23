package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.TaskSummary;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query tasks with sorting" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByTimeIntervalsAccTest extends AbstractAccTest {
  private static SortDirection asc = SortDirection.ASCENDING;

  QueryTasksByTimeIntervalsAccTest() {
    super();
  }

  @Nested
  class TimeIntervalTest {

    @WithAccessId(user = "admin")
    @Test
    void testCreatedWithin2Intervals() {
      TaskService taskService = kadaiEngine.getTaskService();

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

      assertThat(results).hasSize(62);
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
      TaskService taskService = kadaiEngine.getTaskService();

      TimeInterval interval1 = new TimeInterval(null, getInstant("2018-01-29T15:55:17"));

      List<TaskSummary> results =
          taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

      assertThat(results).hasSize(40);
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
      TaskService taskService = kadaiEngine.getTaskService();

      TimeInterval interval1 = new TimeInterval(getInstant("2018-01-29T15:55:17"), null);

      List<TaskSummary> results =
          taskService.createTaskQuery().createdWithin(interval1).orderByCreated(asc).list();

      assertThat(results).hasSize(62);
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
      TaskService taskService = kadaiEngine.getTaskService();

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

      assertThat(results).hasSize(45);
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
      TaskService taskService = kadaiEngine.getTaskService();

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
      TaskService taskService = kadaiEngine.getTaskService();

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
      TaskService taskService = kadaiEngine.getTaskService();

      TimeInterval interval =
          new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
      List<TaskSummary> results =
          taskService.createTaskQuery().plannedWithin(interval).orderByPlanned(asc).list();

      assertThat(results).hasSize(94);
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
      TaskService taskService = kadaiEngine.getTaskService();

      TimeInterval interval =
          new TimeInterval(getInstant("2018-01-29T15:55:00"), getInstant("2018-01-30T15:55:22"));
      List<TaskSummary> results =
          taskService.createTaskQuery().dueWithin(interval).orderByPlanned(asc).list();

      assertThat(results).hasSize(94);
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

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class Received {
      @WithAccessId(user = "admin")
      @Test
      void should_ReturnCorrectResults_When_QueryingForReceivedWithUpperBoundTimeInterval() {
        List<TaskSummary> results =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(new TimeInterval(null, Instant.parse("2018-01-29T15:55:20Z")))
                .list();
        long resultCount =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(new TimeInterval(null, Instant.parse("2018-01-29T15:55:20Z")))
                .count();
        assertThat(results).hasSize(22);
        assertThat(resultCount).isEqualTo(22);
      }

      @WithAccessId(user = "admin")
      @Test
      void should_ReturnCorrectResults_When_QueryingForReceivedWithLowerBoundTimeInterval() {
        List<TaskSummary> results =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(new TimeInterval(Instant.parse("2018-01-29T15:55:20Z"), null))
                .list();
        long resultCount =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(new TimeInterval(Instant.parse("2018-01-29T15:55:20Z"), null))
                .count();
        assertThat(results).hasSize(50);
        assertThat(resultCount).isEqualTo(50);
      }

      @WithAccessId(user = "admin")
      @Test
      void should_ReturnCorrectResults_When_QueryingForReceivedWithMultipleTimeIntervals() {
        long resultCount =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(
                    new TimeInterval(null, Instant.parse("2018-01-29T15:55:20Z")),
                    new TimeInterval(Instant.parse("2018-01-29T15:55:22Z"), null))
                .count();

        long resultCount2 =
            kadaiEngine
                .getTaskService()
                .createTaskQuery()
                .receivedWithin(
                    new TimeInterval(
                        Instant.parse("2018-01-29T15:55:25Z"),
                        Instant.parse("2018-01-29T15:55:30Z")),
                    new TimeInterval(
                        Instant.parse("2018-01-29T15:55:18Z"),
                        Instant.parse("2018-01-29T15:55:21Z")))
                .count();
        assertThat(resultCount).isEqualTo(70);
        assertThat(resultCount2).isEqualTo(4);
      }
    }
  }
}
