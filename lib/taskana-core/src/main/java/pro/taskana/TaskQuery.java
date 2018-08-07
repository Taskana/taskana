package pro.taskana;

import pro.taskana.exceptions.InvalidArgumentException;

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
     * Add the UserIds of the creator to your query.
     *
     * @param creators
     *            of the queried tasks
     * @return the query
     */
    TaskQuery creatorIn(String... creators);

    /**
     * Add the UserIds of the creator for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param creators
     *            of the queried tasks
     * @return the query
     */
    TaskQuery creatorLike(String... creators);

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
     * Exclude these states from your query.
     *
     * @param states
     *            the states as {@link TaskState}
     * @return the query
     */
    TaskQuery stateNotIn(TaskState... states);

    /**
     * Add your classificationKey to your query.
     *
     * @param classificationKeys
     *            the classification key
     * @return the query
     */
    TaskQuery classificationKeyIn(String... classificationKeys);

    /**
     * Exlude these classificationKeys from your query.
     *
     * @param classificationKeys
     *            the classification key
     * @return the query
     */
    TaskQuery classificationKeyNotIn(String... classificationKeys);

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
     * Add your classificationId to your query.
     *
     * @param classificationIds
     *            the classification Ids
     * @return the query
     */
    TaskQuery classificationIdIn(String... classificationIds);

    /**
     * Add your classificationCategory to your query.
     *
     * @param classificationCategories
     *            the classification category for filtering
     * @return the query
     */
    TaskQuery classificationCategoryIn(String... classificationCategories);

    /**
     * Add your classificationCategory for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param classificationCategories
     *            the classification categories for filtering
     * @return the query
     */
    TaskQuery classificationCategoryLike(String... classificationCategories);

    /**
     * Add your workbasket key to the query.
     *
     * @param workbasketIdentifiers
     *            the key - domain combinations that identify workbaskets
     * @return the query
     */
    TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers);

    /**
     * Add your workbasket key to the query.
     *
     * @param workbasketIds
     *            the ids of workbaskets
     * @return the query
     */
    TaskQuery workbasketIdIn(String... workbasketIds);

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
     * Add the values of custom attribute number num for exact matching to your query.
     *
     * @param num
     *            identifies which custom attribute is affected. Taskana concatenates "custom_" with num and the
     *            resulting String must match the name of the database column that contains the custom attribute. Valid
     *            values are "1", "2" .. "16"
     * @param searchArguments
     *            the custom_num values of the searched for tasks
     * @return the query
     * @throws InvalidArgumentException
     *             if num has not a value of "1", "2" ... "16"
     */
    TaskQuery customAttributeIn(String num, String... searchArguments) throws InvalidArgumentException;

    /**
     * Add the values of custom attribute number num for pattern matching to your query. They will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param num
     *            identifies which custom attribute is affected. Taskana concatenates "custom_" with num and the
     *            resulting String must match the name of the database column that contains the custom attribute. Valid
     *            values are "1", "2" .. "16"
     * @param searchArguments
     *            the custom_num values of the searched-for tasks
     * @return the query
     * @throws InvalidArgumentException
     *             if num has not a value of "1", "2" ... "16"
     */
    TaskQuery customAttributeLike(String num, String... searchArguments) throws InvalidArgumentException;


    /**
     * Add the attachment classification keys for exact matching to your query.
     *
     * @param attachmentClassificationKeys
     *            the attachmentClassificationKeys values of the searched for tasks
     * @return the query
     */
    TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys);

    /**
     * Add the attachment classification Keys for pattern matching to your query. It will be compared in SQL with the LIKE
     * operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
     * combined with the OR keyword.
     *
     * @param attachmentClassificationKey
     *            the attachmentClassificationKeys values of the searched for tasks
     * @return the query
     */
    TaskQuery attachmentClassificationKeyLike(String... attachmentClassificationKey);


    /**
     * Add the attachment classification Ids for exact matching to your query.
     *
     * @param attachmentClassificationId
     *            the attachmentClassificationId values of the searched for tasks
     * @return the query
     */
    TaskQuery attachmentClassificationIdIn(String... attachmentClassificationId);

    /**
     * Add the values of attachment classification ids for pattern matching to your query. They will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param attachmentClassificationId
     *            the attachmentClassificationId values of the searched-for tasks
     * @return the query
     */
    TaskQuery attachmentClassificationIdLike(String... attachmentClassificationId);

    /**
     * Add the values of attachment channel for exact matching to your query.
     *
     * @param attachmentChannel
     *            the attachmentChannel values of the searched for tasks
     * @return the query
     */
    TaskQuery attachmentChannelIn(String... attachmentChannel);

    /**
     * Add the values of attachment channel for pattern matching to your query. They will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param attachmentChannel
     *            the attachmentChannel values of the searched-for tasks
     * @return the query
     */
    TaskQuery attachmentChannelLike(String... attachmentChannel);

    /**
     * Add the values of reference values for exact matching to your query.
     *
     * @param referenceValue
     *            the referenceValue values of the searched for tasks
     * @return the query
     */
    TaskQuery attachmentReferenceValueIn(String... referenceValue);

    /**
     * Add the values of reference values for pattern matching to your query. They will be compared in SQL
     * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple arguments
     * they are combined with the OR keyword.
     *
     * @param referenceValue
     *            the referenceValue values of the searched-for tasks
     * @return the query
     */
    TaskQuery attachmentReferenceValueLike(String... referenceValue);


    /**
     * Add your received-dates to your query.
     *
     * @param receivedIn
     *            the {@link TimeInterval} within which the searched-for tasks attachment were received the last time.
     * @return the query
     */

    TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn);

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
     * This method sorts the query result according to creators name.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByCreator(SortDirection sortDirection);

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
     * This method sorts the query result according to the value of a custom field.
     * The custom field is choosen by parameter num.
     *
     * @param num
     *            identifies which custom attribute is affected. Taskana concatenates "custom_" with num and the
     *            resulting String must match the name of the database column that contains the custom attribute. Valid
     *            values are "1", "2" .. "16"
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     * @throws InvalidArgumentException when number is not a valid number between 1 and 16
     */
    TaskQuery orderByCustomAttribute(String num, SortDirection sortDirection) throws InvalidArgumentException;

    /**
     * Filter for summaries which are containing one of the given taskIds.
     *
     * @param taskIds
     *            The ids of the searched-for tasks.
     * @return the taskQuery
     */
    TaskQuery idIn(String... taskIds);

    /**
     * This method sorts the query result according to the workbasket-Id of the tasks.
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByWorkbasketId(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the attachment classification key.
     * (Should only be used if there is one attachment per task in other case the result would be wrong.)
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection);


    /**
     * This method sorts the query result according to the attachment classification id.
     * (Should only be used if there is one attachment per task in other case the result would be wrong.)
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection);

    /**
     * This method sorts the query result according to the attachment channel.
     * (Should only be used if there is one attachment per task in other case the result would be wrong.)
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByAttachmentChannel(SortDirection sortDirection);



    /**
     * This method sorts the query result according to the attachment reference value.
     * (Should only be used if there is one attachment per task in other case the result would be wrong.)
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByAttachmentReference(SortDirection sortDirection);


    /**
     * This method sorts the query result according to the attachment received.
     * (Should only be used if there is one attachment per task in other case the result would be wrong.)
     *
     * @param sortDirection
     *            Determines whether the result is sorted in ascending or descending order. If sortDirection is null,
     *            the result is sorted in ascending order
     * @return the query
     */
    TaskQuery orderByAttachmentReceived(SortDirection sortDirection);

}
