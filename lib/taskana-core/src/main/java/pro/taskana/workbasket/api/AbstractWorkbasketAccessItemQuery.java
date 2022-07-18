package pro.taskana.workbasket.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/**
 * AbstractWorkbasketAccessItemQuery for generating dynamic sql.
 *
 * @param <Q> the actual WorkbasketAccessItemQuery behind this interface class
 * @param <T> the workbasket access item
 */
public interface AbstractWorkbasketAccessItemQuery<
        Q extends AbstractWorkbasketAccessItemQuery<Q, T>, T extends WorkbasketAccessItem>
    extends BaseQuery<T, AccessItemQueryColumnName> {

  /**
   * Add your unique entry id to your query as filter.
   *
   * @param ids the unique entry IDs
   * @return the query
   */
  Q idIn(String... ids);

  /**
   * Add the {@linkplain Workbasket#getId() id} to your query.
   *
   * @param workbasketId the {@linkplain Workbasket#getId() id} of the Workbasket
   * @return the query
   */
  Q workbasketIdIn(String... workbasketId);

  /**
   * Add your accessIds to your query.
   *
   * @param accessId as access Ids
   * @return the query
   */
  Q accessIdIn(String... accessId);

  /**
   * Sort the query result by workbasket id.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  Q orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sort the query result by access Id.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  Q orderByAccessId(SortDirection sortDirection);

  /**
   * Sort the query result by Id.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  Q orderById(SortDirection sortDirection);
}
