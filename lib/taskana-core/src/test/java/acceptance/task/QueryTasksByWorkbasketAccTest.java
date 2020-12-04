package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import helper.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedToQueryWorkbasketException;

/** Acceptance test for all "query tasks by workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

  QueryTasksByWorkbasketAccTest() {
    super();
  }

  @WithAccessId(user = "user-1-2", groups = GROUP_2_DN)
  @Test
  void testQueryForWorkbasketKeyDomain() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<KeyDomain> workbasketIdentifiers =
        List.of(new KeyDomain("GPK_KSC_2", "DOMAIN_A"), new KeyDomain("USER-1-2", "DOMAIN_A"));

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(workbasketIdentifiers.toArray(new KeyDomain[0]))
            .list();
    assertThat(results).hasSize(30);

    String[] ids =
        results.stream().map(t -> t.getWorkbasketSummary().getId()).toArray(String[]::new);

    List<TaskSummary> result2 = taskService.createTaskQuery().workbasketIdIn(ids).list();
    assertThat(result2).hasSize(30);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket() {
    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable call =
        () -> {
          taskService
              .createTaskQuery()
              .workbasketKeyDomainIn(new KeyDomain("USER-2-1", "DOMAIN_A"))
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket() {
    TaskService taskService = taskanaEngine.getTaskService();
    ThrowingCallable call =
        () -> {
          taskService
              .createTaskQuery()
              .workbasketKeyDomainIn(
                  new KeyDomain("USER-1-1", "DOMAIN_A"), new KeyDomain("USER-2-1", "DOMAIN_A"))
              .list();
        };
    assertThatThrownBy(call).isInstanceOf(NotAuthorizedToQueryWorkbasketException.class);
  }
}
