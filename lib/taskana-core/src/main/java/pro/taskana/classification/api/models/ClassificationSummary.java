package pro.taskana.classification.api.models;

import pro.taskana.classification.api.ClassificationCustomField;

/**
 * Interface for ClassificationSummaries. This is a specific short model-object which only requires
 * the most important information. Specific ones can be load afterwards via ID.
 */
public interface ClassificationSummary {

  /**
   * Returns the ID of the {@linkplain Classification}.
   *
   * @return classificationId
   */
  String getId();

  /**
   * Returns the key of the {@linkplain Classification}.
   *
   * @return classificationKey
   */
  String getKey();

  /**
   * Returns the category of the {@linkplain Classification}.
   *
   * @return classificationCategory
   */
  String getCategory();

  /**
   * Returns the type of the {@linkplain Classification}.
   *
   * @return classificationType
   */
  String getType();

  /**
   * Returns the domain of the {@linkplain Classification}.
   *
   * @return classificationDomain
   */
  String getDomain();

  /**
   * Returns the name of the {@linkplain Classification}.
   *
   * @return classificationName
   */
  String getName();

  /**
   * Returns the ID of the Parent-{@linkplain Classification}.
   *
   * @return parentId
   */
  String getParentId();

  /**
   * Returns the key of the Parent-{@linkplain Classification}.
   *
   * @return parentKey
   */
  String getParentKey();

  /**
   * Returns the service level of the {@linkplain Classification}.
   *
   * <p>It is a String in ISO-8601 duration format. See {@linkplain java.time.Duration#parse
   * Duration.parse()} for details.
   *
   * @return the service level
   */
  String getServiceLevel();

  /**
   * Returns the application entry point of the {@linkplain Classification}.
   *
   * @return the application entry point
   */
  String getApplicationEntryPoint();

  /**
   * Returns the priority of the {@linkplain Classification}.
   *
   * @return the priority
   */
  int getPriority();

  /**
   * Returns the custom attribute of the {@linkplain Classification}.
   *
   * @param customField identifies which custom attribute is requested
   * @return the value for the given customField
   */
  String getCustomAttribute(ClassificationCustomField customField);

  /**
   * Duplicates this ClassificationSummary without the ID.
   *
   * @return a copy of this ClassificationSummary
   */
  ClassificationSummary copy();
}
