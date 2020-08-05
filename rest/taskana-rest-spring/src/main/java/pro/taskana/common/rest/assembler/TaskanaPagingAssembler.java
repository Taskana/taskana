package pro.taskana.common.rest.assembler;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.servlet.http.HttpServletRequest;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;

public interface TaskanaPagingAssembler<T, D extends RepresentationModel<? super D>>
    extends RepresentationModelAssembler<T, D> {

  TaskanaPagedModelKeys getProperty();

  default TaskanaPagedModel<D> toPageModel(Iterable<T> entities, PageMetadata pageMetadata) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(), l -> new TaskanaPagedModel<>(getProperty(), l, pageMetadata)));
  }

  default TaskanaPagedModel<D> toPageModel(Iterable<T> entities) {
    return toPageModel(entities, null);
  }

  default TaskanaPagedModel<D> addLinksToPagedResource(TaskanaPagedModel<D> model) {
    final UriComponentsBuilder original = getBaseUri();
    final PageMetadata page = model.getMetadata();

    model.add(Link.of(original.toUriString()).withSelfRel());
    if (page != null) {
      model.add(
          Link.of(original.replaceQueryParam("page", 1).toUriString())
              .withRel(IanaLinkRelations.FIRST));
      model.add(
          Link.of(original.replaceQueryParam("page", page.getTotalPages()).toUriString())
              .withRel(IanaLinkRelations.LAST));
      if (page.getNumber() > 1) {
        model.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() - 1).toUriString())
                .withRel(IanaLinkRelations.PREV));
      }
      if (page.getNumber() < page.getTotalPages()) {
        model.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() + 1).toUriString())
                .withRel(IanaLinkRelations.NEXT));
      }
    }

    return model;
  }

  default UriComponentsBuilder getBaseUri() {
    final HttpServletRequest request =
        ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    final UriComponentsBuilder baseUri =
        ServletUriComponentsBuilder.fromServletMapping(request).path(request.getRequestURI());

    for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
      for (String value : entry.getValue()) {
        baseUri.queryParam(entry.getKey(), value);
      }
    }

    return baseUri;
  }
}
