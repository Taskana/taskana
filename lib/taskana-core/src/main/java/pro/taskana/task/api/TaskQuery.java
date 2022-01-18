package pro.taskana.task.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;

/** TaskQuery for generating dynamic sql. */
public interface TaskQuery extends BaseQuery<TaskSummary, TaskQueryColumnName> {

  // region id

  /**
   * Filter for summaries which contain one of the given taskIds.
   *
   * @param taskIds The ids of the searched-for tasks.
   * @return the taskQuery
   */
  TaskQuery idIn(String... taskIds);

  /**
   * Exclude summaries which contain one of the given taskIds.
   *
   * @param taskIds The ids of the searched-for tasks.
   * @return the taskQuery
   */
  TaskQuery idNotIn(String... taskIds);

  /**
   * This method sorts the query result according to the primary task id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByTaskId(SortDirection sortDirection);

  // endregion
  // region externalId

  /**
   * Add your external ids to your query.
   *
   * @param externalIds the external ids as Strings
   * @return the query
   */
  TaskQuery externalIdIn(String... externalIds);

  /**
   * Exclude these external ids from your query.
   *
   * @param externalIds the external ids as Strings
   * @return the query
   */
  TaskQuery externalIdNotIn(String... externalIds);

  // endregion
  // region received

  /**
   * Add the time intervals within which the task is received to your query. For each time interval,
   * the database query will search for tasks whose received timestamp is before the interval's
   * begin and after the interval's end. If more than one interval is specified, the query will
   * connect them with the OR keyword. If either begin or end of an interval are null, these values
   * will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task is received
   * @return the query
   */
  TaskQuery receivedWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task wasn't received from your query. For each time
   * interval, the database query will search for tasks whose received timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task wasn't received
   * @return the query
   */
  TaskQuery receivedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the received timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByReceived(SortDirection sortDirection);

  // endregion
  // region created

  /**
   * Add the time intervals within which the task was created to your query. For each time interval,
   * the database query will search for tasks whose created timestamp is after or at the interval's
   * begin and before or at the interval's end. If more than one interval is specified, the query
   * will connect them with the OR keyword. If either begin or end of an interval are null, these
   * values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task was created
   * @return the query
   */
  TaskQuery createdWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task wasn't created from your query. For each time
   * interval, the database query will search for tasks whose created timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task wasn't created
   * @return the query
   */
  TaskQuery createdNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the created timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByCreated(SortDirection sortDirection);

  // endregion
  // region claimed

  /**
   * Add the time intervals within which the task was claimed to your query. For each time interval,
   * the database query will search for tasks whose claimed timestamp is after or at the interval's
   * begin and before or at the interval's end. If more than one interval is specified, the query
   * will connect them with the OR keyword. If either begin or end of an interval are null, these
   * values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task was claimed
   * @return the query
   */
  TaskQuery claimedWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task wasn't claimed from your query. For each time
   * interval, the database query will search for tasks whose claimed timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task wasn't claimed
   * @return the query
   */
  TaskQuery claimedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the claimed timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByClaimed(SortDirection sortDirection);

  // endregion
  // region modified

  /**
   * Add the time intervals within which the task wasn't modified to your query. For each time
   * interval, the database query will search for tasks whose modified timestamp is after or at the
   * interval's begin and before or at the interval's end. If more than one interval is specified,
   * the query will connect them with the OR keyword. If either begin or end of an interval are
   * null, these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task was modified
   * @return the query
   */
  TaskQuery modifiedWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task wasn't modified from your query. For each time
   * interval, the database query will search for tasks whose modified timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task wasn't modified
   * @return the query
   */
  TaskQuery modifiedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the modified timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByModified(SortDirection sortDirection);

  // endregion
  // region planned

