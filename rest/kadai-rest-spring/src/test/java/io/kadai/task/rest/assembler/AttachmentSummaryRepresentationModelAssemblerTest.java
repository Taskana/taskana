package io.kadai.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.classification.api.ClassificationService;
import io.kadai.classification.api.models.ClassificationSummary;
import io.kadai.classification.rest.models.ClassificationSummaryRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.AttachmentSummary;
import io.kadai.task.internal.models.AttachmentSummaryImpl;
import io.kadai.task.internal.models.ObjectReferenceImpl;
import io.kadai.task.rest.models.AttachmentSummaryRepresentationModel;
import io.kadai.task.rest.models.ObjectReferenceRepresentationModel;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Test for {@link AttachmentSummaryRepresentationModelAssembler}. */
@KadaiSpringBootTest
class AttachmentSummaryRepresentationModelAssemblerTest {

  private final AttachmentSummaryRepresentationModelAssembler assembler;
  private final ClassificationService classService;
  private final TaskService taskService;

  @Autowired
  AttachmentSummaryRepresentationModelAssemblerTest(
      AttachmentSummaryRepresentationModelAssembler assembler,
      ClassificationService classService,
      TaskService taskService) {
    this.assembler = assembler;
    this.classService = classService;
    this.taskService = taskService;
  }

  static void testEquality(
      AttachmentSummary attachment, AttachmentSummaryRepresentationModel repModel) {
    assertThat(attachment).hasNoNullFieldsOrProperties();
    assertThat(repModel).hasNoNullFieldsOrProperties();
    assertThat(attachment.getId()).isEqualTo(repModel.getAttachmentId());
    assertThat(attachment.getTaskId()).isEqualTo(repModel.getTaskId());
    assertThat(attachment.getCreated()).isEqualTo(repModel.getCreated());
    assertThat(attachment.getModified()).isEqualTo(repModel.getModified());
    assertThat(attachment.getReceived()).isEqualTo(repModel.getReceived());
    assertThat(attachment.getClassificationSummary().getId())
        .isEqualTo(repModel.getClassificationSummary().getClassificationId());
    ObjectReferenceRepresentationModelAssemblerTest.testEquality(
        attachment.getObjectReference(), repModel.getObjectReference());
    assertThat(attachment.getChannel()).isEqualTo(repModel.getChannel());
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
    AttachmentSummaryRepresentationModel repModel = new AttachmentSummaryRepresentationModel();
    repModel.setClassificationSummary(summary);
    repModel.setAttachmentId("id");
    repModel.setTaskId("taskId");
    repModel.setChannel("channel");
    repModel.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    repModel.setObjectReference(reference);
    repModel.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    AttachmentSummary attachment = assembler.toEntityModel(repModel);

    testEquality(attachment, repModel);
  }

  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    AttachmentSummaryImpl attachment =
        (AttachmentSummaryImpl) taskService.newAttachment().asSummary();
    ObjectReferenceImpl reference = new ObjectReferenceImpl();
    ClassificationSummary summary =
        classService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    reference.setId("abc");
    attachment.setClassificationSummary(summary);
    attachment.setId("id");
    attachment.setTaskId("taskId");
    attachment.setChannel("channel");
    attachment.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setObjectReference(reference);
    attachment.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    AttachmentSummaryRepresentationModel repModel = assembler.toModel(attachment);

    testEquality(attachment, repModel);
    testLinks(repModel);
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    AttachmentSummaryImpl attachment =
        (AttachmentSummaryImpl) taskService.newAttachment().asSummary();
    ObjectReferenceImpl reference = new ObjectReferenceImpl();
    ClassificationSummary summary =
        classService.newClassification("ckey", "cdomain", "MANUAL").asSummary();
    reference.setId("abc");
    attachment.setClassificationSummary(summary);
    attachment.setId("id");
    attachment.setTaskId("taskId");
    attachment.setChannel("channel");
    attachment.setCreated(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setModified(Instant.parse("2019-09-13T08:44:17.588Z"));
    attachment.setObjectReference(reference);
    attachment.setReceived(Instant.parse("2019-09-13T08:44:17.588Z"));

    AttachmentSummaryRepresentationModel repModel = assembler.toModel(attachment);
    AttachmentSummary attachment2 = assembler.toEntityModel(repModel);

    assertThat(attachment)
        .hasNoNullFieldsOrProperties()
        .isNotSameAs(attachment2)
        .isEqualTo(attachment2);
  }

  private void testLinks(AttachmentSummaryRepresentationModel repModel) {}
}
