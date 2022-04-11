package acceptance.taskcomment.create;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.time.Instant;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
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
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class CreateTaskCommentAccTest {
  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;

  Classification defaultClassification;
  Workbasket defaultWorkbasket;
  Task taskWithComments;
  TaskComment comment1;
  TaskComment comment2;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassification =
        DefaultTestEntities.defaultTestClassification().buildAndStore(classificationService);
    defaultWorkbasket =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStore(workbasketService);
    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasket.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    taskWithComments =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(taskWithComments.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    comment2 =
        TaskCommentBuilder.newTaskComment()
            .taskId(taskWithComments.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTaskComment_For_TaskThatAlreadyHasComments() throws Exception {
    TaskComment taskCommentToCreate = taskService.newTaskComment(taskWithComments.getId());
    taskCommentToCreate.setTextField("Some text");
    taskService.createTaskComment(taskCommentToCreate);

    List<TaskComment> taskCommentsAfterInsert =
        taskService.getTaskComments(taskWithComments.getId());
    assertThat(taskCommentsAfterInsert)
        .containsExactlyInAnyOrder(comment1, comment2, taskCommentToCreate);

    // Deleting the comment so that the comments remain the same in different tests inside this
    // class
    taskService.deleteTaskComment(taskCommentToCreate.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_FailToCreateTaskComment_When_UserHasNoWorkbasketPermission() {
    TaskComment taskCommentToCreate = taskService.newTaskComment(taskWithComments.getId());
    taskCommentToCreate.setTextField("Some text");

    ThrowingCallable call = () -> taskService.createTaskComment(taskCommentToCreate);

    MismatchedWorkbasketPermissionException e =
        catchThrowableOfType(call, MismatchedWorkbasketPermissionException.class);
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasket.getId());
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_CreateTaskComment_When_CopyingAnotherComment() throws Exception {
    TaskComment taskCommentToCreate = comment1.copy();

    taskService.createTaskComment(taskCommentToCreate);

    List<TaskComment> taskCommentsAfterInsert =
        taskService.getTaskComments(taskWithComments.getId());
    assertThat(taskCommentsAfterInsert)
        .containsExactlyInAnyOrder(comment1, comment2, taskCommentToCreate);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToCreateTaskComment_When_TaskIdIsNullOrNonExisting() throws Exception {
    TaskComment newTaskCommentForNonExistingTask =
        taskService.newTaskComment("Definitely non existing ID");
    newTaskCommentForNonExistingTask.setTextField("a newly created taskComment");
    TaskComment newTaskCommentForTaskIdNull = taskService.newTaskComment(null);
    newTaskCommentForTaskIdNull.setTextField("a newly created taskComment");

    ThrowingCallable call = () -> taskService.createTaskComment(newTaskCommentForNonExistingTask);
    TaskNotFoundException e = catchThrowableOfType(call, TaskNotFoundException.class);
    assertThat(e.getTaskId()).isEqualTo("Definitely non existing ID");

    call = () -> taskService.createTaskComment(newTaskCommentForTaskIdNull);
    e = catchThrowableOfType(call, TaskNotFoundException.class);
    assertThat(e.getTaskId()).isNull();
  }
}
