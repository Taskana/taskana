package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.models.WorkbasketAccessItem;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketAccessItemsSetAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateWorkbasketAccessItemsUpdatedHistoryEvent_When_AccessItemsAreSet()
      throws Exception {

    final String workbasketId = "WBI:100000000000000000000000000000000004";
    final String accessId1 = "peter";
    final String accessId2 = "claudia";
    final String accessId3 = "sven";

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();

    List<WorkbasketAccessItem> newItems = new ArrayList<>();

    WorkbasketAccessItem newWorkbasketAccessItem =
        workbasketService.newWorkbasketAccessItem(workbasketId, accessId1);
    WorkbasketAccessItem newWorkbasketAccessItem2 =
        workbasketService.newWorkbasketAccessItem(workbasketId, accessId2);
    WorkbasketAccessItem newWorkbasketAccessItem3 =
        workbasketService.newWorkbasketAccessItem(workbasketId, accessId3);

    newItems.add(newWorkbasketAccessItem);
    newItems.add(newWorkbasketAccessItem2);
    newItems.add(newWorkbasketAccessItem3);

    workbasketService.setWorkbasketAccessItems(workbasketId, newItems);

    events = historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(WorkbasketHistoryEventType.ACCESS_ITEMS_UPDATED.getName());

    assertThat(details).contains("peter");
  }
}
