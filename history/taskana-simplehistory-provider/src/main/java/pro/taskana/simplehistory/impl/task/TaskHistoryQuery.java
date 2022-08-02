package pro.taskana.simplehistory.impl.task;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.spi.history.api.events.task.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;

/** HistoryQuery for generating dynamic sql. */
public interface TaskHistoryQuery extends BaseQuery<TaskHistoryEvent, TaskHistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param ids as String
   * @return the query
   */
  TaskHistoryQuery idIn(String... ids);

  /**
   * Add your businessProcessId to your query.
   *
   * @param businessProcessIds as String
   * @return the query
   */
  TaskHistoryQuery businessProcessIdIn(String... businessProcessIds);

  /**
   * Add your parentBusinessProcessId to your query.
   *
   * @param parentBusinessProcessIds as String
   * @return the query
   */
  TaskHistoryQuery parentBusinessProcessIdIn(String... parentBusinessProcessIds);

  /**
   * Add your taskId to your query.
   *
   * @param taskIds as String
   * @return the query
   */
  TaskHistoryQuery taskIdIn(String... taskIds);

  /**
   * Add your eventType to your query.
   *
   * @param eventTypes as String
   * @return the query
   */
  TaskHistoryQuery eventTypeIn(String... eventTypes);

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
   * @param userIds as String
   * @return the query
   */
  TaskHistoryQuery userIdIn(String... userIds);

  /**
   * Add your domain to your query.
   *
   * @param domains as String
   * @return the query
   */
  TaskHistoryQuery domainIn(String... domains);

  /**
   * Add your workbasketKey to your query.
   *
   * @param workbasketKeys as String
   * @return the query
   */
  TaskHistoryQuery workbasketKeyIn(String... workbasketKeys);

  /**
   * Add your porCompany to your query.
   *
   * @param porCompanies as String
   * @return the query
   */
  TaskHistoryQuery porCompanyIn(String... porCompanies);

  /**
   * Add your porSystem to your query.
   *
   * @param porSystems as String
   * @return the query
   */
  TaskHistoryQuery porSystemIn(String... porSystems);

  /**
   * Add your porInstance to your query.
   *
   * @param porInstances as String
   * @return the query
   */
  TaskHistoryQuery porInstanceIn(String... porInstances);

  /**
   * Add your porType to your query.
   *
   * @param porTypes as String
   * @return the query
   */
  TaskHistoryQuery porTypeIn(String... porTypes);

  /**
   * Add your porValue to your query.
   *
   * @param porValues as String
   * @return the query
   */
  TaskHistoryQuery porValueIn(String... porValues);

  /**
   * Add your taskClassificationKey to your query.
   *
   * @param taskClassificationKeys as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationKeyIn(String... taskClassificationKeys);

  /**
   * Add your taskClassificationCategory to your query.
   *
   * @param taskClassificationCategories as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationCategoryIn(String... taskClassificationCategories);

  /**
   * Add your attachmentClassificationKey to your query.
   *
   * @param attachmentClassificationKeys as String
   * @return the query
   */
  TaskHistoryQuery attachmentClassificationKeyIn(String... attachmentClassificationKeys);

  /**
   * Add your oldValue to your query.
   *
   * @param oldValues as String
   * @return the query
   */
  TaskHistoryQuery oldValueIn(String... oldValues);

  /**
   * Add your newValue to your query.
   *
   * @param newValues as String
   * @return the query
   */
  TaskHistoryQuery newValueIn(String... newValues);

  /**
   * Add your businessProcessId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param businessProcessIds as String
   * @return the query
   */
  TaskHistoryQuery businessProcessIdLike(String... businessProcessIds);

  /**
   * Add your parentBusinessProcessId to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param parentBusinessProcessIds as String
   * @return the query
   */
  TaskHistoryQuery parentBusinessProcessIdLike(String... parentBusinessProcessIds);

  /**
   * Add your taskId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param taskIds as String
   * @return the query
   */
  TaskHistoryQuery taskIdLike(String... taskIds);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventTypes as Strings
   * @return the query
   */
  TaskHistoryQuery eventTypeLike(String... eventTypes);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userIds as String
   * @return the query
   */
  TaskHistoryQuery userIdLike(String... userIds);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domains as String
   * @return the query
   */
  TaskHistoryQuery domainLike(String... domains);

  /**
   * Add your workbasketKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketKeys as String
   * @return the query
   */
  TaskHistoryQuery workbasketKeyLike(String... workbasketKeys);

  /**
   * Add your porCompany to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porCompanies as String
   * @return the query
   */
  TaskHistoryQuery porCompanyLike(String... porCompanies);

  /**
   * Add your porSystem to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porSystems as String
   * @return the query
   */
  TaskHistoryQuery porSystemLike(String... porSystems);

  /**
   * Add your porInstance to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porInstances as String
   * @return the query
   */
  TaskHistoryQuery porInstanceLike(String... porInstances);

  /**
   * Add your porType to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porTypes as String
   * @return the query
   */
  TaskHistoryQuery porTypeLike(String... porTypes);

  /**
   * Add your porValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porValues as String
   * @return the query
   */
  TaskHistoryQuery porValueLike(String... porValues);

  /**
   * Add your taskClassificationKey to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationKeys as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationKeyLike(String... taskClassificationKeys);

  /**
   * Add your taskClassificationCategory to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationCategories as String
   * @return the query
   */
  TaskHistoryQuery taskClassificationCategoryLike(String... taskClassificationCategories);

  /**
   * Add your attachmentClassificationKey to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param attachmentClassificationKeys as String
   * @return the query
   */
  TaskHistoryQuery attachmentClassificationKeyLike(String... attachmentClassificationKeys);

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
   * @param newValues as String
   * @return the query
   */
  TaskHistoryQuery newValueLike(String... newValues);

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
   * Sort the query result by the id of the events.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  TaskHistoryQuery orderByTaskHistoryEventId(SortDirection sortDirection);

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
