package acceptance.security;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.internal.security.JaasExtension;
import pro.taskana.common.internal.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class TaskQueryAccTest extends AbstractAccTest {

  TaskQueryAccTest() {
    super();
  }

  @Test
  void testTaskQueryUnauthenticated() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().ownerLike("%a%", "%u%").list();

    assertThat(results).isEmpty();
  }

  @WithAccessId(user = "user_1_1") // , groupNames = {"businessadmin"})
  @Test
  void testTaskQueryUser_1_1() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().ownerLike("%a%", "%u%").list();

    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "user_1_1", groups = "businessadmin")
  @Test
  void testTaskQueryUser_1_1BusinessAdm() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().ownerLike("%a%", "%u%").list();

    assertThat(results).hasSize(3);
  }

  @WithAccessId(user = "user_1_1", groups = "admin")
  @Test
  void testTaskQueryUser_1_1Admin() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().ownerLike("%a%", "%u%").list();

    assertThat(results).hasSize(35);
  }
}
