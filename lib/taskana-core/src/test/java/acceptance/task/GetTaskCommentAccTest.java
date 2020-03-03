package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;

@ExtendWith(JaasExtension.class)
public class GetTaskCommentAccTest extends AbstractAccTest {

  GetTaskCommentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskComments()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    taskService.getTask("TKI:000000000000000000000000000000000000");
    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetNonExistingTaskCommentsShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThatThrownBy(
        () -> taskService.getTaskComments("TKI:000000000000000000000000000000000036"))
        .isInstanceOf(TaskCommentNotFoundException.class);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskCommentsOfNotVisibleTaskShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThatThrownBy(() -> taskService.getTask("TKI:000000000000000000000000000000000004"))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskComment() throws TaskCommentNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskComment taskComment =
        taskService.getTaskComment("TCI:000000000000000000000000000000000007");
    assertThat(taskComment.getCreator()).isEqualTo("user_1_1");
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetNonExistingTaskCommentShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThatThrownBy(() -> taskService.getTaskComment("Definately Non Existing Task Comment Id"))
        .isInstanceOf(TaskCommentNotFoundException.class);
  }

}