  /**
   * Add the time intervals within which the task is planned to your query. For each time interval,
   * the database query will search for tasks whose planned timestamp is after or at the interval's
   * begin and before or at the interval's end. If more than one interval is specified, the query
   * will connect them with the OR keyword. If either begin or end of an interval are null, these
   * values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task is planned
   * @return the query
   */
  TaskQuery plannedWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task isn't planned from your query. For each time
   * interval, the database query will search for tasks whose planned timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task isn't planned
   * @return the query
   */
  TaskQuery plannedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the planned timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPlanned(SortDirection sortDirection);

  // endregion
  // region due

  /**
   * Add the time intervals within which the task is due to your query. For each time interval, the
   * database query will search for tasks whose due timestamp is after or at the interval's begin
   * and before or at the interval's end. If more than one interval is specified, the query will
   * connect them with the OR keyword. If either begin or end of an interval are null, these values
   * will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task is due
   * @return the query
   */
  TaskQuery dueWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task isn't due from your query. For each time
   * interval, the database query will search for tasks whose due timestamp is before the interval's
   * begin and after the interval's end. If more than one interval is specified, the query will
   * connect them with the OR keyword. If either begin or end of an interval are null, these values
   * will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task isn't due
   * @return the query
   */
  TaskQuery dueNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the due timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByDue(SortDirection sortDirection);

  // endregion
  // region completed

  /**
   * Add the time intervals within which the task was completed to your query. For each time
   * interval, the database query will search for tasks whose completed timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task was completed
   * @return the query
   */
  TaskQuery completedWithin(TimeInterval... intervals);

  /**
   * Exclude the time intervals within which the task wasn't completed from your query. For each
   * time interval, the database query will search for tasks whose completed timestamp is before the
   * interval's begin and after the interval's end. If more than one interval is specified, the
   * query will connect them with the OR keyword. If either begin or end of an interval are null,
   * these values will not be specified in the query.
   *
   * @param intervals - the TimeIntervals within which the task wasn't completed
   * @return the query
   */
  TaskQuery completedNotWithin(TimeInterval... intervals);

  /**
   * This method sorts the query result according to the completed timestamp.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByCompleted(SortDirection sortDirection);

  // endregion
  // region name

  /**
   * Add your names to your query.
   *
   * @param names the names as Strings
   * @return the query
   */
  TaskQuery nameIn(String... names);

  /**
   * Exclude these names from your query.
   *
   * @param names the names as Strings
   * @return the query
   */
  TaskQuery nameNotIn(String... names);

  /**
   * Add your name for pattern matching to your query. It will be compared in SQL with the LIKE
   * operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param names your names
   * @return the query
   */
  TaskQuery nameLike(String... names);

  /**
   * Exclude these names for pattern matching from your query. It will be compared in SQL with the
   * LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param names your names
   * @return the query
   */
  TaskQuery nameNotLike(String... names);

  /**
   * This method sorts the query result according to name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByName(SortDirection sortDirection);

  // endregion
  // region creator

  /**
   * Add the UserIds of the creator to your query.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorIn(String... creators);

  /**
   * Exclude the UserIds of the creator from your query.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorNotIn(String... creators);

  /**
   * Add the UserIds of the creator for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorLike(String... creators);

  /**
   * Exclude the UserIds of the creator for pattern matching from your query. It will be compared in
   * SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorNotLike(String... creators);

  /**
   * This method sorts the query result according to creators name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByCreator(SortDirection sortDirection);

  // endregion
  // region note

  /**
   * Add your custom note for pattern matching to your query. It will be compared in SQL with the
   * LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param note your custom note
   * @return the query
   */
  TaskQuery noteLike(String... note);

  /**
   * Exclude your custom note for pattern matching from your query. It will be compared in SQL with
   * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param note your custom note
   * @return the query
   */
  TaskQuery noteNotLike(String... note);

