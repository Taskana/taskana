package io.kadai.workbasket.api;

import io.kadai.common.api.BaseQuery;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.api.TimeInterval;
import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.workbasket.api.models.WorkbasketSummary;
import java.util.List;

/** WorkitemQuery for generating dynamic sql. */
public interface WorkbasketQuery extends BaseQuery<WorkbasketSummary, WorkbasketQueryColumnName> {

  /**
   * Add your ids to your query. The ids are compared to the ids of workbaskets with the IN
   * operator.
   *
   * @param ids the id as Strings
   * @return the query
   */
  WorkbasketQuery idIn(String... ids);

  /**
   * Add your keys to your query. The keys are compared case-insensitively to the keys of
   * workbaskets with the IN operator.
   *
   * @param keys the keys as Strings
   * @return the query
   */
  WorkbasketQuery keyIn(String... keys);

  /**
   * Add keys to your query. The keys are compared case-insensitively to the keys of workbaskets
   * with the SQL LIKE operator. You may add a wildcard like '%' to search generically. If you
   * specify multiple keys they are connected with an OR operator, this is, the query searches
   * workbaskets whose keys are like key1 or like key2, etc.
   *
   * @param keys the keys as Strings
   * @return the query
   */
  WorkbasketQuery keyLike(String... keys);

  /**
   * Add your names to your query. The names are compared case-insensitively to the names of
   * workbaskets
   *
   * @param names the names as Strings
   * @return the query
   */
  WorkbasketQuery nameIn(String... names);

  /**
   * Add names to your query. The names are compared case-insensitively to the names of workbaskets
   * with the SQL LIKE operator. You may add a wildcard like '%' to search generically. If you
   * specify multiple names, they are connected with an OR operator, this is, the query searches
   * workbaskets whose names are like name1 or like name2, etc.
   *
   * @param names the names as Strings
   * @return the query
   */
  WorkbasketQuery nameLike(String... names);

  /**
   * Add search strings to your query that are searched case-insensitively in the key and name
   * fields of workbaskets. You may add a wildcard like '%' to search generically. If you specify
   * multiple keys they are connected with an OR operator, this is, the query searches workbaskets
   * whose keys are like string1 or whose names are like string1 or whose keys are like string2 or
   * whose names are like string2, etc...
   *
   * @param searchStrings the search strings
   * @return the query
   */
  WorkbasketQuery keyOrNameLike(String... searchStrings);

  /**
   * Add your domains to your query.
   *
   * @param domains the domains as Strings
   * @return the query
   */
  WorkbasketQuery domainIn(String... domains);

  /**
   * Add your types to your query.
   *
   * @param types the types
   * @return the query
   */
  WorkbasketQuery typeIn(WorkbasketType... types);

  /**
   * Add the time intervals within which the workbasket was created to your query. For each time
   * interval, the database query will search for workbaskets whose created timestamp is after or at
   * the interval's begin and before or at the interval's end. If more than one interval is
   * specified, the query will connect them with the OR keyword. If either begin or end of an
   * interval are null, these values will not be specified in the query.
   *
   * @param createdWithin - the {@linkplain TimeInterval TimeIntervals} within which the workbasket
   *     was created
   * @return the query
   */
  WorkbasketQuery createdWithin(TimeInterval... createdWithin);

  /**
   * Add the time intervals within which the workbasket was modified to your query. For each time
   * interval, the database query will search for workbaskets whose created timestamp is after or at
   * the interval's begin and before or at the interval's end. If more than one interval is
   * specified, the query will connect them with the OR keyword. If either begin or end of an
   * interval are null, these values will not be specified in the query.
   *
   * @param modifiedWithin - the {@linkplain TimeInterval TimeIntervals} within which the workbasket
   *     was created
   * @return the query
   */
  WorkbasketQuery modifiedWithin(TimeInterval... modifiedWithin);

  /**
   * Add your description to your query. It will be compared case-insensitively to the descriptions
   * of workbaskets using the LIKE operator. You may use a wildcard like '%' to search generically.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param descriptions your description
   * @return the query
   */
  WorkbasketQuery descriptionLike(String... descriptions);

  /**
   * Add the owners to your query.
   *
   * @param owners the owners as String
   * @return the query
   */
  WorkbasketQuery ownerIn(String... owners);

  /**
   * Add the owners for pattern matching to your query. It will be compared in SQL with the LIKE
   * operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param owners the owners as Strings
   * @return the query
   */
  WorkbasketQuery ownerLike(String... owners);

