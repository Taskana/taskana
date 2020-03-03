package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.security.JaasExtension;
import pro.taskana.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.models.TaskCommentImpl;

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
          TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000025");
    assertThat(taskComments).hasSize(1);
    assertThat(taskComments.get(0).getTextField()).isEqualTo("some text in textfield");

    TaskCommentImpl taskComment = new TaskCommentImpl();
    taskComment.setId("TCI:000000000000000000000000000000000002");
    taskComment.setTaskId("TKI:000000000000000000000000000000000025");
    taskComment.setTextField("updated textfield");
    taskComment.setCreated(taskComments.get(0).getCreated());
    taskComment.setModified(taskComments.get(0).getModified());
    taskComment.setCreator("user_1_1");

    taskService.updateTaskComment(taskComment);

    List<TaskCommentImpl> taskCommentsAfterUpdate =
        taskService.getTaskComments("TKI:000000000000000000000000000000000025");
    assertThat(taskCommentsAfterUpdate.get(0).getTextField()).isEqualTo("updated textfield");
  }

  @WithAccessId(
      userName = "user_1_2",
      groupNames = {"group_1"})
  @Test
  void testUpdateTaskCommentWithNoAuthorizationShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(2);
    assertThat(taskComments.get(1).getTextField()).isEqualTo("some other text in textfield");

    TaskCommentImpl taskComment = new TaskCommentImpl();
    taskComment.setId("TCI:000000000000000000000000000000000001");
    taskComment.setTaskId("TKI:000000000000000000000000000000000000");
    taskComment.setTextField("updated textfield");
    taskComment.setCreated(taskComments.get(1).getCreated());
    taskComment.setModified(taskComments.get(1).getModified());
    taskComment.setCreator("user_1_1");

    assertThatThrownBy(() -> taskService.updateTaskComment(taskComment))
        .isInstanceOf(NotAuthorizedException.class);

    // make sure the task comment wasn't updated
    List<TaskCommentImpl> taskCommentsAfterUpdateAttempt =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterUpdateAttempt.get(1).getTextField())
        .isEqualTo("some other text in textfield");
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testUpdateTaskCommentWithConcurrentModificationShouldFail()
      throws TaskCommentNotFoundException, NotAuthorizedException, TaskNotFoundException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(2);
    assertThat(taskComments.get(1).getTextField()).isEqualTo("some other text in textfield");

    TaskCommentImpl taskComment = new TaskCommentImpl();
    taskComment.setId("TCI:000000000000000000000000000000000001");
    taskComment.setTaskId("TKI:000000000000000000000000000000000000");
    taskComment.setTextField("updated textfield");
    taskComment.setCreated(taskComments.get(1).getCreated());
    taskComment.setModified(Instant.now());
    taskComment.setCreator("user_1_1");

    assertThatThrownBy(() -> taskService.updateTaskComment(taskComment))
        .isInstanceOf(ConcurrencyException.class);

    // make sure the task comment wasn't updated
    List<TaskCommentImpl> taskCommentsAfterUpdateAttempt =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskCommentsAfterUpdateAttempt.get(1).getTextField())
        .isEqualTo("some other text in textfield");
  }
}
