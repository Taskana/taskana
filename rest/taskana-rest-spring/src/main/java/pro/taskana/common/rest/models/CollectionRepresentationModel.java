package pro.taskana.common.rest.models;

import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

@Getter
@RequiredArgsConstructor
public class CollectionRepresentationModel<T extends RepresentationModel<? super T>>
    extends RepresentationModel<CollectionRepresentationModel<T>> {

  private final Collection<T> content;
}
