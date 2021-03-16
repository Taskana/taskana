package pro.taskana.classification.api.models;

import java.time.Instant;

import pro.taskana.classification.api.ClassificationCustomField;

/** Interface used to specify the Classification-Model. */
public interface Classification extends ClassificationSummary {

  /**
   * Sets a reference to the current Parent-Classification via ID.
   *
   * <p>EMPTY if there is no parent.
   *
   * @param parentId the ID of the Parent-Classification
   */
  void setParentId(String parentId);

  /**
   * Sets a reference to the current Parent-Classification via key.
   *
   * <p>EMPTY if there is no parent.
   *
   * @param parentKey the key of the Parent-Classification
   */
  void setParentKey(String parentKey);

  /**
   * Sets the category of this Classification.
   *
   * @param category the category of this Classification
   */
  void setCategory(String category);

  /**
   * Returns the current domain-name of this Classification.
   *
   * @return domainName
   */
  String getDomain();

  /**
   * Returns the logical name of the associated application entry point.
   *
   * @return applicationEntryPoint
   */
  String getApplicationEntryPoint();

  /**
   * Sets the logical name of the associated application entry point.
   *
   * @param applicationEntryPoint the application entry point
   */
  void setApplicationEntryPoint(String applicationEntryPoint);

  /**
   * Duplicates this Classification without the ID.
   *
   * @param key for the new Classification
   * @return a copy of this Classification
   */
  Classification copy(String key);

  /**
   * Returns a flag if the Classification is currently valid in the used domain.
   *
   * @return isValidInDomain - flag
   */
  Boolean getIsValidInDomain();

  /**
   * Sets the flag which marks the Classification as valid/invalid in the currently used domain.
   *
   * @param isValidInDomain flag
   */
  void setIsValidInDomain(Boolean isValidInDomain);

  /**
   * Returns the timestamp when this Classification was as created.
   *
   * @return created as {@linkplain Instant}
   */
  Instant getCreated();

  /**
   * Returns the timestamp when this Classification was modified the last time.
   *
   * @return modified as {@linkplain Instant}
   */
  Instant getModified();

  /**
   * Sets the name of this Classification.
   *
   * @param name the name of this Classification
   */
  void setName(String name);

  /**
   * Returns the description of this Classification.
   *
   * @return description
   */
  String getDescription();

  /**
   * Sets the description of this Classification.
   *
   * @param description the description of this Classification
   */
  void setDescription(String description);

  /**
   * Sets the numeric priority of this Classification.
   *
   * @param priority the Priority of this Classification
   */
  void setPriority(int priority);

  /**
   * Sets the service level of this Classification.
   *
   * @param serviceLevel the service level. Must be a String in ISO-8601 duration format. See
   *     {@linkplain java.time.Duration#parse Duration.parse()} for details.
   */
  void setServiceLevel(String serviceLevel);

  /**
   * Sets the value for custom attribute of this Classification.
   *
   * @param customField identifies which custom attribute is to be set
   * @param value the value of the custom attribute to be set
   */
  void setCustomAttribute(ClassificationCustomField customField, String value);

  /**
   * Returns a summary of this Classification.
   *
   * @return the {@linkplain ClassificationSummary} object for this Classification
   */
  ClassificationSummary asSummary();
}
