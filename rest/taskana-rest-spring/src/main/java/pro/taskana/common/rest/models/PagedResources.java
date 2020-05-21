package pro.taskana.common.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.hateoas.RepresentationModel;

/**
 * Base Class for CollectionModel with pagination.
 *
 * @param <T> The Class of the paginatied content
 */
public class PagedResources<T> extends RepresentationModel<PagedResources<T>> {

  private final Collection<T> content;

  private final PageMetadata metadata;

  /** Default constructor to allow instantiation by reflection. */
  protected PagedResources() {
    this(new ArrayList<>(), null);
  }

  /**
   * Creates a new {@link PagedResources} from the given content, {@link PageMetadata} and {@link
   * Link}s (optional).
   *
   * @param content must not be {@literal null}.
   * @param metadata the metadata
   * @param links the links
   */
  public PagedResources(Collection<T> content, PageMetadata metadata, Link... links) {
    this(content, metadata, Arrays.asList(links));
  }

  /**
   * Creates a new {@link PagedResources} from the given content {@link PageMetadata} and {@link
   * Link}s.
   *
   * @param content must not be {@literal null}.
   * @param metadata the metadata
   * @param links the links
   */
  public PagedResources(Collection<T> content, PageMetadata metadata, Iterable<Link> links) {
    super();
    this.content = content;
    this.metadata = metadata;
    this.add(links);
  }

  /**
   * Returns the pagination metadata.
   *
   * @return the metadata
   */
  @JsonProperty("page")
  public PageMetadata getMetadata() {
    if (Objects.isNull(metadata)) {
      Collection<T> contentCollection = getContent();
      return new PageMetadata(contentCollection.size(), 0, contentCollection.size());
    }
    return metadata;
  }

  /**
   * Returns the content.
   *
   * @return the content
   */
  @JsonProperty("content")
  public Collection<T> getContent() {
    return Collections.unmodifiableCollection(content);
  }

}
