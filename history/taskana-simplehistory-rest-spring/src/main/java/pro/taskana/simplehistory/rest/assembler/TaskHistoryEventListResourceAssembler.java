package pro.taskana.simplehistory.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.simplehistory.rest.TaskHistoryEventController;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventListResource;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventRepresentationModel;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** Mapper to convert from a list of TaskHistoryEvent to a TaskHistoryEventResource. */
public class TaskHistoryEventListResourceAssembler {

  public TaskHistoryEventListResource toResources(
      List<TaskHistoryEvent> historyEvents, PageMetadata pageMetadata) {

    TaskHistoryEventRepresentationModelAssembler assembler =
        new TaskHistoryEventRepresentationModelAssembler();
    List<TaskHistoryEventRepresentationModel> resources =
        new ArrayList<>(assembler.toCollectionModel(historyEvents).getContent());
    TaskHistoryEventListResource pagedResources =
        new TaskHistoryEventListResource(resources, pageMetadata);

    pagedResources.add(Link.of(getBaseUri().toUriString()).withSelfRel());
    if (pageMetadata != null) {
      pagedResources.add(linkTo(TaskHistoryEventController.class).withRel("allTaskHistoryEvent"));
      pagedResources.add(
          Link.of(getBaseUri().replaceQueryParam("page", 1).toUriString())
              .withRel(IanaLinkRelations.FIRST));
      pagedResources.add(
          Link.of(
                  getBaseUri()
                      .replaceQueryParam("page", pageMetadata.getTotalPages())
                      .toUriString())
              .withRel(IanaLinkRelations.LAST));
      if (pageMetadata.getNumber() > 1) {
        pagedResources.add(
            Link.of(
                    getBaseUri()
                        .replaceQueryParam("page", pageMetadata.getNumber() - 1)
                        .toUriString())
                .withRel(IanaLinkRelations.PREV));
      }
      if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
        pagedResources.add(
            Link.of(
                    getBaseUri()
                        .replaceQueryParam("page", pageMetadata.getNumber() + 1)
                        .toUriString())
                .withRel(IanaLinkRelations.NEXT));
      }
    }

    return pagedResources;
  }

  private UriComponentsBuilder getBaseUri() {
    HttpServletRequest request =
        ((ServletRequestAttributes) Objects.requireNonNull(
            RequestContextHolder.getRequestAttributes())).getRequest();
    UriComponentsBuilder baseUri =
        ServletUriComponentsBuilder.fromServletMapping(request).path(request.getRequestURI());

    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      for (String value : entry.getValue()) {
        baseUri.queryParam(entry.getKey(), value);
      }
    }

    return baseUri;
  }
}
