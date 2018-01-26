package pro.taskana;

import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.model.TaskState;

/**
 * TaskQuery for generating dynamic sql.
 */
public interface TaskQuery extends BaseQuery<TaskSummary> {

    /**
     * Add your names to your query.
     *
     * @param name
     *            the names as Strings
     * @return the query
     */
    TaskQuery nameIn(String... name);

    /**
     * Add your description for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern.
     *
     * @param description
     *            your description
     * @return the query
     */
    TaskQuery descriptionLike(String description);

    /**
     * Add your custom note for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern.
     *
     * @param note
     *            your custom note
     * @return the query
     */
    TaskQuery noteLike(String note);

    /**
     * Add your priorities to your query.
     *
     * @param priorities
     *            as a integer
     * @return the query
     */
    TaskQuery priorityIn(int... priorities);

    /**
     * Add your state to your query.
     *
     * @param states
     *            the states as {@link TaskState}
     * @return the query
     */
    TaskQuery stateIn(TaskState... states);

    /**
     * Add your classificationKey to your query.
     *
     * @param classificationKey
     *            the classification key
     * @return the query
     */
    TaskQuery classificationKeyIn(String... classificationKey);

    /**
     * Add your workbasket key to the query.
     *
     * @param workbasketKeys
     *            the workbasket keys as String
     * @return the query
     * @throws NotAuthorizedException
     *             if the user have no rights
     */
    TaskQuery workbasketKeyIn(String... workbasketKeys) throws NotAuthorizedException;

    /**
     * Add your domain to the query.
     *
     * @param domain
     *            the domain as String
     * @return the query
     */
    TaskQuery domainIn(String... domain);

    /**
     * Add the owners to your query.
     *
     * @param owners
     *            the owners as String
     * @return the query
     */
    TaskQuery ownerIn(String... owners);

    /**
     * Add the companies of the primary object reference for exact matching to your query.
     *
     * @param companies
     *            the companies of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceCompanyIn(String... companies);

    /**
     * Add the company of the primary object reference for pattern matching to your query. It will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern.
     *
     * @param company
     *            the company of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceCompanyLike(String company);

    /**
     * Add the systems of the primary object reference for exact matching to your query.
     *
     * @param systems
     *            the systems of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemIn(String... systems);

    /**
     * Add the system of the primary object reference for pattern matching to your query. It will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern.
     *
     * @param system
     *            the system of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemLike(String system);

    /**
     * Add the system instances of the primary object reference for exact matching to your query.
     *
     * @param systemInstances
     *            the system instances of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances);

    /**
     * Add the system instance of the primary object reference for pattern matching to your query. It will be compared
     * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
     *
     * @param systemInstance
     *            the system instance of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemInstanceLike(String systemInstance);

    /**
     * Add the types of the primary object reference for exact matching to your query.
     *
     * @param types
     *            the types your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceTypeIn(String... types);

    /**
     * Add the type of the primary object reference for pattern matching to your query. It will be compared in SQL with
     * the LIKE operator. You may use a wildcard like % to specify the pattern.
     *
     * @param type
     *            the type of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceTypeLike(String type);

    /**
     * Add the values of the primary object reference for exact matching to your query.
     *
     * @param values
     *            the values of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceValueIn(String... values);

    /**
     * Add the value of the primary object reference for pattern matching to your query. It will be compared in SQL with
     * the LIKE operator. You may use a wildcard like % to specify the pattern.
     *
     * @param value
     *            the value of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceValueLike(String value);

    /**
     * Add the isRead flag to the query.
     *
     * @param isRead
     *            as Boolean. If null, it won't be integrated into the statement. You have to set false.
     * @return the query
     */
    TaskQuery readEquals(Boolean isRead);

    /**
     * Add the isTransferred flag to the query.
     *
     * @param isTransferred
     *            as Boolean. If null, it won't be integrated into the statement. You have to set false.
     * @return the query
     */
    TaskQuery transferredEquals(Boolean isTransferred);

    /**
     * Filter the custom fields with this query. The scan will be run over all 10 fields.
     *
     * @param customFields
     *            the value in the fields
     * @return the query
     */
    TaskQuery customFieldsIn(String... customFields);

    /**
     * This method provides a query builder for quering the database.
     *
     * @return a {@link ObjectReferenceQuery}
     */
    ObjectReferenceQuery createObjectReferenceQuery();
}
