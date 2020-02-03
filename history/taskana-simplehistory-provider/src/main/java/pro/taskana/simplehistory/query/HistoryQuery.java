package pro.taskana.simplehistory.query;

import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.simplehistory.impl.HistoryEventImpl;

/** HistoryQuery for generating dynamic sql. */
public interface HistoryQuery extends BaseQuery<HistoryEventImpl, HistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param idIn as String
   * @return the query
   */
  HistoryQuery idIn(String... idIn);

  /**
   * Add your businessProcessId to your query.
   *
   * @param businessProcessId as String
   * @return the query
   */
  HistoryQuery businessProcessIdIn(String... businessProcessId);

  /**
   * Add your parentBusinessProcessId to your query.
   *
   * @param parentBusinessProcessId as String
   * @return the query
   */
  HistoryQuery parentBusinessProcessIdIn(String... parentBusinessProcessId);

  /**
   * Add your taskId to your query.
   *
   * @param taskId as String
   * @return the query
   */
  HistoryQuery taskIdIn(String... taskId);

  /**
   * Add your eventType to your query.
   *
   * @param eventType as String
   * @return the query
   */
  HistoryQuery eventTypeIn(String... eventType);

  /**
   * Add your created TimeInterval to your query.
   *
   * @param createdWithin the {@link TimeInterval} within which the searched-for classifications
   *     were created.
   * @return the query
   */
  HistoryQuery createdWithin(TimeInterval... createdWithin);

  /**
   * Add your userId to your query.
   *
   * @param userId as String
   * @return the query
   */
  HistoryQuery userIdIn(String... userId);

  /**
   * Add your domain to your query.
   *
   * @param domain as String
   * @return the query
   */
  HistoryQuery domainIn(String... domain);

  /**
   * Add your workbasketKey to your query.
   *
   * @param workbasketKey as String
   * @return the query
   */
  HistoryQuery workbasketKeyIn(String... workbasketKey);

  /**
   * Add your porCompany to your query.
   *
   * @param porCompany as String
   * @return the query
   */
  HistoryQuery porCompanyIn(String... porCompany);

  /**
   * Add your porSystem to your query.
   *
   * @param porSystem as String
   * @return the query
   */
  HistoryQuery porSystemIn(String... porSystem);

  /**
   * Add your porInstance to your query.
   *
   * @param porInstance as String
   * @return the query
   */
  HistoryQuery porInstanceIn(String... porInstance);

  /**
   * Add your porType to your query.
   *
   * @param porType as String
   * @return the query
   */
  HistoryQuery porTypeIn(String... porType);

  /**
   * Add your porValue to your query.
   *
   * @param porValue as String
   * @return the query
   */
  HistoryQuery porValueIn(String... porValue);

  /**
   * Add your taskClassificationKey to your query.
   *
   * @param taskClassificationKey as String
   * @return the query
   */
  HistoryQuery taskClassificationKeyIn(String... taskClassificationKey);

  /**
   * Add your taskClassificationCategory to your query.
   *
   * @param taskClassificationCategory as String
   * @return the query
   */
  HistoryQuery taskClassificationCategoryIn(String... taskClassificationCategory);

  /**
   * Add your attachmentClassificationKey to your query.
   *
   * @param attachmentClassificationKey as String
   * @return the query
   */
  HistoryQuery attachmentClassificationKeyIn(String... attachmentClassificationKey);

  /**
   * Add your comment to your query.
   *
   * @param comment as String
   * @return the query
   */
  HistoryQuery commentIn(String... comment);

  /**
   * Add your oldValue to your query.
   *
   * @param oldValue as String
   * @return the query
   */
  HistoryQuery oldValueIn(String... oldValue);

  /**
   * Add your newValue to your query.
   *
   * @param newValue as String
   * @return the query
   */
  HistoryQuery newValueIn(String... newValue);

  /**
   * Add your custom1 to your query.
   *
   * @param custom1 as String
   * @return the query
   */
  HistoryQuery custom1In(String... custom1);

  /**
   * Add your custom2 to your query.
   *
   * @param custom2 as String
   * @return the query
   */
  HistoryQuery custom2In(String... custom2);

  /**
   * Add your custom3 to your query.
   *
   * @param custom3 as String
   * @return the query
   */
  HistoryQuery custom3In(String... custom3);

  /**
   * Add your custom4 to your query.
   *
   * @param custom4 as String
   * @return the query
   */
  HistoryQuery custom4In(String... custom4);

  /**
   * Add your oldData to your query.
   *
   * @param oldData as String
   * @return the query
   */
  HistoryQuery oldDataIn(String... oldData);

  /**
   * Add your newData to your query.
   *
   * @param newData as String
   * @return the query
   */
  HistoryQuery newDataIn(String... newData);

  /**
   * Add your businessProcessId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param businessProcessId as String
   * @return the query
   */
  HistoryQuery businessProcessIdLike(String... businessProcessId);

  /**
   * Add your parentBusinessProcessId to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param parentBusinessProcessId as String
   * @return the query
   */
  HistoryQuery parentBusinessProcessIdLike(String... parentBusinessProcessId);

  /**
   * Add your taskId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param taskId as String
   * @return the query
   */
  HistoryQuery taskIdLike(String... taskId);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventType as String
   * @return the query
   */
  HistoryQuery eventTypeLike(String... eventType);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userId as String
   * @return the query
   */
  HistoryQuery userIdLike(String... userId);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domain as String
   * @return the query
   */
  HistoryQuery domainLike(String... domain);

  /**
   * Add your workbasketKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param workbasketKey as String
   * @return the query
   */
  HistoryQuery workbasketKeyLike(String... workbasketKey);

  /**
   * Add your porCompany to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porCompany as String
   * @return the query
   */
  HistoryQuery porCompanyLike(String... porCompany);

  /**
   * Add your porSystem to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porSystem as String
   * @return the query
   */
  HistoryQuery porSystemLike(String... porSystem);

  /**
   * Add your porInstance to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param porInstance as String
   * @return the query
   */
  HistoryQuery porInstanceLike(String... porInstance);

  /**
   * Add your porType to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porType as String
   * @return the query
   */
  HistoryQuery porTypeLike(String... porType);

  /**
   * Add your porValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param porValue as String
   * @return the query
   */
  HistoryQuery porValueLike(String... porValue);

  /**
   * Add your taskClassificationKey to your query. It will be compared in SQL with an LIKE. If you
   * use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationKey as String
   * @return the query
   */
  HistoryQuery taskClassificationKeyLike(String... taskClassificationKey);

  /**
   * Add your taskClassificationCategory to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param taskClassificationCategory as String
   * @return the query
   */
  HistoryQuery taskClassificationCategoryLike(String... taskClassificationCategory);

  /**
   * Add your attachmentClassificationKey to your query. It will be compared in SQL with an LIKE. If
   * you use a wildcard like % then it will be transmitted to the database.
   *
   * @param attachmentClassificationKey as String
   * @return the query
   */
  HistoryQuery attachmentClassificationKeyLike(String... attachmentClassificationKey);

  /**
   * Add your comment to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param comment as String
   * @return the query
   */
  HistoryQuery commentLike(String... comment);

  /**
   * Add your oldValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param oldValue as String
   * @return the query
   */
  HistoryQuery oldValueLike(String... oldValue);

  /**
   * Add your newValue to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param newValue as String
   * @return the query
   */
  HistoryQuery newValueLike(String... newValue);

  /**
   * Add your custom1 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom1 as String
   * @return the query
   */
  HistoryQuery custom1Like(String... custom1);

  /**
   * Add your custom2 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom2 as String
   * @return the query
   */
  HistoryQuery custom2Like(String... custom2);

  /**
   * Add your custom3 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom3 as String
   * @return the query
   */
  HistoryQuery custom3Like(String... custom3);

  /**
   * Add your custom4 to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param custom4 as String
   * @return the query
   */
  HistoryQuery custom4Like(String... custom4);

  /**
   * Add your oldData to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param oldData as String
   * @return the query
   */
  HistoryQuery oldDataLike(String... oldData);

  /**
   * Add your newData to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param newData as String
   * @return the query
   */
  HistoryQuery newDataLike(String... newData);

  /**
   * Sort the query result by businessProcessId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByBusinessProcessId(SortDirection sortDirection);

  /**
   * Sort the query result by parentBusinessProcessId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByParentBusinessProcessId(SortDirection sortDirection);

  /**
   * Sort the query result by taskId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByTaskId(SortDirection sortDirection);

  /**
   * Sort the query result by eventType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByEventType(SortDirection sortDirection);

  /**
   * Sort the query result by created.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sort the query result by userId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByUserId(SortDirection sortDirection);

  /**
   * Sort the query result by Domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by WorkbasketKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByWorkbasketKey(SortDirection sortDirection);

  /**
   * Sort the query result by porCompany.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByPorCompany(SortDirection sortDirection);

  /**
   * Sort the query result by porSystem.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByPorSystem(SortDirection sortDirection);

  /**
   * Sort the query result by porInstance.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByPorInstance(SortDirection sortDirection);

  /**
   * Sort the query result by porType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByPorType(SortDirection sortDirection);

  /**
   * Sort the query result by porValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByPorValue(SortDirection sortDirection);

  /**
   * Sort the query result by taskClassificationKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByTaskClassificationKey(SortDirection sortDirection);

  /**
   * Sort the query result by taskClassificationCategory.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByTaskClassificationCategory(SortDirection sortDirection);

  /**
   * Sort the query result by attachmentClassificationKey.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByAttachmentClassificationKey(SortDirection sortDirection);

  /**
   * Sort the query result by comment.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByComment(SortDirection sortDirection);

  /**
   * Sort the query result by oldValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByOldValue(SortDirection sortDirection);

  /**
   * Sort the query result by newValue.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByNewValue(SortDirection sortDirection);

  /**
   * Sort the query result by oldData.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByOldData(SortDirection sortDirection);

  /**
   * Sort the query result by newData.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  HistoryQuery orderByNewData(SortDirection sortDirection);

  /**
   * Sort the query result by a custom.
   *
   * @param num the number of the custom as String (eg "4")
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   * @throws InvalidArgumentException when the number of the custom is incorrect.
   */
  HistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
      throws InvalidArgumentException;
}
