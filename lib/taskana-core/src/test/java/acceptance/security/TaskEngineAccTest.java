package acceptance.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.TaskanaRole;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class TaskEngineAccTest extends AbstractAccTest {

  TaskEngineAccTest() {
    super();
  }

  @Test
  void testUnauthenticated() {
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN));
  }

  @WithAccessId(userName = "user_1_1") // , groupNames = {"businessadmin"})
  @Test
  void testUser() throws NotAuthorizedException {
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
    Assertions.assertThrows(
        NotAuthorizedException.class,
        () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"businessadmin"})
  @Test
  void testBusinessAdmin() throws NotAuthorizedException {
    assertTrue(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
    taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"admin"})
  @Test
  void testAdmin() throws NotAuthorizedException {
    assertFalse(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN));
    assertTrue(taskanaEngine.isUserInRole(TaskanaRole.ADMIN));
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
  }
}
