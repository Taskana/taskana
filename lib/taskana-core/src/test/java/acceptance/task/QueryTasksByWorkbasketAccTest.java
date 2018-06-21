package acceptance.task;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for all "query tasks by workbasket" scenarios.
 */
@RunWith(JAASRunner.class)
public class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

    public QueryTasksByWorkbasketAccTest() {
        super();
    }

    @WithAccessId(
        userName = "teamlead_1",
        groupNames = {"group_1"})
    @Test
    public void testQueryForWorkbasketKeyDomain() {
        TaskService taskService = taskanaEngine.getTaskService();
        List<KeyDomain> workbasketIdentifiers = Arrays.asList(new KeyDomain("GPK_KSC", "DOMAIN_A"),
            new KeyDomain("USER_1_2", "DOMAIN_A"));

        List<TaskSummary> results = taskService.createTaskQuery()
            .workbasketKeyDomainIn(workbasketIdentifiers.toArray(new KeyDomain[0]))
            .list();
        assertThat(results.size(), equalTo(42));

        String[] ids = results.stream()
            .map(t -> t.getWorkbasketSummary().getId())
            .collect(Collectors.toList())
            .toArray(new String[0]);

        List<TaskSummary> result2 = taskService.createTaskQuery()
            .workbasketIdIn(ids)
            .list();
        assertThat(result2.size(), equalTo(42));
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedToQueryWorkbasketException.class)
    public void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket() {
        TaskService taskService = taskanaEngine.getTaskService();
        taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_2_1", "DOMAIN_A"))
            .list();
    }

    @WithAccessId(
        userName = "user_1_1",
        groupNames = {"group_1"})
    @Test(expected = NotAuthorizedToQueryWorkbasketException.class)
    public void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket() {
        TaskService taskService = taskanaEngine.getTaskService();
        taskService.createTaskQuery()
            .workbasketKeyDomainIn(new KeyDomain("USER_1_1", "DOMAIN_A"), new KeyDomain("USER_2_1", "DOMAIN_A"))
            .list();
    }

}
