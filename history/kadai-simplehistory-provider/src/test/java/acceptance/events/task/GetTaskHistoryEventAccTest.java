package acceptance.events.task;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.KadaiConfiguration;
import io.kadai.spi.history.api.events.task.TaskHistoryEvent;
import io.kadai.spi.history.api.events.task.TaskHistoryEventType;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;

class GetTaskHistoryEventAccTest extends AbstractAccTest {

  @Test
  void should_ReturnSpecificTaskHistoryEventWithDetails_For_HistoryEventId() throws Exception {

    String detailsJson =
        "{\"changes\":[{"
            + "\"newValue\":\"BPI:01\","
            + "\"fieldName\":\"businessProcessId\","
            + "\"oldValue\":\"BPI:02\"},"
            + "{\"newValue\":\"user-1-1\","
            + "\"fieldName\":\"owner\","
            + "\"oldValue\":\"owner1\"}]}";

    TaskHistoryEvent taskHistoryEvent =
        getHistoryService().getTaskHistoryEvent("THI:000000000000000000000000000000000000");
    assertThat(taskHistoryEvent.getBusinessProcessId()).isEqualTo("BPI:01");
    assertThat(taskHistoryEvent.getUserId()).isEqualTo("user-1-1");
    assertThat(taskHistoryEvent.getEventType()).isEqualTo(TaskHistoryEventType.UPDATED.getName());
    assertThat(taskHistoryEvent.getDetails()).isEqualTo(detailsJson);
  }

  @Test
  void should_SetTaskOwnerLongNameOfTask_When_PropertyEnabled() throws Exception {

    createKadaiEngineWithNewConfig(true);

    TaskHistoryEvent taskHistoryEvent =
        getHistoryService().getTaskHistoryEvent("THI:000000000000000000000000000000000000");
    assertThat(taskHistoryEvent.getUserId()).isEqualTo("user-1-1");

    String userLongName =
        kadaiEngine.getUserService().getUser(taskHistoryEvent.getUserId()).getLongName();
    assertThat(taskHistoryEvent)
        .extracting(TaskHistoryEvent::getUserLongName)
        .isEqualTo(userLongName);
  }

  @Test
  void should_NotSetTaskOwnerLongNameOfTask_When_PropertyDisabled() throws Exception {

    createKadaiEngineWithNewConfig(false);

    TaskHistoryEvent taskHistoryEvent =
        getHistoryService().getTaskHistoryEvent("THI:000000000000000000000000000000000000");

    assertThat(taskHistoryEvent.getUserId()).isEqualTo("user-1-1");

    assertThat(taskHistoryEvent).extracting(TaskHistoryEvent::getUserLongName).isNull();
  }

  private void createKadaiEngineWithNewConfig(boolean addAdditionalUserInfo) throws SQLException {
    KadaiConfiguration configuration =
        new KadaiConfiguration.Builder(AbstractAccTest.kadaiConfiguration)
            .addAdditionalUserInfo(addAdditionalUserInfo)
            .build();
    initKadaiEngine(configuration);
  }
}
