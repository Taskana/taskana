package pro.taskana.common.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Collection;
import org.springframework.hateoas.RepresentationModel;

public abstract class PagedRepresentationModel<T extends RepresentationModel<? super T>>
    extends CollectionRepresentationModel<T> {

  @Schema(name = "page", description = "the page meta data for a paged request.")
  @JsonProperty("page")
  private final PageMetadata pageMetadata;

  protected PagedRepresentationModel(Collection<T> content, PageMetadata pageMetadata) {
    super(content);
    this.pageMetadata = pageMetadata;
  }

  public PageMetadata getPageMetadata() {
    return pageMetadata;
  }
}
