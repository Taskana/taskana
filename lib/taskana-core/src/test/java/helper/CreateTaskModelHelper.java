package helper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.internal.models.ClassificationImpl;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.internal.models.TaskImpl;

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
    task.setDomain("");
    task.setAttachments(new ArrayList<>());
    Instant now = Instant.now().minus(Duration.ofMinutes(1L));
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
