package acceptance.taskcomment.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import java.time.Instant;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaEngineConfigurationModifier;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.TaskCommentBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.MismatchedWorkbasketPermissionException;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class GetTaskCommentAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  Classification defaultClassification;
  Workbasket defaultWorkbasket;
  Task task1;
  Task task2;
  Task task3;

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
    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task3 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    User userWithName = taskanaEngine.getUserService().newUser();
    userWithName.setId("user-1-1");
    userWithName.setFirstName("Max");
    userWithName.setLastName("Mustermann");
    userWithName.setFullName("Max Mustermann");
    taskanaEngine.getUserService().createUser(userWithName);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskComments_For_TaskId() throws Exception {
    TaskComment comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task3.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    TaskComment comment2 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task3.getId())
            .textField("Text2")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    TaskComment comment3 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task3.getId())
            .textField("Text3")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);

    List<TaskComment> taskComments = taskService.getTaskComments(task3.getId());

    assertThat(taskComments).containsExactlyInAnyOrder(comment1, comment2, comment3);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnEmptyList_When_TaskCommentsDontExist() throws Exception {
    assertThat(taskService.getTaskComments(task2.getId())).isEmpty();
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_FailToReturnTaskComments_When_TaskIsNotVisible() {
    ThrowingCallable call = () -> taskService.getTaskComments(task1.getId());
    MismatchedWorkbasketPermissionException e =
        catchThrowableOfType(call, MismatchedWorkbasketPermissionException.class);

    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasket.getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_FailToReturnTaskComment_When_TaskIsNotVisible() throws Exception {
    TaskComment comment =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService, "user-1-1");

    ThrowingCallable call = () -> taskService.getTaskComment(comment.getId());
    MismatchedWorkbasketPermissionException e =
        catchThrowableOfType(call, MismatchedWorkbasketPermissionException.class);

    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getRequiredPermissions()).containsExactly(WorkbasketPermission.READ);
    assertThat(e.getWorkbasketId()).isEqualTo(defaultWorkbasket.getId());
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ReturnTaskComment_For_TaskCommentId() throws Exception {
    TaskComment comment =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);

    TaskComment taskComment = taskService.getTaskComment(comment.getId());

    assertThat(taskComment).isEqualTo(comment);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToReturnTaskComment_When_TaskCommentIsNotExisting() {
    String nonExistingId = "Definately Non Existing Task Comment Id";

    ThrowingCallable call = () -> taskService.getTaskComment(nonExistingId);
    TaskCommentNotFoundException e = catchThrowableOfType(call, TaskCommentNotFoundException.class);

    assertThat(e.getTaskCommentId()).isEqualTo(nonExistingId);
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoEnabled implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @TaskanaInject UserService userService;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaConfigurationBuilder) {
      return taskanaConfigurationBuilder.addAdditionalUserInfo(true);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetCreatorFullNameOfTaskComment_When_PropertyEnabled() throws Exception {
      TaskComment comment =
          TaskCommentBuilder.newTaskComment()
              .taskId(task1.getId())
              .textField("Text1")
              .created(Instant.now())
              .modified(Instant.now())
              .buildAndStore(taskService);

      TaskComment taskComment = taskService.getTaskComment(comment.getId());
      String creatorFullName = userService.getUser(taskComment.getCreator()).getFullName();
      assertThat(taskComment)
          .extracting(TaskComment::getCreatorFullName)
          .isEqualTo(creatorFullName);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_SetCreatorFullNameOfTaskComments_When_PropertyEnabled() throws Exception {
      TaskCommentBuilder.newTaskComment()
          .taskId(task1.getId())
          .textField("Text1")
          .created(Instant.now())
          .modified(Instant.now())
          .buildAndStore(taskService);

      List<TaskComment> taskComments = taskService.getTaskComments(task1.getId());

      taskComments.forEach(
          wrap(
              taskComment -> {
                String creatorFullName =
                    userService.getUser(taskComment.getCreator()).getFullName();
                assertThat(taskComment)
                    .extracting(TaskComment::getCreatorFullName)
                    .isEqualTo(creatorFullName);
              }));
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class WithAdditionalUserInfoDisabled implements TaskanaEngineConfigurationModifier {

    @TaskanaInject TaskService taskService;

    @Override
    public TaskanaConfiguration.Builder modify(
        TaskanaConfiguration.Builder taskanaConfigurationBuilder) {
      return taskanaConfigurationBuilder.addAdditionalUserInfo(false);
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_NotSetCreatorFullNameOfTaskComment_When_PropertyDisabled() throws Exception {
      TaskComment comment =
          TaskCommentBuilder.newTaskComment()
              .taskId(task1.getId())
              .textField("Text1")
              .created(Instant.now())
              .modified(Instant.now())
              .buildAndStore(taskService);

      TaskComment taskComment = taskService.getTaskComment(comment.getId());

      assertThat(taskComment).extracting(TaskComment::getCreatorFullName).isNull();
    }

    @WithAccessId(user = "user-1-1")
    @Test
    void should_NotSetCreatorFullNameOfTaskComments_When_PropertyDisabled() throws Exception {
      TaskCommentBuilder.newTaskComment()
          .taskId(task1.getId())
          .textField("Text1")
          .created(Instant.now())
          .modified(Instant.now())
          .buildAndStore(taskService);

      List<TaskComment> taskComments = taskService.getTaskComments(task1.getId());

      taskComments.forEach(
          taskComment ->
              assertThat(taskComment).extracting(TaskComment::getCreatorFullName).isNull());
    }
  }
}
