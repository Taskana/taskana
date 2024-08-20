package io.kadai.simplehistory.impl.workbasket;

import io.kadai.common.api.BaseQuery;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import io.kadai.workbasket.api.WorkbasketCustomField;

/** HistoryQuery for generating dynamic sql. */
public interface WorkbasketHistoryQuery
    extends BaseQuery<WorkbasketHistoryEvent, WorkbasketHistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param ids as String
   * @return the query
   */
  WorkbasketHistoryQuery idIn(String... ids);

  /**
   * Add your workbasketId to your query.
   *
   * @param workbasketIds as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketIdIn(String... workbasketIds);

  /**
   * Add your eventType to your query.
   *
   * @param eventTypes as String
   * @return the query
   */
  WorkbasketHistoryQuery eventTypeIn(String... eventTypes);

  /**
   * Add your created TimeInterval to your query.
   *
   * @param createdWithin the {@link TimeInterval} within which the searched-for classifications
   *     were created.
   * @return the query
   */
  WorkbasketHistoryQuery createdWithin(TimeInterval... createdWithin);

  /**
   * Add your userId to your query.
   *
   * @param userIds as String
   * @return the query
   */
  WorkbasketHistoryQuery userIdIn(String... userIds);

  /**
   * Add your domain to your query.
   *
   * @param domains as String
   * @return the query
   */
  WorkbasketHistoryQuery domainIn(String... domains);

  /**
   * Add your workbasketKey to your query.
   *
   * @param workbasketKeys as String
   * @return the query
   */
  WorkbasketHistoryQuery keyIn(String... workbasketKeys);

  /**
   * Add your workbasketTypes to your query.
   *
   * @param workbasketTypes as String
   * @return the query
   */
  WorkbasketHistoryQuery typeIn(String... workbasketTypes);

  /**
   * Add your owner to your query.
   *
   * @param owners as String
   * @return the query
   */
  WorkbasketHistoryQuery ownerIn(String... owners);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevels1 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel1In(String... orgLevels1);

  /**
   * Add your orgLevel2 to your query.
   *
   * @param orgLevels2 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel2In(String... orgLevels2);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevels3 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel3In(String... orgLevels3);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevels4 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel4In(String... orgLevels4);

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   */
  WorkbasketHistoryQuery customAttributeIn(
      WorkbasketCustomField customField, String... searchArguments);

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched-for tasks
   * @return the query
   */
  WorkbasketHistoryQuery customAttributeLike(
      WorkbasketCustomField customField, String... searchArguments);

  /**
   * Add your workbasketId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketIds as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketIdLike(String... workbasketIds);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventTypes as String
   * @return the query
   */
  WorkbasketHistoryQuery eventTypeLike(String... eventTypes);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userIds as String
   * @return the query
   */
  WorkbasketHistoryQuery userIdLike(String... userIds);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domains as String
   * @return the query
   */
  WorkbasketHistoryQuery domainLike(String... domains);

  /**
   * Add your workbasketKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketKeys as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketKeyLike(String... workbasketKeys);

  /**
   * Add your workbasketType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketTypes as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketTypeLike(String... workbasketTypes);

  /**
   * Add your owner to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param owners as String
   * @return the query
   */
  WorkbasketHistoryQuery ownerLike(String... owners);

  /**
   * Add your orgLevel1 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevels1 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel1Like(String... orgLevels1);

  /**
   * Add your orgLevel2 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevels2 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel2Like(String... orgLevels2);

  /**
   * Add your orgLevel3 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevels3 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel3Like(String... orgLevels3);

  /**
   * Add your orgLevel4 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevels4 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel4Like(String... orgLevels4);

  /**
   * Sort the query result by workbasketId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sort the query result by eventType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByEventType(SortDirection sortDirection);

  /**
   * Sort the query result by created.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sort the query result by userId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByUserId(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain WorkbasketHistoryEvent#getId() id} of the {@linkplain
   * WorkbasketHistoryEvent}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderById(SortDirection sortDirection);

  /**
   * Sort the query result by Domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by WorkbasketKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByKey(SortDirection sortDirection);

  /**
   * Sort the query result by WorkbasketType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByType(SortDirection sortDirection);

  /**
   * Sort the query result by a custom attribute.
   *
   * @param num the number of the custom attribute as String (eg "4")
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   * @throws InvalidArgumentException if the number of the custom is incorrect.
   */
  WorkbasketHistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
      throws InvalidArgumentException;

  /**
   * Sort the query result by an orgLevel .
   *
   * @param num the number of the orgLevel as String (eg "4")
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   * @throws InvalidArgumentException if the number of the orgLevel is incorrect.
   */
  WorkbasketHistoryQuery orderByOrgLevel(int num, SortDirection sortDirection)
      throws InvalidArgumentException;
}
