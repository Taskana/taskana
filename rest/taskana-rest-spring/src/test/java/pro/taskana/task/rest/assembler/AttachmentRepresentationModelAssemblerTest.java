package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;

/** Test for {@link AttachmentRepresentationModelAssembler}. */
@TaskanaSpringBootTest
class AttachmentRepresentationModelAssemblerTest {

  private final AttachmentRepresentationModelAssembler assembler;
  private final ClassificationService classService;
  private final TaskService taskService;

  @Autowired
  AttachmentRepresentationModelAssemblerTest(
      AttachmentRepresentationModelAssembler assembler,
      ClassificationService classService,
      TaskService taskService) {
    this.assembler = assembler;
    this.classService = classService;
    this.taskService = taskService;
  }

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    ObjectReferenceRepresentationModel reference = new ObjectReferenceRepresentationModel();
    reference.setId("abc");
    ClassificationSummaryRepresentationModel summary =
        new ClassificationSummaryRepresentationModel();
    summary.setKey("keyabc");
    summary.setDomain("DOMAIN_A");
    summary.setType("MANUAL");
    AttachmentRepresentationModel repModel = new AttachmentRepresentationModel();
    repModel.setCustomAttributes(Map.of("abc", "def"));
    repModel.setClassificationSummary(summary);
    repModel.setAttachmentId("id");
    repModel.setTaskId("taskId");
    repModel.setChannel("channel");
    repModel.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setObjectReference(reference);
    repModel.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    Attachment attachment = assembler.toEntityModel(repModel);

    testEquality(attachment, repModel);
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    ObjectReference reference = new ObjectReference();
    ClassificationSummary summary =
        classService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    reference.setId("abc");
    attachment.setCustomAttributeMap(Map.of("abc", "def"));
    attachment.setClassificationSummary(summary);
    attachment.setId("id");
    attachment.setTaskId("taskId");
    attachment.setChannel("channel");
    attachment.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setObjectReference(reference);
    attachment.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    AttachmentRepresentationModel repModel = assembler.toModel(attachment);

    testEquality(attachment, repModel);
    testLinks(repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    ObjectReference reference = new ObjectReference();
    ClassificationSummary summary =
        classService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    reference.setId("abc");
    attachment.setCustomAttributeMap(Map.of("abc", "def"));
    attachment.setClassificationSummary(summary);
    attachment.setId("id");
    attachment.setTaskId("taskId");
    attachment.setChannel("channel");
    attachment.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setObjectReference(reference);
    attachment.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    AttachmentRepresentationModel repModel = assembler.toModel(attachment);
    Attachment attachment2 = assembler.toEntityModel(repModel);

    assertThat(attachment)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(attachment2)
        .isEqualTo(attachment2);
  }

  void testEquality(Attachment attachment, AttachmentRepresentationModel repModel) {
    AttachmentSummaryRepresentationModelAssemblerTest.testEquality(attachment, repModel);

    assertThat(attachment.getCustomAttributeMap()).isEqualTo(repModel.getCustomAttributes());
  }

  void testLinks(AttachmentRepresentationModel repModel) {}
}
