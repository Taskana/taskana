package acceptance.events.classification;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.classification.api.ClassificationCustomField;
import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.Classification;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClassificationsUpdateAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final ClassificationService classificationService =
      kadaiEngine.getClassificationService();
  private final ClassificationHistoryEventMapper classificationHistoryEventMapper =
      getClassificationHistoryEventMapper();

  @WithAccessId(user = "businessadmin")
  @Test
  void should_CreateClassificationUpdatedHistoryEvent_When_ClassificationIsUpdated()
      throws Exception {

    Classification classification =
        classificationService.getClassification("CLI:000000000000000000000000000000000017");

    List<ClassificationHistoryEvent> events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classification.getId())
            .list();

    assertThat(events).isEmpty();

    classification.setName("new name");
    classification.setDescription("new description");
    classification.setCategory("EXTERNAL");
    classification.setCustomField(ClassificationCustomField.CUSTOM_1, "new custom 1");
    classification.setCustomField(ClassificationCustomField.CUSTOM_2, "new custom 2");
    classification.setCustomField(ClassificationCustomField.CUSTOM_3, "new custom 3");
    classification.setCustomField(ClassificationCustomField.CUSTOM_4, "new custom 4");
    classificationService.updateClassification(classification);

    events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classification.getId())
            .list();

    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = classificationHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(WorkbasketHistoryEventType.UPDATED.getName());

    assertThat(details).contains("\"newValue\":\"new description\"");
  }
}
