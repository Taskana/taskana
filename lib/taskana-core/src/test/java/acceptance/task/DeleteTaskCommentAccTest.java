package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.models.TaskCommentImpl;

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

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskComments).hasSize(2);

    taskService.deleteTaskComment("TCI:000000000000000000000000000000000003");

    // make sure the task comment was deleted
    List<TaskCommentImpl> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000001");
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testDeleteTaskCommentWithNoAuthorizationShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    assertThatThrownBy(
        () -> taskService.deleteTaskComment("TCI:000000000000000000000000000000000004"))
        .isInstanceOf(NotAuthorizedException.class);

    // make sure the task comment was not deleted
    List<TaskCommentImpl> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDeleteTaskCommentWithInvalidTaskCommentIdShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    assertThatThrownBy(() -> taskService.deleteTaskComment(""))
        .isInstanceOf(InvalidArgumentException.class);

    // make sure the task comment was not deleted
    List<TaskCommentImpl> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testDeleteNonExistingTaskCommentShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskComments).hasSize(2);

    assertThatThrownBy(() -> taskService.deleteTaskComment("non existing task comment id"))
        .isInstanceOf(TaskCommentNotFoundException.class);

    // make sure the task comment was not deleted
    List<TaskCommentImpl> taskCommentsAfterDeletion =
        taskService.getTaskComments("TKI:000000000000000000000000000000000002");
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }
}
