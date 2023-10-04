package pro.taskana.common.rest.models;

import java.beans.ConstructorProperties;
import java.util.Objects;

/**
 * This is copied from {@link org.springframework.hateoas.PagedModel.PageMetadata}. Reason: The
 * Spring Auto REST Docs Doclet only parses our code to check for JavaDoc comments. Since we want
 * this class to be documented we had to copy it.
 */
public class PageMetadata {

  /** The element size of the page. */
  private final long size;

  /** The total number of elements available. */
  private final long totalElements;

  /** Amount of pages that are available in total. */
  private final long totalPages;

  /** The current page number. */
  private final long number;

  @ConstructorProperties({"size", "totalElements", "totalPages", "number"})
  public PageMetadata(long size, long totalElements, long totalPages, long number) {
    this.size = size;
    this.totalElements = totalElements;
    this.totalPages = totalPages;
    this.number = number;
  }

  public long getSize() {
    return size;
  }

  public long getTotalElements() {
    return totalElements;
  }

  public long getTotalPages() {
    return totalPages;
  }

  public long getNumber() {
    return number;
  }

  @Override
  public int hashCode() {
    return Objects.hash(size, totalElements, totalPages, number);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof PageMetadata)) {
      return false;
    }
    PageMetadata other = (PageMetadata) obj;
    return size == other.size
        && totalElements == other.totalElements
        && totalPages == other.totalPages
        && number == other.number;
  }

  @Override
  public String toString() {
    return "PageMetadata [size="
        + size
        + ", totalElements="
        + totalElements
        + ", totalPages="
        + totalPages
        + ", number="
        + number
        + "]";
  }
}
