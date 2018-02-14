package pro.taskana;

import pro.taskana.model.TaskState;

/**
 * TaskQuery for generating dynamic sql.
 */
public interface TaskQuery extends BaseQuery<TaskSummary> {

    /**
     * Add your names to your query.
     *
     * @param names
     *            the names as Strings
     * @return the query
     */
    TaskQuery nameIn(String... names);

    /**
     * Add your name for pattern matching to your query. It will be compared in SQL with the LIKE operator. You may use
     * a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param names
     *            your names
     * @return the query
     */
    TaskQuery nameLike(String... names);

    /**
     * Add your description for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param description
     *            your description
     * @return the query
     */
    TaskQuery descriptionLike(String... description);

    /**
     * Add your custom note for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param note
     *            your custom note
     * @return the query
     */
    TaskQuery noteLike(String... note);

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
     * @param classificationKeys
     *            the classification key
     * @return the query
     */
    TaskQuery classificationKeyIn(String... classificationKeys);

    /**
     * Add your classificationKey for pattern matching to your query. It will be compared in SQL with the LIKE operator.
     * You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with
     * the OR keyword.
     *
     * @param classificationKeys
     *            the classification key
     * @return the query
     */
    TaskQuery classificationKeyLike(String... classificationKeys);

    /**
     * Add your workbasket key to the query.
     *
     * @param workbasketKeys
     *            the workbasket keys as String
     * @return the query
     */
    TaskQuery workbasketKeyIn(String... workbasketKeys);

    /**
     * Add your workbasketKey for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param workbasketKeys
     *            the workbasket keys
     * @return the query
     */
    TaskQuery workbasketKeyLike(String... workbasketKeys);

    /**
     * Add your domain to the query.
     *
     * @param domains
     *            the domain as String
     * @return the query
     */
    TaskQuery domainIn(String... domains);

    /**
     * Add your domains for pattern matching to your query. It will be compared in SQL with the LIKE operator. You may
     * use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param domains
     *            the domains of the searched-for workbaskets
     * @return the query
     */
    TaskQuery domainLike(String... domains);

    /**
     * Add the owners to your query.
     *
     * @param owners
     *            the owners as String
     * @return the query
     */
    TaskQuery ownerIn(String... owners);

    /**
     * Add the owner for pattern matching to your query. It will be compared in SQL with the LIKE operator. You may use
     * a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param owners
     *            the owners of the searched tasks
     * @return the query
     */
    TaskQuery ownerLike(String... owners);

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
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param company
     *            the company of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceCompanyLike(String... company);

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
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param systems
     *            the system of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemLike(String... systems);

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
     * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple
     * arguments they are combined with the OR keyword.
     *
     * @param systemInstances
     *            the system instances of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceSystemInstanceLike(String... systemInstances);

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
     * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they
     * are combined with the OR keyword.
     *
     * @param types
     *            the types of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceTypeLike(String... types);

    /**
     * Add the value of the primary object reference for pattern matching to your query. It will be compared in SQL with
     * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they
     * are combined with the OR keyword.
     *
     * @param values
     *            the values of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceValueLike(String... values);

    /**
     * Add the values of the primary object reference for exact matching to your query.
     *
     * @param values
     *            the values of your primary object reference
     * @return the query
     */
    TaskQuery primaryObjectReferenceValueIn(String... values);

    /**
     * Add the time intervals within which the task was created to your query. For each time interval, the database
     * query will search for tasks whose created timestamp is after or at the interval's begin and before or at the
     * interval's end. If more than one interval is specified, the query will connect them with the OR keyword. If
     * either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task was created
     * @return the query
     */
    TaskQuery createdWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the task was claimed to your query. For each time interval, the database
     * query will search for tasks whose claimed timestamp is after or at the interval's begin and before or at the
     * interval's end. If more than one interval is specified, the query will connect them with the OR keyword. If
     * either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task was claimed
     * @return the query
     */
    TaskQuery claimedWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the task was completed to your query. For each time interval, the database
     * query will search for tasks whose completed timestamp is after or at the interval's begin and before or at the
     * interval's end. If more than one interval is specified, the query will connect them with the OR keyword. If
     * either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task was completed
     * @return the query
     */
    TaskQuery completedWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the task was modified to your query. For each time interval, the database
     * query will search for tasks whose modified timestamp is after or at the interval's begin and before or at the
     * interval's end. If more than one interval is specified, the query will connect them with the OR keyword. If
     * either begin or end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task was modified
     * @return the query
     */
    TaskQuery modifiedWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the task is planned to your query. For each time interval, the database query
     * will search for tasks whose planned timestamp is after or at the interval's begin and before or at the interval's
     * end. If more than one interval is specified, the query will connect them with the OR keyword. If either begin or
     * end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task is planned
     * @return the query
     */
    TaskQuery plannedWithin(TimeInterval... intervals);

