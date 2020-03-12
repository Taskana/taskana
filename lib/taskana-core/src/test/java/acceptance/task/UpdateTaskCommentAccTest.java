package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;

@ExtendWith(JaasExtension.class)
public class UpdateTaskCommentAccTest extends AbstractAccTest {

  UpdateTaskCommentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTaskComment()
      throws TaskCommentNotFoundException, NotAuthorizedException, ConcurrencyException,
          TaskNotFoundException, InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000025");
    assertThat(taskComments).hasSize(1);
    assertThat(taskComments.get(0).getTextField()).isEqualTo("some text in textfield");

    TaskComment taskComment =
        taskService.getTaskComment("TCI:000000000000000000000000000000000003");
    taskComment.setTextField("updated textfield");

    taskService.updateTaskComment(taskComment);

    List<TaskComment> taskCommentsAfterUpdate =
        taskService.getTaskComments("TKI:000000000000000000000000000000000025");
    assertThat(taskCommentsAfterUpdate.get(0).getTextField()).isEqualTo("updated textfield");
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testUpdateTaskCommentWithNoAuthorizationShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);
    assertThat(taskComments.get(1).getTextField()).isEqualTo("some other text in textfield");

    TaskComment taskComment =
        taskService.getTaskComment("TCI:000000000000000000000000000000000001");
    taskComment.setTextField("updated textfield");

    assertThatThrownBy(() -> taskService.updateTaskComment(taskComment))
        .isInstanceOf(NotAuthorizedException.class);

    // make sure the task comment wasn't updated
    List<TaskComment> taskCommentsAfterUpdateAttempt =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterUpdateAttempt.get(1).getTextField())
        .isEqualTo("some other text in textfield");
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTaskCommentWithConcurrentModificationShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException,
          ConcurrencyException, InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);
    assertThat(taskComments.get(2).getTextField()).isEqualTo("some other text in textfield");

    TaskComment taskCommentToUpdate =
        taskService.getTaskComment("TCI:000000000000000000000000000000000002");
    taskCommentToUpdate.setTextField("updated textfield");

    TaskComment concurrentTaskCommentToUpdate =
        taskService.getTaskComment("TCI:000000000000000000000000000000000002");
    concurrentTaskCommentToUpdate.setTextField("concurrently updated textfield");

    taskService.updateTaskComment(taskCommentToUpdate);

    assertThatThrownBy(() -> taskService.updateTaskComment(concurrentTaskCommentToUpdate))
        .isInstanceOf(ConcurrencyException.class);

    // make sure the task comment wasn't updated
    List<TaskComment> taskCommentsAfterUpdateAttempt =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterUpdateAttempt.get(2).getTextField()).isEqualTo("updated textfield");
  }
}
