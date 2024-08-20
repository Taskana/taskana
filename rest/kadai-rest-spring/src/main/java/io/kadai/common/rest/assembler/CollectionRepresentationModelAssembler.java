package io.kadai.common.rest.assembler;

import io.kadai.common.rest.models.CollectionRepresentationModel;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

public interface CollectionRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, C extends CollectionRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  C buildCollectionEntity(List<D> content);

  default C toKadaiCollectionModel(Iterable<T> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                content -> addLinksToCollectionModel(buildCollectionEntity(content))));
  }

  default C addLinksToCollectionModel(C model) {
    final UriComponentsBuilder original = ServletUriComponentsBuilder.fromCurrentRequest();

    try {
      model.add(Link.of(URLDecoder.decode(original.toUriString(), "UTF-8")).withSelfRel());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 encoding not supported. This is unexpected.");
    }
    return model;
  }
}
