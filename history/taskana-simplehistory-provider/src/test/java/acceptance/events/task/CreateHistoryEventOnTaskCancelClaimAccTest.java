package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
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
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.Task;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnTaskCancelClaimAccTest extends AbstractAccTest {

  private final TaskService taskService = taskanaEngine.getTaskService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @WithAccessId(user = "admin")
  @TestFactory
  Stream<DynamicTest> should_CreateCancelClaimedHistoryEvent_When_TaskIsCancelClaimed() {

    List<Triplet<String, String, String>> list =
        List.of(
            Triplet.of(
                "With Attachment and secondary Object Reference",
                "TKI:000000000000000000000000000000000002",
                "user-1-1"),
            Triplet.of(
                "Without Attachment and secondary Object References",
                "TKI:000000000000000000000000000000000043",
                "user-b-1"));
    ThrowingConsumer<Triplet<String, String, String>> test =
        t -> {
          String taskId = t.getMiddle();
          Task task = taskService.getTask(taskId);
          final Instant oldModified = task.getModified();
          final Instant oldClaimed = task.getClaimed();

          TaskHistoryQueryMapper taskHistoryQueryMapper = getHistoryQueryMapper();
          List<TaskHistoryEvent> events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

          assertThat(events).isEmpty();

          assertThat(task.getState()).isEqualTo(TaskState.CLAIMED);
          task = taskService.forceCancelClaim(taskId);
          assertThat(task.getState()).isEqualTo(TaskState.READY);

          events =
              taskHistoryQueryMapper.queryHistoryEvents(
                  (TaskHistoryQueryImpl) historyService.createTaskHistoryQuery().taskIdIn(taskId));

          assertThat(events).hasSize(1);

          TaskHistoryEvent event = events.get(0);

          assertThat(event.getEventType())
              .isEqualTo(TaskHistoryEventType.CLAIM_CANCELLED.getName());

          event = historyService.getTaskHistoryEvent(event.getId());

          assertThat(event.getDetails()).isNotNull();

          JSONArray changes = new JSONObject(event.getDetails()).getJSONArray("changes");
          String oldOwner = t.getRight();
          JSONObject expectedClaimed =
              new JSONObject()
                  .put("newValue", "")
                  .put("fieldName", "claimed")
                  .put("oldValue", oldClaimed.toString());
          JSONObject expectedModified =
              new JSONObject()
                  .put("newValue", task.getModified().toString())
                  .put("fieldName", "modified")
                  .put("oldValue", oldModified.toString());
          JSONObject expectedState =
              new JSONObject()
                  .put("newValue", TaskState.READY.name())
                  .put("fieldName", "state")
                  .put("oldValue", TaskState.CLAIMED.name());
          JSONObject expectedOwner =
              new JSONObject()
                  .put("newValue", "")
                  .put("fieldName", "owner")
                  .put("oldValue", oldOwner);

          JSONArray expectedChanges =
              new JSONArray()
                  .put(expectedClaimed)
                  .put(expectedModified)
                  .put(expectedState)
                  .put(expectedOwner);

          assertThat(changes.similar(expectedChanges)).isTrue();
        };
    return DynamicTest.stream(list.iterator(), Triplet::getLeft, test);
  }
}
