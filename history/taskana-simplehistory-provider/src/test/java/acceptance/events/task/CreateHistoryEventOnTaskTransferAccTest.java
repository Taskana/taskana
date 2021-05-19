package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.internal.util.CheckedConsumer.wrap;

import acceptance.AbstractAccTest;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.ThrowingConsumer;

import pro.taskana.common.internal.util.Triplet;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.TaskHistoryQueryImpl;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryMapper;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskTransferAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_CreateTransferredHistoryEvent_When_TaskIsTransferred() {
    List<Triplet<String, String, Consumer<String>>> testCases =
        List.of(
            Triplet.of(
                "Using WorkbasketId",
                "TKI:000000000000000000000000000000000003",
                wrap(
                    (String taskId) ->
                        taskService.transfer(taskId, "WBI:100000000000000000000000000000000006"))),
            Triplet.of(
                "Using WorkbasketKey and Domain",
                "TKI:000000000000000000000000000000000004",
                wrap((String taskId) -> taskService.transfer(taskId, "USER-1-1", "DOMAIN_A"))));

    ThrowingConsumer<Triplet<String, String, Consumer<String>>> test =
        t -> {
          String taskId = t.getMiddle();
          Consumer<String> transferMethod = t.getRight();

          TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

          List<TaskHistoryEvent> events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

          assertThat(events).isEmpty();

          transferMethod.accept(taskId);

          events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

          assertThat(events).hasSize(1);
          assertTransferHistoryEvent(
              events.get(0).getId(),
              "WBI:100000000000000000000000000000000001",
              "WBI:100000000000000000000000000000000006",
              "admin");
        };

    return DynamicTest.stream(testCases.iterator(), Triplet::getLeft, test);
  }

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_CreateTransferredHistoryEvents_When_TaskBulkTransfer() {
    List<Triplet<String, List<String>, Consumer<List<String>>>> testCases =
        List.of(
            Triplet.of(
                "Using WorkbasketId",
                List.of(
                    "TKI:000000000000000000000000000000000021",
                    "TKI:000000000000000000000000000000000022"),
                wrap(
                    (List<String> taskIds) ->
                        taskService.transferTasks(
                            "WBI:100000000000000000000000000000000007", taskIds))),
            Triplet.of(
                "Using WorkbasketKey and Domain",
                List.of(
                    "TKI:000000000000000000000000000000000023",
                    "TKI:000000000000000000000000000000000024"),
                wrap(
                    (List<String> taskIds) ->
                        taskService.transferTasks("USER-1-2", "DOMAIN_A", taskIds))));

    ThrowingConsumer<Triplet<String, List<String>, Consumer<List<String>>>> test =
        t -> {
          List<String> taskIds = t.getMiddle();
          Consumer<List<String>> transferMethod = t.getRight();

          TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();

          List<TaskHistoryEvent> events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl)
                      historyService
                          .createTaskHistoryQuery()
                          .taskIdIn(taskIds.toArray(new String[0])));

          assertThat(events).isEmpty();

          transferMethod.accept(taskIds);

          events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl)
                      historyService
                          .createTaskHistoryQuery()
                          .taskIdIn(taskIds.toArray(new String[0])));

          assertThat(events)
              .extracting(TaskHistoryEvent::getTaskId)
              .containsExactlyInAnyOrderElementsOf(taskIds);

          for (TaskHistoryEvent event : events) {
            assertTransferHistoryEvent(
                event.getId(),
                "WBI:100000000000000000000000000000000001",
                "WBI:100000000000000000000000000000000007",
                "admin");
          }
        };

    return DynamicTest.stream(testCases.iterator(), Triplet::getLeft, test);
  }

  private void assertTransferHistoryEvent(
      String eventId, String expectedOldValue, String expectedNewValue, String expectedUser)
      throws Exception {
    TaskHistoryEvent event = historyService.getTaskHistoryEvent(eventId);
    assertThat(event.getDetails()).isNotNull();
    JSONArray changes = new JSONObject(event.getDetails()).getJSONArray("changes");

    assertThat(changes.length()).isPositive();
    for (int i = 0; i < changes.length(); i++) {
      JSONObject change = changes.getJSONObject(i);
      if (change.get("fieldName").equals("workbasketSummary")) {
        String oldWorkbasketStr = (String) change.get("oldValue");
        String newWorkbasketStr = (String) change.get("newValue");
        WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
        Workbasket oldWorkbasket = workbasketService.getWorkbasket(expectedOldValue);
        assertThat(oldWorkbasket.asSummary()).hasToString(oldWorkbasketStr);
        Workbasket newWorkbasket = workbasketService.getWorkbasket(expectedNewValue);
        assertThat(newWorkbasket.asSummary()).hasToString(newWorkbasketStr);
      }
    }

    assertThat(event.getId()).startsWith("THI:");
    assertThat(event.getOldValue()).isEqualTo(expectedOldValue);
    assertThat(event.getNewValue()).isEqualTo(expectedNewValue);
    assertThat(event.getUserId()).isEqualTo(expectedUser);
    assertThat(event.getEventType()).isEqualTo(TaskHistoryEventType.TRANSFERRED.getName());
  }
}
