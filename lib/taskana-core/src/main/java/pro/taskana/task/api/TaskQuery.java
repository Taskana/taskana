package pro.taskana.task.api;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.models.Attachment;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.api.models.TaskSummary;
import pro.taskana.workbasket.api.models.Workbasket;

/** The TaskQuery allows for a custom search across all {@linkplain Task Tasks}. */
public interface TaskQuery extends BaseQuery<TaskSummary, TaskQueryColumnName> {

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getName()} value that is
   * equal to any of the passed values.
   *
   * @param names the values of interest
   * @return the query
   */
  TaskQuery nameIn(String... names);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getName()} value that
   * contains any of the passed patterns.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param names your names
   * @return the query
   */
  TaskQuery nameLike(String... names);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getExternalId()} value that
   * is equal to any of the passed values.
   *
   * @param externalIds the values of interest
   * @return the query
   */
  TaskQuery externalIdIn(String... externalIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getExternalId()} value that
   * contains any of the passed patterns.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param externalIds your external ids
   * @return the query
   */
  TaskQuery externalIdLike(String... externalIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getCreator()} value that is
   * equal to any of the passed values.
   *
   * @param creators the values of interest
   * @return the query
   */
  TaskQuery creatorIn(String... creators);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getCreator()} value that
   * contains any of the passed patterns.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorLike(String... creators);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getDescription()} value that
   * contains any of the passed patterns.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param description your description
   * @return the query
   */
  TaskQuery descriptionLike(String... description);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getNote()} value that
   * contains any of the passed patterns.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param note your custom note
   * @return the query
   */
  TaskQuery noteLike(String... note);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getPriority()} value that is
   * equal to any of the passed values.
   *
   * @param priorities the values of interest
   * @return the query
   */
  TaskQuery priorityIn(int... priorities);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getState()} value that is
   * equal to any of the passed values.
   *
   * @param states the values of interest
   * @return the query
   */
  TaskQuery stateIn(TaskState... states);

  /**
   * Selects only {@linkplain Task Tasks} which do not have a {@linkplain Task#getState()} value
   * that is equal to any of the passed values.
   *
   * @param states the values of interest
   * @return the query
   */
  TaskQuery stateNotIn(TaskState... states);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} has a {@linkplain
   * Classification#getKey()} value that is equal to any of the passed values.
   *
   * @param classificationKeys the values of interest
   * @return the query
   */
  TaskQuery classificationKeyIn(String... classificationKeys);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} does not have a
   * {@linkplain Classification#getKey()} value that is equal to any of the passed values.
   *
   * @param classificationKeys the values of interest
   * @return the query
   */
  TaskQuery classificationKeyNotIn(String... classificationKeys);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} has a {@linkplain
   * Classification#getKey()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param classificationKeys the patterns of interest
   * @return the query
   */
  TaskQuery classificationKeyLike(String... classificationKeys);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} has a {@linkplain
   * Classification#getId()} value that is equal to any of the passed values.
   *
   * @param classificationIds the values of interest
   * @return the query
   */
  TaskQuery classificationIdIn(String... classificationIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getClassificationCategory()}
   * value that is equal to any of the passed values.
   *
   * @param classificationCategories the values of interest
   * @return the query
   */
  TaskQuery classificationCategoryIn(String... classificationCategories);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getClassificationCategory()}
   * value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param classificationCategories the patterns of interest
   * @return the query
   */
  TaskQuery classificationCategoryLike(String... classificationCategories);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} has a {@linkplain
   * Classification#getName()} value that is equal to any of the passed values.
   *
   * @param classificationNames the values of interest
   * @return the query
   */
  TaskQuery classificationNameIn(String... classificationNames);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Classification} has a {@linkplain
   * Classification#getName()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param classificationNames the patterns of interest
   * @return the query
   */
  TaskQuery classificationNameLike(String... classificationNames);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Workbasket} has a {@linkplain
   * Workbasket#getKey()} and {@linkplain Workbasket#getDomain()} value equal to any of the passed
   * values.
   *
   * @param workbasketIdentifiers the values of interest
   * @return the query
   */
  TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Workbasket} has a {@linkplain
   * Workbasket#getId()} value equal to any of the passed values.
   *
   * @param workbasketIds the ids of {@linkplain Workbasket Workbaskets}
   * @return the query
   */
  TaskQuery workbasketIdIn(String... workbasketIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getOwner()} value that is
   * equal to any of the passed values.
   *
   * @param owners the values of interest
   * @return the query
   */
  TaskQuery ownerIn(String... owners);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getOwner()} value that
   * contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param owners the patterns of interest
   * @return the query
   */
  TaskQuery ownerLike(String... owners);

  /**
   * Selects only {@linkplain Task Tasks} which have a primary {@linkplain Task#getPrimaryObjRef()}
   * value that is equal to any of the passed values.
   *
   * @param objectReferences the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceIn(ObjectReference... objectReferences);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getCompany()} value that is equal to any of the passed values.
   *
   * @param companies the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyIn(String... companies);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getCompany()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param company the patterns of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyLike(String... company);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getSystem()} value that is equal to any of the passed values.
   *
   * @param systems the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemIn(String... systems);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getSystem()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param systems the patterns of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemLike(String... systems);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getSystemInstance()} value that is equal to any of the passed
   * values.
   *
   * @param systemInstances the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getSystemInstance()} value that contains any of the passed
   * patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param systemInstances the patterns of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceLike(String... systemInstances);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getType()} value that is equal to any of the passed values.
   *
   * @param types the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeIn(String... types);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getType()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param types the patterns of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeLike(String... types);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getValue()} that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param values the patterns of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueLike(String... values);

  /**
   * Selects only {@linkplain Task Tasks} whose primary {@linkplain ObjectReference} has a
   * {@linkplain ObjectReference#getValue()} that is equal to any of the passed values.
   *
   * @param values the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueIn(String... values);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getCreated()} is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery createdWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getClaimed() } is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery claimedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getCompleted()} is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery completedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getModified()} is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery modifiedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getPlanned()} is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery plannedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} whose {@linkplain Task#getDue()} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery dueWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#isRead()} flag equal to the
   * passed flag.
   *
   * @param isRead the flag of interest
   * @return the query
   */
  TaskQuery readEquals(Boolean isRead);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#isTransferred()} flag equal
   * to the passed flag.
   *
   * @param isTransferred the flag of interest
   * @return the query
   */
  TaskQuery transferredEquals(Boolean isTransferred);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain
   * Task#getParentBusinessProcessId()} value that is equal to the passed values.
   *
   * @param parentBusinessProcessIds the values of interest
   * @return the query
   */
  TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain
   * Task#getParentBusinessProcessId()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param parentBusinessProcessIds the patterns of interest
   * @return the query
   */
  TaskQuery parentBusinessProcessIdLike(String... parentBusinessProcessIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getBusinessProcessId()}
   * value that is equal to the passed values.
   *
   * @param businessProcessIds the values of interest
   * @return the query
   */
  TaskQuery businessProcessIdIn(String... businessProcessIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getBusinessProcessId()}
   * value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param businessProcessIds the patterns of interest
   * @return the query
   */
  TaskQuery businessProcessIdLike(String... businessProcessIds);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain
   * Task#getCustomAttribute(TaskCustomField)} value equal to any of the passed values.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the value of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  TaskQuery customAttributeIn(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain
   * Task#getCustomAttribute(TaskCustomField)} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the patterns of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  TaskQuery customAttributeLike(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getKey()} value that contains any of the passed values.
   *
   * @param attachmentClassificationKeys the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getKey()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param attachmentClassificationKey the patterns of interest
   * @return the query
   */
  TaskQuery attachmentClassificationKeyLike(String... attachmentClassificationKey);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getId()} value that contains any of the passed values.
   *
   * @param attachmentClassificationId the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationIdIn(String... attachmentClassificationId);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getKey()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param attachmentClassificationId the patterns of interest
   * @return the query
   */
  TaskQuery attachmentClassificationIdLike(String... attachmentClassificationId);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getName()} value that contains any of the passed values.
   *
   * @param attachmentClassificationName the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationNameIn(String... attachmentClassificationName);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Classification#getKey()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param attachmentClassificationName the patterns of interest
   * @return the query
   */
  TaskQuery attachmentClassificationNameLike(String... attachmentClassificationName);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Attachment#getChannel()} value that contains any of the passed values.
   *
   * @param attachmentChannel the values of interest
   * @return the query
   */
  TaskQuery attachmentChannelIn(String... attachmentChannel);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Attachment#getChannel()} value that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param attachmentChannel the patterns of interest
   * @return the query
   */
  TaskQuery attachmentChannelLike(String... attachmentChannel);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain ObjectReference#getValue()} that contains any of the passed values.
   *
   * @param referenceValue the values of interest
   * @return the query
   */
  TaskQuery attachmentReferenceValueIn(String... referenceValue);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain ObjectReference#getValue()} that contains any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param referenceValue the patterns of interest
   * @return the query
   */
  TaskQuery attachmentReferenceValueLike(String... referenceValue);

  /**
   * Selects only {@linkplain Task Tasks} which have an {@linkplain Attachment} that has a
   * {@linkplain Attachment#getReceived()} value that is after or at the passed interval's begin and
   * before or at the passed interval's end.
   *
   * @param receivedIn the intervals of interest
   * @return the query
   */
  TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn);

  /**
   * Selects only {@linkplain Task Tasks} which have a {@linkplain Task#getCallbackInfo()} value
   * that is equal to any of the passed values.
   *
   * @param states the values of interest
   * @return the query
   */
  TaskQuery callbackStateIn(CallbackState... states);

  /**
   * Provides a query builder for querying the database.
   *
   * @return a {@linkplain ObjectReferenceQuery}
   */
  ObjectReferenceQuery createObjectReferenceQuery();

  /**
   * Sorts the query result according to the {@linkplain Task#getBusinessProcessId()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByBusinessProcessId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getClaimed()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByClaimed(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Classification#getKey()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByClassificationKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Classification#getName()}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByClassificationName(SortDirection sortDirection);

  /**
   * Adds your wildcard search value for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. Must be used in combination with the wilcardSearchFieldIn parameter
   *
   * @param wildcardSearchValue the wildcard search value
   * @return the query
   */
  TaskQuery wildcardSearchValueLike(String wildcardSearchValue);

  /**
   * Adds the {@linkplain Task} fields for which the wildcard search should be performed as an exact
   * match to your query.
   *
   * <p>Must be used in combination with the wildcardSearchValueLike parameter
   *
   * @param wildcardSearchFields the {@linkplain Task} fields of your wildcard search
   * @return the query
   */
  TaskQuery wildcardSearchFieldsIn(WildcardSearchField... wildcardSearchFields);

  /**
   * Sorts the query result according to the {@linkplain Task#getCompleted()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCompleted(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getCreated()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getDomain()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getDue()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByDue(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getId()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByTaskId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getModified()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByModified(SortDirection sortDirection);

  /**
   * Sorts the query result according to {@linkplain Task#getName()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByName(SortDirection sortDirection);

  /**
   * Sorts the query result according to {@linkplain Task#getCreator()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCreator(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getNote()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByNote(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getOwner()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByOwner(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getParentBusinessProcessId()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getPlanned()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPlanned(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getCompany()}} of the
   * {@linkplain Task#getPrimaryObjRef()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getSystem()} of the
   * {@linkplain Task#getPrimaryObjRef()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getSystemInstance()} of the
   * {@linkplain Task#getPrimaryObjRef()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection);

  /**
   * Sorts the query result according to the type of the {@linkplain Task#getPrimaryObjRef()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getValue()} of the
   * {@linkplain Task#getPrimaryObjRef()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getPriority()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getState()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByState(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Task#getWorkbasketKey()}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the value of a {@linkplain
   * Task#getCustomAttribute(TaskCustomField)}.
   *
   * @param customField identifies which custom attribute is affected
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCustomAttribute(TaskCustomField customField, SortDirection sortDirection);

  /**
   * Filters for summaries which are containing one of the given {@linkplain Task#getId()}.
   *
   * @param taskIds the ids of the searched-for {@linkplain Task Tasks}
   * @return the taskQuery
   */
  TaskQuery idIn(String... taskIds);

  /**
   * Sorts the query result according to the {@linkplain Workbasket#getId()} of the {@linkplain Task
   * Tasks}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Workbasket#getName()} of the {@linkplain
   * Task Tasks}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketName(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Classification#getKey()} of the {@linkplain
   * Attachment#getClassificationSummary()}.
   *
   * <p>Should only be used if there is one {@linkplain Attachment} per {@linkplain Task} in other
   * case the result would be wrong.)
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Classification#getName()} of the
   * {@linkplain Attachment#getClassificationSummary()}.
   *
   * <p>Should only be used if there is one attachment per task in other case the result would be
   * wrong.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationName(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Classification#getId()} of the {@linkplain
   * Attachment#getClassificationSummary()}.
   *
   * <p>Should only be used if there is one attachment per task in other case the result would be
   * wrong.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Attachment#getChannel()}.
   *
   * <p>Should only be used if there is one attachment per task in other case the result would be
   * wrong.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentChannel(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Attachment#getObjectReference()}.
   *
   * <p>Should only be used if there is one attachment per task in other case the result would be
   * wrong.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentReference(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain Attachment#getReceived()}.
   *
   * <p>Should only be used if there is one attachment per task in other case the result would be
   * wrong.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentReceived(SortDirection sortDirection);
}
