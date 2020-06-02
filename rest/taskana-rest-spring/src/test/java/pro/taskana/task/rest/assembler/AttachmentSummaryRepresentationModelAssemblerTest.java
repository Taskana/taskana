package pro.taskana.task.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.rest.models.AttachmentSummaryRepresentationModel;

/**
 * Test for {@link AttachmentSummaryRepresentationModelAssembler}.
 */
@TaskanaSpringBootTest
class AttachmentSummaryRepresentationModelAssemblerTest {

  @Autowired
  AttachmentSummaryRepresentationModelAssembler assembler;

  @Autowired
  ClassificationService classService;

  @Autowired
  TaskService taskService;

  @Test
  void should_ReturnEntity_When_ConvertingRepresentationModelToEntity() {
    ObjectReference reference = new ObjectReference();
    reference.setId("abc");
    ClassificationSummaryRepresentationModel summary
        = new ClassificationSummaryRepresentationModel();
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

    AttachmentSummaryImpl attachment = (AttachmentSummaryImpl) assembler.toEntityModel(repModel);

    testEqualityAfterConversion(repModel, attachment);
  }


  @Test
  void should_ReturnRepresentationModel_When_ConvertingEntityToRepresentationModel() {
    AttachmentSummaryImpl attachment = (AttachmentSummaryImpl) taskService.newAttachment();
    ObjectReference reference = new ObjectReference();
    ClassificationSummary summary = classService.newClassification("ckey", "cdomain", "MANUAL");
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

    testEqualityAfterConversion(repModel, attachment);
    testLinks();
  }

  @Test
  void should_Equal_When_ComparingEntityWithConvertedEntity() {
    AttachmentSummaryImpl attachment = (AttachmentSummaryImpl) taskService.newAttachment();
    ObjectReference reference = new ObjectReference();
    ClassificationSummary summary = classService.newClassification("ckey", "cdomain", "MANUAL");
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
    AttachmentSummaryImpl attachment2 = (AttachmentSummaryImpl) assembler.toEntityModel(repModel);

    testEqualityOfEntities(attachment, attachment2);
  }

  void testEqualityAfterConversion(AttachmentSummaryRepresentationModel repModel,
      AttachmentSummary attachment) {
    assertThat(repModel.getAttachmentId()).isEqualTo(attachment.getId());
    assertThat(repModel.getTaskId()).isEqualTo(attachment.getTaskId());
    assertThat(repModel.getChannel()).isEqualTo(attachment.getChannel());
    assertThat(repModel.getCreated()).isEqualTo(attachment.getCreated());
    assertThat(repModel.getModified()).isEqualTo(attachment.getModified());
    assertThat(repModel.getObjectReference()).isEqualTo(attachment.getObjectReference());
    assertThat(repModel.getReceived()).isEqualTo(attachment.getReceived());
    assertThat(repModel.getClassificationSummary().getClassificationId())
        .isEqualTo(attachment.getClassificationSummary().getId());
  }

  void testLinks() {
  }

  private void testEqualityOfEntities(AttachmentSummary attachment, AttachmentSummary attachment2) {
    assertThat(attachment2.getId()).isEqualTo(attachment.getId());
    assertThat(attachment2.getTaskId()).isEqualTo(attachment.getTaskId());
    assertThat(attachment2.getChannel()).isEqualTo(attachment.getChannel());
    assertThat(attachment2.getCreated()).isEqualTo(attachment.getCreated());
    assertThat(attachment2.getModified()).isEqualTo(attachment.getModified());
    assertThat(attachment2.getObjectReference()).isEqualTo(attachment.getObjectReference());
    assertThat(attachment2.getReceived()).isEqualTo(attachment.getReceived());
    assertThat(attachment2.getClassificationSummary().getId())
        .isEqualTo(attachment.getClassificationSummary().getId());
  }

}
