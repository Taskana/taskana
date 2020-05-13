package pro.taskana.task.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
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

  private final ClassificationSummaryRepresentationModelAssembler classificationAssembler;

  @Autowired
  public AttachmentRepresentationModelAssembler(TaskService taskService,
      ClassificationSummaryRepresentationModelAssembler classificationAssembler) {
    this.taskService = taskService;
    this.classificationAssembler = classificationAssembler;
  }

  @NonNull
  @Override
  public AttachmentRepresentationModel toModel(@NonNull Attachment attachment) {
    AttachmentRepresentationModel resource = new AttachmentRepresentationModel(attachment);
    resource.add(linkTo(AttachmentController.class).slash(attachment.getId()).withSelfRel());
    return resource;
  }

  public List<Attachment> toAttachmentList(List<AttachmentRepresentationModel> resources) {
    return resources.stream().map(this::apply).collect(Collectors.toList());
  }

  private AttachmentImpl apply(AttachmentRepresentationModel attachmentRepresentationModel) {
    AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
    BeanUtils.copyProperties(attachmentRepresentationModel, attachment);
    attachment.setId(attachmentRepresentationModel.getAttachmentId());
    attachment.setClassificationSummary(classificationAssembler.toEntityModel(
        attachmentRepresentationModel.getClassificationSummary()));
    return attachment;
  }
}
