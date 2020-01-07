package acceptance.task;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.KeyDomain;
import pro.taskana.TaskService;
import pro.taskana.TaskSummary;
import pro.taskana.exceptions.NotAuthorizedToQueryWorkbasketException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;

/** Acceptance test for all "query tasks by workbasket" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByWorkbasketAccTest extends AbstractAccTest {

  QueryTasksByWorkbasketAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1"})
  @Test
  void testQueryForWorkbasketKeyDomain() {
    TaskService taskService = taskanaEngine.getTaskService();
    List<KeyDomain> workbasketIdentifiers =
        Arrays.asList(new KeyDomain("GPK_KSC", "DOMAIN_A"), new KeyDomain("USER_1_2", "DOMAIN_A"));

    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(workbasketIdentifiers.toArray(new KeyDomain[0]))
            .list();
    assertThat(results.size(), equalTo(42));

    String[] ids =
        results.stream()
            .map(t -> t.getWorkbasketSummary().getId())
            .collect(Collectors.toList())
            .toArray(new String[0]);

    List<TaskSummary> result2 = taskService.createTaskQuery().workbasketIdIn(ids).list();
    assertThat(result2.size(), equalTo(42));
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfNoOpenerPermissionOnQueriedWorkbasket() {
    TaskService taskService = taskanaEngine.getTaskService();

    Assertions.assertThrows(
        NotAuthorizedToQueryWorkbasketException.class,
        () ->
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(new KeyDomain("USER_2_1", "DOMAIN_A"))
                .list());
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testThrowsExceptionIfNoOpenerPermissionOnAtLeastOneQueriedWorkbasket() {
    TaskService taskService = taskanaEngine.getTaskService();
    Assertions.assertThrows(
        NotAuthorizedToQueryWorkbasketException.class,
        () ->
            taskService
                .createTaskQuery()
                .workbasketKeyDomainIn(
                    new KeyDomain("USER_1_1", "DOMAIN_A"), new KeyDomain("USER_2_1", "DOMAIN_A"))
                .list());
  }
}
