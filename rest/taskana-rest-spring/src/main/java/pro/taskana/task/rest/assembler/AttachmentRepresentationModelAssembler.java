package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.internal.models.AttachmentImpl;
import pro.taskana.task.rest.AttachmentController;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;

/**
 * EntityModel assembler for {@link AttachmentRepresentationModel}.
 */
@Component
public class AttachmentRepresentationModelAssembler
    implements RepresentationModelAssembler<Attachment, AttachmentRepresentationModel> {

  private final TaskService taskService;

  private final ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler;

  @Autowired
  public AttachmentRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler) {
    this.taskService = taskService;
    this.classificationSummaryAssembler = classificationSummaryAssembler;
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
    repModel.setObjectReference(attachment.getObjectReference());
    repModel.setChannel(attachment.getChannel());
    repModel.setCustomAttributes(attachment.getCustomAttributes());
    repModel.add(linkTo(AttachmentController.class).slash(attachment.getId()).withSelfRel());
    return repModel;
  }

  public AttachmentImpl toEntityModel(
      AttachmentRepresentationModel attachmentRepresentationModel) {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    attachment.setId(attachmentRepresentationModel.getAttachmentId());
    attachment.setTaskId(attachmentRepresentationModel.getTaskId());
    attachment.setCreated(attachmentRepresentationModel.getCreated());
    attachment.setModified(attachmentRepresentationModel.getModified());
    attachment.setReceived(attachmentRepresentationModel.getReceived());
    attachment.setClassificationSummary(
        classificationSummaryAssembler.toEntityModel(
            attachmentRepresentationModel.getClassificationSummary()));
    attachment.setObjectReference(attachmentRepresentationModel.getObjectReference());
    attachment.setChannel(attachmentRepresentationModel.getChannel());
    attachment.setCustomAttributes(attachmentRepresentationModel.getCustomAttributes());
    return attachment;
  }
}

