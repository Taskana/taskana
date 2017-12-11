package pro.taskana;

import java.util.Date;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.model.Workbasket;
import pro.taskana.model.WorkbasketAuthorization;

/**
 * WorkitemQuery for generating dynamic sql.
 */
public interface WorkbasketQuery extends BaseQuery<Workbasket> {

    /**
     * Add your names to your query.
     * @param name
     *            the names as Strings
     * @return the query
     */
    WorkbasketQuery name(String... name);

    /**
     * Add your created-Dates to your query.
     * @param created TODO
     * @return TODO
     */
    WorkbasketQuery created(Date... created);

    /**
     * Add your modified-Dates to your query.
     * @param created TODO
     * @return TODO
     */
    WorkbasketQuery modified(Date... created);

    /**
     * Add your description to your query. It will be compared in SQL with an LIKE.
     * If you use a wildcard like % tehn it will be transmitted to the database.
     * @param description
     *            your description
     * @return the query
     */
    WorkbasketQuery descriptionLike(String description);

    /**
     * Add the owners to your query.
     * @param owners
     *            the owners as String
     * @return the query
     */
    WorkbasketQuery owner(String... owners);

    /**
     * Add the access requested.
     * @param permission
     *            the permissions requested
     * @param accessIds
     *            the accessIds as String
     * @return the query
     * @throws InvalidArgumentException TODO
     */
    WorkbasketQuery access(WorkbasketAuthorization permission, String... accessIds) throws InvalidArgumentException;

}
