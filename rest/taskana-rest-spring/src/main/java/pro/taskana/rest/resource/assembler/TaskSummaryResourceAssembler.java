package pro.taskana.rest.resource.assembler;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;

import pro.taskana.TaskSummary;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskSummaryResource;

/**
 * Resource assembler for {@link TaskSummaryResource}.
 */
public class TaskSummaryResourceAssembler
    extends ResourceAssemblerSupport<TaskSummary, TaskSummaryResource> {

    private WorkbasketSummaryResourceAssembler workbasketAssembler = new WorkbasketSummaryResourceAssembler();
    private ClassificationSummaryResourceAssembler classificationAssembler = new ClassificationSummaryResourceAssembler();
    private AttachmentSummaryResourcesAssembler attachmentsAssembler = new AttachmentSummaryResourcesAssembler();

    public TaskSummaryResourceAssembler() {
        super(TaskController.class, TaskSummaryResource.class);
    }

    @Override
    public TaskSummaryResource toResource(TaskSummary taskSummary) {
        TaskSummaryResource resource = createResourceWithId(taskSummary.getTaskId(), taskSummary);
        BeanUtils.copyProperties(taskSummary, resource);
        if (taskSummary.getCreated() != null) {
            resource.setCreated(taskSummary.getCreated().toString());
        }
        if (taskSummary.getModified() != null) {
            resource.setModified(taskSummary.getModified().toString());
        }
        if (taskSummary.getClaimed() != null) {
            resource.setClaimed(taskSummary.getClaimed().toString());
        }
        if (taskSummary.getCompleted() != null) {
            resource.setCompleted(taskSummary.getCompleted().toString());
        }
        if (taskSummary.getDue() != null) {
            resource.setDue(taskSummary.getDue().toString());
        }
        resource.setClassificationSummaryResource(
            classificationAssembler.toResource(taskSummary.getClassificationSummary()));
        resource.setWorkbasketSummaryResource(workbasketAssembler.toResource(taskSummary.getWorkbasketSummary()));
        resource.setAttachmentSummaries(attachmentsAssembler.toResources(taskSummary.getAttachmentSummaries()));
        try {
            if (taskSummary.getCustomAttribute("1") != null) {
                resource.setCustom1(taskSummary.getCustomAttribute("1"));
            }
            if (taskSummary.getCustomAttribute("2") != null) {
                resource.setCustom2(taskSummary.getCustomAttribute("2"));
            }
            if (taskSummary.getCustomAttribute("3") != null) {
                resource.setCustom3(taskSummary.getCustomAttribute("3"));
            }
            if (taskSummary.getCustomAttribute("4") != null) {
                resource.setCustom4(taskSummary.getCustomAttribute("4"));
            }
            if (taskSummary.getCustomAttribute("5") != null) {
                resource.setCustom5(taskSummary.getCustomAttribute("5"));
            }
            if (taskSummary.getCustomAttribute("6") != null) {
                resource.setCustom6(taskSummary.getCustomAttribute("6"));
            }
            if (taskSummary.getCustomAttribute("7") != null) {
                resource.setCustom7(taskSummary.getCustomAttribute("7"));
            }
            if (taskSummary.getCustomAttribute("8") != null) {
                resource.setCustom8(taskSummary.getCustomAttribute("8"));
            }
            if (taskSummary.getCustomAttribute("8") != null) {
                resource.setCustom9(taskSummary.getCustomAttribute("9"));
            }
            if (taskSummary.getCustomAttribute("10") != null) {
                resource.setCustom10(taskSummary.getCustomAttribute("10"));
            }
            if (taskSummary.getCustomAttribute("11") != null) {
                resource.setCustom11(taskSummary.getCustomAttribute("11"));
            }
            if (taskSummary.getCustomAttribute("12") != null) {
                resource.setCustom12(taskSummary.getCustomAttribute("12"));
            }
            if (taskSummary.getCustomAttribute("13") != null) {
                resource.setCustom13(taskSummary.getCustomAttribute("13"));
            }
            if (taskSummary.getCustomAttribute("14") != null) {
                resource.setCustom14(taskSummary.getCustomAttribute("14"));
            }
            if (taskSummary.getCustomAttribute("15") != null) {
                resource.setCustom15(taskSummary.getCustomAttribute("15"));
            }
            if (taskSummary.getCustomAttribute("16") != null) {
                resource.setCustom16(taskSummary.getCustomAttribute("16"));
            }
        } catch (InvalidArgumentException e) {
            throw new SystemException("caught unexpected Exception.", e.getCause());
        }

        return resource;
    }

}
