package acceptance.task.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class QueryTasksByRoleAccTest extends AbstractAccTest {

  @Nested
  class RoleTest {

    @Test
    void should_ReturnNoResult_When_UserIsNotAuthenticated() {
      TaskService taskService = taskanaEngine.getTaskService();

      List<TaskSummary> results = taskService.createTaskQuery().list();

      assertThat(results).isEmpty();
    }

    @WithAccessId(user = "admin")
    @WithAccessId(user = "taskadmin")
    @WithAccessId(user = "businessadmin")
    @WithAccessId(user = "monitor")
    @WithAccessId(user = "teamlead-1")
    @WithAccessId(user = "user-1-1")
    @WithAccessId(user = "user-taskrouter")
    @TestTemplate
    void should_FindAllAccessibleTasksDependentOnTheUser_When_MakingTaskQuery() {
      TaskService taskService = taskanaEngine.getTaskService();
      List<TaskSummary> results = taskService.createTaskQuery().list();

      int expectedSize;

      switch (taskanaEngine.getCurrentUserContext().getUserid()) {
        case "admin":
        case "taskadmin":
          expectedSize = 88;
          break;
        case "businessadmin":
        case "monitor":
          expectedSize = 0;
          break;
        case "teamlead-1":
          expectedSize = 26;
          break;
        case "user-1-1":
          expectedSize = 7;
          break;
        case "user-taskrouter":
          expectedSize = 0;
          break;
        default:
          throw new SystemException(
              String.format(
                  "Invalid User: '%s'", taskanaEngine.getCurrentUserContext().getUserid()));
      }

      assertThat(results).hasSize(expectedSize);
    }
  }
}
