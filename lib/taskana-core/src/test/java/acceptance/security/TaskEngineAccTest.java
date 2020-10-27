package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.TaskanaEngineProxy;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class TaskEngineAccTest extends AbstractAccTest {

  TaskEngineAccTest() {
    super();
  }

  @Test
  void should_ThrowException_When_AccessIdIsUnauthenticated() {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_RunAsAdminOnlyTemorarily_When_RunAsAdminMethodIsCalled() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();

    new TaskanaEngineProxy(taskanaEngine)
        .getEngine()
        .runAsAdmin(() -> assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue());

    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ThrowException_When_CheckingNormalUserForAdminRoles() {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    ThrowingCallable call = () -> taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1", groups = "businessadmin")
  @Test
  void should_ConfirmBusinessAdminRole_When_AccessIdIsBusinessAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    taskanaEngine.checkRoleMembership(TaskanaRole.BUSINESS_ADMIN);
  }

  @WithAccessId(user = "user-1-1", groups = "taskadmin")
  @Test
  void should_ConfirmTaskAdminRole_When_AccessIdIsTaskAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.TASK_ADMIN)).isTrue();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isFalse();
    taskanaEngine.checkRoleMembership(TaskanaRole.TASK_ADMIN);
  }

  @WithAccessId(user = "user-1-1", groups = "admin")
  @Test
  void should_ConfirmAdminRole_When_AccessIdIsAdmin() throws Exception {
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.BUSINESS_ADMIN)).isFalse();
    assertThat(taskanaEngine.isUserInRole(TaskanaRole.ADMIN)).isTrue();
    taskanaEngine.checkRoleMembership(TaskanaRole.ADMIN);
  }
}
