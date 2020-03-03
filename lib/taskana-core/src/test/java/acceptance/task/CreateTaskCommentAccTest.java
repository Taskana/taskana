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
import pro.taskana.task.api.exceptions.TaskCommentAlreadyExistException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.internal.models.TaskCommentImpl;

@ExtendWith(JaasExtension.class)
public class CreateTaskCommentAccTest extends AbstractAccTest {

  CreateTaskCommentAccTest() {
    super();
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreateTaskComment()
      throws TaskCommentNotFoundException, TaskCommentAlreadyExistException, TaskNotFoundException,
          NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000026");
    assertThat(taskComments).hasSize(2);

    TaskCommentImpl newTaskComment = new TaskCommentImpl();
    newTaskComment.setTaskId("TKI:000000000000000000000000000000000026");
    newTaskComment.setTextField("a newly created taskComment");

    taskService.createTaskComment(newTaskComment);

    // make sure that the new task comment was added
    List<TaskCommentImpl> taskCommentsAfterInsert =
        taskService.getTaskComments("TKI:000000000000000000000000000000000026");
    assertThat(taskCommentsAfterInsert).hasSize(3);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreateTaskCommentWithAlreadyExistingIdShouldFail()
      throws TaskCommentNotFoundException, TaskNotFoundException, NotAuthorizedException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskCommentImpl> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000027");
    assertThat(taskComments).hasSize(2);

    TaskCommentImpl newTaskComment = new TaskCommentImpl();
    newTaskComment.setTaskId("TKI:000000000000000000000000000000000027");
    newTaskComment.setId("TCI:000000000000000000000000000000000000");
    newTaskComment.setTextField("a newly created taskComment");

    assertThatThrownBy(() -> taskService.createTaskComment(newTaskComment))
        .isInstanceOf(TaskCommentAlreadyExistException.class);

    // make sure there is no new task comment
    List<TaskCommentImpl> taskComments1 =
        taskService.getTaskComments("TKI:000000000000000000000000000000000027");
    assertThat(taskComments1).hasSize(2);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreateTaskCommentForNonExistingTaskShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskCommentImpl newTaskComment = new TaskCommentImpl();
    newTaskComment.setTaskId("Definately non existing ID");
    newTaskComment.setTextField("a newly created taskComment");

    assertThatThrownBy(() -> taskService.createTaskComment(newTaskComment))
        .isInstanceOf(TaskNotFoundException.class);
  }
}
