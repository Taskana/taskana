package pro.taskana.workbasket.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/**
 * The WorkbasketAccessItemQuery allows for a custom search across all {@linkplain
 * WorkbasketAccessItem WorkbasketAccessItems}.
 */
public interface WorkbasketAccessItemQuery
    extends BaseQuery<WorkbasketAccessItem, AccessItemQueryColumnName> {

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getId() id} value that is equal to any of the passed values.
   *
   * @param ids the values of interest
   * @return the query
   */
  WorkbasketAccessItemQuery idIn(String... ids);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getWorkbasketId() workbasket id}value that is equal to any of the passed
   * values.
   *
   * @param workbasketId the values of interest
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketIdIn(String... workbasketId);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getWorkbasketKey() workbasket key} value that is equal to any of the
   * passed values.
   *
   * @param keys the values of interest
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketKeyIn(String... keys);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getWorkbasketKey() workbasket key} value that contains any of the passed
   * patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param key the patterns of interest
   * @return the query
   */
  WorkbasketAccessItemQuery workbasketKeyLike(String... key);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getAccessId() access id} value that is equal to any of the passed values.
   *
   * @param accessId the values of interest
   * @return the query
   */
  WorkbasketAccessItemQuery accessIdIn(String... accessId);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItem} which have a {@linkplain
   * WorkbasketAccessItem#getAccessId() access id} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param ids the patterns of interest
   * @return the query
   */
  WorkbasketAccessItemQuery accessIdLike(String... ids);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getWorkbasketId() workbasket id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getWorkbasketKey() workbasket key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketAccessItemQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getAccessId() access id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketAccessItemQuery orderByAccessId(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getId() id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketAccessItemQuery orderById(SortDirection sortDirection);
}
