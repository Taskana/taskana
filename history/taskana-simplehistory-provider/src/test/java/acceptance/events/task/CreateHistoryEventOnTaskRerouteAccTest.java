package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.events.task.CreateHistoryEventOnTaskRerouteAccTest.TaskRoutingProviderForDomainA;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.ibatis.session.SqlSessionManager;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.TaskanaHistoryEngineImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.TaskanaHistory;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.spi.routing.api.TaskRoutingProvider;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.TaskBuilder;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

@WithServiceProvider(
    serviceProviderInterface = TaskRoutingProvider.class,
    serviceProviders = TaskRoutingProviderForDomainA.class)
@WithServiceProvider(
    serviceProviderInterface = TaskanaHistory.class,
    serviceProviders = SimpleHistoryServiceImpl.class)
@TaskanaIntegrationTest
@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskRerouteAccTest {
  @TaskanaInject TaskanaEngine taskanaEngine;
  @TaskanaInject TaskService taskService;
  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject ClassificationService classificationService;

  ClassificationSummary classificationSummary;
  WorkbasketSummary domainAWorkbasketSummary;
  WorkbasketSummary domainBWorkbasketSummary;
  Task task1;
  Task task2;
  Task task3;
  Task task4;
  SimpleHistoryServiceImpl historyService;
  TaskanaHistoryEngineImpl taskanaHistoryEngine;

  @WithAccessId(user = "admin")
  @BeforeAll
  void setup() throws Exception {
    historyService = new SimpleHistoryServiceImpl();
    taskanaHistoryEngine = TaskanaHistoryEngineImpl.createTaskanaEngine(taskanaEngine);
    historyService.initialize(taskanaEngine);
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);
    domainAWorkbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket()
            .domain("DOMAIN_A")
            .buildAndStoreAsSummary(workbasketService);
    domainBWorkbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket()
            .domain("DOMAIN_B")
            .buildAndStoreAsSummary(workbasketService);

    task1 =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(domainAWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task2 =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(domainAWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);
    task3 =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(domainBWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    task4 =
        TaskBuilder.newTask()
            .classificationSummary(classificationSummary)
            .workbasketSummary(domainAWorkbasketSummary)
            .primaryObjRef(DefaultTestEntities.defaultTestObjectReference().build())
            .buildAndStore(taskService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(domainAWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .workbasketId(domainBWorkbasketSummary.getId())
        .accessId("user-1-1")
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateRerouteHistoryEvent_When_TaskIsRerouted() throws Exception {
    historyService.deleteHistoryEventsByTaskIds(List.of(task4.getId()));
    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();
    taskService.rerouteTask(task4.getId());

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(task4.getId()));

    assertThat(events).hasSize(1);
    String eventType = events.get(0).getEventType();
    assertThat(eventType).isEqualTo(TaskHistoryEventType.REROUTED.getName());
    assertRerouteHistoryEvent(
        events.get(0).getId(),
        domainAWorkbasketSummary.getId(),
        domainBWorkbasketSummary.getId(),
        "admin");

    historyService.deleteHistoryEventsByTaskIds(List.of(task4.getId()));
  }

  @WithAccessId(user = "admin")
  @Test
  void should_CreateRerouteHistoryEvent_When_MultipleTasksAreRerouted() throws Exception {
    List<String> taskIds = List.of(task1.getId(), task2.getId(), task3.getId());
    historyService.deleteHistoryEventsByTaskIds(taskIds);
    TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();
    taskService.rerouteTasks(taskIds);

    List<TaskHistoryEvent> events =
        taskHistoryQueryMapper.queryHistoryEvents(
            (TaskHistoryQueryImpl)
                historyService.createTaskHistoryQuery().taskIdIn(taskIds.toArray(new String[0])));

    assertThat(events)
        .extracting(TaskHistoryEvent::getTaskId)
        .containsExactlyInAnyOrderElementsOf(taskIds);

    for (TaskHistoryEvent event : events) {
      if (event.getTaskId().equals(task1.getId())) {
        assertRerouteHistoryEvent(
            event.getId(),
            domainAWorkbasketSummary.getId(),
            domainBWorkbasketSummary.getId(),
            "admin");
      } else if (event.getTaskId().equals(task2.getId())) {
        assertRerouteHistoryEvent(
            event.getId(),
            domainAWorkbasketSummary.getId(),
            domainBWorkbasketSummary.getId(),
            "admin");
      } else {
        assertRerouteHistoryEvent(
            event.getId(),
            domainBWorkbasketSummary.getId(),
            domainAWorkbasketSummary.getId(),
            "admin");
      }
    }
  }

  private TaskHistoryQueryMapper getHistoryQueryMapper()
      throws NoSuchFieldException, IllegalAccessException {
    Field sessionManagerField = TaskanaHistoryEngineImpl.class.getDeclaredField("sessionManager");
    sessionManagerField.setAccessible(true);
    SqlSessionManager sqlSessionManager =
        (SqlSessionManager) sessionManagerField.get(taskanaHistoryEngine);

    return sqlSessionManager.getMapper(TaskHistoryQueryMapper.class);
  }

  private void assertRerouteHistoryEvent(
      String eventId, String expectedOldValue, String expectedNewValue, String expectedUser)
      throws Exception {
    TaskHistoryEvent event = historyService.getTaskHistoryEvent(eventId);
    assertThat(event.getDetails()).isNotNull();
    JSONArray changes = new JSONObject(event.getDetails()).getJSONArray("changes");
    assertThat(changes.length()).isPositive();
    boolean foundField = false;
    for (int i = 0; i < changes.length() && !foundField; i++) {
      JSONObject change = changes.getJSONObject(i);
      if (change.get("fieldName").equals("workbasketSummary")) {
        foundField = true;
        String oldWorkbasketStr = change.get("oldValue").toString();
        String newWorkbasketStr = change.get("newValue").toString();
        Workbasket oldWorkbasket = workbasketService.getWorkbasket(expectedOldValue);
        assertThat(oldWorkbasketStr)
            .isEqualTo(JSONObject.wrap(oldWorkbasket.asSummary()).toString());
        Workbasket newWorkbasket = workbasketService.getWorkbasket(expectedNewValue);
        assertThat(newWorkbasketStr)
            .isEqualTo(JSONObject.wrap(newWorkbasket.asSummary()).toString());
      }
    }
    assertThat(foundField).describedAs("changes do not contain field 'workbasketSummary'").isTrue();

    assertThat(event.getId()).startsWith("THI:");
    assertThat(event.getOldValue()).isEqualTo(expectedOldValue);
    assertThat(event.getNewValue()).isEqualTo(expectedNewValue);
    assertThat(event.getUserId()).isEqualTo(expectedUser);
    assertThat(event.getEventType()).isEqualTo(TaskHistoryEventType.REROUTED.getName());
  }

  class TaskRoutingProviderForDomainA implements TaskRoutingProvider {

    @Override
    public void initialize(TaskanaEngine taskanaEngine) {}

    @Override
    public String determineWorkbasketId(Task task) {
      if ("DOMAIN_A".equals(task.getDomain())) {
        return domainBWorkbasketSummary.getId();
      } else if ("DOMAIN_B".equals(task.getDomain())) {
        return domainAWorkbasketSummary.getId();
      }
      return null;
    }
  }
}
