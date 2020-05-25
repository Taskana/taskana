package pro.taskana.common.rest.assembler;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;

public interface TaskanaPagingAssembler<T, D extends RepresentationModel<? super D>>
    extends RepresentationModelAssembler<T, D> {

  TaskanaPagedModelKeys getProperty();

  default TaskanaPagedModel<D> toPageModel(
      Iterable<? extends T> entities, PageMetadata pageMetadata) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(), l -> new TaskanaPagedModel<>(getProperty(), l, pageMetadata)));
  }

  default TaskanaPagedModel<D> toPageModel(Iterable<? extends T> entities) {
    return toPageModel(entities, null);
  }
}