  /**
   * This method sorts the query result according to the note.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByNote(SortDirection sortDirection);

  // endregion
  // region description

  /**
   * Add your description for pattern matching to your query. It will be compared in SQL with the
   * LIKE operator. You may use a wildcard like % to specify the pattern. If you specify multiple
   * arguments they are combined with the OR keyword.
   *
   * @param description your description
   * @return the query
   */
  TaskQuery descriptionLike(String... description);

  /**
   * Exclude your description for pattern matching from your query. It will be compared in SQL with
   * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param description your description
   * @return the query
   */
  TaskQuery descriptionNotLike(String... description);

  // endregion
  // region priority

  /**
   * Add your priorities to your query.
   *
   * @param priorities as a integer
   * @return the query
   */
  TaskQuery priorityIn(int... priorities);

  /**
   * Exclude the priorities from your query.
   *
   * @param priorities as a integer
   * @return the query
   */
  TaskQuery priorityNotIn(int... priorities);

  /**
   * This method sorts the query result according to the priority.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPriority(SortDirection sortDirection);

  // endregion
  // region state

  /**
   * Add your state to your query.
   *
   * @param states the states as {@link TaskState}
   * @return the query
   */
  TaskQuery stateIn(TaskState... states);

  /**
   * Exclude these states from your query.
   *
   * @param states the states as {@link TaskState}
   * @return the query
   */
  TaskQuery stateNotIn(TaskState... states);

  /**
   * This method sorts the query result according to the state.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByState(SortDirection sortDirection);

  // endregion
  // region classificationId

  /**
   * Add your classificationId to your query.
   *
   * @param classificationIds the classification Ids
   * @return the query
   */
  TaskQuery classificationIdIn(String... classificationIds);

  /**
   * Exclude these classificationIds from your query.
   *
   * @param classificationIds the classification Ids
   * @return the query
   */
  TaskQuery classificationIdNotIn(String... classificationIds);

  // endregion
  // region classificationKey

  /**
   * Add your classificationKey to your query.
   *
   * @param classificationKeys the classification key
   * @return the query
   */
  TaskQuery classificationKeyIn(String... classificationKeys);

  /**
   * Exclude these classificationKeys from your query.
   *
   * @param classificationKeys the classification key
   * @return the query
   */
  TaskQuery classificationKeyNotIn(String... classificationKeys);

  /**
   * Add your classificationKey for pattern matching to your query. It will be compared in SQL with
   * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param classificationKeys the classification key
   * @return the query
   */
  TaskQuery classificationKeyLike(String... classificationKeys);

  /**
   * Exclude the classificationKeys for pattern matching from your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param classificationKeys the classification key
   * @return the query
   */
  TaskQuery classificationKeyNotLike(String... classificationKeys);

  /**
   * This method sorts the query result according to the classification key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByClassificationKey(SortDirection sortDirection);

  // endregion
  // region classificationCategory

  /**
   * Add your classificationCategory to your query.
   *
   * @param classificationCategories the classification category for filtering
   * @return the query
   */
  TaskQuery classificationCategoryIn(String... classificationCategories);

  /**
   * Exclude the classificationCategory from your query.
   *
   * @param classificationCategories the classification category for filtering
   * @return the query
   */
  TaskQuery classificationCategoryNotIn(String... classificationCategories);

  /**
   * Add your classificationCategory for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword.
   *
   * @param classificationCategories the classification categories for filtering
   * @return the query
   */
  TaskQuery classificationCategoryLike(String... classificationCategories);

  /**
   * Exclude the classificationCategory for pattern matching from your query. It will be compared in
   * SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param classificationCategories the classification categories for filtering
   * @return the query
   */
  TaskQuery classificationCategoryNotLike(String... classificationCategories);

  // endregion
  // region classificationName

  /**
   * Add your classificationName to your query.
   *
   * @param classificationNames the classification name
   * @return the query
   */
  TaskQuery classificationNameIn(String... classificationNames);

  /**
   * Exclude the classificationName from your query.
   *
   * @param classificationNames the classification name
   * @return the query
   */
  TaskQuery classificationNameNotIn(String... classificationNames);

