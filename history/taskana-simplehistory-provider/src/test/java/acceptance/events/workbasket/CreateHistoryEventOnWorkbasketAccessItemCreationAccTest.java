package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketAccessItemCreationAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateWorkbasketAccessItemCreatedHistoryEvent_When_AccessItemIsCreated()
      throws Exception {

    final String workbasketId = "WBI:100000000000000000000000000000000004";

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();

    WorkbasketAccessItem newWorkbasketAccessItem =
        workbasketService.newWorkbasketAccessItem(workbasketId, "peter");

    workbasketService.createWorkbasketAccessItem(newWorkbasketAccessItem);

    events = historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();

    assertThat(eventType)
        .isEqualTo(WorkbasketHistoryEventType.WORKBASKET_ACCESS_ITEM_CREATED.getName());
  }
}
