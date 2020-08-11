package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Test;

import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;


class GetTaskHistoryEventAccTest extends AbstractAccTest {

  @Test
  void should_ReturnSpecificTaskHistoryEventWithDetails_For_HistoryEventId() throws Exception {

    String detailsJson =
        "{\"changes\":[{"
            + "\"newValue\":\"BPI:01\","
            + "\"fieldName\":\"businessProcessId\","
            + "\"oldValue\":\"BPI:02\"},"
            + "{\"newValue\":\"admin\","
            + "\"fieldName\":\"owner\","
            + "\"oldValue\":\"owner1\"}]}";

    TaskHistoryEvent taskHistoryEvent =
        getHistoryService().getTaskHistoryEvent("HEI:000000000000000000000000000000000000");
    assertThat(taskHistoryEvent.getBusinessProcessId()).isEqualTo("BPI:01");
    assertThat(taskHistoryEvent.getUserId()).isEqualTo("admin");
    assertThat(taskHistoryEvent.getEventType()).isEqualTo(TaskHistoryEventType.UPDATED.getName());
    assertThat(taskHistoryEvent.getDetails()).isEqualTo(detailsJson);
  }
}
