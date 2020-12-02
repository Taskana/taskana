package pro.taskana.common.rest.assembler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public interface CollectionRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, C extends CollectionRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  C buildCollectionEntity(List<D> content);

  default C toTaskanaCollectionModel(Iterable<T> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(Collectors.collectingAndThen(Collectors.toList(), this::buildCollectionEntity));
  }
}
