package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import io.kadai.common.api.KeyDomain;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.api.TaskService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for all "query tasks by workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WorkbasketTest {
    @WithAccessId(user = "user-1-1")
    @Test
    void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket() {
      TaskService taskService = kadaiEngine.getTaskService();

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
      TaskService taskService = kadaiEngine.getTaskService();
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
