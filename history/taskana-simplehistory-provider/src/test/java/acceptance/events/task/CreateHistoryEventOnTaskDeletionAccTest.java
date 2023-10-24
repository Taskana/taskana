package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@TaskanaIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = TaskanaHistory.class,
    serviceProviders = SimpleHistoryServiceImpl.class)
@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskDeletionAccTest {
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;
  ClassificationSummary defaultClassificationSummary;
  WorkbasketSummary defaultWorkbasketSummary;
  Task task1;
  Task task2;
  Task task3;
  Task task4;
  SimpleHistoryServiceImpl historyService;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setUp() throws Exception {

    defaultClassificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    defaultWorkbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    task1 = createTask().buildAndStore(taskService);
    task2 = createTask().state(TaskState.COMPLETED).buildAndStore(taskService);
    task3 = createTask().state(TaskState.COMPLETED).buildAndStore(taskService);
    task4 = createTask().state(TaskState.COMPLETED).buildAndStore(taskService);

    historyService = new SimpleHistoryServiceImpl();
    historyService.initialize(taskanaEngine);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateDeleteHistoryEvent_When_TaskIsDeleted() throws Exception {

    historyService.deleteHistoryEventsByTaskIds(List.of(task4.getId()));

    taskService.deleteTask(task4.getId());

    List<TaskHistoryEvent> events =
        historyService.createTaskHistoryQuery().taskIdIn(task4.getId()).list();
    assertThat(events).hasSize(1);
    assertDeleteHistoryEvent(events.get(0).getId(), "admin", task4.getId());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateDeleteHistoryEvent_When_TaskIsForceDeleted() throws Exception {
    historyService.deleteHistoryEventsByTaskIds(List.of(task1.getId()));

    taskService.forceDeleteTask(task1.getId());

    List<TaskHistoryEvent> events =
        historyService.createTaskHistoryQuery().taskIdIn(task1.getId()).list();
    assertThat(events).hasSize(1);
    assertDeleteHistoryEvent(events.get(0).getId(), "admin", task1.getId());
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateDeleteHistoryEvents_When_MultipleTasksAreDeleted() throws Exception {
    List<String> taskIds = List.of(task2.getId(), task3.getId());
    historyService.deleteHistoryEventsByTaskIds(taskIds);

    taskService.deleteTasks(taskIds);

    TaskHistoryEvent eventTask2 =
        historyService.createTaskHistoryQuery().taskIdIn(task2.getId()).single();
    TaskHistoryEvent eventTask3 =
        historyService.createTaskHistoryQuery().taskIdIn(task3.getId()).single();
    assertDeleteHistoryEvent(eventTask2.getId(), "admin", task2.getId());
    assertDeleteHistoryEvent(eventTask3.getId(), "admin", task3.getId());
  }

  private void assertDeleteHistoryEvent(String eventId, String expectedUser, String taskId)
      throws Exception {
    TaskHistoryEvent event = historyService.getTaskHistoryEvent(eventId);
    assertThat(event.getUserId()).isEqualTo(expectedUser);
    assertThat(event.getEventType()).isEqualTo(TaskHistoryEventType.DELETED.getName());
    assertThat(event.getTaskId()).isEqualTo(taskId);
  }

  private TaskBuilder createTask() {
    return TaskBuilder.newTask()
        .classificationSummary(defaultClassificationSummary)
        .workbasketSummary(defaultWorkbasketSummary)
        .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());
  }
}
