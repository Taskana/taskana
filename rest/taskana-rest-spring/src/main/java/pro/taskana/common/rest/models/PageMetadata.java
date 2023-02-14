package pro.taskana.common.rest.models;

import java.beans.ConstructorProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * This is copied from {@link org.springframework.hateoas.PagedModel.PageMetadata}. Reason: The
 * Spring Auto REST Docs Doclet only parses our code to check for JavaDoc comments. Since we want
 * this class to be documented we had to copy it.
 */
@Getter
@RequiredArgsConstructor(
    onConstructor =
        @__({@ConstructorProperties({"size", "totalElements", "totalPages", "number"})}))
@EqualsAndHashCode
@ToString
public class PageMetadata {

  /** The element size of the page. */
  private final long size;
  /** The total number of elements available. */
  private final long totalElements;
  /** Amount of pages that are available in total. */
  private final long totalPages;
  /** The current page number. */
  private final long number;
}
