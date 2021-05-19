package pro.taskana.workbasket.api.models;

import java.time.Instant;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/** Workbasket entity interface. */
public interface Workbasket extends WorkbasketSummary {

  /**
   * Sets the name of this Workbasket.
   *
   * @param workbasketName the name of this Workbasket
   */
  void setName(String workbasketName);

  /**
   * Sets the description of this Workbasket.
   *
   * @param description the description of this Workbasket
   */
  void setDescription(String description);

  /**
   * Sets the type of this Workbasket.
   *
   * @param type the type of this Workbasket
   */
  void setType(WorkbasketType type);

  /**
   * Sets the value for custom attribute.
   *
   * @param customField identifies which custom attribute is to be set
   * @param value the value of the custom attribute to be set
   */
  void setCustomAttribute(WorkbasketCustomField customField, String value);

  /**
   * Sets the value for orgLevel1 attribute.
   *
   * @param orgLevel1 the orgLevel1 property of this Workbasket
   */
  void setOrgLevel1(String orgLevel1);

  /**
   * Sets the value for orgLevel2 attribute.
   *
   * @param orgLevel2 the orgLevel2 property of this Workbasket
   */
  void setOrgLevel2(String orgLevel2);

  /**
   * Sets the value for orgLevel3 attribute.
   *
   * @param orgLevel3 the orgLevel3 property of this Workbasket
   */
  void setOrgLevel3(String orgLevel3);

  /**
   * Sets the value for orgLevel4 attribute.
   *
   * @param orgLevel4 the orgLevel4 property of this Workbasket
   */
  void setOrgLevel4(String orgLevel4);

  /**
   * Returns the value for the markedForDeletion attribute.
   *
   * @return markedForDeletion
   */
  boolean isMarkedForDeletion();

  /**
   * Sets the value for markedForDeletion attribute.
   *
   * @param markedForDeletion the markedForDeletion property of this Workbasket
   */
  void setMarkedForDeletion(boolean markedForDeletion);

  /**
   * Duplicates this Workbasket without the ID.
   *
   * @param key for the new Workbasket
   * @return a copy of this Workbasket
   */
  Workbasket copy(String key);

  /**
   * Sets the owner-ID of this Workbasket.
   *
   * @param owner of this Workbasket
   */
  void setOwner(String owner);

  /**
   * Returns the date when this Workbasket was created.
   *
   * @return created as {@linkplain Instant}
   */
  Instant getCreated();

  /**
   * Returns the date when this Workbasket was modified the last time.
   *
   * @return modified as {@linkplain Instant}
   */
  Instant getModified();

  /**
   * Returns a summary of this Workbasket.
   *
   * @return the {@linkplain WorkbasketSummary} object for this Workbasket
   */
  WorkbasketSummary asSummary();
}
