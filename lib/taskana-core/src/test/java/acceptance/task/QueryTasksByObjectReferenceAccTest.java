package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "query tasks by object reference" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

  QueryTasksByObjectReferenceAccTest() {
    super();
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByExcactValueOfObjectReference() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceValueIn("11223344", "22334455").list();
    assertThat(results).hasSize(33);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByExcactValueAndTypeOfObjectReference() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .primaryObjectReferenceTypeIn("SDNR")
            .primaryObjectReferenceValueIn("11223344")
            .list();
    assertThat(results).hasSize(10);
  }

  @WithAccessId(user = "admin")
  @Test
  void testQueryTasksByValueLikeOfObjectReference() throws Exception {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceValueLike("%567%").list();
    assertThat(results).hasSize(10);
  }
}
