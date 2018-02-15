package pro.taskana;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.WorkbasketAuthorization;
import pro.taskana.impl.WorkbasketType;

/**
 * WorkitemQuery for generating dynamic sql.
 */
public interface WorkbasketQuery extends BaseQuery<WorkbasketSummary> {

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
     */
    WorkbasketQuery accessIdsHavePermission(WorkbasketAuthorization permission, String... accessIds)
        throws InvalidArgumentException;

    /**
     * Setting up the permissions for the accessIds of the CurrentUserContext. READ permissions need to be granted,too
     * by default.<br>
     * The UserContext-AccessIds and the given permission will throw a Exception if they would be NULL.
     *
     * @return the current query object.
     * @param permission
     *            which should be used for results.
     * @throws InvalidArgumentException
     *             when permission OR accessIds of the userContext are NULL.
     */
    WorkbasketQuery callerHasPermission(WorkbasketAuthorization permission) throws InvalidArgumentException;

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

}
