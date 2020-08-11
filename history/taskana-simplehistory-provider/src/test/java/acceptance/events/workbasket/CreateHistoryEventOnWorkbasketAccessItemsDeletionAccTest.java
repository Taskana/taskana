package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import pro.taskana.workbasket.api.WorkbasketService;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketAccessItemsDeletionAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateWorkbasketAccessItemDeletedHistoryEvents_When_AccessItemsAreDeleted()
      throws Exception {

    final String accessId = "teamlead-1";

    String[] workbasketIds =
        new String[] {
          "WBI:100000000000000000000000000000000001",
          "WBI:100000000000000000000000000000000004",
          "WBI:100000000000000000000000000000000005",
          "WBI:100000000000000000000000000000000010"
        };

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketIds).list();

    assertThat(events).isEmpty();

    workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

    events = historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketIds).list();

    assertThat(events).hasSize(4);

    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(events)
        .extracting(WorkbasketHistoryEvent::getEventType)
        .containsOnly(WorkbasketHistoryEventType.ACCESS_ITEM_DELETED.getName());

    assertThat(details).contains("\"oldValue\":\"WBI:100000000000000000000000000000000001\"");
  }

  @WithAccessId(user = "admin")
  @Test
  void should_NotCreateWorkbasketAccessItemDeletedHistoryEvents_When_ProvidingInvalidAccessId()
      throws Exception {

    final String workbasketId = "WBI:100000000000000000000000000000000011";

    final String accessId = "NonExistingWorkbasketAccessItemID";

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();

    workbasketService.deleteWorkbasketAccessItemsForAccessId(accessId);

    events = historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasketId).list();

    assertThat(events).isEmpty();
  }
}
