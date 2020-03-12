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
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.TaskComment;

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
      throws TaskNotFoundException, NotAuthorizedException, InvalidArgumentException {

    TaskService taskService = taskanaEngine.getTaskService();

    List<TaskComment> taskComments =
        taskService.getTaskComments("TKI:000000000000000000000000000000000026");
    assertThat(taskComments).hasSize(2);

    TaskComment taskCommentToCreate =
        taskService.newTaskComment("TKI:000000000000000000000000000000000026");
    taskCommentToCreate.setTextField("a newly created taskComment");

    taskService.createTaskComment(taskCommentToCreate);

    // make sure that the new task comment was added
    List<TaskComment> taskCommentsAfterInsert =
        taskService.getTaskComments("TKI:000000000000000000000000000000000026");
    assertThat(taskCommentsAfterInsert).hasSize(3);
  }

  @WithAccessId(
      userName = "user_1_1",
      groupNames = {"group_1"})
  @Test
  void testCreateTaskCommentForNullOrNonExistingTaskIdShouldFail() {

    TaskService taskService = taskanaEngine.getTaskService();

    TaskComment newTaskCommentForNonExistingTask =
        taskService.newTaskComment("Definately non existing ID");
    newTaskCommentForNonExistingTask.setTextField("a newly created taskComment");

    TaskComment newTaskCommentForTaskIdNull = taskService.newTaskComment(null);
    newTaskCommentForTaskIdNull.setTextField("a newly created taskComment");

    assertThatThrownBy(() -> taskService.createTaskComment(newTaskCommentForNonExistingTask))
        .isInstanceOf(TaskNotFoundException.class);

    assertThatThrownBy(() -> taskService.createTaskComment(newTaskCommentForTaskIdNull))
        .isInstanceOf(TaskNotFoundException.class);
  }
}
