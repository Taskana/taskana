package acceptance.security;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import acceptance.AbstractAccTest;
import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JAASExtension;
import pro.taskana.security.WithAccessId;

/**
 * Acceptance test for task queries and authorization.
 */
@ExtendWith(JAASExtension.class)
public class TaskEngineAccTest extends AbstractAccTest {

    public TaskEngineAccTest() {
        super();
    }

    @Test
    public void testUnauthenticated() {
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN));
    }

    @WithAccessId(
        userName = "user_1_1") // , groupNames = {"businessadmin"})
    @Test
    public void testUser() throws NotAuthorizedException {
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
        Assertions.assertThrows(NotAuthorizedException.class, () ->
            taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN));
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
