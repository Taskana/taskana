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
public class DeleteTaskCommentAccTest extends AbstractAccTest {

  DeleteTaskCommentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDeleteTaskComment()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskComments).hasSize(2);

    taskService.deleteTaskComment(
        "TKI:000000000000000000000000000000000001", "TCI:000000000000000000000000000000000004");

    // make sure the task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testDeleteTaskCommentWithNoAuthorizationShouldFail()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    ThrowingCallable lambda =
        () ->
            taskService.deleteTaskComment(
                "TKI:000000000000000000000000000000000001",
                "TCI:000000000000000000000000000000000005");
    assertThatThrownBy(lambda).isInstanceOf(NotAuthorizedException.class);

    // make sure the task comment was not deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDeleteTaskCommentWithInvalidTaskCommentIdShouldFail()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    assertThatThrownBy(() -> taskService.deleteTaskComment("", ""))
        .isInstanceOf(InvalidArgumentException.class);

    assertThatThrownBy(() -> taskService.deleteTaskComment("", null))
        .isInstanceOf(InvalidArgumentException.class);

    assertThatThrownBy(() -> taskService.deleteTaskComment(null, ""))
        .isInstanceOf(InvalidArgumentException.class);

    assertThatThrownBy(() -> taskService.deleteTaskComment(null, null))
        .isInstanceOf(InvalidArgumentException.class);

    // make sure that no task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDeleteNonExistingTaskCommentShouldFail()
      throws NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    ThrowingCallable lambda =
        () -> taskService.deleteTaskComment("invalidTaskId", "non existing task comment id");
    assertThatThrownBy(lambda).isInstanceOf(TaskCommentNotFoundException.class);

    // make sure the task comment was not deleted
    List<TaskComment> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }
}
