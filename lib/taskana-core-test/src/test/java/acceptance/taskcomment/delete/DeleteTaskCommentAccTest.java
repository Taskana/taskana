package acceptance.taskcomment.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.TaskCommentBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
public class DeleteTaskCommentAccTest {

  private static final String ACCESS_USER_1_1_ID = "user-1-1";

  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;

  Classification defaultClassification;
  Workbasket defaultWorkbasket;
  Task task1;
  TaskComment comment1;
  TaskComment comment2;

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @BeforeEach
  public void createTaskAndTaskComments() throws Exception {
    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    comment2 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    List<TaskComment> taskComments = taskService.getTaskComments(task1.getId());
    assertThat(taskComments).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @AfterEach
  public void deleteTask() throws Exception {
    taskService.cancelTask(task1.getId());
    taskService.deleteTask(task1.getId());
  }

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    defaultWorkbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasket.getId())
        .accessId(ACCESS_USER_1_1_ID)
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @Test
  void should_DeleteTaskComment_For_TaskCommentId() throws Exception {

    taskService.deleteTaskComment(comment1.getId());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(user = "user-1-2", groups = "user-1-1") // to read comments
  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment(comment1.getId()))
        .isInstanceOf(NotAuthorizedException.class);

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(2);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_DeleteTaskComment_When_UserIsInAdministrativeRole() throws Exception {
    taskService.deleteTaskComment(comment1.getId());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_DeleteTaskComment_When_NoExplicitPermissionsButUserIsInAdministrativeRole()
      throws Exception {

    taskService.deleteTaskComment(comment1.getId());

    // make sure the task comment was deleted
    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(1);
  }

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIdIsInvalid() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment(""))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIdIsNull() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment(null))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = ACCESS_USER_1_1_ID)
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIsNotExisting() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment("non existing task comment id"))
        .isInstanceOf(TaskCommentNotFoundException.class);
  }
}
