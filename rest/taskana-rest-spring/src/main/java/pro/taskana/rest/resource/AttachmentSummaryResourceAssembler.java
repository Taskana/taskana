package pro.taskana.rest.resource;

import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.rest.AttachmentController;
import pro.taskana.task.api.models.AttachmentSummary;

/** EntityModel assembler for {@link AttachmentSummaryResource}. */
@Component
public class AttachmentSummaryResourceAssembler
    extends RepresentationModelAssemblerSupport<AttachmentSummary, AttachmentSummaryResource> {

  public AttachmentSummaryResourceAssembler() {
    super(AttachmentController.class, AttachmentSummaryResource.class);
  }

  @Override
  public AttachmentSummaryResource toModel(AttachmentSummary attachmentSummary) {
    return new AttachmentSummaryResource(attachmentSummary);
  }
}
