package pro.taskana.workbasket.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/**
 * The ClassificationQuery allows for a custom search across all {@linkplain Workbasket
 * Workbaskets}.
 */
public interface WorkbasketQuery extends BaseQuery<WorkbasketSummary, WorkbasketQueryColumnName> {

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getId()}
   * value that is equal to any of the passed values.
   *
   * @param id the values of interest
   * @return the query
   */
  WorkbasketQuery idIn(String... id);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getKey()}
   * value that is equal to any of the passed values.
   *
   * @param key the values of interest
   * @return the query
   */
  WorkbasketQuery keyIn(String... key);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getKey()}
   * value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param key the patterns of interest
   * @return the query
   */
  WorkbasketQuery keyLike(String... key);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getName()}
   * value that is equal to any of the passed values.
   *
   * @param name the values of interest
   * @return the query
   */
  WorkbasketQuery nameIn(String... name);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getName()}
   * value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param name the patterns of interest
   * @return the query
   */
  WorkbasketQuery nameLike(String... name);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getKey()}
   * or {@linkplain Workbasket#getName()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param searchString the patterns of interest
   * @return the query
   */
  WorkbasketQuery keyOrNameLike(String... searchString);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getDomain()} value that is equal to any of the passed values.
   *
   * @param domain the values of interest
   * @return the query
   */
  WorkbasketQuery domainIn(String... domain);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain Workbasket#getType()}
   * value that is equal to any of the passed values.
   *
   * @param type the values of interest
   * @return the query
   */
  WorkbasketQuery typeIn(WorkbasketType... type);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} whose {@linkplain Workbasket#getCreated()} is
   * after or at the passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  WorkbasketQuery createdWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} whose {@linkplain Workbasket#getModified()} is
   * after or at the passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  WorkbasketQuery modifiedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getDescription()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param description the patterns of interest
   * @return the query
   */
  WorkbasketQuery descriptionLike(String... description);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOwner()} value that is equal to any of the passed values.
   *
   * @param owners the values of interest
   * @return the query
   */
  WorkbasketQuery ownerIn(String... owners);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOwner()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param owners the patterns of interest
   * @return the query
   */
  WorkbasketQuery ownerLike(String... owners);

  /**
   * Sets up the permission which should be granted on the result {@linkplain Workbasket} and the
   * users which should be checked.
   *
   * <p>READ permission will always be checked by default. <br>
   * The accessIds and the given permission will throw an exception if they would be NULL.
   *
   * @param permission which should be used for results
   * @param accessIds users which should be checked for given permissions on {@linkplain Workbasket
   *     Workbaskets}
   * @return the current query object
   * @throws InvalidArgumentException if permission OR the accessIds are NULL
   * @throws NotAuthorizedException if the current user is not member of role BUSINESS_ADMIN or
   *     ADMIN
   */
  WorkbasketQuery accessIdsHavePermission(WorkbasketPermission permission, String... accessIds)
      throws InvalidArgumentException, NotAuthorizedException;

  /**
   * Selects only {@linkplain Workbasket Workbaskets} to which the caller (one of the accessIds of
   * the caller) has permission to access.
   *
   * @param permission the values of interest
   * @return the query
   */
  WorkbasketQuery callerHasPermission(WorkbasketPermission permission);

  /**
   * Sorts the query result by {@linkplain Workbasket#getName()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByName(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getKey()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByKey(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getDescription()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByDescription(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getOwner()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByOwner(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getType()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByType(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getDomain()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByDomain(SortDirection sortDirection);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getDomain()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param domain the patterns of interest
   * @return the query
   */
  WorkbasketQuery domainLike(String... domain);

  /**
   * Sorts the query result according to the value of a {@linkplain
   * Workbasket#getCustomAttribute(WorkbasketCustomField)}.
   *
   * @param customField identifies which custom attribute is affected
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByCustomAttribute(
      WorkbasketCustomField customField, SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getOrgLevel1()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel1(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getOrgLevel2()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel2(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getOrgLevel3()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel3(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Workbasket#getOrgLevel4()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel4(SortDirection sortDirection);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getCustomAttribute(WorkbasketCustomField)} value that is equal to any of the passed
   * values.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the values of interest
   * @return the query
   */
  WorkbasketQuery customAttributeIn(WorkbasketCustomField customField, String... searchArguments);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getCustomAttribute(WorkbasketCustomField)} value that contains any of the passed
   * patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the patterns of interest
   * @return the query
   */
  WorkbasketQuery customAttributeLike(WorkbasketCustomField customField, String... searchArguments);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel1()} value that is equal to any of the passed values.
   *
   * @param orgLevel1 the values of interest
   * @return the query
   */
  WorkbasketQuery orgLevel1In(String... orgLevel1);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel1()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param orgLevel1 the patterns of interest
   * @return the query
   */
  WorkbasketQuery orgLevel1Like(String... orgLevel1);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel2()} value that is equal to any of the passed values.
   *
   * @param orgLevel2 the values of interest
   * @return the query
   */
  WorkbasketQuery orgLevel2In(String... orgLevel2);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel2()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param orgLevel2 the patterns of interest
   * @return the query
   */
  WorkbasketQuery orgLevel2Like(String... orgLevel2);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel3()} value that is equal to any of the passed values.
   *
   * @param orgLevel3 the values of interest
   * @return the query
   */
  WorkbasketQuery orgLevel3In(String... orgLevel3);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel3()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param orgLevel3 the patterns of interest
   * @return the query
   */
  WorkbasketQuery orgLevel3Like(String... orgLevel3);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel4()} value that is equal to any of the passed values.
   *
   * @param orgLevel4 the values of interest
   * @return the query
   */
  WorkbasketQuery orgLevel4In(String... orgLevel4);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#getOrgLevel4()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param orgLevel4 the patterns of interest
   * @return the query
   */
  WorkbasketQuery orgLevel4Like(String... orgLevel4);

  /**
   * Selects only {@linkplain Workbasket Workbaskets} which have a {@linkplain
   * Workbasket#isMarkedForDeletion()} flag that is equal to the passed flag.
   *
   * @param markedForDeletion the flag of interest
   * @return the query
   */
  WorkbasketQuery markedForDeletion(boolean markedForDeletion);
}
