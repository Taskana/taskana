package acceptance.builder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import acceptance.DefaultTestEntities;
import acceptance.FooBar;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.internal.ClassificationBuilder;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.TaskAttachmentBuilder;
import pro.taskana.task.internal.models.AttachmentImpl;

@ExtendWith(JaasExtension.class)
class TaskAttachmentBuilderTest {

  private static ClassificationService classificationService;
  private static TaskService taskService;

  @BeforeAll
  static void setup() throws Exception {
    TaskanaEngine taskanaEngine = FooBar.getTaskanaEngineForTests();
    classificationService = taskanaEngine.getClassificationService();
    taskService = taskanaEngine.getTaskService();
  }

  @Test
  void should_PopulateAttachment_When_UsingEveryBuilderFunction() throws Exception {
    ClassificationSummary classificationSummary =
        ClassificationBuilder.newClassification()
            .domain("DOMAIN_A")
            .key("key")
            .buildAndStore(classificationService, "businessadmin")
            .asSummary();
    ObjectReference objectReference = DefaultTestEntities.defaultTestObjectReference().build();

    final Attachment attachment =
        TaskAttachmentBuilder.newAttachment()
            .received(Instant.parse("2010-01-01T12:00:00Z"))
            .created(Instant.parse("2010-01-02T12:00:00Z"))
            .modified(Instant.parse("2010-01-03T12:00:00Z"))
            .classificationSummary(classificationSummary)
            .objectReference(objectReference)
            .channel("Channel Super Fun")
            .customAttributes(Map.of("custom", "attribute"))
            .build();

    AttachmentImpl expectedAttachment = (AttachmentImpl) taskService.newAttachment();
    expectedAttachment.setReceived(Instant.parse("2010-01-01T12:00:00Z"));
    expectedAttachment.setCreated(Instant.parse("2010-01-02T12:00:00Z"));
    expectedAttachment.setModified(Instant.parse("2010-01-03T12:00:00Z"));
    expectedAttachment.setClassificationSummary(classificationSummary);
    expectedAttachment.setObjectReference(objectReference);
    expectedAttachment.setChannel("Channel Super Fun");
    expectedAttachment.setCustomAttributes(Map.of("custom", "attribute"));

    assertThat(attachment)
        .hasNoNullFieldsOrPropertiesExcept("id", "taskId")
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedAttachment);
  }
}
