package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.Link;

import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.rest.simplehistory.TaskHistoryEventController;
import pro.taskana.simplehistory.impl.HistoryEventImpl;

/**
 * Mapper to convert from a list of HistoryEventImpl to a TaskHistoryEventResource.
 */
public class TaskHistoryEventListAssembler extends AbstractRessourcesAssembler {

    public TaskHistoryEventListAssembler() {
    }

    public TaskHistoryEventListResource toResources(List<HistoryEventImpl> historyEvents,
        PageMetadata pageMetadata) {

        TaskHistoryEventAssembler assembler = new TaskHistoryEventAssembler();
        List<TaskHistoryEventResource> resources = assembler.toResources(historyEvents);
        TaskHistoryEventListResource pagedResources = new TaskHistoryEventListResource(
            resources,
            pageMetadata);

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
