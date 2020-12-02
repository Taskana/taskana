package pro.taskana.simplehistory.impl.classification;

import pro.taskana.classification.api.ClassificationCustomField;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.spi.history.api.events.classification.ClassificationHistoryEvent;

/** HistoryQuery for generating dynamic sql. */
public interface ClassificationHistoryQuery
    extends BaseQuery<ClassificationHistoryEvent, ClassificationHistoryQueryColumnName> {

  /**
   * Add your Id to your query.
   *
   * @param idIn as String
   * @return the query
   */
  ClassificationHistoryQuery idIn(String... idIn);

  /**
   * Add your eventType to your query.
   *
   * @param eventType as String
   * @return the query
   */
  ClassificationHistoryQuery eventTypeIn(String... eventType);

  /**
   * Add your created TimeInterval to your query.
   *
   * @param createdWithin the {@link TimeInterval} within which the searched-for classifications
   *     were created. Both borders in each {@link TimeInterval} are inclusive.
   * @return the query
   */
  ClassificationHistoryQuery createdWithin(TimeInterval... createdWithin);

  /**
   * Add your userId to your query.
   *
   * @param userId as String
   * @return the query
   */
  ClassificationHistoryQuery userIdIn(String... userId);

  /**
   * Add your classificationId to your query.
   *
   * @param classificationId as String
   * @return the query
   */
  ClassificationHistoryQuery classificationIdIn(String... classificationId);

  /**
   * Add your applicationEntryPoint to your query.
   *
   * @param applicationEntryPoint as String
   * @return the query
   */
  ClassificationHistoryQuery applicationEntryPointIn(String... applicationEntryPoint);

  /**
   * Add your category to your query.
   *
   * @param category as String
   * @return the query
   */
  ClassificationHistoryQuery categoryIn(String... category);

  /**
   * Add your domain to your query.
   *
   * @param domain as String
   * @return the query
   */
  ClassificationHistoryQuery domainIn(String... domain);

  /**
   * Add your key to your query.
   *
   * @param key as String
   * @return the query
   */
  ClassificationHistoryQuery keyIn(String... key);

  /**
   * Add your name to your query.
   *
   * @param name as String
   * @return the query
   */
  ClassificationHistoryQuery nameIn(String... name);

  /**
   * Add your parentId to your query.
   *
   * @param parentId as String
   * @return the query
   */
  ClassificationHistoryQuery parentIdIn(String... parentId);

  /**
   * Add your parentKey to your query.
   *
   * @param parentKey as String
   * @return the query
   */
  ClassificationHistoryQuery parentKeyIn(String... parentKey);

  /**
   * Add your priority to your query.
   *
   * @param priorities as integers
   * @return the query
   */
  ClassificationHistoryQuery priorityIn(int... priorities);

  /**
   * Add your serviceLevel to your query.
   *
   * @param serviceLevelIn as String
   * @return the query
   */
  ClassificationHistoryQuery serviceLevelIn(String... serviceLevelIn);

  /**
   * Add your type to your query.
   *
   * @param type as String
   * @return the query
   */
  ClassificationHistoryQuery typeIn(String... type);

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   */
  ClassificationHistoryQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments);

  /**
   * Add your eventType to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param eventType as String
   * @return the query
   */
  ClassificationHistoryQuery eventTypeLike(String... eventType);

  /**
   * Add your userId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param userId as String
   * @return the query
   */
  ClassificationHistoryQuery userIdLike(String... userId);

  /**
   * Add your classificationId to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param classificationId as String
   * @return the query
   */
  ClassificationHistoryQuery classificationIdLike(String... classificationId);

  /**
   * Add your applicationEntryPoint to your query. It will be compared in SQL with an LIKE. If you
   * use a * wildcard like % then it will be transmitted to the database.
   *
   * @param applicationEntryPointLike name of the applications entrypoint
   * @return the query
   */
  ClassificationHistoryQuery applicationEntryPointLike(String... applicationEntryPointLike);

  /**
   * Add your category to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param category as String
   * @return the query
   */
  ClassificationHistoryQuery categoryLike(String... category);

  /**
   * Add your domain to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param domain as String
   * @return the query
   */
  ClassificationHistoryQuery domainLike(String... domain);

  /**
   * Add your key to your query. It will be compared in SQL with an LIKE. If you use a wildcard like
   * % then it will be transmitted to the database.
   *
   * @param key as String
   * @return the query
   */
  ClassificationHistoryQuery keyLike(String... key);

  /**
   * Add your name to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param name as String
   * @return the query
   */
  ClassificationHistoryQuery nameLike(String... name);

  /**
   * Add your parentId to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param parentId as String
   * @return the query
   */
  ClassificationHistoryQuery parentIdLike(String... parentId);

  /**
   * Add your parentKey to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param parentKey as String
   * @return the query
   */
  ClassificationHistoryQuery parentKeyLike(String... parentKey);

  /**
   * Add your serviceLevel to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param serviceLevel as String
   * @return the query
   */
  ClassificationHistoryQuery serviceLevelLike(String... serviceLevel);

  /**
   * Add your type to your query. It will be compared in SQL with an LIKE. If you use a wildcard
   * like % then it will be transmitted to the database.
   *
   * @param type as String
   * @return the query
   */
  ClassificationHistoryQuery typeLike(String... type);

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched-for tasks
   * @return the query
   */
  ClassificationHistoryQuery customAttributeLike(
      ClassificationCustomField customField, String... searchArguments);

  /**
   * Sort the query result by eventType.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByEventType(SortDirection sortDirection);

  /**
   * Sort the query result by created.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByCreated(SortDirection sortDirection);

  /**
   * Sort the query result by userId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByUserId(SortDirection sortDirection);

  /**
   * Sort the query result by classificationId.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByClassificationId(SortDirection sortDirection);

  /**
   * Sort the query result by applicationEntryPoint.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByApplicationEntryPoint(SortDirection sortDirection);

  /**
   * Sort the query result by category.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByCategory(SortDirection sortDirection);

  /**
   * Sort the query result by Domain.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by key.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByKey(SortDirection sortDirection);

  /**
   * Sort the query result by name.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByName(SortDirection sortDirection);

  /**
   * Sort the query result by parentId.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByParentId(SortDirection sortDirection);

  /**
   * Sort the query result by parentKey.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByParentKey(SortDirection sortDirection);

  /**
   * Sort the query result by priority.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sort the query result by serviceLevel.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByServiceLevel(SortDirection sortDirection);

  /**
   * Sort the query result by type.
   *
   * @param sortDirection Determines whether the result is sorted dain ascending or descending
   *     order. If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationHistoryQuery orderByType(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the value of a custom field.
   *
   * @param num the number of the custom attribute as String (eg "4")
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   * @throws InvalidArgumentException if the number of the custom is incorrect.
   */
  ClassificationHistoryQuery orderByCustomAttribute(int num, SortDirection sortDirection)
      throws InvalidArgumentException;
}
