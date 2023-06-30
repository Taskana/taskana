package acceptance.taskrouting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestClassification;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestObjectReference;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;

import acceptance.taskrouting.TaskReroutingAccTest.CustomTaskRoutingProvider;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BulkOperationResults;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.TaskanaException;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.exceptions.InvalidTaskStateException;
import pro.taskana.task.api.exceptions.TaskNotFoundException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.ObjectReferenceBuilder;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.exceptions.NotAuthorizedOnWorkbasketException;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = CustomTaskRoutingProvider.class)
@TaskanaIntegrationTest
class TaskReroutingAccTest {

  @TaskanaInject TaskService taskService;
  @TaskanaInject ClassificationService classificationService;
  @TaskanaInject WorkbasketService workbasketService;

  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  WorkbasketSummary workbasketSummary1;
  WorkbasketSummary workbasketSummary2;
  WorkbasketSummary workbasketSummary3;
  ObjectReference defaultObjectReference;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup() throws Exception {
    defaultClassificationSummary =
        defaultTestClassification().buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary = defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);
    workbasketSummary1 =
        defaultTestWorkbasket().key("key_1").buildAndStoreAsSummary(workbasketService);
    workbasketSummary2 =
        defaultTestWorkbasket().key("key_2").buildAndStoreAsSummary(workbasketService);
    workbasketSummary3 =
        defaultTestWorkbasket().key("key_3").buildAndStoreAsSummary(workbasketService);
    defaultObjectReference = defaultTestObjectReference().build();

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(defaultWorkbasketSummary.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .permission(WorkbasketPermission.TRANSFER)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary1.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary2.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(workbasketSummary3.getId())
        .accessId("user-1-2")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.READTASKS)
        .permission(WorkbasketPermission.EDITTASKS)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RerouteTask_When_PorValueIsChanged() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService, "admin");
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    task.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value1"));
    taskService.updateTask(task);
    Task reroutedTask = taskService.rerouteTask(task.getId());

    assertTaskIsRerouted(before, reroutedTask.asSummary(), task.getState(), workbasketSummary1);
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_NotRerouteTask_When_UserHasNoAppendPermissionToDestinationWb() throws Exception {
    Task task = createDefaultTask().buildAndStore(taskService, "admin");

    task.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value2"));
    taskService.updateTask(task);

    ThrowingCallable call = () -> taskService.rerouteTask(task.getId());
    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);

    assertThat(e.getWorkbasketId()).isEqualTo(workbasketSummary2.getId());
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getRequiredPermissions()).containsExactlyInAnyOrder(WorkbasketPermission.APPEND);

    Task readTask = taskService.getTask(task.getId());
    assertThat(readTask.getWorkbasketSummary().getId())
        .isEqualTo(task.getWorkbasketSummary().getId());
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_NotRerouteTask_When_UserHasNoTransferPermissionToOriginWb() throws Exception {
    Task task =
        createDefaultTask()
            .workbasketSummary(workbasketSummary1)
            .buildAndStore(taskService, "admin");

    task.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value2"));
    taskService.updateTask(task);

    ThrowingCallable call = () -> taskService.rerouteTask(task.getId());
    NotAuthorizedOnWorkbasketException e =
        catchThrowableOfType(call, NotAuthorizedOnWorkbasketException.class);

    assertThat(e.getWorkbasketId()).isEqualTo(workbasketSummary1.getId());
    assertThat(e.getCurrentUserId()).isEqualTo("user-1-2");
    assertThat(e.getRequiredPermissions()).containsExactlyInAnyOrder(WorkbasketPermission.TRANSFER);

    Task readTask = taskService.getTask(task.getId());
    assertThat(readTask.getWorkbasketSummary().getId())
        .isEqualTo(task.getWorkbasketSummary().getId());
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RerouteTasksToSameWb_When_PorValueIsChanged() throws Exception {
    Task task1 = createDefaultTask().buildAndStore(taskService, "admin");
    Task task2 = createDefaultTask().buildAndStore(taskService, "admin");
    Task task3 = createDefaultTask().buildAndStore(taskService, "admin");
    List<Task> tasks = Arrays.asList(task1, task2, task3);
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    for (Task task : tasks) {
      task.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value1"));
      taskService.updateTask(task);
    }
    List<String> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

    BulkOperationResults<String, TaskanaException> results = taskService.rerouteTasks(taskIds);
    assertThat(results.containsErrors()).isFalse();
    List<TaskSummary> reroutedTasks =
        taskService.createTaskQuery().idIn(taskIds.toArray(new String[0])).list();
    assertThat(reroutedTasks).isNotEmpty();
    for (int i = 0; i < reroutedTasks.size(); i++) {
      assertTaskIsRerouted(
          before, reroutedTasks.get(i), tasks.get(i).getState(), workbasketSummary1);
    }
  }

  @WithAccessId(user = "taskadmin")
  @Test
  void should_RerouteTasksToMultipleWorkbaskets_When_PorValueIsChanged() throws Exception {
    final Task task1 = createDefaultTask().buildAndStore(taskService, "admin");
    final Task task2 = createDefaultTask().buildAndStore(taskService, "admin");
    final Task task3 = createDefaultTask().buildAndStore(taskService, "admin");
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    task1.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value1"));
    taskService.updateTask(task1);
    task2.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value2"));
    taskService.updateTask(task2);
    task3.setPrimaryObjRef(createObjectReference("company", null, null, "MyType1", "Value3"));
    taskService.updateTask(task3);
    List<Task> tasks = Arrays.asList(task1, task2, task3);
    List<String> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());

    BulkOperationResults<String, TaskanaException> results = taskService.rerouteTasks(taskIds);
    assertThat(results.containsErrors()).isFalse();
    List<TaskSummary> reroutedTasks =
        taskService.createTaskQuery().idIn(taskIds.toArray(new String[0])).list();
    assertThat(reroutedTasks).isNotEmpty();
    for (TaskSummary reroutedTask : reroutedTasks) {
      if (reroutedTask.getId().equals(task1.getId())) {
        assertTaskIsRerouted(before, reroutedTask, task1.getState(), workbasketSummary1);
      } else if (reroutedTask.getId().equals(task2.getId())) {
        assertTaskIsRerouted(before, reroutedTask, task2.getState(), workbasketSummary2);
      } else {
        assertTaskIsRerouted(before, reroutedTask, task3.getState(), workbasketSummary3);
      }
    }
  }

  @WithAccessId(user = "user-1-2")
  @Test
  void should_RerouteValidTasksEvenIfErrorsExist_When_PorValueIsChanged() throws Exception {
    final Task taskToBeRerouted1 = createDefaultTask().buildAndStore(taskService, "admin");
    final Task taskToBeRerouted3 = createDefaultTask().buildAndStore(taskService, "admin");
    final Task taskNotNeededToReroute =
        createDefaultTask()
            .workbasketSummary(workbasketSummary1)
            .buildAndStore(taskService, "admin");
    final Task taskWithFinalState =
        createDefaultTask().state(TaskState.COMPLETED).buildAndStore(taskService, "admin");
    final Task taskWithNoTransferPerm =
        createDefaultTask()
            .workbasketSummary(workbasketSummary1)
            .buildAndStore(taskService, "admin");
    final Task taskWithNoAppendDestPerm = createDefaultTask().buildAndStore(taskService, "admin");
    Instant before = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    taskToBeRerouted1.setPrimaryObjRef(
        createObjectReference("company", null, null, "MyType1", "Value1"));
    taskService.updateTask(taskToBeRerouted1);
    taskToBeRerouted3.setPrimaryObjRef(
        createObjectReference("company", null, null, "MyType1", "Value3"));
    taskService.updateTask(taskToBeRerouted3);
    taskNotNeededToReroute.setPrimaryObjRef("company", null, null, "MyType1", "Value1");
    taskService.updateTask(taskNotNeededToReroute);
    taskWithFinalState.setPrimaryObjRef("company", null, null, "MyType1", "Value1");
    taskService.updateTask(taskWithFinalState);
    taskWithNoTransferPerm.setPrimaryObjRef("company", null, null, "MyType1", "Value3");
    taskService.updateTask(taskWithNoTransferPerm);
    taskWithNoAppendDestPerm.setPrimaryObjRef("company", null, null, "MyType1", "Value2");
    taskService.updateTask(taskWithNoAppendDestPerm);
    List<Task> tasks =
        Arrays.asList(
            taskToBeRerouted1,
            taskToBeRerouted3,
            taskNotNeededToReroute,
            taskWithFinalState,
            taskWithNoTransferPerm,
            taskWithNoAppendDestPerm);
    List<String> taskIds = tasks.stream().map(Task::getId).collect(Collectors.toList());
    taskIds.add("invalid-id");

    BulkOperationResults<String, TaskanaException> results = taskService.rerouteTasks(taskIds);
    final List<TaskSummary> reroutedTasks =
        taskService.createTaskQuery().idIn(taskIds.toArray(new String[0])).list();
    assertThat(results.containsErrors()).isTrue();
    assertThat(results.getErrorMap()).hasSize(4);
    assertThat(results.getErrorForId("invalid-id")).isOfAnyClassIn(TaskNotFoundException.class);
    assertThat(results.getErrorForId(taskWithFinalState.getId()))
        .isOfAnyClassIn(InvalidTaskStateException.class);
    assertThat(results.getErrorForId(taskWithNoTransferPerm.getId()))
        .isOfAnyClassIn(NotAuthorizedOnWorkbasketException.class);
    assertThat(results.getErrorForId(taskWithNoAppendDestPerm.getId()))
        .isOfAnyClassIn(NotAuthorizedOnWorkbasketException.class);

    for (TaskSummary reroutedTask : reroutedTasks) {
      if (reroutedTask.getId().equals(taskToBeRerouted1.getId())) {
        assertTaskIsRerouted(
            before, reroutedTask, taskToBeRerouted1.getState(), workbasketSummary1);
      } else if (reroutedTask.getId().equals(taskToBeRerouted3.getId())) {
        assertTaskIsRerouted(
            before, reroutedTask, taskToBeRerouted3.getState(), workbasketSummary3);
      } else if (reroutedTask.getId().equals(taskNotNeededToReroute.getId())) {
        assertThat(reroutedTask).isEqualTo(taskNotNeededToReroute.asSummary());
      } else if (reroutedTask.getId().equals(taskWithFinalState.getId())) {
        assertThat(reroutedTask).isEqualTo(taskWithFinalState.asSummary());
      } else if (reroutedTask.getId().equals(taskWithNoTransferPerm.getId())) {
        assertThat(reroutedTask).isEqualTo(taskWithNoTransferPerm.asSummary());
      } else if (reroutedTask.getId().equals(taskWithNoAppendDestPerm.getId())) {
        assertThat(reroutedTask).isEqualTo(taskWithNoAppendDestPerm.asSummary());
      }
    }
  }

  ObjectReference createObjectReference(
      String company, String system, String systemInstance, String type, String value) {
    return ObjectReferenceBuilder.newObjectReference()
        .company(company)
        .system(system)
        .systemInstance(systemInstance)
        .type(type)
        .value(value)
        .build();
  }

  private void assertTaskIsRerouted(
      Instant before,
      TaskSummary reroutedTask,
      TaskState stateBeforeTransfer,
      WorkbasketSummary wbAfterReroute) {
    assertThat(reroutedTask).isNotNull();
    assertThat(reroutedTask.isRead()).isFalse();
    assertThat(reroutedTask.isTransferred()).isTrue();
    assertThat(reroutedTask.getState()).isEqualTo(getStateAfterTransfer(stateBeforeTransfer));
    assertThat(reroutedTask.getOwner()).isNull();
    assertThat(reroutedTask.getWorkbasketSummary().getId()).isEqualTo(wbAfterReroute.getId());
    assertThat(reroutedTask.getDomain()).isEqualTo(wbAfterReroute.getDomain());
    assertThat(reroutedTask.getModified()).isAfterOrEqualTo(before);
  }

  private TaskBuilder createDefaultTask() {
    return (TaskBuilder.newTask()
            .workbasketSummary(defaultWorkbasketSummary)
            .primaryObjRef(defaultObjectReference))
        .classificationSummary(defaultClassificationSummary);
  }

  private TaskState getStateAfterTransfer(TaskState stateBeforeTransfer) {
    if (stateBeforeTransfer.equals(TaskState.CLAIMED)) {
      return TaskState.READY;
    }
    if (stateBeforeTransfer.equals(TaskState.IN_REVIEW)) {
      return TaskState.READY_FOR_REVIEW;
    } else {
      return stateBeforeTransfer;
    }
  }

  class CustomTaskRoutingProvider implements TaskRoutingProvider {

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {}

    @Override
    public String determineWorkbasketId(Task task) {
      if (task.getPrimaryObjRef().getValue().equals("Value1")) {
        return workbasketSummary1.getId();
      } else if (task.getPrimaryObjRef().getValue().equals("Value2")) {
        return workbasketSummary2.getId();
      } else if (task.getPrimaryObjRef().getValue().equals("Value3")) {
        return workbasketSummary3.getId();
      }
      return null;
    }
  }
}
