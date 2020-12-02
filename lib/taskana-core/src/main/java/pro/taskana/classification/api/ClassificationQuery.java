package pro.taskana.classification.api;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

/** ClassificationQuery for generating dynamic sql. */
public interface ClassificationQuery
    extends BaseQuery<ClassificationSummary, ClassificationQueryColumnName> {

  /**
   * Add your key to your query.
   *
   * @param key as String
   * @return the query
   */
  ClassificationQuery keyIn(String... key);

  /**
   * Add your Id to your query.
   *
   * @param id as String
   * @return the query
   */
  ClassificationQuery idIn(String... id);

  /**
   * Add your parentIds to your query.
   *
   * @param parentId as an array of Strings
   * @return the query
   */
  ClassificationQuery parentIdIn(String... parentId);

  /**
   * Add your parentKeys to your query.
   *
   * @param parentKey as an array of Strings
   * @return the query
   */
  ClassificationQuery parentKeyIn(String... parentKey);

  /**
   * Add your category to your query.
   *
   * @param category as String
   * @return the query
   */
  ClassificationQuery categoryIn(String... category);

  /**
   * Add your type to your query.
   *
   * @param type as String
   * @return the query
   */
  ClassificationQuery typeIn(String... type);

  /**
   * Add your domains to your query which are used as filter.
   *
   * @param domain or domains for filtering.
   * @return the query
   */
  ClassificationQuery domainIn(String... domain);

  /**
   * Add to your query if the Classification shall be valid in its domain.
   *
   * @param validInDomain a simple flag showing if domain is valid
   * @return the query
   */
  ClassificationQuery validInDomainEquals(Boolean validInDomain);

  /**
   * Add your created-Dates to your query.
   *
   * @param createdIn the {@link TimeInterval} within which the searched-for classifications were
   *     created.
   * @return the query
   */
  ClassificationQuery createdWithin(TimeInterval... createdIn);

  /**
   * Add your modified-Dates to your query.
   *
   * @param modifiedIn the {@link TimeInterval} within which the searched-for classifications were
   *     modified the last time.
   * @return the query
   */
  ClassificationQuery modifiedWithin(TimeInterval... modifiedIn);

  /**
   * Add your name to your query.
   *
   * @param nameIn as String
   * @return the query
   */
  ClassificationQuery nameIn(String... nameIn);

  /**
   * Add your name to your query. It will be compared in SQL with an LIKE.
   *
   * @param nameLike as String
   * @return the query
   */
  ClassificationQuery nameLike(String... nameLike);

  /**
   * Add your description to your query. It will be compared in SQL with an LIKE. If you use a
   * wildcard like % then it will be transmitted to the database.
   *
   * @param descriptionLike your description
   * @return the query
   */
  ClassificationQuery descriptionLike(String descriptionLike);

  /**
   * Add your priority to your query.
   *
   * @param priorities as integers
   * @return the query
   */
  ClassificationQuery priorityIn(int... priorities);

  /**
   * Add your serviceLevel to your query.
   *
   * @param serviceLevelIn as String
   * @return the query
   */
  ClassificationQuery serviceLevelIn(String... serviceLevelIn);

  /**
   * Add your serviceLevel to your query. It will be compared in SQL with an LIKE.
   *
   * @param serviceLevelLike as String
   * @return the query
   */
  ClassificationQuery serviceLevelLike(String... serviceLevelLike);

  /**
   * Add your applicationEntryPoint to your query.
   *
   * @param applicationEntryPointIn name of the applications entrypoint
   * @return the query
   */
  ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn);

  /**
   * Add your applicationEntryPoint to your query. It will be compared in SQL with an LIKE.
   *
   * @param applicationEntryPointLike name of the applications entrypoint
   * @return the query
   */
  ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike);

  /**
   * Add the values of custom attributes for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  ClassificationQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Add the values of custom attributes for pattern matching to your query. They will be compared
   * in SQL with the LIKE operator. You may use a wildcard like % to specify the pattern. If you
   * specify multiple arguments they are combined with the OR keyword.
   *
   * @param customField identifies which custom attribute is affected.
   * @param searchArguments the customField values of the searched-for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  ClassificationQuery customAttributeLike(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Sort the query result by key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByKey(SortDirection sortDirection);

  /**
   * Sort the query result by the parent classification ID.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentId(SortDirection sortDirection);

  /**
   * Sort the query result by the parent classification key.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentKey(SortDirection sortDirection);

  /**
   * Sort the query result by category.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByCategory(SortDirection sortDirection);

  /**
   * Sort the query result by domain.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByName(SortDirection sortDirection);

  /**
   * Sort the query result by service level.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByServiceLevel(SortDirection sortDirection);

  /**
   * Sort the query result by priority.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sort the query result by the application entry point name.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the value of a custom field.
   *
   * @param customField identifies which custom attribute is affected.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByCustomAttribute(
      ClassificationCustomField customField, SortDirection sortDirection);
}
