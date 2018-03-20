package pro.taskana;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;

/**
 * WorkitemQuery for generating dynamic sql.
 */
public interface WorkbasketQuery extends BaseQuery<WorkbasketSummary> {

    /**
     * Add your ids to your query. The ids are compared to the ids of workbaskets with the IN operator.
     *
     * @param id
     *            the id as Strings
     * @return the query
     */
    WorkbasketQuery idIn(String... id);

    /**
     * Add your keys to your query. The keys are compared case-insensitively to the keys of workbaskets with the IN
     * operator.
     *
     * @param key
     *            the keys as Strings
     * @return the query
     */
    WorkbasketQuery keyIn(String... key);

    /**
     * Add keys to your query. The keys are compared case-insensitively to the keys of workbaskets with the SQL LIKE
     * operator. You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected
     * with an OR operator, this is, the query searches workbaskets whose keys are like key1 or like key2, etc.
     *
     * @param key
     *            the keys as Strings
     * @return the query
     */
    WorkbasketQuery keyLike(String... key);

    /**
     * Add your names to your query. The names are compared case-insensitively to the names of workbaskets
     *
     * @param name
     *            the names as Strings
     * @return the query
     */
    WorkbasketQuery nameIn(String... name);

    /**
     * Add names to your query. The names are compared case-insensitively to the names of workbaskets with the SQL LIKE
     * operator. You may add a wildcard like '%' to search generically. If you specify multiple names, they are
     * connected with an OR operator, this is, the query searches workbaskets whose names are like name1 or like name2,
     * etc.
     *
     * @param name
     *            the names as Strings
     * @return the query
     */
    WorkbasketQuery nameLike(String... name);

    /**
     * Add search strings to your query that are searched case-insensitively in the key and name fields of workbaskets.
     * You may add a wildcard like '%' to search generically. If you specify multiple keys they are connected with an OR
     * operator, this is, the query searches workbaskets whose keys are like string1 or whose names are like string1 or
     * whose keys are like string2 or whose names are like string2, etc...
     *
     * @param searchString
     *            the seach strings
     * @return the query
     */
    WorkbasketQuery keyOrNameLike(String... searchString);

    /**
     * Add your domains to your query.
     *
     * @param domain
     *            the domains as Strings
     * @return the query
     */
    WorkbasketQuery domainIn(String... domain);

    /**
     * Add your types to your query.
     *
     * @param type
     *            the types
     * @return the query
     */
    WorkbasketQuery typeIn(WorkbasketType... type);

    /**
     * Add the time intervals within which the workbasket was created to your query. For each time interval, the
     * database query will search for workbaskets whose created timestamp is after or at the interval's begin and before
     * or at the interval's end. If more than one interval is specified, the query will connect them with the OR
     * keyword. If either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the workbasket was created
     * @return the query
     */
    WorkbasketQuery createdWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the workbasket was modified to your query. For each time interval, the
     * database query will search for workbaskets whose created timestamp is after or at the interval's begin and before
     * or at the interval's end. If more than one interval is specified, the query will connect them with the OR
     * keyword. If either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the workbasket was created
     * @return the query
     */
    WorkbasketQuery modifiedWithin(TimeInterval... intervals);

    /**
     * Add your description to your query. It will be compared case-insensitively to the descriptions of workbaskets
     * using the LIKE operator. You may use a wildcard like '%' to search generically. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param description
     *            your description
     * @return the query
     */
    WorkbasketQuery descriptionLike(String... description);

    /**
     * Add the owners to your query.
     *
     * @param owners
     *            the owners as String
     * @return the query
     */
    WorkbasketQuery ownerIn(String... owners);

    /**
     * Add the owners for pattern matching to your query. It will be compared in SQL with the LIKE operator. You may use
     * a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param owners
     *            the owners as Strings
     * @return the query
     */
    WorkbasketQuery ownerLike(String... owners);

    /**
     * Setting up the permission which should be granted on the result workbaskets and the users which should be
     * checked. READ permission will always be checked by default.<br>
     * The AccessIds and the given permission will throw a Exception if they would be NULL.
     *
     * @param permission
     *            which should be used for results.
     * @param accessIds
     *            Users which sould be checked for given permissions on workbaskets.
     * @return the current query object.
     * @throws InvalidArgumentException
     *             when permission OR the accessIds are NULL.
     * @throws NotAuthorizedException
     *             if the current user is not member of role BUSINESS_ADMIN or ADMIN
     */
    WorkbasketQuery accessIdsHavePermission(WorkbasketPermission permission, String... accessIds)
        throws InvalidArgumentException, NotAuthorizedException;

    /**
     * Add condition to query if the caller (one of the accessIds of the caller) has the given permission on the
     * workbasket.
     *
     * @return the updated query.
     * @param permission
     *            the permission for the query condition.
     */
    WorkbasketQuery callerHasPermission(WorkbasketPermission permission);

