package pro.taskana.simplehistory.rest.resource;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;

import pro.taskana.resource.rest.AbstractRessourcesAssembler;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.rest.TaskHistoryEventController;

/** Mapper to convert from a list of HistoryEventImpl to a TaskHistoryEventResource. */
public class TaskHistoryEventListResourceAssembler extends AbstractRessourcesAssembler {

  public TaskHistoryEventListResource toResources(
      List<HistoryEventImpl> historyEvents, PageMetadata pageMetadata) {

    TaskHistoryEventResourceAssembler assembler = new TaskHistoryEventResourceAssembler();
    List<TaskHistoryEventResource> resources =
        new ArrayList<>(assembler.toCollectionModel(historyEvents).getContent());
    TaskHistoryEventListResource pagedResources =
        new TaskHistoryEventListResource(resources, pageMetadata);

    pagedResources.add(Link.of(this.getOriginal().toUriString()).withSelfRel());
    if (pageMetadata != null) {
      pagedResources.add(linkTo(TaskHistoryEventController.class).withRel("allTaskHistoryEvent"));
      pagedResources.add(
          Link.of(this.getOriginal().replaceQueryParam("page", 1).toUriString())
              .withRel(IanaLinkRelations.FIRST));
      pagedResources.add(
          Link.of(
                  this.getOriginal()
                      .replaceQueryParam("page", pageMetadata.getTotalPages())
                      .toUriString())
              .withRel(IanaLinkRelations.LAST));
      if (pageMetadata.getNumber() > 1) {
        pagedResources.add(
            Link.of(
                    this.getOriginal()
                        .replaceQueryParam("page", pageMetadata.getNumber() - 1)
                        .toUriString())
                .withRel(IanaLinkRelations.PREV));
      }
      if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
        pagedResources.add(
            Link.of(
                    this.getOriginal()
                        .replaceQueryParam("page", pageMetadata.getNumber() + 1)
                        .toUriString())
                .withRel(IanaLinkRelations.NEXT));
      }
    }

    return pagedResources;
  }
}
