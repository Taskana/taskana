package pro.taskana.classification.api.models;

import java.time.Instant;

import pro.taskana.classification.api.ClassificationCustomField;

/** Interface used to specify the Classification-Model. */
public interface Classification extends ClassificationSummary {

  /**
   * Set/Change a reference to the current parent classification via ID. EMPTY if there is no
   * parent.
   *
   * @param parentId The ID of the parent classification.
   */
  void setParentId(String parentId);

  /**
   * Set/Change a reference to the current parent classification via key. EMPTY if there is no
   * parent.
   *
   * @param parentKey The key of the parent classification.
   */
  void setParentKey(String parentKey);

  /**
   * Set/Change the category of this classification.
   *
   * @param category The category of the classification.
   */
  void setCategory(String category);

  /**
   * Get the current domain-name of this classification.
   *
   * @return domain name
   */
  String getDomain();

  /**
   * Get the logical name of the associated application entry point.
   *
   * @return applicationEntryPoint
   */
  String getApplicationEntryPoint();

  /**
   * Set the logical name of the associated application entry point.
   *
   * @param applicationEntryPoint The application entry point
   */
  void setApplicationEntryPoint(String applicationEntryPoint);

  /**
   * Duplicates this Classification without the id.
   *
   * @param key for the new Classification
   * @return a copy of this Classification
   */
  Classification copy(String key);

  /**
   * Get a flag if the classification if currently valid in the used domain.
   *
   * @return isValidInDomain - flag
   */
  Boolean getIsValidInDomain();

  /**
   * Set/Change the flag which marks the classification as valid/invalid in the currently used
   * domain.
   *
   * @param isValidInDomain - flag
   */
  void setIsValidInDomain(Boolean isValidInDomain);

  /**
   * Get the timestamp when this classification was as created.
   *
   * @return created as instant
   */
  Instant getCreated();

  /**
   * Get the timestamp when this classification was as modified the last time.
   *
   * @return modified as instant
   */
  Instant getModified();

  /**
   * Set/Change the classification name.
   *
   * @param name the name of the Classification
   */
  void setName(String name);

  /**
   * Get the description of a classification.
   *
   * @return description
   */
  String getDescription();

  /**
   * Set/Change the classification description.
   *
   * @param description the description of the Classification
   */
  void setDescription(String description);

  /**
   * Set/Change the numeric priority of a classification.
   *
   * @param priority the Priority of the Classification
   */
  void setPriority(int priority);

  /**
   * Set/Change the service level.
   *
   * @param serviceLevel the service level. Must be a String in ISO-8601 duration format. See the
   *     parse() method of {@code Duration} for details.
   */
  void setServiceLevel(String serviceLevel);

  /**
   * Sets the value for custom Attribute.
   *
   * @param customField identifies which custom attribute is to be set.
   * @param value the value of the custom attribute to be set
   * @deprecated Use {@link #setCustomField(ClassificationCustomField, String)} instead
   */
  void setCustomAttribute(ClassificationCustomField customField, String value);

  /**
   * Sets the value for custom field.
   *
   * @param customField identifies which custom field is to be set.
   * @param value the value of the custom field to be set
   */
  void setCustomField(ClassificationCustomField customField, String value);

  /**
   * Return a summary of the current Classification.
   *
   * @return the ClassificationSummary object for the current classification
   */
  ClassificationSummary asSummary();
}
