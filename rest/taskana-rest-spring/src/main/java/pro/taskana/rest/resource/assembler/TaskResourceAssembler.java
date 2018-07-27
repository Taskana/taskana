package pro.taskana.rest.resource.assembler;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import pro.taskana.Task;
import pro.taskana.TaskService;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.impl.TaskImpl;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskResource;

/**
 * Resource assembler for {@link TaskResource}.
 */
@Component
public class TaskResourceAssembler
    extends ResourceAssemblerSupport<Task, TaskResource> {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClassificationSummaryResourceAssembler classificationAssembler;

    @Autowired
    private WorkbasketSummaryResourceAssembler workbasketAssembler;

    @Autowired
    private AttachmentResourcesAssembler attachmentAssembler;

    public TaskResourceAssembler() {
        super(TaskController.class, TaskResource.class);
    }

    @Override
    public TaskResource toResource(Task task) {
        TaskResource resource = createResourceWithId(task.getId(), task);
        BeanUtils.copyProperties(task, resource);
        resource.setTaskId(task.getId());
        if (task.getCreated() != null) {
            resource.setCreated(task.getCreated().toString());
        }
        if (task.getModified() != null) {
            resource.setModified(task.getModified().toString());
        }
        if (task.getClaimed() != null) {
            resource.setClaimed(task.getClaimed().toString());
        }
        if (task.getCompleted() != null) {
            resource.setCompleted(task.getCompleted().toString());
        }
        if (task.getDue() != null) {
            resource.setDue(task.getDue().toString());
        }
        resource.setClassificationSummaryResource(
            classificationAssembler.toResource(task.getClassificationSummary()));
        resource.setWorkbasketSummaryResource(workbasketAssembler.toResource(task.getWorkbasketSummary()));
        resource.setAttachments(attachmentAssembler.toResources(task.getAttachments()));
        try {
            if (task.getCustomAttribute("1") != null) {
                resource.setCustom1(task.getCustomAttribute("1"));
            }
            if (task.getCustomAttribute("2") != null) {
                resource.setCustom2(task.getCustomAttribute("2"));
            }
            if (task.getCustomAttribute("3") != null) {
                resource.setCustom3(task.getCustomAttribute("3"));
            }
            if (task.getCustomAttribute("4") != null) {
                resource.setCustom4(task.getCustomAttribute("4"));
            }
            if (task.getCustomAttribute("5") != null) {
                resource.setCustom5(task.getCustomAttribute("5"));
            }
            if (task.getCustomAttribute("6") != null) {
                resource.setCustom6(task.getCustomAttribute("6"));
            }
            if (task.getCustomAttribute("7") != null) {
                resource.setCustom7(task.getCustomAttribute("7"));
            }
            if (task.getCustomAttribute("8") != null) {
                resource.setCustom8(task.getCustomAttribute("8"));
            }
            if (task.getCustomAttribute("8") != null) {
                resource.setCustom9(task.getCustomAttribute("9"));
            }
            if (task.getCustomAttribute("10") != null) {
                resource.setCustom10(task.getCustomAttribute("10"));
            }
            if (task.getCustomAttribute("11") != null) {
                resource.setCustom11(task.getCustomAttribute("11"));
            }
            if (task.getCustomAttribute("12") != null) {
                resource.setCustom12(task.getCustomAttribute("12"));
            }
            if (task.getCustomAttribute("13") != null) {
                resource.setCustom13(task.getCustomAttribute("13"));
            }
            if (task.getCustomAttribute("14") != null) {
                resource.setCustom14(task.getCustomAttribute("14"));
            }
            if (task.getCustomAttribute("15") != null) {
                resource.setCustom15(task.getCustomAttribute("15"));
            }
            if (task.getCustomAttribute("16") != null) {
                resource.setCustom16(task.getCustomAttribute("16"));
            }
        } catch (InvalidArgumentException e) {
            throw new SystemException("caught unexpected Exception.", e.getCause());
        }

        return resource;
    }

    public Task toModel(TaskResource resource) throws InvalidArgumentException {
        validateTaskResource(resource);
        TaskImpl task = (TaskImpl) taskService.newTask(resource.getWorkbasketSummaryResource().getWorkbasketId());
        task.setId(resource.getTaskId());
        BeanUtils.copyProperties(resource, task);
        if (resource.getCreated() != null) {
            task.setCreated(Instant.parse(resource.getCreated()));
        }
        if (resource.getModified() != null) {
            task.setModified(Instant.parse(resource.getModified().toString()));
        }
        if (resource.getClaimed() != null) {
            task.setClaimed(Instant.parse(resource.getClaimed().toString()));
        }
        if (resource.getCompleted() != null) {
            task.setCompleted(Instant.parse(resource.getCompleted().toString()));
        }
        if (resource.getDue() != null) {
            task.setDue(Instant.parse(resource.getDue().toString()));
        }
        task.setClassificationSummary(classificationAssembler.toModel(resource.getClassificationSummaryResource()));
        task.setWorkbasketSummary(workbasketAssembler.toModel(resource.getWorkbasketSummaryResource()));
        task.setAttachments(attachmentAssembler.toModel(resource.getAttachments()));
        if (resource.getCustom1() != null) {
            task.setCustom1(resource.getCustom1());
        }
        if (resource.getCustom2() != null) {
            task.setCustom2(resource.getCustom2());
        }
        if (resource.getCustom3() != null) {
            task.setCustom3(resource.getCustom3());
        }
        if (resource.getCustom4() != null) {
            task.setCustom4(resource.getCustom4());
        }
        if (resource.getCustom5() != null) {
            task.setCustom5(resource.getCustom5());
        }
        if (resource.getCustom6() != null) {
            task.setCustom6(resource.getCustom6());
        }
        if (resource.getCustom7() != null) {
            task.setCustom7(resource.getCustom7());
        }
        if (resource.getCustom8() != null) {
            task.setCustom8(resource.getCustom8());
        }
        if (resource.getCustom9() != null) {
            task.setCustom9(resource.getCustom9());
        }
        if (resource.getCustom10() != null) {
            task.setCustom10(resource.getCustom10());
        }
        if (resource.getCustom11() != null) {
            task.setCustom11(resource.getCustom11());
        }
        if (resource.getCustom12() != null) {
            task.setCustom12(resource.getCustom12());
        }
        if (resource.getCustom13() != null) {
            task.setCustom13(resource.getCustom13());
        }
        if (resource.getCustom14() != null) {
            task.setCustom14(resource.getCustom14());
        }
        if (resource.getCustom15() != null) {
            task.setCustom15(resource.getCustom15());
        }
        if (resource.getCustom16() != null) {
            task.setCustom16(resource.getCustom16());
        }

        return task;
    }

    private void validateTaskResource(TaskResource resource) throws InvalidArgumentException {
        if (resource.getWorkbasketSummaryResource() == null
            || resource.getWorkbasketSummaryResource().getWorkbasketId() == null
            || resource.getWorkbasketSummaryResource().getWorkbasketId().isEmpty()) {
            throw new InvalidArgumentException(
                "TaskResource must have a workbasket summary with a valid workbasketId.");
        }
        if (resource.getClassificationSummaryResource() == null
            || resource.getClassificationSummaryResource().getKey() == null
            || resource.getClassificationSummaryResource().getKey().isEmpty()) {
            throw new InvalidArgumentException(
                "TaskResource must have a classification summary with a valid classification key.");
        }
    }

}
