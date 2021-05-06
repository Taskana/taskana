package pro.taskana.classification.api;

import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.common.api.BaseQuery;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.common.api.exceptions.InvalidArgumentException;

/**
 * The ClassificationQuery allows for a custom search across all {@linkplain
 * pro.taskana.classification.api.models.Classification Classifications}.
 */
public interface ClassificationQuery
    extends BaseQuery<ClassificationSummary, ClassificationQueryColumnName> {

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getKey() key}
   * value equal to any of the passed values.
   *
   * @param key the values of interest
   * @return the query
   */
  ClassificationQuery keyIn(String... key);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getId() id} value
   * equal to any of the passed values.
   *
   * @param id the values of interest
   * @return the query
   */
  ClassificationQuery idIn(String... id);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getParentId()
   * parent id} value equal to any of the passed values.
   *
   * @param parentId the values of interest
   * @return the query
   */
  ClassificationQuery parentIdIn(String... parentId);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getParentKey()
   * parent key} value equal to any of the passed values.
   *
   * @param parentKey the values of interest
   * @return the query
   */
  ClassificationQuery parentKeyIn(String... parentKey);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getCategory()
   * category} value equal to any of the passed values.
   *
   * @param category the values of interest
   * @return the query
   */
  ClassificationQuery categoryIn(String... category);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getType() type}
   * value equal to any of the passed values.
   *
   * @param type the values of interest
   * @return the query
   */
  ClassificationQuery typeIn(String... type);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getDomain()
   * domain} value equal to any of the passed values.
   *
   * @param domain the values of interest
   * @return the query
   */
  ClassificationQuery domainIn(String... domain);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain
   * pro.taskana.classification.api.models.Classification#getIsValidInDomain() valid in domain} flag
   * equal to the passed flag.
   *
   * @param validInDomain the flag of interest
   * @return the query
   */
  ClassificationQuery validInDomainEquals(Boolean validInDomain);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getCreated()
   * created} value equal to any of the passed values.
   *
   * @param createdIn the values of interest
   * @return the query
   */
  ClassificationQuery createdWithin(TimeInterval... createdIn);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getModified()
   * modified} value equal to any of the passed values.
   *
   * @param modifiedIn the values of interest
   * @return the query
   */
  ClassificationQuery modifiedWithin(TimeInterval... modifiedIn);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getName() name}
   * value equal to any of the passed values.
   *
   * @param nameIn the values of interest
   * @return the query
   */
  ClassificationQuery nameIn(String... nameIn);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getName() name}
   * value that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getDescription()
   * description} value that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getPriority()
   * priority} value equal to any of the passed values.
   *
   * @param priorities the values of interest
   * @return the query
   */
  ClassificationQuery priorityIn(int... priorities);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getServiceLevel()
   * service level} value equal to any of the passed values.
   *
   * @param serviceLevelIn the values of interest
   * @return the query
   */
  ClassificationQuery serviceLevelIn(String... serviceLevelIn);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain pro.taskana.classification.api.models.Classification#getServiceLevel()
   * service level} value that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain
   * pro.taskana.classification.api.models.Classification#getApplicationEntryPoint() name} value
   * equal to any of the passed values.
   *
   * @param applicationEntryPointIn the values of interest
   * @return the query
   */
  ClassificationQuery applicationEntryPointIn(String... applicationEntryPointIn);

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain
   * pro.taskana.classification.api.models.Classification#getApplicationEntryPoint() application
   * entry point} value that contains any of the passed patterns.
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
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain
   * pro.taskana.classification.api.models.Classification#getCustomAttribute custom attribute} value
   * equal to any of the passed values.
   *
   * @param customField identifies which custom attribute is affected
   * @param searchArguments the values of interest
   * @return the query
   * @throws InvalidArgumentException if searchArguments is empty or null
   */
  ClassificationQuery customAttributeIn(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Selects only {@linkplain pro.taskana.classification.api.models.Classification Classifications}
   * which have a {@linkplain
   * pro.taskana.classification.api.models.Classification#getCustomAttribute custom attribute} value
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
  ClassificationQuery customAttributeLike(
      ClassificationCustomField customField, String... searchArguments)
      throws InvalidArgumentException;

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getKey() key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByKey(SortDirection sortDirection);

  /**
   * Sorts the query result by the {@linkplain
   * pro.taskana.classification.api.models.Classification#getParentId parent id}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByParentId(SortDirection sortDirection);

  /**
   * Sorts the query result by the {@linkplain
   * pro.taskana.classification.api.models.Classification#getParentKey parent key}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByParentKey(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getCategory() category}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByCategory(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getDomain() domain}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByDomain(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getName() name}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByName(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getServiceLevel() service level}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByServiceLevel(SortDirection sortDirection);

  /**
   * Sorts the query result by {@linkplain
   * pro.taskana.classification.api.models.Classification#getPriority() priority}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByPriority(SortDirection sortDirection);

  /**
   * Sorts the query result by the {@linkplain
   * pro.taskana.classification.api.models.Classification#getApplicationEntryPoint() application
   * entry point}.
   *
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByApplicationEntryPoint(SortDirection sortDirection);

  /**
   * Sorts the query result according to the value of a {@linkplain
   * pro.taskana.classification.api.models.Classification#getCustomAttribute custom attribute}.
   *
   * @param customField identifies which {@linkplain
   *     pro.taskana.classification.api.models.Classification#getCustomAttribute custom attribute}
   *     is affected.
   * @param sortDirection determines whether the result is sorted in ascending or descending order.
   *     If sortDirection is null, the result is sorted in ascending order.
   * @return the query
   */
  ClassificationQuery orderByCustomAttribute(
      ClassificationCustomField customField, SortDirection sortDirection);
}
