package acceptance.taskcomment.delete;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.exceptions.NotAuthorizedOnTaskCommentException;
import io.kadai.task.api.exceptions.TaskCommentNotFoundException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.Task;
import io.kadai.task.api.models.TaskComment;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.TaskCommentBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import io.kadai.workbasket.api.models.Workbasket;
import java.time.Instant;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

@KadaiIntegrationTest
class DeleteTaskCommentAccTest {

  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject KadaiEngine kadaiEngine;

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
    assertThat(e.getCurrentUserId()).isEqualTo(kadaiEngine.getCurrentUserContext().getUserid());

    List<TaskComment> taskCommentsAfterDeletion = taskService.getTaskComments(task1.getId());
    assertThat(taskCommentsAfterDeletion).hasSize(1);

    kadaiEngine.runAsAdmin(
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
    TaskCommentNotFoundException e = catchThrowableOfType(TaskCommentNotFoundException.class, call);
    assertThat(e.getTaskCommentId()).isEqualTo("non existing task comment id");
  }
}
