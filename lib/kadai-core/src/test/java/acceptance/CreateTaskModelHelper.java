package acceptance;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;

import io.kadai.classification.api.models.Classification;
import io.kadai.classification.internal.models.ClassificationImpl;
import io.kadai.task.api.TaskState;
import io.kadai.task.internal.models.AttachmentImpl;
import io.kadai.task.internal.models.TaskImpl;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class CreateTaskModelHelper {

  public static Classification createDummyClassification() {
    ClassificationImpl classification = new ClassificationImpl();
    classification.setName("dummy-classification");
    classification.setDomain("dummy-domain");
    classification.setKey("dummy-classification-key");
    classification.setId("DummyClassificationId");
    return classification;
  }

  public static AttachmentImpl createAttachment(String id, String taskId) {
    AttachmentImpl attachment = new AttachmentImpl();
    attachment.setId(id);
    attachment.setTaskId(taskId);
    return attachment;
  }

  public static TaskImpl createUnitTestTask(
      String id, String name, String workbasketKey, Classification classification) {
    TaskImpl task = new TaskImpl();
    task.setId(id);
    task.setExternalId(id);
    task.setName(name);
    task.setWorkbasketKey(workbasketKey);
    task.setDomain(MASTER_DOMAIN);
    task.setAttachments(new ArrayList<>());
    Instant now = Instant.now().minus(Duration.ofMinutes(1L));
    task.setReceived(now);
    task.setCreated(now);
    task.setModified(now);
    task.setState(TaskState.READY);
    if (classification == null) {
      classification = createDummyClassification();
    }
    task.setClassificationSummary(classification.asSummary());
    task.setClassificationKey(classification.getKey());
    task.setDomain(classification.getDomain());
    return task;
  }
}
