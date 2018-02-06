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

    /**
     * This method sorts the query result according to the business process id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByBusinessProcessId(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the claimed timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByClaimed(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the classification key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByClassificationKey(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the completed timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCompleted(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the created timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCreated(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the domain.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByDomain(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the due timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByDue(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the modified timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByModified(SortDirection sortDirection);

    /**
     * This method sorts the query result according to name.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByName(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the note.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByNote(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the owner.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByOwner(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the parent business process id.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the planned timestamp.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPlanned(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the company of the primary object reference.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the system of the primary object reference.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the system instance of the primary object reference.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the type of the primary object reference.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the value of the primary object reference.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the priority.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByPriority(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the state.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByState(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the workbasket key.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByWorkbasketKey(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom1 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom1(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom2 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom2(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom3 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom3(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom4 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom4(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom5 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom5(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom6 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom6(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom7 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom7(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom8 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom8(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom9 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom9(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the custom10 value.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCustom10(SortDirection sortDirection);
}
