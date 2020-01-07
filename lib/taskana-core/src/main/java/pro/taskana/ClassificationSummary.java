package pro.taskana;

/**
 * Interface for ClassificationSummaries. This is a specific short model-object which only requieres
 * the most important informations. Specific ones can be load afterwards via ID.
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
   * Gets the service level of the parent classification. It is a String in ISO-8601 duration
   * format. See the parse() method of {@code Duration} for details.
   *
   * @return the service level
   */
  String getServiceLevel();

  /**
   * Gets the priority of the classification.
   *
   * @return the priority
   */
  int getPriority();

  /**
   * Get the 1. custom-attribute.
   *
   * @return custom1
   */
  String getCustom1();

  /**
   * Get the 2. custom-attribute.
   *
   * @return custom2
   */
  String getCustom2();

  /**
   * Get the 3. custom-attribute.
   *
   * @return custom3
   */
  String getCustom3();

  /**
   * Get the 4. custom-attribute.
   *
   * @return custom4
   */
  String getCustom4();

  /**
   * Get the 5. custom-attribute.
   *
   * @return custom5
   */
  String getCustom5();

  /**
   * Get the 6. custom-attribute.
   *
   * @return custom6
   */
  String getCustom6();

  /**
   * Get the 7. custom-attribute.
   *
   * @return custom7
   */
  String getCustom7();

  /**
   * Get the 8. custom-attribute.
   *
   * @return custom8
   */
  String getCustom8();
}
