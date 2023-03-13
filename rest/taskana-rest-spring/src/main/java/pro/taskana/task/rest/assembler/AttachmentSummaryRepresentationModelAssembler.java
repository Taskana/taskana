package pro.taskana.task.rest.assembler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import pro.taskana.classification.rest.assembler.ClassificationSummaryRepresentationModelAssembler;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.AttachmentSummary;
import pro.taskana.task.internal.models.AttachmentSummaryImpl;
import pro.taskana.task.rest.models.AttachmentSummaryRepresentationModel;

/** EntityModel assembler for {@link AttachmentSummaryRepresentationModel}. */
@Component
public class AttachmentSummaryRepresentationModelAssembler
    implements RepresentationModelAssembler<
        AttachmentSummary, AttachmentSummaryRepresentationModel> {

  private final ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler;
  private final ObjectReferenceRepresentationModelAssembler objectReferenceAssembler;
  private final TaskService taskService;

  @Autowired
  public AttachmentSummaryRepresentationModelAssembler(
      TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationSummaryAssembler,
      ObjectReferenceRepresentationModelAssembler objectReferenceAssembler) {
    this.taskService = taskService;
    this.classificationSummaryAssembler = classificationSummaryAssembler;
    this.objectReferenceAssembler = objectReferenceAssembler;
  }

  @NonNull
  @Override
  public AttachmentSummaryRepresentationModel toModel(@NonNull AttachmentSummary summary) {
    AttachmentSummaryRepresentationModel repModel = new AttachmentSummaryRepresentationModel();
    repModel.setAttachmentId(summary.getId());
    repModel.setTaskId(summary.getTaskId());
    repModel.setCreated(summary.getCreated());
    repModel.setModified(summary.getModified());
    repModel.setReceived(summary.getReceived());
    repModel.setClassificationSummary(
        classificationSummaryAssembler.toModel(summary.getClassificationSummary()));
    repModel.setObjectReference(objectReferenceAssembler.toModel(summary.getObjectReference()));
    repModel.setChannel(summary.getChannel());
    return repModel;
  }

  public AttachmentSummary toEntityModel(AttachmentSummaryRepresentationModel repModel) {
    AttachmentSummaryImpl attachment =
        (AttachmentSummaryImpl) taskService.newAttachment().asSummary();
    attachment.setId(repModel.getAttachmentId());
    attachment.setTaskId(repModel.getTaskId());
    attachment.setCreated(repModel.getCreated());
    attachment.setModified(repModel.getModified());
    attachment.setReceived(repModel.getReceived());
    attachment.setClassificationSummary(
        classificationSummaryAssembler.toEntityModel(repModel.getClassificationSummary()));
    attachment.setObjectReference(objectReferenceAssembler.toEntity(repModel.getObjectReference()));
    attachment.setChannel(repModel.getChannel());
    return attachment;
  }
}
