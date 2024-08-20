package io.kadai.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import io.kadai.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Attachment;
import io.kadai.task.internal.models.AttachmentImpl;
import io.kadai.task.rest.AttachmentController;
import io.kadai.task.rest.models.AttachmentRepresentationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/** EntityModel assembler for {@link AttachmentRepresentationModel}. */
@Component
public class AttachmentRepresentationModelAssembler
    implements RepresentationModelAssembler<Attachment, AttachmentRepresentationModel> {

  private final TaskService taskService;

  private final ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler;
  private final ObjectReferenceRepresentationModelAssembler objectReferenceAssembler;

  @Autowired
  public AttachmentRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler,
      ObjectReferenceRepresentationModelAssembler objectReferenceAssembler) {
    this.taskService = taskService;
    this.classificationSummaryAssembler = classificationSummaryAssembler;
    this.objectReferenceAssembler = objectReferenceAssembler;
  }

  @NonNull
  @Override
  public AttachmentRepresentationModel toModel(@NonNull Attachment attachment) {
    AttachmentRepresentationModel repModel = new AttachmentRepresentationModel();
    repModel.setAttachmentId(attachment.getId());
    repModel.setTaskId(attachment.getTaskId());
    repModel.setCreated(attachment.getCreated());
    repModel.setModified(attachment.getModified());
    repModel.setReceived(attachment.getReceived());
    repModel.setClassificationSummary(
        classificationSummaryAssembler.toModel(attachment.getClassificationSummary()));
    repModel.setObjectReference(objectReferenceAssembler.toModel(attachment.getObjectReference()));
    repModel.setChannel(attachment.getChannel());
    repModel.setCustomAttributes(attachment.getCustomAttributeMap());
    repModel.add(linkTo(AttachmentController.class).slash(attachment.getId()).withSelfRel());
    return repModel;
  }

  public Attachment toEntityModel(AttachmentRepresentationModel attachmentRepresentationModel) {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setId(attachmentRepresentationModel.getAttachmentId());
    attachment.setTaskId(attachmentRepresentationModel.getTaskId());
    attachment.setCreated(attachmentRepresentationModel.getCreated());
    attachment.setModified(attachmentRepresentationModel.getModified());
    attachment.setReceived(attachmentRepresentationModel.getReceived());
    attachment.setClassificationSummary(
        classificationSummaryAssembler.toEntityModel(
            attachmentRepresentationModel.getClassificationSummary()));
    if (attachmentRepresentationModel.getObjectReference() != null) {
      attachment.setObjectReference(
          objectReferenceAssembler.toEntity(attachmentRepresentationModel.getObjectReference()));
    }
    attachment.setChannel(attachmentRepresentationModel.getChannel());
    attachment.setCustomAttributeMap(attachmentRepresentationModel.getCustomAttributes());
    return attachment;
  }
}
