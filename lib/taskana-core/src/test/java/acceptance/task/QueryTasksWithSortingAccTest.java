package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
import pro.taskana.TaskState;
import pro.taskana.TaskSummary;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks with sorting" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksWithSortingAccTest extends AbstractAccTest {

    private static SortDirection asc = SortDirection.ASCENDING;
    private static SortDirection desc = SortDirection.DESCENDING;

    public QueryTasksWithSortingAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByModifiedAndDomain() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByModified(desc)
            .orderByDomain(null)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(!previousSummary.getModified().isBefore(taskSummary.getModified()));
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByDomainNameAndCreated() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByDomain(asc)
            .orderByName(asc)
            .orderByCreated(null)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            // System.out.println("domain: " + taskSummary.getDomain() + ", name: " + taskSummary.getName() + ",
            // created: " + taskSummary.getCreated());
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getDomain().compareToIgnoreCase(previousSummary.getDomain()) >= 0);
                if (taskSummary.getDomain().equals(previousSummary.getDomain())) {
                    Assert.assertTrue(taskSummary.getName().compareToIgnoreCase(previousSummary.getName()) >= 0);
                    if (taskSummary.getName().equals(previousSummary.getName())) {
                        Assert.assertTrue(!taskSummary.getCreated().isBefore(previousSummary.getCreated()));
                    }
                }
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByPorSystemNoteDueAndOwner() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystem(SortDirection.DESCENDING)
            .orderByNote(null)
            .orderByDue(null)
            .orderByOwner(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getSystem().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getSystem()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByPorSystemInstanceParentProcPlannedAndState() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceSystemInstance(desc)
            .orderByParentBusinessProcessId(asc)
            .orderByPlanned(asc)
            .orderByState(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getSystemInstance().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getSystemInstance()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByPorCompanyAndClaimed() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_3_2", "DOMAIN_B"))
            .orderByPrimaryObjectReferenceCompany(desc)
            .orderByClaimed(asc)
            .list();

        assertThat(results.size(), equalTo(25));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            // System.out.println("porCompany: " + taskSummary.getPrimaryObjRef().getCompany() + ", claimed: "
            // + taskSummary.getClaimed());
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getPrimaryObjRef().getCompany().compareToIgnoreCase(
                    previousSummary.getPrimaryObjRef().getCompany()) <= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortByWbKeyPrioPorValueAndCompleted() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .stateIn(TaskState.READY)
            .orderByWorkbasketKey(null)
            .workbasketIdIn("WBI:100000000000000000000000000000000015")
            .orderByPriority(desc)
            .orderByPrimaryObjectReferenceValue(asc)
            .orderByCompleted(desc)
            .list();

        assertThat(results.size(), equalTo(22));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getWorkbasketSummary().getKey().compareToIgnoreCase(
                    previousSummary.getWorkbasketSummary().getKey()) >= 0);
            }
            previousSummary = taskSummary;
        }
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1", "group_2"})
    @Test
    public void testSortBpIdClassificationIdDescriptionAndPorType() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<TaskSummary> results = taskService.createTaskQuery()
            .stateIn(TaskState.READY)
            .workbasketIdIn("WBI:100000000000000000000000000000000015")
            .orderByBusinessProcessId(asc)
            .orderByClassificationKey(null)
            .orderByPrimaryObjectReferenceType(SortDirection.DESCENDING)
            .list();

        assertThat(results.size(), equalTo(22));
        TaskSummary previousSummary = null;
        for (TaskSummary taskSummary : results) {
            if (previousSummary != null) {
                Assert.assertTrue(taskSummary.getBusinessProcessId().compareToIgnoreCase(
                    previousSummary.getBusinessProcessId()) >= 0);
            }
            previousSummary = taskSummary;
        }
    }

}
