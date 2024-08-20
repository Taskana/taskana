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
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketDistributionTargetsSetAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void
      should_CreateWorkbasketDistributionTargetsUpdatedHistoryEvent_When_DistributionTargetsAreSet()
          throws Exception {

    final String sourceWorkbasketId = "WBI:100000000000000000000000000000000004";

    List<String> targetWorkbaskets =
        List.of(
            "WBI:100000000000000000000000000000000002", "WBI:100000000000000000000000000000000003");

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(sourceWorkbasketId).list();

    assertThat(events).isEmpty();

    workbasketService.setDistributionTargets(
        "WBI:100000000000000000000000000000000004", targetWorkbaskets);

    events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(sourceWorkbasketId).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType)
        .isEqualTo(WorkbasketHistoryEventType.DISTRIBUTION_TARGETS_UPDATED.getName());

    assertThat(details)
        .contains(
            "\"newValue\":[\"WBI:100000000000000000000000000000000002\","
                + "\"WBI:100000000000000000000000000000000003\"");
  }
}
