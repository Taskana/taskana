package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;

import pro.taskana.TaskSummary;
import pro.taskana.rest.TaskController;
import pro.taskana.rest.resource.TaskSummaryResource;

/**
 * Resources assembler for {@link TaskSummaryResource}.
 */
public class TaskSummaryResourcesAssembler extends AbstractRessourcesAssembler {

    public TaskSummaryResourcesAssembler() {
        super();
    }

    public PagedResources<TaskSummaryResource> toResources(List<TaskSummary> taskSummaries,
        PageMetadata pageMetadata) {

        TaskSummaryResourceAssembler assembler = new TaskSummaryResourceAssembler();
        List<TaskSummaryResource> resources = assembler.toResources(taskSummaries);
        PagedResources<TaskSummaryResource> pagedResources = new PagedResources<TaskSummaryResource>(
            resources,
            pageMetadata);

        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        if (pageMetadata != null) {
            pagedResources.add(linkTo(TaskController.class).withRel("allTasks"));
            addPageLinks(pagedResources, pageMetadata);
        }

        return pagedResources;
    }

}
