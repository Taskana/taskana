package pro.taskana.workbasket.api.models;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/**
 * Interface for WorkbasketSummary. This is a specific short model-object which only contains the
 * most important information.
 */
public interface WorkbasketSummary {

  /**
   * Gets the id of the workbasket.
   *
   * @return workbasketId
   */
  String getId();

  /**
   * Gets the key of the workbasket.
   *
   * @return workbasketKey
   */
  String getKey();

  /**
   * Gets the name of the workbasket.
   *
   * @return workbasket's name
   */
  String getName();

  /**
   * Gets the description of the workbasket.
   *
   * @return workbasket's description
   */
  String getDescription();

  /**
   * Gets the owner of the workbasket.
   *
   * @return workbasket's owner
   */
  String getOwner();

  /**
   * Gets the domain of the workbasket.
   *
   * @return workbasket's domain
   */
  String getDomain();

  /**
   * Gets the type of the workbasket.
   *
   * @return workbasket's type
   */
  WorkbasketType getType();

  /**
   * Gets the custom attribute of the workbasket.
   *
   * @param customField identifies which custom attribute is requested.
   * @return the value for the given customField
   * @deprecated Use {@link #getCustomField(WorkbasketCustomField)} instead
   */
  String getCustomAttribute(WorkbasketCustomField customField);

  /**
   * Gets the custom attribute of the workbasket.
   *
   * @param customField identifies which custom attribute is requested.
   * @return the value for the given customField
   */
  String getCustomField(WorkbasketCustomField customField);

  /**
   * Gets the orglevel1 property of the workbasket.
   *
   * @return the workbasket's orglevel1 property
   */
  String getOrgLevel1();

  /**
   * Gets the orglevel2 property of the workbasket.
   *
   * @return the workbasket's orglevel2 property
   */
  String getOrgLevel2();

  /**
   * Gets the orglevel3 property of the workbasket.
   *
   * @return the workbasket's orglevel3 property
   */
  String getOrgLevel3();

  /**
   * Gets the orglevel4 property of the workbasket.
   *
   * @return the workbasket's orglevel4 property
   */
  String getOrgLevel4();

  /**
   * Gets the markedForDeletion property of the workbasket.
   *
   * @return the workbasket's markedForDeletion property
   */
  boolean isMarkedForDeletion();

  /**
   * Duplicates this WorkbasketSummary without the id.
   *
   * @return a copy of this WorkbasketSummary
   */
  WorkbasketSummary copy();
}
