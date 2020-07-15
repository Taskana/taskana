package pro.taskana.simplehistory.impl.task;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.spi.history.api.events.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** HistoryQuery for generating dynamic sql. */
public interface TaskHistoryQuery extends BaseQuery<TaskHistoryEvent, TaskHistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param idIn as String
   * @return the query
   */
  TaskHistoryQuery idIn(String... idIn);

  /**
   * Add your businessProcessId to your query.
   *
   * @param businessProcessId as String
   * @return the query
   */
  TaskHistoryQuery businessProcessIdIn(String... businessProcessId);

  /**
   * Add your parentBusinessProcessId to your query.
   *
   * @param parentBusinessProcessId as String
   * @return the query
   */
  TaskHistoryQuery parentBusinessProcessIdIn(String... parentBusinessProcessId);

  /**
   * Add your taskId to your query.
   *
   * @param taskId as String
   * @return the query
   */
  TaskHistoryQuery taskIdIn(String... taskId);

  /**
   * Add your eventType to your query.
   *
   * @param eventType as String
   * @return the query
   */
  TaskHistoryQuery eventTypeIn(String... eventType);

  /**
   * Add your created TimeInterval to your query.
   *
   * @param createdWithin the {@link TimeInterval} within which the searched-for classifications
   *     were created.
   * @return the query
   */
  TaskHistoryQuery createdWithin(TimeInterval... createdWithin);

  /**
   * Add your userId to your query.
   *
   * @param userId as String
   * @return the query
   */
  TaskHistoryQuery userIdIn(String... userId);

  /**
   * Add your domain to your query.
   *
   * @param domain as String
   * @return the query
   */
  TaskHistoryQuery domainIn(String... domain);

  /**
   * Add your workbasketKey to your query.
   *
   * @param workbasketKey as String
   * @return the query
   */
  TaskHistoryQuery workbasketKeyIn(String... workbasketKey);

  /**
   * Add your porCompany to your query.
   *
   * @param porCompany as String
   * @return the query
   */
  TaskHistoryQuery porCompanyIn(String... porCompany);

  /**
   * Add your porSystem to your query.
   *
   * @param porSystem as String
   * @return the query
   */
  TaskHistoryQuery porSystemIn(String... porSystem);

  /**
   * Add your porInstance to your query.
   *
   * @param porInstance as String
   * @return the query
   */
  TaskHistoryQuery porInstanceIn(String... porInstance);

  /**
   * Add your porType to your query.
   *
   * @param porType as String
   * @return the query
   */
  TaskHistoryQuery porTypeIn(String... porType);

  /**
   * Add your porValue to your query.
   *
   * @param porValue as String
   * @return the query
   */
  TaskHistoryQuery porValueIn(String... porValue);

  /**
   * Add your taskClassificationKey to your query.
   *
   * @param taskClassificationKey as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationKeyIn(String... taskClassificationKey);

  /**
   * Add your taskClassificationCategory to your query.
   *
   * @param taskClassificationCategory as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationCategoryIn(String... taskClassificationCategory);

  /**
   * Add your attachmentClassificationKey to your query.
   *
   * @param attachmentClassificationKey as String
   * @return the query
   */
  TaskHistoryQuery attachmentClassificationKeyIn(String... attachmentClassificationKey);

  /**
   * Add your oldValue to your query.
   *
   * @param oldValue as String
   * @return the query
   */
  TaskHistoryQuery oldValueIn(String... oldValue);

  /**
   * Add your newValue to your query.
   *
   * @param newValue as String
   * @return the query
   */
  TaskHistoryQuery newValueIn(String... newValue);

  /**
   * Add your businessProcessId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param businessProcessId as String
   * @return the query
   */
  TaskHistoryQuery businessProcessIdLike(String... businessProcessId);

  /**
   * Add your parentBusinessProcessId to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param parentBusinessProcessId as String
   * @return the query
   */
  TaskHistoryQuery parentBusinessProcessIdLike(String... parentBusinessProcessId);

  /**
   * Add your taskId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param taskId as String
   * @return the query
   */
  TaskHistoryQuery taskIdLike(String... taskId);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventType as String
   * @return the query
   */
  TaskHistoryQuery eventTypeLike(String... eventType);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userId as String
   * @return the query
   */
  TaskHistoryQuery userIdLike(String... userId);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domain as String
   * @return the query
   */
  TaskHistoryQuery domainLike(String... domain);

  /**
   * Add your workbasketKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketKey as String
   * @return the query
   */
  TaskHistoryQuery workbasketKeyLike(String... workbasketKey);

  /**
   * Add your porCompany to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porCompany as String
   * @return the query
   */
  TaskHistoryQuery porCompanyLike(String... porCompany);

  /**
   * Add your porSystem to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porSystem as String
   * @return the query
   */
  TaskHistoryQuery porSystemLike(String... porSystem);

  /**
   * Add your porInstance to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porInstance as String
   * @return the query
   */
  TaskHistoryQuery porInstanceLike(String... porInstance);

  /**
   * Add your porType to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porType as String
   * @return the query
   */
  TaskHistoryQuery porTypeLike(String... porType);

  /**
   * Add your porValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porValue as String
   * @return the query
   */
  TaskHistoryQuery porValueLike(String... porValue);

  /**
   * Add your taskClassificationKey to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationKey as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationKeyLike(String... taskClassificationKey);

  /**
   * Add your taskClassificationCategory to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationCategory as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationCategoryLike(String... taskClassificationCategory);

  /**
   * Add your attachmentClassificationKey to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param attachmentClassificationKey as String
   * @return the query
   */
  TaskHistoryQuery attachmentClassificationKeyLike(String... attachmentClassificationKey);

  /**
   * Add your oldValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param oldValue as String
   * @return the query
   */
  TaskHistoryQuery oldValueLike(String... oldValue);

  /**
   * Add your newValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param newValue as String
   * @return the query
   */
  TaskHistoryQuery newValueLike(String... newValue);

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   */
  TaskHistoryQuery customAttributeIn(TaskHistoryCustomField customField, String... searchArguments);

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched-for tasks
   * @return the query
   */
  TaskHistoryQuery customAttributeLike(
      TaskHistoryCustomField customField, String... searchArguments);

  /**
   * Sort the query result by businessProcessId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByBusinessProcessId(SortDirection sortDirection);

  /**
   * Sort the query result by parentBusinessProcessId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByParentBusinessProcessId(SortDirection sortDirection);

  /**
   * Sort the query result by taskId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByTaskId(SortDirection sortDirection);

  /**
   * Sort the query result by eventType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByEventType(SortDirection sortDirection);

  /**
   * Sort the query result by created.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sort the query result by userId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByUserId(SortDirection sortDirection);

  /**
   * Sort the query result by Domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by WorkbasketKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sort the query result by porCompany.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByPorCompany(SortDirection sortDirection);

  /**
   * Sort the query result by porSystem.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByPorSystem(SortDirection sortDirection);

  /**
   * Sort the query result by porInstance.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByPorInstance(SortDirection sortDirection);

  /**
   * Sort the query result by porType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByPorType(SortDirection sortDirection);

  /**
   * Sort the query result by porValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByPorValue(SortDirection sortDirection);

  /**
   * Sort the query result by taskClassificationKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByTaskClassificationKey(SortDirection sortDirection);

  /**
   * Sort the query result by taskClassificationCategory.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByTaskClassificationCategory(SortDirection sortDirection);

  /**
   * Sort the query result by attachmentClassificationKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByAttachmentClassificationKey(SortDirection sortDirection);

  /**
   * Sort the query result by oldValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByOldValue(SortDirection sortDirection);

  /**
   * Sort the query result by newValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByNewValue(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the value of a custom field.
   *
   * @param customField identifies which custom attribute is affected.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByCustomAttribute(
      TaskHistoryCustomField customField, SortDirection sortDirection);
}
