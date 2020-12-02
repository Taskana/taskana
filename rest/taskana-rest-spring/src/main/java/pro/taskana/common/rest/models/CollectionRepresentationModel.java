package pro.taskana.common.rest.models;

import java.util.Collection;
import org.springframework.hateoas.RepresentationModel;

public class CollectionRepresentationModel<T extends RepresentationModel<? super T>>
    extends RepresentationModel<CollectionRepresentationModel<T>> {

  private final Collection<T> content;

  public CollectionRepresentationModel(Collection<T> content) {
    this.content = content;
  }

  public Collection<T> getContent() {
    return content;
  }
}
