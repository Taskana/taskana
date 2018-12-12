package pro.taskana.rest.resource;

import java.util.List;
import pro.taskana.AttachmentSummary;

/**
 * Resources assembler for {@link AttachmentSummaryResource}.
 */
public class AttachmentSummaryResourcesAssembler {

    public List<AttachmentSummaryResource> toResources(
            List<AttachmentSummary> attachmentSummaries) {
        AttachmentSummaryResourceAssembler assembler = new AttachmentSummaryResourceAssembler();
        List<AttachmentSummaryResource> resources = assembler.toResources(attachmentSummaries);
        return resources;
    }
}
