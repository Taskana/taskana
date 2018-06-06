package acceptance.task;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
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

    @Ignore  // BB
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

    @Ignore  // BB
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
