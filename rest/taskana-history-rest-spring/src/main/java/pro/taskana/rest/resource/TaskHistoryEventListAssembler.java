package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static pro.taskana.rest.resource.AbstractRessourcesAssembler.getBuilderForOriginalUri;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.rest.TaskHistoryEventController;
import pro.taskana.simplehistory.impl.HistoryEventImpl;

/**
 * Mapper to convert from a list of HistoryEventImpl to a TaskHistoryEventResource.
 */
public class TaskHistoryEventListAssembler {

    public TaskHistoryEventListAssembler() {
    }

    public PagedResources<TaskHistoryEventResource> toResources(List<HistoryEventImpl> historyEvents,
        PagedResources.PageMetadata pageMetadata) {

        TaskHistoryEventAssembler assembler = new TaskHistoryEventAssembler();
        List<TaskHistoryEventResource> resources = assembler.toResources(historyEvents);
        PagedResources<TaskHistoryEventResource> pagedResources = new PagedResources<TaskHistoryEventResource>(
            resources,
            pageMetadata);

        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        if (pageMetadata != null) {
            pagedResources.add(linkTo(TaskHistoryEventController.class).withRel("allTaskHistoryEvent"));
            pagedResources.add(new Link(original.replaceQueryParam("page", 1).toUriString()).withRel(Link.REL_FIRST));
            pagedResources.add(new Link(original.replaceQueryParam("page", pageMetadata.getTotalPages()).toUriString())
                .withRel(Link.REL_LAST));
            if (pageMetadata.getNumber() > 1) {
                pagedResources
                    .add(new Link(original.replaceQueryParam("page", pageMetadata.getNumber() - 1).toUriString())
                        .withRel(Link.REL_PREVIOUS));
            }
            if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
                pagedResources
                    .add(new Link(original.replaceQueryParam("page", pageMetadata.getNumber() + 1).toUriString())
                        .withRel(Link.REL_NEXT));
            }
        }

        return pagedResources;
    }
}
