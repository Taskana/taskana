package acceptance.task.delete;

import static io.kadai.testapi.DefaultTestEntities.defaultTestClassification;
import static io.kadai.testapi.DefaultTestEntities.defaultTestObjectReference;
import static io.kadai.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.common.api.BulkOperationResults;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.exceptions.KadaiException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.TaskState;
import io.kadai.task.api.exceptions.InvalidTaskStateException;
import io.kadai.task.api.exceptions.TaskNotFoundException;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.api.models.ObjectReference;
import io.kadai.task.api.models.Task;
import io.kadai.task.internal.AttachmentMapper;
import io.kadai.testapi.DefaultTestEntities;
import io.kadai.testapi.KadaiEngineProxy;
import io.kadai.testapi.KadaiInject;
import io.kadai.testapi.KadaiIntegrationTest;
import io.kadai.testapi.builder.TaskAttachmentBuilder;
import io.kadai.testapi.builder.TaskBuilder;
import io.kadai.testapi.builder.WorkbasketAccessItemBuilder;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

@KadaiIntegrationTest
class DeleteTaskAccTest {
  @KadaiInject TaskService taskService;
  @KadaiInject ClassificationService classificationService;
  @KadaiInject WorkbasketService workbasketService;
  @KadaiInject KadaiEngine kadaiEngine;

  Task task1;
  Task task2;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
    task1 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService);
    task2 =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.COMPLETED)
            .buildAndStore(taskService);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteAttachments_When_MultipleTasksAreDeleted() throws Exception {
    task1 = createNewCompletedTask();
    task2 = createNewCompletedTask();
    task1.addAttachment(createNewAttachment());
    task2.addAttachment(createNewAttachment());
    taskService.deleteTasks(List.of(task1.getId(), task2.getId()));

    KadaiEngineProxy engineProxy = new KadaiEngineProxy(kadaiEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getEngine().getSqlSession().getMapper(AttachmentMapper.class);
    try {
      engineProxy.openConnection();
      assertThat(
              attachmentMapper.findAttachmentSummariesByTaskIds(
                  List.of(task1.getId(), task2.getId())))
          .isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteAttachments_When_SingleTaskIsDeleted() throws Exception {
    task1 = createNewCompletedTask();
    task1.addAttachment(createNewAttachment());
    task1.addAttachment(createNewAttachment());
    taskService.deleteTask(task1.getId());

    KadaiEngineProxy engineProxy = new KadaiEngineProxy(kadaiEngine);
    AttachmentMapper attachmentMapper =
        engineProxy.getSqlSession().getMapper(AttachmentMapper.class);
    try {
      engineProxy.openConnection();
      assertThat(attachmentMapper.findAttachmentsByTaskId(task1.getId())).isEmpty();
    } finally {
      engineProxy.returnConnection();
    }
  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRoleButTriesToBulkDeleteTasks() {
    ThrowingCallable call = () -> taskService.deleteTasks(List.of(task1.getId(), task2.getId()));

    NotAuthorizedException e = catchThrowableOfType(NotAuthorizedException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo(kadaiEngine.getCurrentUserContext().getUserid());
    assertThat(e.getRoles()).containsExactly(KadaiRole.ADMIN);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_DeleteSingleTask() throws Exception {
    task1 = createNewCompletedTask();
    taskService.deleteTask(task1.getId());

    ThrowingCallable call = () -> taskService.getTask(task1.getId());

    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo(task1.getId());
  }

  @WithAccessId(user = "businessadmin")
  @WithAccessId(user = "taskadmin")
  @WithAccessId(user = "user-1-1")
  @TestTemplate
  void should_ThrowException_When_UserIsNotInAdminRole() {
    ThrowingCallable call = () -> taskService.deleteTask(task1.getId());

    NotAuthorizedException e = catchThrowableOfType(NotAuthorizedException.class, call);
    assertThat(e.getCurrentUserId()).isEqualTo(kadaiEngine.getCurrentUserContext().getUserid());
    assertThat(e.getRoles()).containsExactly(KadaiRole.ADMIN);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ThrowException_When_TaskIsNotCompleted() throws Exception {
    Task taskNotComplete =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService);

    ThrowingCallable call = () -> taskService.deleteTask(taskNotComplete.getId());

    InvalidTaskStateException e = catchThrowableOfType(InvalidTaskStateException.class, call);
    assertThat(e.getTaskId()).isEqualTo(taskNotComplete.getId());
    assertThat(e.getTaskState()).isEqualTo(TaskState.READY);
    assertThat(e.getRequiredTaskStates())
        .containsExactlyInAnyOrder(TaskState.COMPLETED, TaskState.CANCELLED, TaskState.TERMINATED);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ForceDeleteTask_When_TaskIsNotCompleted() throws Exception {
    Task taskNotComplete =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService);

    taskService.forceDeleteTask(taskNotComplete.getId());
    ThrowingCallable call = () -> taskService.getTask(taskNotComplete.getId());

    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo(taskNotComplete.getId());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_BulkDeleteTask() throws Exception {
    task1 = createNewCompletedTask();
    task2 = createNewCompletedTask();
    BulkOperationResults<String, KadaiException> results =
        taskService.deleteTasks(List.of(task1.getId(), task2.getId()));

    assertThat(results.containsErrors()).isFalse();

    ThrowingCallable call = () -> taskService.getTask(task1.getId());
    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo(task1.getId());

    ThrowingCallable call2 = () -> taskService.getTask(task2.getId());
    TaskNotFoundException e2 = catchThrowableOfType(TaskNotFoundException.class, call2);
    assertThat(e2.getTaskId()).isEqualTo(task2.getId());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_BulkDeleteTasksWithException() throws Exception {
    task1 = createNewCompletedTask();
    task2 = createNewCompletedTask();
    Task taskNotComplete =
        TaskBuilder.newTask()
            .classificationSummary(defaultClassificationSummary)
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .state(TaskState.READY)
            .buildAndStore(taskService);
    BulkOperationResults<String, KadaiException> results =
        taskService.deleteTasks(
            List.of(task1.getId(), task2.getId(), taskNotComplete.getId(), "INVALID_TASK_ID"));

    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap().keySet())
        .containsExactlyInAnyOrder(taskNotComplete.getId(), "INVALID_TASK_ID");
    assertThat(results.getErrorMap().get(taskNotComplete.getId()))
        .isInstanceOf(InvalidTaskStateException.class);
    assertThat(results.getErrorMap().get("INVALID_TASK_ID"))
        .isInstanceOf(TaskNotFoundException.class);

    Task notDeletedTask = taskService.getTask(taskNotComplete.getId());
    assertThat(notDeletedTask).isNotNull();
    ThrowingCallable call = () -> taskService.getTask(task1.getId());
    TaskNotFoundException e = catchThrowableOfType(TaskNotFoundException.class, call);
    assertThat(e.getTaskId()).isEqualTo(task1.getId());
    ThrowingCallable call2 = () -> taskService.getTask(task2.getId());
    TaskNotFoundException e2 = catchThrowableOfType(TaskNotFoundException.class, call2);
    assertThat(e2.getTaskId()).isEqualTo(task2.getId());
  }

  private Task createNewCompletedTask() throws Exception {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
        .state(TaskState.COMPLETED)
        .buildAndStore(taskService, "admin");
  }

  private Attachment createNewAttachment() throws Exception {
    return TaskAttachmentBuilder.newAttachment()
        .classificationSummary(defaultClassificationSummary)
        .objectReference(defaultObjectReference)
        .build();
  }
}
