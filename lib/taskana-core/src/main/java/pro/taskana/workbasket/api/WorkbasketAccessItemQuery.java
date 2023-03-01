/*-
 * #%L
 * pro.taskana:taskana-core
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
package pro.taskana.workbasket.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** WorkbasketAccessItemQuery for generating dynamic SQL. */
public interface WorkbasketAccessItemQuery
    extends BaseQuery<WorkbasketAccessItem, AccessItemQueryColumnName> {

  /**
   * Add your unique entry id to your query as filter.
   *
   * @param ids the unique entry IDs
   * @return the query
   */
  WorkbasketAccessItemQuery idIn(String... ids);

  /**
   * Add your workbasket id to your query.
   *
   * @param workbasketIds the workbasket Id
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketIdIn(String... workbasketIds);

  /**
   * Add your unique entry workbasket key to your query as filter.
   *
   * @param keys the unique entry Keys
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketKeyIn(String... keys);

  /**
   * Add keys to your query. The keys are compared case-insensitively to the keys of access items
   * with the SQL LIKE operator. You may add a wildcard like '%' to search generically. If you
   * specify multiple keys they are connected with an OR operator, this is, the query searches
   * access items workbaskets whose keys are like key1 or like key2, etc.
   *
   * @param keys the keys as Strings
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketKeyLike(String... keys);

  /**
   * Add your accessIds to your query.
   *
   * @param accessIds as access Ids
   * @return the query
   */
  WorkbasketAccessItemQuery accessIdIn(String... accessIds);

  /**
   * Add keys to your query. The keys are compared case-insensitively to the keys of access items
   * with the SQL LIKE operator. You may add a wildcard like '%' to search generically. If you
   * specify multiple keys they are connected with an OR operator, this is, the query searches
   * access items whose ids are like id1 or like id2, etc.
   *
   * @param ids the ids as Strings
   * @return the query
   */
  WorkbasketAccessItemQuery accessIdLike(String... ids);

  /**
   * Sort the query result by workbasket id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sort the query result by workbasket key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sort the query result by access Id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection);

  /**
   * Sort the query result by Id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderById(SortDirection sortDirection);
}