  /**
   * Setting up the permissions which should be granted on the result workbaskets and the users
   * which should be checked. READ permission will always be checked by default.<br>
   * The AccessIds and the given permission will throw an Exception if they would be NULL.
   *
   * @param permissions which should be used for results.
   * @param accessIds Users which should be checked for given permissions on workbaskets.
   * @return the current query object.
   * @throws InvalidArgumentException if permissions OR the accessIds are NULL or empty.
   * @throws NotAuthorizedException if the current user is not member of {@linkplain
   *     KadaiRole#BUSINESS_ADMIN} or {@linkplain KadaiRole#ADMIN}
   */
  WorkbasketQuery accessIdsHavePermissions(
      List<WorkbasketPermission> permissions, String... accessIds)
      throws InvalidArgumentException, NotAuthorizedException;

  /**
   * Add condition to query if the caller (one of the accessIds of the caller) has the given
   * permissions on the workbasket.
   *
   * @param permissions the permissions for the query condition.
   * @return the updated query.
   */
  WorkbasketQuery callerHasPermissions(WorkbasketPermission... permissions);

  /**
   * Sort the query result by name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByName(SortDirection sortDirection);

  /**
   * Sort the query result by key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByKey(SortDirection sortDirection);

  /**
   * Sort the query result by description.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByDescription(SortDirection sortDirection);

  /**
   * Sort the query result by owner.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByOwner(SortDirection sortDirection);

  /**
   * Sort the query result by type.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByType(SortDirection sortDirection);

  /**
   * Sort the query result by domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByDomain(SortDirection sortDirection);

  /**
   * Add the domains for pattern matching to your query. It will be compared in SQL with the LIKE
   * operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param domains the domains of workbaskets as Strings
   * @return the query
   */
  WorkbasketQuery domainLike(String... domains);

  /**
   * This method sorts the query result according to the value of a custom field.
   *
   * @param customField identifies which custom attribute is affected.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByCustomAttribute(
      WorkbasketCustomField customField, SortDirection sortDirection);

  /**
   * Sort the query result by organization level 1.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel1(SortDirection sortDirection);

  /**
   * Sort the query result by organization level 2.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel2(SortDirection sortDirection);

  /**
   * Sort the query result by organization level 3.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel3(SortDirection sortDirection);

  /**
   * Sort the query result by organization level 4.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  WorkbasketQuery orderByOrgLevel4(SortDirection sortDirection);

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   */
  WorkbasketQuery customAttributeIn(WorkbasketCustomField customField, String... searchArguments);

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched-for tasks
   * @return the query
   */
  WorkbasketQuery customAttributeLike(WorkbasketCustomField customField, String... searchArguments);

  /**
   * Add the 1st organization level to your query.
   *
   * @param orgLevels1 the 1st organization level as String
   * @return the query
   */
  WorkbasketQuery orgLevel1In(String... orgLevels1);

  /**
   * Add the 1st organization level for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param orgLevels1 the 1st organization level as Strings
   * @return the query
   */
  WorkbasketQuery orgLevel1Like(String... orgLevels1);

  /**
   * Add the 2nd organization level to your query.
   *
   * @param orgLevels2 the 2nd organization level as String
   * @return the query
   */
  WorkbasketQuery orgLevel2In(String... orgLevels2);

  /**
   * Add the 2nd organization level for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param orgLevels2 the 2nd organization level as Strings
   * @return the query
   */
  WorkbasketQuery orgLevel2Like(String... orgLevels2);

  /**
   * Add the 3rd organization level to your query.
   *
   * @param orgLevels3 the 3rd organization level as String
   * @return the query
   */
  WorkbasketQuery orgLevel3In(String... orgLevels3);

  /**
   * Add the 3rd organization level for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param orgLevels3 the 3rd organization level as Strings
   * @return the query
   */
  WorkbasketQuery orgLevel3Like(String... orgLevels3);

  /**
   * Add the 4th organization level to your query.
   *
   * @param orgLevels4 the 4th organization level as String
   * @return the query
   */
  WorkbasketQuery orgLevel4In(String... orgLevels4);

  /**
   * Add the 4th organization level for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param orgLevels4 the 4th organization level as Strings
   * @return the query
   */
  WorkbasketQuery orgLevel4Like(String... orgLevels4);

  /**
   * Add to your query if the Workbasket shall be marked for deletion.
   *
   * @param markedForDeletion a simple flag showing if the workbasket is marked for deletion
   * @return the query
   */
  WorkbasketQuery markedForDeletion(boolean markedForDeletion);
}
