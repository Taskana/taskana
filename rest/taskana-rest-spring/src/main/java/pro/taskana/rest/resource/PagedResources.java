package pro.taskana.rest.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAttribute;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.util.Assert;

/**
 * Base Class for CollectionModel with pagination.
 *
 * @param <T> The Class of the paginatied content
 */
public class PagedResources<T> extends RepresentationModel<PagedResources<T>> {

  private Collection<T> content;

  private PageMetadata metadata;

  /** Default constructor to allow instantiation by reflection. */
  protected PagedResources() {
    this(new ArrayList<T>(), null);
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
      Collection<T> content = getContent();
      return new PageMetadata(content.size(), 0, content.size());
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

  /** Class for Page Metadata. */
  public static class PageMetadata {

    @XmlAttribute @JsonProperty private long size;
    @XmlAttribute @JsonProperty private long totalElements;
    @XmlAttribute @JsonProperty private long totalPages;
    @XmlAttribute @JsonProperty private long number;

    protected PageMetadata() {}

    /**
     * Creates a new {@link PageMetadata} from the given size, number, total elements and total
     * pages.
     *
     * @param size the size
     * @param number zero-indexed, must be less than totalPages
     * @param totalElements number of elements
     * @param totalPages the total pages
     */
    public PageMetadata(long size, long number, long totalElements, long totalPages) {
      Assert.isTrue(size > -1, "Size must not be negative!");
      Assert.isTrue(number > -1, "Number must not be negative!");
      Assert.isTrue(totalElements > -1, "Total elements must not be negative!");
      Assert.isTrue(totalPages > -1, "Total pages must not be negative!");

      this.size = size;
      this.number = number;
      this.totalElements = totalElements;
      this.totalPages = totalPages;
    }

    /**
     * Creates a new {@link PageMetadata} from the given size, number and total elements.
     *
     * @param size the size of the page
     * @param number the number of the page
     * @param totalElements the total number of elements available
     */
    public PageMetadata(long size, long number, long totalElements) {
      this(
          size,
          number,
          totalElements,
          size == 0 ? 0 : (long) Math.ceil((double) totalElements / (double) size));
    }

    /**
     * Returns the requested size of the page.
     *
     * @return the size a positive long.
     */
    public long getSize() {
      return size;
    }

    /**
     * Returns the total number of elements available.
     *
     * @return the totalElements a positive long.
     */
    public long getTotalElements() {
      return totalElements;
    }

    /**
     * Returns how many pages are available in total.
     *
     * @return the totalPages a positive long.
     */
    public long getTotalPages() {
      return totalPages;
    }

    /**
     * Returns the number of the current page.
     *
     * @return the number a positive long.
     */
    public long getNumber() {
      return number;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

      int result = 17;
      result += 31 * (int) (this.number ^ this.number >>> 32);
      result += 31 * (int) (this.size ^ this.size >>> 32);
      result += 31 * (int) (this.totalElements ^ this.totalElements >>> 32);
      result += 31 * (int) (this.totalPages ^ this.totalPages >>> 32);
      return result;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {

      if (this == obj) {
        return true;
      }

      if (obj == null || !obj.getClass().equals(getClass())) {
        return false;
      }

      PageMetadata that = (PageMetadata) obj;

      return this.number == that.number
          && this.size == that.size
          && this.totalElements == that.totalElements
          && this.totalPages == that.totalPages;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return String.format(
          "Metadata { number: %d, total pages: %d, total elements: %d, size: %d }",
          number, totalPages, totalElements, size);
    }
  }
}
