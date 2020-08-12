package acceptance.events.classification;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import acceptance.security.JaasExtension;
import acceptance.security.WithAccessId;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.classification.ClassificationHistoryEventMapper;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEventType;

@ExtendWith(JaasExtension.class)
class CreateHistoryEventOnClassificationDeletionAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();
  private final ClassificationService classificationService =
      taskanaEngine.getClassificationService();
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