    /**
     * Sort the query result by name.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByName(SortDirection sortDirection);

    /**
     * Sort the query result by key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByKey(SortDirection sortDirection);

    /**
     * Sort the query result by description.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByDescription(SortDirection sortDirection);

    /**
     * Sort the query result by owner.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByOwner(SortDirection sortDirection);

    /**
     * Sort the query result by type.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByType(SortDirection sortDirection);

    /**
     * Sort the query result by domain.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByDomain(SortDirection sortDirection);

    /**
     * Add the domains for pattern matching to your query. It will be compared in SQL with the LIKE operator. You may
     * use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param domain
     *            the domains of workbaskets as Strings
     * @return the query
     */
    WorkbasketQuery domainLike(String... domain);

    /**
     * Sort the query result by custom property 1.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByCustom1(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 2.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByCustom2(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 3.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByCustom3(SortDirection sortDirection);

    /**
     * Sort the query result by custom property 4.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByCustom4(SortDirection sortDirection);

    /**
     * Sort the query result by organization level 1.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByOrgLevel1(SortDirection sortDirection);

    /**
     * Sort the query result by organization level 2.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByOrgLevel2(SortDirection sortDirection);

    /**
     * Sort the query result by organization level 3.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByOrgLevel3(SortDirection sortDirection);

    /**
     * Sort the query result by organization level 4.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    WorkbasketQuery orderByOrgLevel4(SortDirection sortDirection);

    /**
     * Add the 1st custom property to your query.
     *
     * @param custom1
     *            the 1st custom property as String
     * @return the query
     */
    WorkbasketQuery custom1In(String... custom1);

    /**
     * Add the 1st custom property for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param custom1
     *            the 1st custom property of workbaskets as Strings
     * @return the query
     */
    WorkbasketQuery custom1Like(String... custom1);

    /**
     * Add the 2nd custom property to your query.
     *
     * @param custom2
     *            the 2nd custom property as String
     * @return the query
     */
    WorkbasketQuery custom2In(String... custom2);

    /**
     * Add the 2nd custom property for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param custom2
     *            the 2nd custom property of workbaskets as Strings
     * @return the query
     */
    WorkbasketQuery custom2Like(String... custom2);

    /**
     * Add the 3rd custom property to your query.
     *
     * @param custom3
     *            the 3rd custom property as String
     * @return the query
     */
    WorkbasketQuery custom3In(String... custom3);

    /**
     * Add the 3rd custom property for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param custom3
     *            the 3rd custom property of workbaskets as Strings
     * @return the query
     */
    WorkbasketQuery custom3Like(String... custom3);

    /**
     * Add the 4th custom property to your query.
     *
     * @param custom4
     *            the 4th custom property as String
     * @return the query
     */
    WorkbasketQuery custom4In(String... custom4);

    /**
     * Add the 4th custom property for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param custom4
     *            the 4th custom property of workbaskets as Strings
     * @return the query
     */
    WorkbasketQuery custom4Like(String... custom4);

    /**
     * Add the 1st organization level to your query.
     *
     * @param orgLevel1
     *            the 1st organization level as String
     * @return the query
     */
    WorkbasketQuery orgLevel1In(String... orgLevel1);

    /**
     * Add the 1st organization level for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param orgLevel1
     *            the 1st organization level as Strings
     * @return the query
     */
    WorkbasketQuery orgLevel1Like(String... orgLevel1);

    /**
     * Add the 2nd organization level to your query.
     *
     * @param orgLevel2
     *            the 2nd organization level as String
     * @return the query
     */
    WorkbasketQuery orgLevel2In(String... orgLevel2);

    /**
     * Add the 2nd organization level for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param orgLevel2
     *            the 2nd organization level as Strings
     * @return the query
     */
    WorkbasketQuery orgLevel2Like(String... orgLevel2);

    /**
     * Add the 3rd organization level to your query.
     *
     * @param orgLevel3
     *            the 3rd organization level as String
     * @return the query
     */
    WorkbasketQuery orgLevel3In(String... orgLevel3);

    /**
     * Add the 3rd organization level for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param orgLevel3
     *            the 3rd organization level as Strings
     * @return the query
     */
    WorkbasketQuery orgLevel3Like(String... orgLevel3);

    /**
     * Add the 4th organization level to your query.
     *
     * @param orgLevel4
     *            the 4th organization level as String
     * @return the query
     */
    WorkbasketQuery orgLevel4In(String... orgLevel4);

    /**
     * Add the 4th organization level for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param orgLevel4
     *            the 4th organization level as Strings
     * @return the query
     */
    WorkbasketQuery orgLevel4Like(String... orgLevel4);
}
