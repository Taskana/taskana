/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.List;
import javax.validation.constraints.Min;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.rest.models.PageMetadata;

public class QueryPagingParameter<T, Q extends BaseQuery<T, ?>>
    implements QueryParameter<Q, List<T>> {

  /** Request a specific page. Requires the definition of the 'page-size'. */
  @JsonProperty("page")
  @Min(1)
  private final Integer page;

  /** Defines the size for each page. This requires a specific requested 'page'. */
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
