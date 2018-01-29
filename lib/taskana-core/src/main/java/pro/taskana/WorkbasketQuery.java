package pro.taskana;

import java.time.Instant;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;

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
    BaseQuery<WorkbasketSummary> keyLike(String... key);

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
    BaseQuery<WorkbasketSummary> nameLike(String... name);

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
    BaseQuery<WorkbasketSummary> keyOrNameLike(String... searchString);

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
     * Add your createdAfter-Date to your query.
     *
     * @param createdAfter
     *            as Instant
     * @return the query
     */
    WorkbasketQuery createdAfter(Instant createdAfter);

    /**
     * Add your createdBefore-Date to your query.
     *
     * @param createdBefore
     *            as Instant
     * @return the query
     */
    WorkbasketQuery createdBefore(Instant createdBefore);

    /**
     * Add your modifiedAfter-Date to your query.
     *
     * @param modifiedAfter
     *            as Instant
     * @return the query
     */
    WorkbasketQuery modifiedAfter(Instant modifiedAfter);

    /**
     * Add your modifiedBefore-Date to your query.
     *
     * @param modifiedBefore
     *            as Instant
     * @return the query
     */
    WorkbasketQuery modifiedBefore(Instant modifiedBefore);

    /**
     * Add your description to your query. It will be compared case-insensitively to the descriptions of workbaskets.
     * You may use a wildcard like '%' to search generically.
     *
     * @param description
     *            your description
     * @return the query
     */
    WorkbasketQuery descriptionLike(String description);

    /**
     * Add the owners to your query.
     *
     * @param owners
     *            the owners as String
     * @return the query
     */
    WorkbasketQuery ownerIn(String... owners);

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
    WorkbasketQuery accessIdsHavePersmission(WorkbasketAuthorization permission, String... accessIds)
        throws InvalidArgumentException;

    /**
     * Setting up the permissions for the accessIds of the CurrentUserContext. READ permissions need to be granted,too
     * by default.<br>
     * The UserContext-AccessIds and the given permission will throw a Exception if they would be NULL.
     *
     * @param permission
     *            which should be used for results.
     * @return the current query object.
     * @throws InvalidArgumentException
     *             when permission OR accessIds of the userContext are NULL.
     */
    WorkbasketQuery callerHasPermission(WorkbasketAuthorization permission) throws InvalidArgumentException;

}
