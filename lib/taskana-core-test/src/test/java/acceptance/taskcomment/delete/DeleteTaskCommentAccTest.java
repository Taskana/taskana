package acceptance.taskcomment.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import java.time.Instant;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import pro.taskana.task.api.exceptions.TaskCommentNotFoundException;
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
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.models.Workbasket;

@TaskanaIntegrationTest
class DeleteTaskCommentAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  Classification defaultClassification;
  Workbasket defaultWorkbasket;
  Task task1;
  TaskComment comment1;

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
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassification.asSummary())
            .workbasketSummary(defaultWorkbasket.asSummary())
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_DeleteTaskComment_For_TaskCommentId() throws Exception {
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);

    taskService.deleteTaskComment(comment1.getId());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).isEmpty();
  }

  @WithAccessId(user = "user-1-2", groups = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_UserHasNoAuthorization() throws Exception {
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService, "user-1-1");
    ThrowingCallable call = () -> taskService.deleteTaskComment(comment1.getId());
    NotAuthorizedOnTaskCommentException e =
        catchThrowableOfType(NotAuthorizedOnTaskCommentException.class, call);
    assertThat(e.getTaskCommentId()).isEqualTo(comment1.getId());
    assertThat(e.getCurrentUserId()).isEqualTo(taskanaEngine.getCurrentUserContext().getUserid());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(1);

    taskanaEngine.runAsAdmin(
        () -> {
          try {
            taskService.deleteTaskComment(comment1.getId());
          } catch (TaskCommentNotFoundException ex) {
            throw new SystemException("Cannot find task comment with id " + ex.getTaskCommentId());
          } catch (TaskNotFoundException ex) {
            throw new SystemException("Cannot find task comment with id " + ex.getTaskId());
          } catch (NotAuthorizedOnTaskCommentException ex) {
            throw new SystemException(
                "User "
                    + ex.getCurrentUserId()
                    + " not authorized on task comment "
                    + ex.getTaskCommentId());
          } catch (NotAuthorizedOnWorkbasketException ex) {
            throw new SystemException(
                "User "
                    + ex.getCurrentUserId()
                    + " not authorized on workbasket "
                    + ex.getWorkbasketId());
          }
        });
    List<TaskComment> taskCommentsAfterDeletionWithAdmin =
        taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletionWithAdmin).isEmpty();
    assertThat(taskService.getTask(task1.getId()).getNumberOfComments()).isZero();
  }

  @WithAccessId(user = "admin")
  @WithAccessId(user = "taskadmin")
  @TestTemplate
  void should_DeleteTaskCommentAndUpdateNumberOfComments_When_UserIsInAdministrativeRole()
      throws Exception {
    comment1 =
        TaskCommentBuilder.newTaskComment()
            .taskId(task1.getId())
            .textField("Text1")
            .created(Instant.now())
            .modified(Instant.now())
            .buildAndStore(taskService);
    taskService.deleteTaskComment(comment1.getId());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).isEmpty();
    assertThat(taskService.getTask(task1.getId()).getNumberOfComments()).isZero();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIdIsInvalid() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment(""))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_TaskCommentIdIsNull() throws Exception {
    assertThatThrownBy(() -> taskService.deleteTaskComment(null))
        .isInstanceOf(InvalidArgumentException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToDeleteTaskComment_When_CommentIdDoesNotExist() throws Exception {

    ThrowingCallable call = () -> taskService.deleteTaskComment("non existing task comment id");
    TaskCommentNotFoundException e = catchThrowableOfType(call, TaskCommentNotFoundException.class);
    assertThat(e.getTaskCommentId()).isEqualTo("non existing task comment id");
  }
}
