package acceptance.taskcomment.update;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.exceptions.ConcurrencyException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.internal.models.TaskCommentImpl;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.TaskCommentBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
class UpdateTaskCommentAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_UpdateTaskComment_For_TaskComment() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);
    TaskComment taskComment =
        TaskCommentBuilder.newTaskComment()
            .taskId(task.getId())
            .textField("some text in textfield")
            .buildAndStore(taskService);

    taskComment.setTextField("updated textfield");
    taskService.updateTaskComment(taskComment);

    List<TaskComment> taskCommentsAfterUpdate = taskService.getTaskComments(task.getId());
    assertThat(taskCommentsAfterUpdate)
        .extracting(TaskComment::getTextField)
        .containsExactly("updated textfield");
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_FailToUpdateTaskComment_When_UserHasNoAuthorization() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService, "user-1-1");
    TaskComment taskComment =
        TaskCommentBuilder.newTaskComment()
            .taskId(task.getId())
            .textField("some text in textfield")
            .buildAndStore(taskService, "user-1-1");
    taskComment.setTextField("updated textfield");

    assertThatThrownBy(() -> taskService.updateTaskComment(taskComment))
        .isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToUpdateTaskComment_When_ChangingCreator() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);
    TaskCommentImpl taskComment =
        (TaskCommentImpl)
            TaskCommentBuilder.newTaskComment()
                .taskId(task.getId())
                .textField("some text in textfield")
                .buildAndStore(taskService);

    taskComment.setCreator("user-1-2");

    ThrowingCallable updateTaskCommentCall = () -> taskService.updateTaskComment(taskComment);
    assertThatThrownBy(updateTaskCommentCall).isInstanceOf(NotAuthorizedException.class);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_FailToUpdateTaskComment_When_TaskCommentWasModifiedConcurrently() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService);
    TaskComment taskCommentToUpdate =
        TaskCommentBuilder.newTaskComment()
            .taskId(task.getId())
            .textField("some text in textfield")
            .buildAndStore(taskService);

    taskCommentToUpdate.setTextField("updated textfield");
    TaskComment concurrentTaskCommentToUpdate =
        taskService.getTaskComment(taskCommentToUpdate.getId());
    concurrentTaskCommentToUpdate.setTextField("concurrently updated textfield");

    taskService.updateTaskComment(taskCommentToUpdate);

    assertThatThrownBy(() -> taskService.updateTaskComment(concurrentTaskCommentToUpdate))
        .isInstanceOf(ConcurrencyException.class);
  }

  private TaskBuilder createDefaultTask() {
    return (TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(defaultObjectReference));
  }
}
