package pro.taskana.classification.api.models;

import pro.taskana.classification.api.ClassificationCustomField;

/**
 * Interface for ClassificationSummaries. This is a specific short model-object which only requieres
 * the most important information. Specific ones can be load afterwards via ID.
 */
public interface ClassificationSummary {

  /**
   * Gets the id of the classification.
   *
   * @return classificationId
   */
  String getId();

  /**
   * Gets the key of the classification.
   *
   * @return classificationKey
   */
  String getKey();

  /**
   * Gets the category of the classification.
   *
   * @return classificationCategory
   */
  String getCategory();

  /**
   * Gets the type of the classification.
   *
   * @return classificationType
   */
  String getType();

  /**
   * Gets the domain of the classification.
   *
   * @return classificationDomain
   */
  String getDomain();

  /**
   * Gets the name of the classification.
   *
   * @return classificationName
   */
  String getName();

  /**
   * Gets the ID of the parent classification.
   *
   * @return parentId
   */
  String getParentId();

  /**
   * Gets the key of the parent classification.
   *
   * @return parentKey
   */
  String getParentKey();

  /**
   * Gets the service level of the classification. It is a String in ISO-8601 duration format. See
   * the parse() method of {@code Duration} for details.
   *
   * @return the service level
   */
  String getServiceLevel();

  /**
   * Gets the application entry point of the classification.
   *
   * @return the application entry point
   */
  String getApplicationEntryPoint();

  /**
   * Gets the priority of the classification.
   *
   * @return the priority
   */
  int getPriority();

  /**
   * Gets the custom attribute of the classification.
   *
   * @param customField identifies which custom attribute is requested.
   * @return the value for the given customField
   * @deprecated Use {@link #getCustomField(ClassificationCustomField)} instead
   */
  String getCustomAttribute(ClassificationCustomField customField);

  /**
   * Gets the custom field of the classification.
   *
   * @param customField identifies which custom field is requested.
   * @return the value for the given custom field
   */
  String getCustomField(ClassificationCustomField customField);

  /**
   * Duplicates this ClassificationSummary without the id.
   *
   * @return a copy of this ClassificationSummary
   */
  ClassificationSummary copy();
}