    /**
     * Add the time intervals within which the task is due to your query. For each time interval, the database query
     * will search for tasks whose due timestamp is after or at the interval's begin and before or at the interval's
     * end. If more than one interval is specified, the query will connect them with the OR keyword. If either begin or
     * end of an interval are null, these values will not be specified in the query.
     *
     * @param intervals
     *            - the TimeIntervals within which the task is due
     * @return the query
     */
    TaskQuery dueWithin(TimeInterval... intervals);

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
     * Add the parent business process ids for exact matching to your query.
     *
     * @param parentBusinessProcessIds
     *            the parent businessProcessIds of the searched for tasks
     * @return the query
     */
    TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds);

    /**
     * Add the parent business process id for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param parentBusinessProcessIds
     *            the parent businessprocess ids of the searched for tasks
     * @return the query
     */
    TaskQuery parentBusinessProcessIdLike(String... parentBusinessProcessIds);

    /**
     * Add the business process ids for exact matching to your query.
     *
     * @param businessProcessIds
     *            the businessProcessIds of the searched for tasks
     * @return the query
     */
    TaskQuery businessProcessIdIn(String... businessProcessIds);

    /**
     * Add the business process id for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param businessProcessIds
     *            the business process ids of the searched-for tasks
     * @return the query
     */
    TaskQuery businessProcessIdLike(String... businessProcessIds);

    /**
     * Filter the custom fields with this query. The scan will be run over all 10 fields.
     *
     * @param customFields
     *            the value in the fields
     * @return the query
     */
    TaskQuery customFieldsIn(String... customFields);

    /**
     * Add the custom_1 values for exact matching to your query.
     *
     * @param strings
     *            the custom_1 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom1In(String... strings);

    /**
     * Add the custom_1 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_1 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom1Like(String... strings);

    /**
     * Add the custom_2 values for exact matching to your query.
     *
     * @param strings
     *            the custom_2 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom2In(String... strings);

    /**
     * Add the custom_2 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_2 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom2Like(String... strings);

    /**
     * Add the custom_3 values for exact matching to your query.
     *
     * @param strings
     *            the custom_3 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom3In(String... strings);

    /**
     * Add the custom_3 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_3 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom3Like(String... strings);

    /**
     * Add the custom_4 values for exact matching to your query.
     *
     * @param strings
     *            the custom_4 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom4In(String... strings);

    /**
     * Add the custom_4 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_4 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom4Like(String... strings);

    /**
     * Add the custom_5 values for exact matching to your query.
     *
     * @param strings
     *            the custom_5 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom5In(String... strings);

    /**
     * Add the custom_5 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_5 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom5Like(String... strings);

    /**
     * Add the custom_6 values for exact matching to your query.
     *
     * @param strings
     *            the custom_6 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom6In(String... strings);

    /**
     * Add the custom_6 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_6 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom6Like(String... strings);

    /**
     * Add the custom_7 values for exact matching to your query.
     *
     * @param strings
     *            the custom_7 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom7In(String... strings);

    /**
     * Add the custom_7 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_7 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom7Like(String... strings);

    /**
     * Add the custom_8 values for exact matching to your query.
     *
     * @param strings
     *            the custom_8 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom8In(String... strings);

    /**
     * Add the custom_8 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_8 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom8Like(String... strings);

    /**
     * Add the custom_9 values for exact matching to your query.
     *
     * @param strings
     *            the custom_9 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom9In(String... strings);

    /**
     * Add the custom_9 value for pattern matching to your query. It will be compared in SQL with the LIKE operator. You
     * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with the OR
     * keyword.
     *
     * @param strings
     *            the custom_9 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom9Like(String... strings);

    /**
     * Add the custom_10 values for exact matching to your query.
     *
     * @param strings
     *            the custom_10 values of the searched for tasks
     * @return the query
     */
    TaskQuery custom10In(String... strings);

    /**
     * Add the custom_10 value for pattern matching to your query. It will be compared in SQL with the LIKE operator.
     * You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are combined with
     * the OR keyword.
     *
     * @param strings
     *            the custom_10 values of the searched-for tasks
     * @return the query
     */
    TaskQuery custom10Like(String... strings);

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

    /*
     * Filter for summaries which are containing one of the given taskIds.
     * @param taskIds
     * @return the taskQuery
     */
    TaskQuery idIn(String... taskIds);
}
