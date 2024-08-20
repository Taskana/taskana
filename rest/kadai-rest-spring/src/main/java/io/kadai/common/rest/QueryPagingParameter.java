package io.kadai.common.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.kadai.common.api.BaseQuery;
import io.kadai.common.rest.models.PageMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import java.beans.ConstructorProperties;
import java.util.List;

public class QueryPagingParameter<T, Q extends BaseQuery<T, ?>>
    implements QueryParameter<Q, List<T>> {

  public Integer getPage() {
    return page;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  @Schema(
      name = "page",
      description = "Request a specific page. Requires the definition of the 'page-size'.")
  @JsonProperty("page")
  @Min(1)
  private final Integer page;

  @Schema(
      name = "page-size",
      description = "Defines the size for each page. This requires a specific requested 'page'.")
  @JsonProperty("page-size")
  @Min(1)
  private final Integer pageSize;

  @JsonIgnore private PageMetadata pageMetadata;

  @ConstructorProperties({"page", "page-size"})
  public QueryPagingParameter(Integer page, Integer pageSize) {
    // TODO: do we really want this? Personally I would throw an InvalidArgumentException
    if (pageSize == null) {
      pageSize = Integer.MAX_VALUE;
    }
    this.page = page;
    this.pageSize = pageSize;
  }

  public PageMetadata getPageMetadata() {
    return pageMetadata;
  }

  @Override
  public List<T> apply(Q query) {
    initPageMetaData(query);
    List<T> resultList;
    if (pageMetadata != null) {
      resultList =
          query.listPage(
              Math.toIntExact(pageMetadata.getNumber()), Math.toIntExact(pageMetadata.getSize()));
    } else {
      resultList = query.list();
    }
    return resultList;
  }

  private void initPageMetaData(Q query) {
    if (page != null) {
      long totalElements = query.count();
      long maxPages = (long) Math.ceil(totalElements / pageSize.doubleValue());
      pageMetadata = new PageMetadata(pageSize, totalElements, maxPages, page);
    }
  }
}
