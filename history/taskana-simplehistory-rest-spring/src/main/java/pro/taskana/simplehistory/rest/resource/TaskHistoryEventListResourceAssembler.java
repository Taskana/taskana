package pro.taskana.simplehistory.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;
import org.springframework.hateoas.Link;

import pro.taskana.rest.resource.AbstractRessourcesAssembler;
import pro.taskana.rest.resource.PagedResources.PageMetadata;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.rest.TaskHistoryEventController;

/** Mapper to convert from a list of HistoryEventImpl to a TaskHistoryEventResource. */
public class TaskHistoryEventListResourceAssembler extends AbstractRessourcesAssembler {

  public TaskHistoryEventListResource toResources(
      List<HistoryEventImpl> historyEvents, PageMetadata pageMetadata) {

    TaskHistoryEventResourceAssembler assembler = new TaskHistoryEventResourceAssembler();
    List<TaskHistoryEventResource> resources = assembler.toResources(historyEvents);
    TaskHistoryEventListResource pagedResources =
        new TaskHistoryEventListResource(resources, pageMetadata);

    pagedResources.add(new Link(this.getOriginal().toUriString()).withSelfRel());
    if (pageMetadata != null) {
      pagedResources.add(linkTo(TaskHistoryEventController.class).withRel("allTaskHistoryEvent"));
      pagedResources.add(
          new Link(this.getOriginal().replaceQueryParam("page", 1).toUriString())
              .withRel(Link.REL_FIRST));
      pagedResources.add(
          new Link(
                  this.getOriginal()
                      .replaceQueryParam("page", pageMetadata.getTotalPages())
                      .toUriString())
              .withRel(Link.REL_LAST));
      if (pageMetadata.getNumber() > 1) {
        pagedResources.add(
            new Link(
                    this.getOriginal()
                        .replaceQueryParam("page", pageMetadata.getNumber() - 1)
                        .toUriString())
                .withRel(Link.REL_PREVIOUS));
      }
      if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
        pagedResources.add(
            new Link(
                    this.getOriginal()
                        .replaceQueryParam("page", pageMetadata.getNumber() + 1)
                        .toUriString())
                .withRel(Link.REL_NEXT));
      }
    }

    return pagedResources;
  }
}
