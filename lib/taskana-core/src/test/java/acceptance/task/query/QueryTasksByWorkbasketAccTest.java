package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;

/** Acceptance test for all "query tasks by workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WorkbasketTest {
    @WithAccessId(user = "user-1-1")
    @Test
    void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket() {
      TaskService taskService = taskanaEngine.getTaskService();

      ThrowingCallable call =
          () ->
              taskService
                  .createTaskQuery()
                  .workbasketKeyDomainIn(new KeyDomain("USER-2-1", "DOMAIN_A"))
                  .list();
      assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket() {
      TaskService taskService = taskanaEngine.getTaskService();
      ThrowingCallable call =
          () ->
              taskService
                  .createTaskQuery()
                  .workbasketKeyDomainIn(
                      new KeyDomain("USER-1-1", "DOMAIN_A"), new KeyDomain("USER-2-1", "DOMAIN_A"))
                  .list();
      assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
    }
  }
}
