package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import helper.AbstractAccTest;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.models.TaskComment;

@ExtendWith(JaasExtension.class)
class GetTaskCommentAccTest extends AbstractAccTest {

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskComments_For_TaskId() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    taskService.getTask("TKI:000000000000000000000000000000000000");
    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000000");
    assertThat(taskComments).hasSize(3);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_ReturnEmptyList_When_TaskCommentsDontExist() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    assertThat(taskService.getTaskComments("TKI:000000000000000000000000000000000036")).isEmpty();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToReturnTaskComments_When_TaskIstNotVisible() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable httpCall =
        () -> {
          taskService.getTaskComments("TKI:000000000000000000000000000000000004");
        };
    assertThatThrownBy(httpCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskComment_For_TaskCommentId() throws Exception {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskComment taskComment =
        taskService.getTaskComment("TCI:000000000000000000000000000000000007");
    assertThat(taskComment.getCreator()).isEqualTo("user-1-1");
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToReturnTaskComment_When_TaskCommentIsNotExisting() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable lambda =
        () -> taskService.getTaskComment("Definately Non Existing Task Comment Id");
    assertThatThrownBy(lambda).isInstanceOf(TaskCommentNotFoundException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToReturntaskComment_When_TaskIstNotVisible() {

    TaskService taskService = taskanaEngine.getTaskService();

    ThrowingCallable lambda =
        () -> taskService.getTaskComment("TCI:000000000000000000000000000000000012");
    assertThatThrownBy(lambda).isInstanceOf(NotAuthorizedException.class);
  }
}
