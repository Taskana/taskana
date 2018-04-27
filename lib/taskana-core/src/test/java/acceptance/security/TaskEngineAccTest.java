package acceptance.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASRunner;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for task queries and authorization.
 */
@RunWith(JAASRunner.class)
public class TaskEngineAccTest extends AbstractAccTest {

    public TaskEngineAccTest() {
        super();
    }

    @Test(expected = NotAuthorizedException.class)
    public void testUnauthenticated() throws NotAuthorizedException {
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    }

    @WithAccessId(
        userName = "user_1_1") // , groupNames = {"businessadmin"})
    @Test(expected = NotAuthorizedException.class)
    public void testUser() throws NotAuthorizedException {
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    }

    @WithAccessId(
        userName = "user_1_1", groupNames = {"businessadmin"})
    @Test
    public void testBusinessAdmin() throws NotAuthorizedException {
        assertTrue(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    }

    @WithAccessId(
        userName = "user_1_1", groupNames = {"admin"})
    @Test
    public void testAdmin() throws NotAuthorizedException {
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertTrue(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
    }

}
