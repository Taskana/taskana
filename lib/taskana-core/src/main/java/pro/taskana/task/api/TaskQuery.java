package pro.taskana.task.api;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.TaskSummary;

/**
 * The TaskQuery allows for a custom search across all {@linkplain pro.taskana.task.api.models.Task
 * Tasks}.
 */
public interface TaskQuery extends BaseQuery<TaskSummary, TaskQueryColumnName> {

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getName() name} value that is equal to any of the passed
   * values.
   *
   * @param names the values of interest
   * @return the query
   */
  TaskQuery nameIn(String... names);

  /**
   * Adds your name for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param names your names
   * @return the query
   */
  TaskQuery nameLike(String... names);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getExternalId() external id} value that is equal to any of the
   * passed values.
   *
   * @param externalIds the values of interest
   * @return the query
   */
  TaskQuery externalIdIn(String... externalIds);

  /**
   * Adds your externalIds for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param externalIds your external ids
   * @return the query
   */
  TaskQuery externalIdLike(String... externalIds);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getCreator() creator} value that is equal to any of the passed
   * values.
   *
   * @param creators the values of interest
   * @return the query
   */
  TaskQuery creatorIn(String... creators);

  /**
   * Adds the userIds of the creator for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param creators of the queried tasks
   * @return the query
   */
  TaskQuery creatorLike(String... creators);

  /**
   * Adds your description for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param description your description
   * @return the query
   */
  TaskQuery descriptionLike(String... description);

  /**
   * Adds your customNote for pattern matching to your query.
   *
   * <p>It will be compared in SQL with the LIKE operator. You may use a wildcard like % to specify
   * the pattern. If you specify multiple arguments they are combined with the OR keyword.
   *
   * @param note your custom note
   * @return the query
   */
  TaskQuery noteLike(String... note);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getPriority() priority} value that is equal to any of the
   * passed values.
   *
   * @param priorities the values of interest
   * @return the query
   */
  TaskQuery priorityIn(int... priorities);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getState() state} value that is equal to any of the passed
   * values.
   *
   * @param states the values of interest
   * @return the query
   */
  TaskQuery stateIn(TaskState... states);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which do not have a
   * {@linkplain pro.taskana.task.api.models.Task#getState() state} value that is equal to any of
   * the passed values.
   *
   * @param states the values of interest
   * @return the query
   */
  TaskQuery stateNotIn(TaskState... states);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() key} value that is equal to any
   * of the passed values.
   *
   * @param classificationKeys the values of interest
   * @return the query
   */
  TaskQuery classificationKeyIn(String... classificationKeys);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} does not have a
   * {@linkplain pro.taskana.classification.api.models.Classification#getKey() key} value that is
   * equal to any of the passed values.
   *
   * @param classificationKeys the values of interest
   * @return the query
   */
  TaskQuery classificationKeyNotIn(String... classificationKeys);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() key} value that contains any of
   * the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getId() id} value that is equal to any of
   * the passed values.
   *
   * @param classificationIds the values of interest
   * @return the query
   */
  TaskQuery classificationIdIn(String... classificationIds);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getClassificationCategory() classification category} value
   * that is equal to any of the passed values.
   *
   * @param classificationCategories the values of interest
   * @return the query
   */
  TaskQuery classificationCategoryIn(String... classificationCategories);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getClassificationCategory() classification category} value
   * that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getName() name} value that is equal to any
   * of the passed values.
   *
   * @param classificationNames the values of interest
   * @return the query
   */
  TaskQuery classificationNameIn(String... classificationNames);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.classification.api.models.Classification Classification} has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getName() name} value that contains any of
   * the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbasket} has a {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket#getKey key} and {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket#getDomain() domain} value equal to any of the
   * passed values.
   *
   * @param workbasketIdentifiers the values of interest
   * @return the query
   */
  TaskQuery workbasketKeyDomainIn(KeyDomain... workbasketIdentifiers);

  /**
   * Adds your workbasketId to the query.
   *
   * @param workbasketIds the ids of {@linkplain pro.taskana.workbasket.api.models.Workbasket
   *     Workbaskets}
   * @return the query
   */
  TaskQuery workbasketIdIn(String... workbasketIds);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getOwner()} value that is equal to any of the passed values.
   *
   * @param owners the values of interest
   * @return the query
   */
  TaskQuery ownerIn(String... owners);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getOwner()} value that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a primary
   * {@linkplain pro.taskana.task.api.models.Task#getPrimaryObjRef() object reference} value that is
   * equal to any of the passed values.
   *
   * @param objectReferences the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceIn(ObjectReference... objectReferences);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference ObjectReference} has a {@linkplain ObjectReference#getCompany() company} value
   * that is equal to any of the passed values.
   *
   * @param companies the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceCompanyIn(String... companies);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getCompany() company} value that contains
   * any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference ObjectReference} has a {@linkplain ObjectReference#getSystem() system} value
   * that is equal to any of the passed values.
   *
   * @param systems the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemIn(String... systems);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getSystem() system} value that contains any
   * of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getSystemInstance() system instance} value
   * that is equal to any of the passed values.
   *
   * @param systemInstances the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceSystemInstanceIn(String... systemInstances);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getSystemInstance() system instance} value
   * that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getType() type} value that is equal to any
   * of the passed values.
   *
   * @param types the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceTypeIn(String... types);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getType() type} value that contains any of
   * the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference} has a {@linkplain ObjectReference#getValue() value} that contains any of the
   * passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose primary {@linkplain
   * ObjectReference ObjectReference} has a {@linkplain ObjectReference#getValue() value} that is
   * equal to any of the passed values.
   *
   * @param values the values of interest
   * @return the query
   */
  TaskQuery primaryObjectReferenceValueIn(String... values);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getCreated() created timestamp} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery createdWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getClaimed() claimed timestamp} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery claimedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getCompleted() completed timestamp} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery completedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getModified() modified timestamp} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery modifiedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getPlanned() planned timestamp} is after or at the passed
   * interval's begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery plannedWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} whose {@linkplain
   * pro.taskana.task.api.models.Task#getDue() due timestamp} is after or at the passed interval's
   * begin and before or at the passed interval's end.
   *
   * @param intervals the intervals of interest
   * @return the query
   */
  TaskQuery dueWithin(TimeInterval... intervals);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#isRead() read} flag equal to the passed flag.
   *
   * @param isRead the flag of interest
   * @return the query
   */
  TaskQuery readEquals(Boolean isRead);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#isTransferred() transferred} flag equal to the passed flag.
   *
   * @param isTransferred the flag of interest
   * @return the query
   */
  TaskQuery transferredEquals(Boolean isTransferred);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getParentBusinessProcessId() parent business process id} value
   * that is equal to the passed values.
   *
   * @param parentBusinessProcessIds the values of interest
   * @return the query
   */
  TaskQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getParentBusinessProcessId()} value that contains any of the
   * passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getBusinessProcessId() business process id} value that is
   * equal to the passed values.
   *
   * @param businessProcessIds the values of interest
   * @return the query
   */
  TaskQuery businessProcessIdIn(String... businessProcessIds);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getBusinessProcessId()} value that contains any of the passed
   * patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getCustomAttribute(TaskCustomField) custom attribute} value
   * equal to any of the passed values.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the value of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  TaskQuery customAttributeIn(TaskCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getCustomAttribute(TaskCustomField) custom attribute} value
   * that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() classification key} value that
   * contains any of the passed values.
   *
   * @param attachmentClassificationKeys the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() classification key} value that
   * contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getId() classification id} value that
   * contains any of the passed values.
   *
   * @param attachmentClassificationId the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationIdIn(String... attachmentClassificationId);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() classification id} value that
   * contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getName() classification name} value that
   * contains any of the passed values.
   *
   * @param attachmentClassificationName the values of interest
   * @return the query
   */
  TaskQuery attachmentClassificationNameIn(String... attachmentClassificationName);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() classification name} value that
   * contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.task.api.models.Attachment#getChannel() channel} value that contains any of the
   * passed values.
   *
   * @param attachmentChannel the values of interest
   * @return the query
   */
  TaskQuery attachmentChannelIn(String... attachmentChannel);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.task.api.models.Attachment#getChannel() channel} value that contains any of the
   * passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * ObjectReference#getValue() object reference value} that contains any of the passed values.
   *
   * @param referenceValue the values of interest
   * @return the query
   */
  TaskQuery attachmentReferenceValueIn(String... referenceValue);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * ObjectReference#getValue() object reference value} that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have an {@linkplain
   * pro.taskana.task.api.models.Attachment Attachment} that has a {@linkplain
   * pro.taskana.task.api.models.Attachment#getReceived() received} value that is after or at the
   * passed interval's begin and before or at the passed interval's end.
   *
   * @param receivedIn the intervals of interest
   * @return the query
   */
  TaskQuery attachmentReceivedWithin(TimeInterval... receivedIn);

  /**
   * Selects only {@linkplain pro.taskana.task.api.models.Task Tasks} which have a {@linkplain
   * pro.taskana.task.api.models.Task#getCallbackInfo() callback state} value that is equal to any
   * of the passed values.
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
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getBusinessProcessId() business process id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByBusinessProcessId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getClaimed() claimed timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByClaimed(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() classification key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByClassificationKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.classification.api.models.Classification#getName() classification name}.
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
   * Adds the {@linkplain pro.taskana.task.api.models.Task Task} fields for which the wildcard
   * search should be performed as an exact match to your query.
   *
   * <p>Must be used in combination with the wildcardSearchValueLike parameter
   *
   * @param wildcardSearchFields the {@linkplain pro.taskana.task.api.models.Task Task} fields of
   *     your wildcard search
   * @return the query
   */
  TaskQuery wildcardSearchFieldsIn(WildcardSearchField... wildcardSearchFields);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getCompleted() completed timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCompleted(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getCreated() created timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getDomain() domain}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain pro.taskana.task.api.models.Task#getDue()
   * due timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByDue(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain pro.taskana.task.api.models.Task#getId()
   * primary task id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByTaskId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getModified() modified timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByModified(SortDirection sortDirection);

  /**
   * Sorts the query result according to {@linkplain pro.taskana.task.api.models.Task#getName()
   * name}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByName(SortDirection sortDirection);

  /**
   * Sorts the query result according to {@linkplain pro.taskana.task.api.models.Task#getCreator()
   * creators name}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCreator(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain pro.taskana.task.api.models.Task#getNote()
   * note}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByNote(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain pro.taskana.task.api.models.Task#getOwner()
   * owner}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByOwner(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getParentBusinessProcessId() parent business process id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByParentBusinessProcessId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getPlanned() planned timestamp}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPlanned(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getCompany()} company} of
   * the {@linkplain pro.taskana.task.api.models.Task#getPrimaryObjRef() primary object reference}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceCompany(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getSystem() system} of the
   * {@linkplain pro.taskana.task.api.models.Task#getPrimaryObjRef() primary object reference}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystem(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getSystemInstance() system
   * instance} of the {@linkplain pro.taskana.task.api.models.Task#getPrimaryObjRef() primary object
   * reference}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceSystemInstance(SortDirection sortDirection);

  /**
   * Sorts the query result according to the type of the {@linkplain
   * pro.taskana.task.api.models.Task#getPrimaryObjRef() primary object reference}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceType(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain ObjectReference#getValue()} of the
   * {@linkplain pro.taskana.task.api.models.Task#getPrimaryObjRef() primary object reference}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPrimaryObjectReferenceValue(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getPriority() priority}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain pro.taskana.task.api.models.Task#getState()
   * state}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByState(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Task#getWorkbasketKey() workbasket key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the value of a {@linkplain
   * pro.taskana.task.api.models.Task#getCustomAttribute(TaskCustomField) custom field}.
   *
   * @param customField identifies which custom attribute is affected
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByCustomAttribute(TaskCustomField customField, SortDirection sortDirection);

  /**
   * Filters for summaries which are containing one of the given {@linkplain
   * pro.taskana.task.api.models.Task#getId() task ids}.
   *
   * @param taskIds the ids of the searched-for {@linkplain pro.taskana.task.api.models.Task Tasks}
   * @return the taskQuery
   */
  TaskQuery idIn(String... taskIds);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket#getId() workbasket id} of the {@linkplain
   * pro.taskana.task.api.models.Task Tasks}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketId(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket#getName() workbasket name} of the {@linkplain
   * pro.taskana.task.api.models.Task Tasks}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByWorkbasketName(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() key} of the {@linkplain
   * pro.taskana.task.api.models.Attachment#getClassificationSummary() Attachment's Classification}.
   *
   * <p>Should only be used if there is one {@linkplain pro.taskana.task.api.models.Attachment
   * Attachment} per {@linkplain pro.taskana.task.api.models.Task Task} in other case the result
   * would be wrong.)
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  TaskQuery orderByAttachmentClassificationKey(SortDirection sortDirection);

  /**
   * Sorts the query result according to the {@linkplain
   * pro.taskana.classification.api.models.Classification#getName name} of the {@linkplain
   * pro.taskana.task.api.models.Attachment#getClassificationSummary() Attachment's Classification}.
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
   * Sorts the query result according to the {@linkplain
   * pro.taskana.classification.api.models.Classification#getId() id} of the {@linkplain
   * pro.taskana.task.api.models.Attachment#getClassificationSummary() Attachment's Classification}.
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
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Attachment#getChannel() Attachment's channel}.
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
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Attachment#getObjectReference() Attachment's reference value}.
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
   * Sorts the query result according to the {@linkplain
   * pro.taskana.task.api.models.Attachment#getReceived() Attachment's received value}.
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
