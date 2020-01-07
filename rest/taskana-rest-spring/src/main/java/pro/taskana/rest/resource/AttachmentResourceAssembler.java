package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.Attachment;
import pro.taskana.TaskService;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.rest.AttachmentController;

/** Resource assembler for {@link AttachmentResource}. */
@Component
public class AttachmentResourceAssembler
    extends ResourceAssemblerSupport<Attachment, AttachmentResource> {

  @Autowired private TaskService taskService;

  @Autowired private ClassificationSummaryResourceAssembler classificationAssembler;

  public AttachmentResourceAssembler() {
    super(AttachmentController.class, AttachmentResource.class);
  }

  @Override
  public AttachmentResource toResource(Attachment attachment) {
    AttachmentResource resource = new AttachmentResource(attachment);
    resource.add(linkTo(AttachmentController.class).slash(attachment.getId()).withSelfRel());
    return resource;
  }

  public List<Attachment> toModel(List<AttachmentResource> resources) {
    return resources.stream()
        .map(
            attachmentResource -> {
              AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
              BeanUtils.copyProperties(attachmentResource, attachment);
              attachment.setId(attachmentResource.getAttachmentId());
              attachment.setClassificationSummary(
                  classificationAssembler.toModel(attachmentResource.getClassificationSummary()));
              return attachment;
            })
        .collect(Collectors.toList());
  }
}
