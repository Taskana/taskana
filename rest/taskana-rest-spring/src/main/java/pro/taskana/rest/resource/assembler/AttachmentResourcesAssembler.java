package pro.taskana.rest.resource.assembler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.taskana.Attachment;
import pro.taskana.TaskService;
import pro.taskana.impl.AttachmentImpl;
import pro.taskana.rest.resource.AttachmentResource;

/**
 * Resource assembler for {@link AttachmentResource}.
 */
@Component
public class AttachmentResourcesAssembler {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassificationSummaryResourceAssembler classificationAssembler;

    public List<AttachmentResource> toResources(List<Attachment> attachments) {
        List<AttachmentResource> resourceList = new ArrayList<>();
        for (Attachment a : attachments) {
            AttachmentResource resource = new AttachmentResource();
            BeanUtils.copyProperties(a, resource);
            if (a.getCreated() != null) {
                resource.setCreated(a.getCreated().toString());
            }
            if (a.getModified() != null) {
                resource.setModified(a.getModified().toString());
            }
            if (a.getReceived() != null) {
                resource.setReceived(a.getReceived().toString());
            }
            resource.setAttachmentId(a.getId());
            resource.setClassificationSummary(
                    classificationAssembler.toResource(a.getClassificationSummary()));
            resourceList.add(resource);
        }

        return resourceList;
    }

    public List<Attachment> toModel(List<AttachmentResource> resources) {

        List<Attachment> attachmentList = new ArrayList<>();
        for (AttachmentResource ar : resources) {
            AttachmentImpl attachment = (AttachmentImpl) taskService.newAttachment();
            BeanUtils.copyProperties(ar, attachment);
            attachment.setId(ar.getAttachmentId());
            attachment.setClassificationSummary(
                    classificationAssembler.toModel(ar.getClassificationSummary()));
            attachmentList.add(attachment);
        }

        return attachmentList;
    }
}
