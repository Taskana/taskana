package pro.taskana.workbasket.api.models;

import java.time.Instant;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/** Workbasket entity interface. */
public interface Workbasket extends WorkbasketSummary {

  /**
   * Sets the name of the workbasket.
   *
   * @param workbasketName the name of the workbasket
   */
  void setName(String workbasketName);

  /**
   * Sets the workbasket-descriptions.
   *
   * @param description the description of the workbasket
   */
  void setDescription(String description);

  /**
   * Sets the type of the workbasket.
   *
   * @param type the type of the workbasket
   */
  void setType(WorkbasketType type);

  /**
   * Sets the value for custom Attribute.
   *
   * @param customField identifies which custom attribute is to be set.
   * @param value the value of the custom attribute to be set
   * @deprecated Use {@link #setCustomField(WorkbasketCustomField, String)} instead
   */
  void setCustomAttribute(WorkbasketCustomField customField, String value);

  /**
   * Sets the value for custom field.
   *
   * @param customField identifies which custom field is to be set.
   * @param value the value of the custom field to be set
   */
  void setCustomField(WorkbasketCustomField customField, String value);

  /**
   * Sets the value for orgLevel1 attribute.
   *
   * @param orgLevel1 the orgLevel1 property of the workbasket
   */
  void setOrgLevel1(String orgLevel1);

  /**
   * Sets the value for orgLevel2 attribute.
   *
   * @param orgLevel2 the orgLevel2 property of the workbasket
   */
  void setOrgLevel2(String orgLevel2);

  /**
   * Sets the value for orgLevel3 attribute.
   *
   * @param orgLevel3 the orgLevel3 property of the workbasket
   */
  void setOrgLevel3(String orgLevel3);

  /**
   * Sets the value for orgLevel4 attribute.
   *
   * @param orgLevel4 the orgLevel4 property of the workbasket
   */
  void setOrgLevel4(String orgLevel4);

  /**
   * Return the value for the markedForDeletion attribute.
   *
   * @return markedForDeletion
   */
  boolean isMarkedForDeletion();

  /**
   * Sets the value for markedForDeletion attribute.
   *
   * @param markedForDeletion the markedForDeletion property of the workbasket
   */
  void setMarkedForDeletion(boolean markedForDeletion);

  /**
   * Duplicates this Workbasket without the id.
   *
   * @param key for the new Workbasket
   * @return a copy of this Workbasket
   */
  Workbasket copy(String key);

  /**
   * Sets the owner-ID of the workbasket.
   *
   * @param owner of the current workbasket
   */
  void setOwner(String owner);

  /**
   * Returns the date when the workbasket was created.
   *
   * @return created as Instant
   */
  Instant getCreated();

  /**
   * Returns the date when the workbasket was modified the last time.
   *
   * @return modified as Instant
   */
  Instant getModified();

  /**
   * Return a summary of the current workbasket.
   *
   * @return the WorkbasketSummary object for the current work basket
   */
  WorkbasketSummary asSummary();
}
