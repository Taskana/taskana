package acceptance.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for all "query tasks by object reference" scenarios. */
@ExtendWith(JaasExtension.class)
class QueryTasksByObjectReferenceAccTest extends AbstractAccTest {

  QueryTasksByObjectReferenceAccTest() {
    super();
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryTasksByExcactValueOfObjectReference() throws SystemException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceValueIn("11223344", "22334455").list();
    assertEquals(33L, results.size());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryTasksByExcactValueAndTypeOfObjectReference() throws SystemException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService
            .createTaskQuery()
            .primaryObjectReferenceTypeIn("SDNR")
            .primaryObjectReferenceValueIn("11223344")
            .list();
    assertEquals(10L, results.size());
  }

  @WithAccessId(
      userName = "teamlead_1",
      groupNames = {"group_1", "group_2"})
  @Test
  void testQueryTasksByValueLikeOfObjectReference() throws SystemException {
    TaskService taskService = taskanaEngine.getTaskService();
    List<TaskSummary> results =
        taskService.createTaskQuery().primaryObjectReferenceValueLike("%567%").list();
    assertEquals(10L, results.size());
  }
}
