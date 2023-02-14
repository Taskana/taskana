package pro.taskana.common.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import lombok.Getter;
import org.springframework.hateoas.RepresentationModel;

@Getter
public abstract class PagedRepresentationModel<T extends RepresentationModel<? super T>>
    extends CollectionRepresentationModel<T> {

  /** the page meta data for a paged request. */
  @JsonProperty("page")
  private final PageMetadata pageMetadata;

  protected PagedRepresentationModel(Collection<T> content, PageMetadata pageMetadata) {
    super(content);
    this.pageMetadata = pageMetadata;
  }
}
