package pro.taskana.classification.api;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

/** ClassificationQuery for generating dynamic sql. */
public interface ClassificationQuery
    extends BaseQuery<ClassificationSummary, ClassificationQueryColumnName> {

  /**
   * Add one or multiple {@linkplain ClassificationSummary#getKey() keys} to your query.
   *
   * @param key as String
   * @return the query
   */
  ClassificationQuery keyIn(String... key);

  /**
   * Add one or multiple {@linkplain ClassificationSummary#getId() ids} to your query.
   *
   * @param id as String
   * @return the query
   */
  ClassificationQuery idIn(String... id);

  /**
   * Add one or multiple {@linkplain ClassificationSummary#getParentId() parentIds} to your query.
   *
   * @param parentId as an array of Strings
   * @return the query
   */
  ClassificationQuery parentIdIn(String... parentId);

  /**
   * Add your {@linkplain ClassificationSummary#getParentKey() parentKeys} to your query.
   *
   * @param parentKey as an array of Strings
   * @return the query
   */
  ClassificationQuery parentKeyIn(String... parentKey);

  /**
   * Add your {@linkplain ClassificationSummary#getCategory() categories} to your query.
   *
   * @param category as String
   * @return the query
   */
  ClassificationQuery categoryIn(String... category);

  /**
   * Add your {@linkplain ClassificationSummary#getType() types} to your query.
   *
   * @param type as String
   * @return the query
   */
  ClassificationQuery typeIn(String... type);

  /**
   * Add your {@linkplain ClassificationSummary#getDomain() domains} to your query which are used as
   * filter.
   *
   * @param domain or domains for filtering.
   * @return the query
   */
  ClassificationQuery domainIn(String... domain);

  /**
   * Add to your query if the {@linkplain pro.taskana.classification.api.models.Classification
   * Classification} shall be valid in its {@linkplain ClassificationSummary#getDomain() domain}.
   *
   * @param validInDomain a simple flag showing if domain is valid
   * @return the query
   */
  ClassificationQuery validInDomainEquals(Boolean validInDomain);

  /**
   * Add your {@linkplain Classification#getCreated() created}-Dates to your query.
   *
   * @param createdIn the {@linkplain TimeInterval} within which the searched-for classifications
   *     were created.
   * @return the query
   */
  ClassificationQuery createdWithin(TimeInterval... createdIn);

  /**
   * Add your {@linkplain Classification#getModified() modified}-Dates to your query.
   *
   * @param modifiedIn the {@linkplain TimeInterval} within which the searched-for classifications
   *     were modified the last time.
   * @return the query
   */
  ClassificationQuery modifiedWithin(TimeInterval... modifiedIn);

  /**
   * Add your {@linkplain ClassificationSummary#getName() names} to your query.
   *
   * @param nameIn as String
   * @return the query
   */
  ClassificationQuery nameIn(String... nameIn);

  /**
   * Add your {@linkplain ClassificationSummary#getName() names} to your query. They will be
   * compared in SQL with a LIKE.
   *
   * @param nameLike as String
   * @return the query
   */
  ClassificationQuery nameLike(String... nameLike);

  /**
   * Add your {@linkplain Classification#getDescription() descriptions} to your query. They will be
   * compared in SQL with a LIKE. If you use a wildcard like % then it will be transmitted to the
   * database.
   *
   * @param descriptionLike your description
   * @return the query
   */
  ClassificationQuery descriptionLike(String descriptionLike);

  /**
   * Add your {@linkplain ClassificationSummary#getPriority() priorities} to your query.
   *
   * @param priorities as integers
   * @return the query
   */
  ClassificationQuery priorityIn(int... priorities);

  /**
   * Add your {@linkplain ClassificationSummary#getServiceLevel() serviceLevels} to your query.
   *
   * @param serviceLevelIn as String
   * @return the query
   */
  ClassificationQuery serviceLevelIn(String... serviceLevelIn);

  /**
   * Add your {@linkplain ClassificationSummary#getServiceLevel() serviceLevels} to your query. They
   * will be compared in SQL with a LIKE.
   *
   * @param serviceLevelLike as String
   * @return the query
   */
  ClassificationQuery serviceLevelLike(String... serviceLevelLike);

  /**
   * Add your {@linkplain ClassificationSummary#getApplicationEntryPoint() applicationEntryPoints}
   * to your query.
   *
   * @param applicationEntryPointIn name of the applications entrypoint
   * @return the query
   */
  ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn);

  /**
   * Add your {@linkplain ClassificationSummary#getApplicationEntryPoint() applicationEntryPoints}
   * to your query. They will be compared in SQL with a LIKE.
   *
   * @param applicationEntryPointLike name of the applications entrypoint
   * @return the query
   */
  ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike);

  /**
   * Add the values of specified {@linkplain ClassificationCustomField ClassificationCustomFields}
   * for exact matching to your query.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the customField values of the searched for tasks
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  ClassificationQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Add the values of specified {@linkplain ClassificationCustomField ClassificationCustomFields}
   * for pattern matching to your query. They will be compared in SQL with the LIKE operator. You
   * may use a wildcard like % to specify the pattern. If you specify multiple arguments they are
   * combined with the OR keyword.
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
   * Sort the query result by {@linkplain ClassificationSummary#getKey() key}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByKey(SortDirection sortDirection);

  /**
   * Sort the query result by the {@linkplain ClassificationSummary#getParentId() id of the parent}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentId(SortDirection sortDirection);

  /**
   * Sort the query result by the {@linkplain ClassificationSummary#getParentKey() key of the
   * parent}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentKey(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain ClassificationSummary#getCategory() category}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByCategory(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain ClassificationSummary#getDomain() domain}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain ClassificationSummary#getName() name}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByName(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain ClassificationSummary#getServiceLevel() serviceLevel}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByServiceLevel(SortDirection sortDirection);

  /**
   * Sort the query result by {@linkplain ClassificationSummary#getPriority() priority}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sort the query result by the {@linkplain ClassificationSummary#getApplicationEntryPoint() name
   * of the application entry point}.
   *
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection);

  /**
   * This method sorts the query result according to the value of the specified {@linkplain
   * ClassificationCustomField custom field}.
   *
   * @param customField identifies which custom attribute is affected.
   * @param sortDirection Determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByCustomAttribute(
      ClassificationCustomField customField, SortDirection sortDirection);
}
