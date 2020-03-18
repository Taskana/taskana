package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;

@ExtendWith(JaasExtension.class)
public class GetTaskCommentAccTest extends AbstractAccTest {

  GetTaskCommentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskComments() throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    taskService.getTask("TKI:000000000000000000000000000000000000");
    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetNonExistingTaskCommentsShouldReturnEmptyList()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThat(taskService.getTaskComments("TKI:000000000000000000000000000000000036")).isEmpty();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskCommentsOfNotVisibleTaskShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable httpCall =
        () -> {
          taskService.getTaskComments("TKI:000000000000000000000000000000000004");
        };
    assertThatThrownBy(httpCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskComment()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException {

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

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testGetTaskCommentOfNotVisibleTaskShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThatThrownBy(() -> taskService.getTaskComment("TCI:000000000000000000000000000000000012"))
        .isInstanceOf(NotAuthorizedException.class);
  }
}
