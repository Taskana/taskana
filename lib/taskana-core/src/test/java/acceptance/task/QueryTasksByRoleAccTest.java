package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;

import helper.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskSummary;

/** Acceptance test for task queries and authorization. */
@ExtendWith(JaasExtension.class)
class QueryTasksByRoleAccTest extends AbstractAccTest {

  @Test
  void should_ReturnNoResult_When_UserIsNotAuthenticated() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).isEmpty();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAllTasks_For_Admin() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).hasSize(87);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_ReturnAllTasks_For_TaskAdmin() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).hasSize(87);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ReturnAllTasks_For_BusinessAdmin() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).isEmpty();
  }

  @WithAccessId(user = "monitor")
  @Test
  void should_ReturnAllTasks_For_MonitorUser() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).isEmpty();
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ReturnAllTasks_For_TeamLead1() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).hasSize(25);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnAllTasks_For_User11() {
    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskSummary> results = taskService.createTaskQuery().list();

    assertThat(results).hasSize(7);
  }

}