  /**
   * Add your classificationName for pattern matching to your query. It will be compared in SQL with
   * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword. *
   *
   * @param classificationNames the classification name
   * @return the query
   */
  TaskQuery classificationNameLike(String... classificationNames);

  /**
   * Exclude the classificationName for pattern matching from your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword. *
   *
   * @param classificationNames the classification name
   * @return the query
   */
  TaskQuery classificationNameNotLike(String... classificationNames);

  /**
   * This method sorts the query result according to the classification name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByClassificationName(SortDirection sortDirection);

  // endregion
  // region workbasketId

  /**
   * Add your workbasket id to the query.
   *
   * @param workbasketIds the ids of workbaskets
   * @return the query
   */
  TaskQuery workbasketIdIn(String... workbasketIds);

  /**
   * Exclude the workbasket id from the query.
   *
   * @param workbasketIds the ids of workbaskets
   * @return the query
   */
  TaskQuery workbasketIdNotIn(String... workbasketIds);

  /**
   * This method sorts the query result according to the workbasket-Id of the tasks.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByWorkbasketId(SortDirection sortDirection);

  // endregion
  // region workbasketKeyDomain

  /**
   * Add your workbasket key to the query.
   *
   * @param workbasketIdentifiers the key - domain combinations that identify workbaskets
   * @return the query
   */
  TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers);

  /**
   * Exclude the workbasket key from the query.
   *
   * @param workbasketIdentifiers the key - domain combinations that identify workbaskets
   * @return the query
   */
  TaskQuery workbasketKeyDomainNotIn(KeyDomain... workbasketIdentifiers);

  // endregion
  // region businessProcessId

  /**
   * Adds the long names of the owners to your query.
   *
   * @param longNames the long names as String
   * @return the query
   */
  TaskQuery ownerLongNameIn(String... longNames);

  /**
   * Adds the long names of the owners to your query, which should not be contained.
   *
   * @param longNames the long names as String
   * @return the query
   */
  TaskQuery ownerLongNameNotIn(String... longNames);

  /**
   * Adds the long names of the owner for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern.
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param longNames the owners of the searched tasks
   * @return the query
   */
  TaskQuery ownerLongNameLike(String... longNames);

  /**
   * Adds the long names of the owner for pattern matching to your query, which should not be
   * contained. It will be compared in SQL with the LIKE operator. You may use a wildcard like % to
   * specify the pattern.
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param longNames the owners of the searched tasks
   * @return the query
   */
  TaskQuery ownerLongNameNotLike(String... longNames);

  /**
   * Add the business process ids for exact matching to your query.
   *
   * @param businessProcessIds the businessProcessIds of the searched for tasks
   * @return the query
   */
  TaskQuery businessProcessIdIn(String... businessProcessIds);

  /**
   * Exclude the business process ids for exact matching from your query.
   *
   * @param businessProcessIds the businessProcessIds of the searched for tasks
   * @return the query
   */
  TaskQuery businessProcessIdNotIn(String... businessProcessIds);

  /**
   * Add your businessProcessId for pattern matching to your query. It will be compared in SQL with
   * the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword. *
   *
   * @param businessProcessIds the classification name
   * @return the query
   */
  TaskQuery businessProcessIdLike(String... businessProcessIds);

  /**
   * Exclude your businessProcessId for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. If you specify
   * multiple arguments they are combined with the OR keyword. *
   *
   * @param businessProcessIds the classification name
   * @return the query
   */
  TaskQuery businessProcessIdNotLike(String... businessProcessIds);

  /**
   * This method sorts the query result according to the business process id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByBusinessProcessId(SortDirection sortDirection);

  // endregion
  // region parentBusinessProcessId

  /**
   * Add the parent business process ids for exact matching to your query.
   *
   * @param parentBusinessProcessIds the parent businessProcessIds of the searched for tasks
   * @return the query
   */
  TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds);

  /**
   * Exclude the parent business process ids for exact matching from your query.
   *
   * @param parentBusinessProcessIds the parent businessProcessIds of the searched for tasks
   * @return the query
   */
  TaskQuery parentBusinessProcessIdNotIn(String... parentBusinessProcessIds);

  /**
   * Add your parent business process id for pattern matching to your query. It will be compared in
   * SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword. *
   *
   * @param businessProcessIds the classification name
   * @return the query
   */
  TaskQuery parentBusinessProcessIdLike(String... businessProcessIds);

  /**
   * Exclude your parent business process id for pattern matching to your query. It will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword. *
   *
   * @param businessProcessIds the classification name
   * @return the query
   */
  TaskQuery parentBusinessProcessIdNotLike(String... businessProcessIds);

  /**
   * This method sorts the query result according to the parent business process id.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection);

  // endregion
  // region owner

  /**
   * Add the owners to your query.
   *
   * @param owners the owners as String
   * @return the query
   */
  TaskQuery ownerIn(String... owners);

  /**
   * Filter out owners.
   *
   * @param owners the owners as String
   * @return the query
   */
  TaskQuery ownerNotIn(String... owners);

  /**
   * Add the owner for pattern matching to your query. It will be compared in SQL with the LIKE
   * operator. You may use a wildcard like % to specify the pattern.
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param owners the owners of the searched tasks
   * @return the query
   */
  TaskQuery ownerLike(String... owners);

  /**
   * Exclude the owner for pattern matching from your query. It will be compared in SQL with the
   * LIKE operator. You may use a wildcard like % to specify the pattern.
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param owners the owners of the searched tasks
   * @return the query
   */
  TaskQuery ownerNotLike(String... owners);

  /**
   * This method sorts the query result according to the owner.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByOwner(SortDirection sortDirection);

  // endregion
  // region primaryObjectReference

  /**
   * Add the {@link ObjectReference} to exact match to your query. Each individual value has to
   * match. Fields with the value 'null' will be ignored. The id of each ObjectReference will be
   * ignored
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param objectReferences the combined values which are searched together.
   * @return the query
   */
  TaskQuery primaryObjectReferenceIn(ObjectReference... objectReferences);
  // endregion
  // region primaryObjectReferenceCompany

  /**
   * Add the companies of the primary object reference for exact matching to your query.
   *
   * @param companies the companies of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyIn(String... companies);

  /**
   * Exclude the companies of the primary object reference for exact matching from your query.
   *
   * @param companies the companies of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyNotIn(String... companies);

  /**
   * Add the company of the primary object reference for pattern matching to your query. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param company the company of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyLike(String... company);

  /**
   * Exclude the company of the primary object reference for pattern matching from your query. It
   * will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param company the company of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyNotLike(String... company);

  /**
   * This method sorts the query result according to the company of the primary object reference.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection);

  // endregion
  // region primaryObjectReferenceSystem

  /**
   * Add the systems of the primary object reference for exact matching to your query.
   *
   * @param systems the systems of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemIn(String... systems);

  /**
   * Exclude the systems of the primary object reference for exact matching from your query.
   *
   * @param systems the systems of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemNotIn(String... systems);

  /**
   * Add the system of the primary object reference for pattern matching to your query. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param systems the system of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemLike(String... systems);

  /**
   * Exclude the system of the primary object reference for pattern matching from your query. It
   * will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param systems the system of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemNotLike(String... systems);

  /**
   * This method sorts the query result according to the system of the primary object reference.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection);

  // endregion
  // region primaryObjectReferenceSystemInstance

  /**
   * Add the system instances of the primary object reference for exact matching to your query.
   *
   * @param systemInstances the system instances of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances);

  /**
   * Exclude the system instances of the primary object reference for exact matching from your
   * query.
   *
   * @param systemInstances the system instances of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceNotIn(String... systemInstances);

  /**
   * Add the system instance of the primary object reference for pattern matching to your query. It
   * will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param systemInstances the system instances of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceLike(String... systemInstances);

  /**
   * Exclude the system instance of the primary object reference for pattern matching from your
   * query. It will be compared in SQL with the LIKE operator. You may use a wildcard like % to
   * specify the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param systemInstances the system instances of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceNotLike(String... systemInstances);

  /**
   * This method sorts the query result according to the system instance of the primary object
   * reference.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection);

  // endregion
  // region primaryObjectReferenceType

  /**
   * Add the types of the primary object reference for exact matching to your query.
   *
   * @param types the types your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeIn(String... types);

  /**
   * Exclude the types of the primary object reference for exact matching from your query.
   *
   * @param types the types your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeNotIn(String... types);

  /**
   * Add the type of the primary object reference for pattern matching to your query. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param types the types of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeLike(String... types);

  /**
   * Exclude the type of the primary object reference for pattern matching from your query. It will
   * be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param types the types of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeNotLike(String... types);

  /**
   * This method sorts the query result according to the type of the primary object reference.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection);

  // endregion
  // region primaryObjectReferenceValue

  /**
   * Add the values of the primary object reference for exact matching to your query.
   *
   * @param values the values of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueIn(String... values);

  /**
   * Exclude the values of the primary object reference for exact matching from your query.
   *
   * @param values the values of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueNotIn(String... values);

  /**
   * Add the value of the primary object reference for pattern matching to your query. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param values the values of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueLike(String... values);

  /**
   * Exclude the value of the primary object reference for pattern matching from your query. It will
   * be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param values the values of your primary object reference
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueNotLike(String... values);

  /**
   * This method sorts the query result according to the value of the primary object reference.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection);

  // endregion
  // region read

  /**
   * Add the isRead flag to the query.
   *
   * @param isRead as Boolean. If null, it won't be integrated into the statement. You have to set
   *     false.
   * @return the query
   */
  TaskQuery readEquals(Boolean isRead);

  // endregion
  // region transferred

  /**
   * Add the isTransferred flag to the query.
   *
   * @param isTransferred as Boolean. If null, it won't be integrated into the statement. You have
   *     to set false.
   * @return the query
   */
  TaskQuery transferredEquals(Boolean isTransferred);

  // endregion
  // region attachmentClassficationId

  /**
   * Add the attachment classification Ids for exact matching to your query.
   *
   * @param attachmentClassificationId the attachmentClassificationId values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationIdIn(String... attachmentClassificationId);

  /**
   * Exclude the attachment classification Ids for exact matching from your query.
   *
   * @param attachmentClassificationId the attachmentClassificationId values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationIdNotIn(String... attachmentClassificationId);

  /**
   * This method sorts the query result according to the attachment classification id. (Should only
   * be used if there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection);

  // endregion
  // region attachmentClassificationKey

  /**
   * Add the attachment classification keys for exact matching to your query.
   *
   * @param attachmentClassificationKeys the attachmentClassificationKeys values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys);

  /**
   * Exclude the attachment classification keys for exact matching from your query.
   *
   * @param attachmentClassificationKeys the attachmentClassificationKeys values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationKeyNotIn(String... attachmentClassificationKeys);

  /**
   * Add the attachment classification Keys for pattern matching to your query. It will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentClassificationKey the attachmentClassificationKeys values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationKeyLike(String... attachmentClassificationKey);

  /**
   * Exclude the attachment classification Keys for pattern matching from your query. It will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentClassificationKey the attachmentClassificationKeys values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationKeyNotLike(String... attachmentClassificationKey);

  /**
   * This method sorts the query result according to the attachment classification key. (Should only
   * be used if there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection);

  // endregion
  // region attachmentClassificationName

  /**
   * Add the attachment classification names for exact matching to your query.
   *
   * @param attachmentClassificationName the attachmentClassificationName values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationNameIn(String... attachmentClassificationName);

  /**
   * Exclude the attachment classification names for exact matching from your query.
   *
   * @param attachmentClassificationName the attachmentClassificationName values of the searched for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationNameNotIn(String... attachmentClassificationName);

  /**
   * Add the values of attachment classification names for pattern matching to your query. They will
   * be compared in SQL with the LIKE operator. You may use a wildcard like % to specify the
   * pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentClassificationName the attachmentClassificationName values of the searched-for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationNameLike(String... attachmentClassificationName);

  /**
   * Exclude the values of attachment classification names for pattern matching from your query.
   * They will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentClassificationName the attachmentClassificationName values of the searched-for
   *     tasks
   * @return the query
   */
  TaskQuery attachmentClassificationNameNotLike(String... attachmentClassificationName);

  /**
   * This method sorts the query result according to the attachment classification name. (Should
   * only be used if there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationName(SortDirection sortDirection);

  // endregion
  // region attachmentChannel

  /**
   * Add the values of attachment channel for exact matching to your query.
   *
   * @param attachmentChannel the attachmentChannel values of the searched for tasks
   * @return the query
   */
  TaskQuery attachmentChannelIn(String... attachmentChannel);

  /**
   * Exclude the values of attachment channel for exact matching from your query.
   *
   * @param attachmentChannel the attachmentChannel values of the searched for tasks
   * @return the query
   */
  TaskQuery attachmentChannelNotIn(String... attachmentChannel);

  /**
   * This method sorts the query result according to the owner's long name. (Should only be used if
   * each Task has an owner. Otherwise, the result is wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByOwnerLongName(SortDirection sortDirection);

  /**
   * Add the values of attachment channel for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentChannel the attachmentChannel values of the searched-for tasks
   * @return the query
   */
  TaskQuery attachmentChannelLike(String... attachmentChannel);

  /**
   * Exclude the values of attachment channel for pattern matching from your query. They will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param attachmentChannel the attachmentChannel values of the searched-for tasks
   * @return the query
   */
  TaskQuery attachmentChannelNotLike(String... attachmentChannel);

  /**
   * This method sorts the query result according to the attachment channel. (Should only be used if
   * there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentChannel(SortDirection sortDirection);

  // endregion
  // region attachmentReferenceValue

  /**
   * Add the values of reference values for exact matching to your query.
   *
   * @param referenceValue the referenceValue values of the searched for tasks
   * @return the query
   */
  TaskQuery attachmentReferenceValueIn(String... referenceValue);

  /**
   * Exclude the values of reference values for exact matching from your query.
   *
   * @param referenceValue the referenceValue values of the searched for tasks
   * @return the query
   */
  TaskQuery attachmentReferenceValueNotIn(String... referenceValue);

  /**
   * Add the values of reference values for pattern matching to your query. They will be compared in
   * SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param referenceValue the referenceValue values of the searched-for tasks
   * @return the query
   */
  TaskQuery attachmentReferenceValueLike(String... referenceValue);

  /**
   * Exclude the values of reference values for pattern matching to your query. They will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param referenceValue the referenceValue values of the searched-for tasks
   * @return the query
   */
  TaskQuery attachmentReferenceValueNotLike(String... referenceValue);

  /**
   * This method sorts the query result according to the attachment reference value. (Should only be
   * used if there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentReference(SortDirection sortDirection);

  // endregion
  // region attachmentReceived

  /**
   * Add your received-dates to your query.
   *
   * @param receivedIn the {@link TimeInterval} within which the searched-for tasks attachment were
   *     received the last time.
   * @return the query
   */
  TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn);

  /**
   * Exclude the received-dates from your query.
   *
   * @param receivedNotIn the {@link TimeInterval} within which the searched-for tasks attachment
   *     weren't received the last time.
   * @return the query
   */
  TaskQuery attachmentNotReceivedWithin(TimeInterval... receivedNotIn);

  /**
   * This method sorts the query result according to the attachment received. (Should only be used
   * if there is one attachment per task in other case the result would be wrong.)
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByAttachmentReceived(SortDirection sortDirection);

  // endregion
  // region secondaryObjectReference

  /**
   * Add the {@link ObjectReference} to exact match to your query. Each individual value has to
   * match. Fields with the value 'null' will be ignored. The id of each ObjectReference will be
   * ignored
   *
   * <p>If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param objectReferences the combined values which are searched together.
   * @return the query
   */
  TaskQuery secondaryObjectReferenceIn(ObjectReference... objectReferences);

  // endregion
  // region secondaryObjectReferenceCompany
  TaskQuery sorCompanyIn(String... companyIn);

  TaskQuery sorCompanyLike(String... companyLike);

  // endregion
  // region secondaryObjectReferenceSystem
  TaskQuery sorSystemIn(String... systemIn);

  TaskQuery sorSystemLike(String... systemLike);

  // endregion
  // region secondaryObjectReferenceSystemInstance
  TaskQuery sorSystemInstanceIn(String... systemInstanceIn);

  TaskQuery sorSystemInstanceLike(String... systemInstanceLike);

  // endregion
  // region secondaryObjectReferenceType
  TaskQuery sorTypeIn(String... typeIn);

  TaskQuery sorTypeLike(String... typeLike);

  // endregion
  // region secondaryObjectReferenceValue
  TaskQuery sorValueIn(String... valueIn);

  TaskQuery sorValueLike(String... valueLike);

  // region customAttributes

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments are not given
   */
  TaskQuery customAttributeIn(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Exclude these values of custom attributes from your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments are not given
   */
  TaskQuery customAttributeNotIn(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments is not given
   */
  TaskQuery customAttributeLike(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Exclude the values of custom attributes for pattern matching from your query. They will be
   * compared in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern.
   * If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments is not given
   */
  TaskQuery customAttributeNotLike(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * This method sorts the query result according to the value of a custom field.
   *
   * @param customField identifies which custom attribute is affected.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByCustomAttribute(TaskCustomField customField, SortDirection sortDirection);

  // endregion
  // region callbackState

  /**
   * Add your callbackState to your query.
   *
   * @param states the callback states as {@link CallbackState}
   * @return the query
   */
  TaskQuery callbackStateIn(CallbackState... states);

  /**
   * Exclude the callbackState from your query.
   *
   * @param states the callback states as {@link CallbackState}
   * @return the query
   */
  TaskQuery callbackStateNotIn(CallbackState... states);

  // endregion
  // region wildcardSearchValue

  /**
   * Add your wildcard search value for pattern matching to your query. It will be compared in SQL
   * with the LIKE operator. You may use a wildcard like % to specify the pattern. Must be used in
   * combination with the wildcardSearchFieldsIn parameter
   *
   * @param wildcardSearchValue the wildcard search value
   * @return the query
   */
  TaskQuery wildcardSearchValueLike(String wildcardSearchValue);

  /**
   * Add the Task fields for which the wildcard search should be performed as an exact match to your
   * query. Must be used in combination with the wildcardSearchValueLike parameter
   *
   * @param wildcardSearchFields the Task fields of your wildcard search
   * @return the query
   */
  TaskQuery wildcardSearchFieldsIn(WildcardSearchField... wildcardSearchFields);

  // endregion

  /**
   * This method provides a query builder for quering the database.
   *
   * @return a {@link ObjectReferenceQuery}
   */
  ObjectReferenceQuery createObjectReferenceQuery();

  /**
   * This method sorts the query result according to the domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByDomain(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the workbasket key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the workbasket name of the tasks.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskQuery orderByWorkbasketName(SortDirection sortDirection);
}
