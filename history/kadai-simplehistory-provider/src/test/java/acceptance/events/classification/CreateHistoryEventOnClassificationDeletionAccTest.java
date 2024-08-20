package acceptance.events.classification;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.classification.api.ClassificationService;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.simplehistory.impl.SimpleHistoryServiceImpl;
import io.kadai.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEvent;
import io.kadai.spi.history.api.events.classification.ClassificationHistoryEventType;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClassificationDeletionAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final ClassificationService classificationService =
      kadaiEngine.getClassificationService();
  private final ClassificationHistoryEventMapper classificationHistoryEventMapper =
      getClassificationHistoryEventMapper();

  @WithAccessId(user = "admin")
  @Test
  void should_CreateClassificationDeletedHistoryEvent_When_ClassificationIsDeleted()
      throws Exception {

    final String classificationId = "CLI:200000000000000000000000000000000015";

    List<ClassificationHistoryEvent> events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classificationId)
            .list();

    assertThat(events).isEmpty();

    classificationService.deleteClassification(classificationId);

    events =
        historyService
            .createClassificationHistoryQuery()
            .classificationIdIn(classificationId)
            .list();
    assertThat(events).hasSize(1);

    String eventType = events.get(0).getEventType();
    String details = classificationHistoryEventMapper.findById(events.get(0).getId()).getDetails();

    assertThat(eventType).isEqualTo(ClassificationHistoryEventType.DELETED.getName());

    assertThat(details).contains("\"oldValue\":\"CLI:200000000000000000000000000000000015\"");
  }
}
