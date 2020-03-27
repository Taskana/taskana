package acceptance.events;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import org.junit.jupiter.api.Test;

import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;
import pro.taskana.spi.history.api.exceptions.TaskanaHistoryEventNotFoundException;

public class GetHistoryEventAccTest extends AbstractAccTest {

  @Test
  public void should_ReturnSpecificTaskHistoryEventWithDetails_For_HistoryEventId()
      throws TaskanaHistoryEventNotFoundException {

    String detailsJson =
        "{\"changes\":[{"
            + "\"newValue\":\"BPI:01\","
            + "\"fieldName\":\"businessProcessId\","
            + "\"oldValue\":\"BPI:02\"},"
            + "{\"newValue\":\"admin\","
            + "\"fieldName\":\"owner\","
            + "\"oldValue\":\"owner1\"}]}";

    TaskanaHistoryEvent taskHistoryEvent = getHistoryService().getHistoryEvent("4");
    assertThat(taskHistoryEvent.getBusinessProcessId()).isEqualTo("BPI:01");
    assertThat(taskHistoryEvent.getUserId()).isEqualTo("admin");
    assertThat(taskHistoryEvent.getEventType()).isEqualTo("TASK_UPDATED");
    assertThat(taskHistoryEvent.getDetails()).isEqualTo(detailsJson);
  }
}
