package pro.taskana.simplehistory.impl.workbasket;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;

/** HistoryQuery for generating dynamic sql. */
public interface WorkbasketHistoryQuery
    extends BaseQuery<WorkbasketHistoryEvent, WorkbasketHistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param idIn as String
   * @return the query
   */
  WorkbasketHistoryQuery idIn(String... idIn);

  /**
   * Add your workbasketId to your query.
   *
   * @param workbasketId as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketIdIn(String... workbasketId);

  /**
   * Add your eventType to your query.
   *
   * @param eventType as String
   * @return the query
   */
  WorkbasketHistoryQuery eventTypeIn(String... eventType);

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
   * @param userId as String
   * @return the query
   */
  WorkbasketHistoryQuery userIdIn(String... userId);

  /**
   * Add your domain to your query.
   *
   * @param domain as String
   * @return the query
   */
  WorkbasketHistoryQuery domainIn(String... domain);

  /**
   * Add your workbasketKey to your query.
   *
   * @param workbasketKey as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketKeyIn(String... workbasketKey);

  /**
   * Add your workbasketType to your query.
   *
   * @param workbasketType as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketTypeIn(String... workbasketType);

  /**
   * Add your owner to your query.
   *
   * @param owner as String
   * @return the query
   */
  WorkbasketHistoryQuery ownerIn(String... owner);


  /**
   * Add your custom1 to your query.
   *
   * @param custom1 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom1In(String... custom1);

  /**
   * Add your custom2 to your query.
   *
   * @param custom2 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom2In(String... custom2);

  /**
   * Add your custom3 to your query.
   *
   * @param custom3 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom3In(String... custom3);

  /**
   * Add your custom4 to your query.
   *
   * @param custom4 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom4In(String... custom4);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevel1 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel1In(String... orgLevel1);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevel2 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel2In(String... orgLevel2);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevel3 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel3In(String... orgLevel3);

  /**
   * Add your orgLevel1 to your query.
   *
   * @param orgLevel4 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel4In(String... orgLevel4);

  /**
   * Add your workbasketId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketId as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketIdLike(String... workbasketId);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventType as String
   * @return the query
   */
  WorkbasketHistoryQuery eventTypeLike(String... eventType);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userId as String
   * @return the query
   */
  WorkbasketHistoryQuery userIdLike(String... userId);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domain as String
   * @return the query
   */
  WorkbasketHistoryQuery domainLike(String... domain);

  /**
   * Add your workbasketKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketKey as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketKeyLike(String... workbasketKey);

  /**
   * Add your workbasketType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketType as String
   * @return the query
   */
  WorkbasketHistoryQuery workbasketTypeLike(String... workbasketType);

  /**
   * Add your owner to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param owner as String
   * @return the query
   */
  WorkbasketHistoryQuery ownerLike(String... owner);

  /**
   * Add your custom1 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom1 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom1Like(String... custom1);

  /**
   * Add your custom2 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom2 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom2Like(String... custom2);

  /**
   * Add your custom3 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom3 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom3Like(String... custom3);

  /**
   * Add your custom4 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom4 as String
   * @return the query
   */
  WorkbasketHistoryQuery custom4Like(String... custom4);

  /**
   * Add your orgLevel1 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevel1 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel1Like(String... orgLevel1);

  /**
   * Add your orgLevel2 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevel2 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel2Like(String... orgLevel2);

  /**
   * Add your orgLevel3 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevel3 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel3Like(String... orgLevel3);

  /**
   * Add your orgLevel4 to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param orgLevel4 as String
   * @return the query
   */
  WorkbasketHistoryQuery orgLevel4Like(String... orgLevel4);

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
  WorkbasketHistoryQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sort the query result by WorkbasketType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketHistoryQuery orderByWorkbasketType(SortDirection sortDirection);

  /**
   * Sort the query result by a custom attribute.
   *
   * @param num the number of the custom attribute as String (eg "4")
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   * @throws InvalidArgumentException when the number of the custom is incorrect.
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
   * @throws InvalidArgumentException when the number of the orgLevel is incorrect.
   */
  WorkbasketHistoryQuery orderByOrgLevel(int num, SortDirection sortDirection)
      throws InvalidArgumentException;
}
