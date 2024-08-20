package acceptance.events.workbasket;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.workbasket.WorkbasketHistoryEventMapper;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import io.kadai.workbasket.api.WorkbasketCustomField;
import io.kadai.workbasket.api.WorkbasketService;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.api.models.Workbasket;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnWorkbasketUpdateAccTest extends AbstractAccTest {

  private final WorkbasketService workbasketService = kadaiEngine.getWorkbasketService();
  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final WorkbasketHistoryEventMapper workbasketHistoryEventMapper =
      getWorkbasketHistoryEventMapper();

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateWorkbasketUpdatedHistoryEvent_When_WorkbasketIsUpdated() throws Exception {

    Workbasket workbasket = workbasketService.getWorkbasket("GPK_KSC", "DOMAIN_A");

    List<WorkbasketHistoryEvent> events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasket.getId()).list();

    assertThat(events).isEmpty();

    workbasket.setName("new name");
    workbasket.setDescription("new description");
    workbasket.setType(WorkbasketType.TOPIC);
    workbasket.setOrgLevel1("new level 1");
    workbasket.setOrgLevel2("new level 2");
    workbasket.setOrgLevel3("new level 3");
    workbasket.setOrgLevel4("new level 4");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_1, "new custom 1");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_2, "new custom 2");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_3, "new custom 3");
    workbasket.setCustomField(WorkbasketCustomField.CUSTOM_4, "new custom 4");
    workbasket.setDescription("new description");
    workbasketService.updateWorkbasket(workbasket);

    events =
        historyService.createWorkbasketHistoryQuery().workbasketIdIn(workbasket.getId()).list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = workbasketHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(WorkbasketHistoryEventType.UPDATED.getName());

    assertThat(details).contains("\"new level 1\"");
  }
}
