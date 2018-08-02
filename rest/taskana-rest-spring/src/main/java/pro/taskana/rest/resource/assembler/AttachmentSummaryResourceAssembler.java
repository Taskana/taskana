package pro.taskana.rest.resource.assembler;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.AttachmentSummary;
import pro.taskana.rest.AttachmentController;
import pro.taskana.rest.resource.AttachmentSummaryResource;

/**
 * Resource assembler for {@link AttachmentSummaryResource}.
 */
@Component
public class AttachmentSummaryResourceAssembler
    extends ResourceAssemblerSupport<AttachmentSummary, AttachmentSummaryResource> {

    public AttachmentSummaryResourceAssembler() {
        super(AttachmentController.class, AttachmentSummaryResource.class);
    }

    @Override
    public AttachmentSummaryResource toResource(AttachmentSummary attachmentSummary) {
        AttachmentSummaryResource resource = createResourceWithId(attachmentSummary.getId(),
                attachmentSummary);
        BeanUtils.copyProperties(attachmentSummary, resource);
        if (attachmentSummary.getCreated() != null) {
            resource.setCreated(attachmentSummary.getCreated().toString());
        }
        if (attachmentSummary.getModified() != null) {
            resource.setModified(attachmentSummary.getModified().toString());
        }
        if (attachmentSummary.getReceived() != null) {
            resource.setReceived(attachmentSummary.getReceived().toString());
        }
        resource.setAttachmentId(attachmentSummary.getId());
        return resource;
    }
}
