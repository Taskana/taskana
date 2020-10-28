package pro.taskana.workbasket.api.models;

import pro.taskana.workbasket.api.WorkbasketPermission;

/**
 * Interface for WorkbasketAccessItem. This interface is used to control access of users to
 * workbaskets.
 */
public interface WorkbasketAccessItem {

  /**
   * Returns the current id of the WorkbasketAccessItem.
   *
   * @return Id
   */
  String getId();

  /**
   * Returns the Id of the referenced workbasket.
   *
   * @return the workbasket Id
   */
  String getWorkbasketId();

  /**
   * Returns the Key of the referenced workbasket.
   *
   * @return the workbasket Key
   */
  String getWorkbasketKey();

  /**
   * Returns the group id or user id for which this WorkbasketAccessItem controls access
   * permissions.
   *
   * @return access id, this is the group id or user id
   */
  String getAccessId();

  /**
   * Returns the name of the group or user for which this WorkbasketAccessItem controls access
   * permissions.
   *
   * @return access name, this is the name of the group or user
   */
  String getAccessName();

  /**
   * Set the name of the group or user for which this WorkbasketAccessItem controls access
   * permissions.
   *
   * @param name the name of the group or user for which this WorkbasketAccessItem controls access
   *     permissions.
   */
  void setAccessName(String name);

  /**
   * Sets a given permission for the referenced workbasket.
   *
   * @param permission the permission which is set.
   * @param value the value for the permission.
   */
  void setPermission(WorkbasketPermission permission, boolean value);

  /**
   * Returns weather the given permission is permitted or not.
   *
   * @param permission the permission in question.
   * @return True, when the given permission is permitted. Otherwise false.
   */
  boolean getPermission(WorkbasketPermission permission);

  /**
   * Duplicates this WorkbasketAccessItem without the id.
   *
   * @return a copy of this WorkbasketAccessItem
   */
  WorkbasketAccessItem copy();
}
