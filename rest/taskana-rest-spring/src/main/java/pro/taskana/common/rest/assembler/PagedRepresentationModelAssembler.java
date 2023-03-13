package pro.taskana.common.rest.assembler;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public interface PagedRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, P extends PagedRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  P buildPageableEntity(Collection<D> content, PageMetadata pageMetadata);

  default P toPagedModel(Iterable<T> entities, PageMetadata pageMetadata) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                content -> addLinksToPagedModel(buildPageableEntity(content, pageMetadata))));
  }

  default P addLinksToPagedModel(P model) {
    final UriComponentsBuilder original = ServletUriComponentsBuilder.fromCurrentRequest();
    final PageMetadata page = model.getPageMetadata();

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
}
