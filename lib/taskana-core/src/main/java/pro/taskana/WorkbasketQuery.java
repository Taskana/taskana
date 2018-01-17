package pro.taskana;

import java.util.Date;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.model.WorkbasketAuthorization;
import pro.taskana.model.WorkbasketType;

/**
 * WorkitemQuery for generating dynamic sql.
 */
public interface WorkbasketQuery extends BaseQuery<WorkbasketSummary> {

    /**
     * Add your keys to your query.
     *
     * @param key
     *            the keys as Strings
     * @return the query
     */
    WorkbasketQuery keyIn(String... key);

    /**
     * Add your names to your query.
     *
     * @param name
     *            the names as Strings
     * @return the query
     */
    WorkbasketQuery nameIn(String... name);

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
     *            the date after which the searched Workbaskets are created
     * @return the query
     */
    WorkbasketQuery createdAfter(Date createdAfter);

    /**
     * Add your createdBefore-Date to your query.
     *
     * @param createdBefore
     *            the date before which the searched Workbaskets are created
     * @return the query
     */
    WorkbasketQuery createdBefore(Date createdBefore);

    /**
     * Add your modifiedAfter-Date to your query.
     *
     * @param modifiedAfter
     *            the date after which the searched Workbaskets are modified
     * @return the query
     */
    WorkbasketQuery modifiedAfter(Date modifiedAfter);

    /**
     * Add your modifiedBefore-Date to your query.
     *
     * @param modifiedBefore
     *            the date before which the searched Workbaskets are modified
     * @return the query
     */
    WorkbasketQuery modifiedBefore(Date modifiedBefore);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE. If you use a wildcard like % tehn it
     * will be transmitted to the database.
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
