package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import pro.taskana.workbasket.api.WorkbasketService;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketDistributionTargetsSetAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = taskanaEngine.getWorkbasketService();
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
        Arrays.asList(
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
