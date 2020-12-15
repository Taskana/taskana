package pro.taskana.common.rest.assembler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public interface CollectionRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, C extends CollectionRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  C buildCollectionEntity(List<D> content);

  default C toTaskanaCollectionModel(Iterable<T> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                content -> addLinksToCollectionModel(buildCollectionEntity(content))));
  }

  default C addLinksToCollectionModel(C model) {
    final UriComponentsBuilder original = ServletUriComponentsBuilder.fromCurrentRequest();

    model.add(Link.of(original.toUriString()).withSelfRel());
    return model;
  }
}
