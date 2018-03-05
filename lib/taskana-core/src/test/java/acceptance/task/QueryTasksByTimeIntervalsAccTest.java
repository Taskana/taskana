package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import org.h2.store.fs.FileUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.TimeInterval;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks with sorting" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksByTimeIntervalsAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryTasksByTimeIntervalsAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testCreatedWithin2Intervals()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval1 = new TimeInterval(
            getInstant("2018-01-29T15:55:10"),
            getInstant("2018-01-29T15:55:17"));
        TimeInterval interval2 = new TimeInterval(
            getInstant("2018-01-29T15:55:23"),
            getInstant("2018-01-29T15:55:25"));

        List<TaskSummary> results = taskService.createTaskQuery()
            .createdWithin(interval1, interval2)
            .orderByCreated(asc)
            .list();

        assertThat(results.size(), equalTo(40));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getCreated();
            Assert.assertTrue(interval1.contains(cr) || interval2.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getCreated().isAfter(taskSummary.getCreated()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testCreatedBefore()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval1 = new TimeInterval(
            null,
            getInstant("2018-01-29T15:55:17"));

        List<TaskSummary> results = taskService.createTaskQuery()
            .createdWithin(interval1)
            .orderByCreated(asc)
            .list();

        assertThat(results.size(), equalTo(36));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getCreated();
            Assert.assertTrue(interval1.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getCreated().isAfter(taskSummary.getCreated()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testCreatedAfter()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval1 = new TimeInterval(
            getInstant("2018-01-29T15:55:17"), null);

        List<TaskSummary> results = taskService.createTaskQuery()
            .createdWithin(interval1)
            .orderByCreated(asc)
            .list();

        assertThat(results.size(), equalTo(36));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getCreated();
            Assert.assertTrue(interval1.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getCreated().isAfter(taskSummary.getCreated()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testClaimedWithin2Intervals()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval1 = new TimeInterval(
            getInstant("2018-01-30T15:55:00"),
            getInstant("2018-01-30T15:55:10"));
        TimeInterval interval2 = new TimeInterval(
            getInstant("2018-01-30T15:55:23"),
            getInstant("2018-01-30T15:55:25"));

        List<TaskSummary> results = taskService.createTaskQuery()
            .claimedWithin(interval1, interval2)
            .orderByCreated(asc)
            .list();

        assertThat(results.size(), equalTo(24));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getClaimed();
            Assert.assertTrue(interval1.contains(cr) || interval2.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getClaimed().isAfter(taskSummary.getClaimed()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testCompletedWithin()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval = new TimeInterval(
            getInstant("2018-01-30T16:55:23"),
            getInstant("2018-01-30T16:55:25"));
        List<TaskSummary> results = taskService.createTaskQuery()
            .completedWithin(interval)
            .orderByCompleted(asc)
            .list();

        assertThat(results.size(), equalTo(5));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getCompleted();
            Assert.assertTrue(interval.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getCompleted().isAfter(taskSummary.getCompleted()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testModifiedWithin()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval = new TimeInterval(
            getInstant("2018-01-30T15:55:00"),
            getInstant("2018-01-30T15:55:22"));
        List<TaskSummary> results = taskService.createTaskQuery()
            .modifiedWithin(interval)
            .orderByModified(asc)
            .list();

        assertThat(results.size(), equalTo(6));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getModified();
            Assert.assertTrue(interval.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getModified().isAfter(taskSummary.getModified()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testPlannedWithin()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval = new TimeInterval(
            getInstant("2018-01-29T15:55:00"),
            getInstant("2018-01-30T15:55:22"));
        List<TaskSummary> results = taskService.createTaskQuery()
            .plannedWithin(interval)
            .orderByPlanned(asc)
            .list();

        assertThat(results.size(), equalTo(70));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getPlanned();
            Assert.assertTrue(interval.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getPlanned().isAfter(taskSummary.getPlanned()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testDueWithin()
        throws SQLException, NotAuthorizedException, InvalidArgumentException {
        TaskService taskService = taskanaEngine.getTaskService();

        TimeInterval interval = new TimeInterval(
            getInstant("2018-01-29T15:55:00"),
            getInstant("2018-01-30T15:55:22"));
        List<TaskSummary> results = taskService.createTaskQuery()
            .dueWithin(interval)
            .orderByPlanned(asc)
            .list();

        assertThat(results.size(), equalTo(70));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            Instant cr = taskSummary.getDue();
            Assert.assertTrue(interval.contains(cr));

            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getPlanned().isAfter(taskSummary.getPlanned()));
            }
            previousSummary = taskSummary;
        }
    }

    @AfterClass
    public static void cleanUpClass() {
        FileUtils.deleteRecursive("~/taskana-h2-data", true);
    }
}
