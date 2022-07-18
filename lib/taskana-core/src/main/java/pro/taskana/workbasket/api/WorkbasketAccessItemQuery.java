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
   * @param workbasketId the workbasket Id
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketIdIn(String... workbasketId);

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
   * @param key the keys as Strings
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketKeyLike(String... key);

  /**
   * Add your accessIds to your query.
   *
   * @param accessId as access Ids
   * @return the query
   */
  WorkbasketAccessItemQuery accessIdIn(String... accessId);

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
   * @param sortDirection determines whether the result is sorted in ascending or descending order
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sort the query result by workbasket key.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sort the query result by access Id.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection);

  /**
   * Sort the query result by Id.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketAccessItemQuery orderById(SortDirection sortDirection);
}
