package pro.taskana.workbasket.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/**
 * AbstractWorkbasketAccessItemQuery for generating dynamic sql.
 *
 * @param <Q> the actual {@linkplain WorkbasketAccessItemQuery} behind this interface class
 * @param <T> the {@linkplain WorkbasketAccessItem}
 */
public interface AbstractWorkbasketAccessItemQuery<
        Q extends AbstractWorkbasketAccessItemQuery<Q, T>, T extends WorkbasketAccessItem>
    extends BaseQuery<T, AccessItemQueryColumnName> {

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getId() id} value that is equal to any of the passed values.
   *
   * @param ids the values of interest
   * @return the query
   */
  Q idIn(String... ids);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getWorkbasketId() workbasket id} value that is equal to any of the passed
   * values.
   *
   * @param workbasketId the values of interest
   * @return the query
   */
  Q workbasketIdIn(String... workbasketId);

  /**
   * Selects only {@linkplain WorkbasketAccessItem WorkbasketAccessItems} which have a {@linkplain
   * WorkbasketAccessItem#getAccessId()} access id} value that is equal to any of the passed values.
   *
   * @param accessId the values of interest
   * @return the query
   */
  Q accessIdIn(String... accessId);

  /**
   * Sorts the query result by {@linkplain pro.taskana.workbasket.api.models.Workbasket#getId
   * workbasket id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  Q orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getAccessId() access id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  Q orderByAccessId(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain WorkbasketAccessItem#getId() id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  Q orderById(SortDirection sortDirection);
}
