package pro.taskana.workbasket.api.models;

import pro.taskana.workbasket.api.WorkbasketCustomField;
import pro.taskana.workbasket.api.WorkbasketType;

/**
 * Interface for WorkbasketSummary. This is a specific short model-object which only contains the
 * most important information.
 */
public interface WorkbasketSummary {

  /**
   * Returns the ID of the {@linkplain Workbasket}.
   *
   * @return workbasketId
   */
  String getId();

  /**
   * Returns the key of the {@linkplain Workbasket}.
   *
   * @return workbasketKey
   */
  String getKey();

  /**
   * Returns the name of the {@linkplain Workbasket}.
   *
   * @return workbasket's name
   */
  String getName();

  /**
   * Returns the description of the {@linkplain Workbasket}.
   *
   * @return workbasket's description
   */
  String getDescription();

  /**
   * Returns the owner of the {@linkplain Workbasket}.
   *
   * @return workbasket's owner
   */
  String getOwner();

  /**
   * Returns the domain of the {@linkplain Workbasket}.
   *
   * @return workbasket's domain
   */
  String getDomain();

  /**
   * Returns the type of the {@linkplain Workbasket}.
   *
   * @return workbasket's type
   */
  WorkbasketType getType();

  /**
   * Returns the custom attribute of the {@linkplain Workbasket}.
   *
   * @param customField identifies which custom attribute is requested
   * @return the value for the given customField
   */
  String getCustomAttribute(WorkbasketCustomField customField);

  /**
   * Returns the orglevel1 property of the {@linkplain Workbasket}.
   *
   * @return the workbasket's orglevel1 property
   */
  String getOrgLevel1();

  /**
   * Returns the orglevel2 property of the {@linkplain Workbasket}.
   *
   * @return the workbasket's orglevel2 property
   */
  String getOrgLevel2();

  /**
   * Returns the orglevel3 property of the {@linkplain Workbasket}.
   *
   * @return the workbasket's orglevel3 property
   */
  String getOrgLevel3();

  /**
   * Returns the orglevel4 property of the {@linkplain Workbasket}.
   *
   * @return the workbasket's orglevel4 property
   */
  String getOrgLevel4();

  /**
   * Returns the markedForDeletion property of the {@linkplain Workbasket}.
   *
   * @return the workbasket's markedForDeletion property
   */
  boolean isMarkedForDeletion();

  /**
   * Duplicates this WorkbasketSummary without the ID.
   *
   * @return a copy of this WorkbasketSummary
   */
  WorkbasketSummary copy();
}
