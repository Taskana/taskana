package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineProxyForTest;
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
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "businessadmin")
  @Test
  void testRunAsAdminIsOnlyTemporary() throws NoSuchFieldException, IllegalAccessException {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();

    new TaskanaEngineProxyForTest(taskanaEngine)
        .getEngine()
        .runAsAdmin(() -> assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue());

    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
  }

  @WithAccessId(user = "user_1_1") // , groupNames = {"businessadmin"})
  @Test
  void testUser() {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user_1_1", groups = "businessadmin")
  @Test
  void testBusinessAdmin() throws NotAuthorizedException {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
  }

  @WithAccessId(user = "user_1_1", groups = "admin")
  @Test
  void testAdmin() throws NotAuthorizedException {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue();
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
  }
}
