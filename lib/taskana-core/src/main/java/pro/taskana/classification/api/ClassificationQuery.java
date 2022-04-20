package pro.taskana.classification.api;

import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

/**
 * The ClassificationQuery allows for a custom search across all {@linkplain Classification
 * Classifications}.
 */
public interface ClassificationQuery
    extends BaseQuery<ClassificationSummary, ClassificationQueryColumnName> {

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getKey() key} equal to any of the passed values.
   *
   * @param key the values of interest
   * @return the query
   */
  ClassificationQuery keyIn(String... key);

  /**
   * Selects only {@linkplain Classification Classifications} which have an {@linkplain
   * Classification#getId() id} equal to any of the passed values.
   *
   * @param id the values of interest
   * @return the query
   */
  ClassificationQuery idIn(String... id);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getParentId() parentId} equal to any of the passed values.
   *
   * @param parentId the values of interest
   * @return the query
   */
  ClassificationQuery parentIdIn(String... parentId);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getParentKey() parentKey} equal to any of the passed values.
   *
   * @param parentKey the values of interest
   * @return the query
   */
  ClassificationQuery parentKeyIn(String... parentKey);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getCategory() category} equal to any of the passed values.
   *
   * @param category the values of interest
   * @return the query
   */
  ClassificationQuery categoryIn(String... category);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getType() type} equal to any of the passed values.
   *
   * @param type the values of interest
   * @return the query
   */
  ClassificationQuery typeIn(String... type);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getDomain() domain} equal to any of the passed values.
   *
   * @param domain the values of interest
   * @return the query
   */
  ClassificationQuery domainIn(String... domain);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getIsValidInDomain() isValidInDomain} flag equal to the passed flag.
   *
   * @param validInDomain the flag of interest
   * @return the query
   */
  ClassificationQuery validInDomainEquals(Boolean validInDomain);

  /**
   * Selects only {@linkplain Classification Classifications} which were {@linkplain
   * Classification#getCreated() created} within any of the passed {@linkplain TimeInterval
   * TimeIntervals}.
   *
   * @param createdIn the {@linkplain TimeInterval TimeIntervals} of interest
   * @return the query
   */
  ClassificationQuery createdWithin(TimeInterval... createdIn);

  /**
   * Selects only {@linkplain Classification Classifications} which were {@linkplain
   * Classification#getModified() modified} within any of the passed {@linkplain TimeInterval
   * TimeIntervals}.
   *
   * @param modifiedIn the {@linkplain TimeInterval TimeIntervals} of interest
   * @return the query
   */
  ClassificationQuery modifiedWithin(TimeInterval... modifiedIn);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getName() name} equal to any of the passed values.
   *
   * @param nameIn the values of interest
   * @return the query
   */
  ClassificationQuery nameIn(String... nameIn);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getName()} value that matches any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param nameLike the patterns of interest
   * @return the query
   */
  ClassificationQuery nameLike(String... nameLike);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getDescription() description} value that matches any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param descriptionLike the patterns of interest
   * @return the query
   */
  ClassificationQuery descriptionLike(String descriptionLike);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getPriority() priority} equal to any of the passed values.
   *
   * @param priorities the values of interest
   * @return the query
   */
  ClassificationQuery priorityIn(int... priorities);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getServiceLevel() serviceLevel} equal to any of the passed values.
   *
   * @param serviceLevelIn the values of interest
   * @return the query
   */
  ClassificationQuery serviceLevelIn(String... serviceLevelIn);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getServiceLevel() serviceLevel} value that matches any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param serviceLevelLike the patterns of interest
   * @return the query
   */
  ClassificationQuery serviceLevelLike(String... serviceLevelLike);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getApplicationEntryPoint() applicationEntryPoint} equal to any of the passed
   * values.
   *
   * @param applicationEntryPointIn the values of interest
   * @return the query
   */
  ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn);

  /**
   * Selects only {@linkplain Classification Classifications} which have a {@linkplain
   * Classification#getApplicationEntryPoint() applicationEntryPoint} that matches any of the passed
   * patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param applicationEntryPointLike the patterns of interest
   * @return the query
   */
  ClassificationQuery applicationEntryPointLike(String... applicationEntryPointLike);

  /**
   * Selects only {@linkplain Classification Classifications} which have the specified {@linkplain
   * Classification#getCustomField(ClassificationCustomField) customField} with the value equal to
   * any of the passed values.
   *
   * @param customField identifies which {@linkplain ClassificationCustomField} is affected
   * @param searchArguments the values of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or NULL
   */
  ClassificationQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Selects only {@linkplain Classification Classifications} which have the specified {@linkplain
   * Classification#getCustomField(ClassificationCustomField) customField} with the value matching
   * any of the passed patterns.
   *
   * <p>Behind this method the SQL LIKE operator is used. SQL LIKE wildcard characters will be
   * resolved correctly.
   *
   * <p>Not using any wildcard will compute an exact match.
   *
   * @param customField identifies which {@linkplain ClassificationCustomField} is affected
   * @param searchArguments the patterns of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or NULL
   */
  ClassificationQuery customAttributeLike(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Sorts the query result by {@linkplain Classification#getKey() key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByKey(SortDirection sortDirection);

  /**
   * Sorts the query result by the {@linkplain Classification#getParentKey() parentKey}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentId(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Classification#getCategory() category}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is null, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByParentKey(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Classification#getCategory() category}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
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
   * Sorts the query result by {@linkplain Classification#getName() name}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByName(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Classification#getDomain() domain}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByServiceLevel(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain Classification#getPriority() priority}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sorts the query result by the {@linkplain Classification#getApplicationEntryPoint()
   * applicationEntryPoint}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection);

  /**
   * Sorts the query result according to the value of the specified {@linkplain
   * Classification#getCustomField(ClassificationCustomField) customField}.
   *
   * @param customField identifies which {@linkplain
   *     Classification#getCustomField(ClassificationCustomField) customField} is affected
   * @param sortDirection determines whether the result is sorted in ascending or descending order;
   *     if sortDirection is NULL, the result is sorted in ascending order
   * @return the query
   */
  ClassificationQuery orderByCustomAttribute(
      ClassificationCustomField customField, SortDirection sortDirection);
}
