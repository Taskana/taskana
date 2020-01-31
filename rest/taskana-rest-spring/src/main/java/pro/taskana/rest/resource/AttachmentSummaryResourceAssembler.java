package pro.taskana.rest.resource;

import java.util.List;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.task.api.AttachmentSummary;
import pro.taskana.rest.AttachmentController;

/** Resource assembler for {@link AttachmentSummaryResource}. */
@Component
public class AttachmentSummaryResourceAssembler
    extends ResourceAssemblerSupport<AttachmentSummary, AttachmentSummaryResource> {

  public AttachmentSummaryResourceAssembler() {
    super(AttachmentController.class, AttachmentSummaryResource.class);
  }

  @Override
  public AttachmentSummaryResource toResource(AttachmentSummary attachmentSummary) {
    return new AttachmentSummaryResource(attachmentSummary);
  }

  public List<AttachmentSummaryResource> toResources(List<AttachmentSummary> attachmentSummaries) {
    List<AttachmentSummaryResource> resources = super.toResources(attachmentSummaries);
    return resources;
  }
}
