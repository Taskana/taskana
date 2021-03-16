package pro.taskana.workbasket.api.models;

import pro.taskana.workbasket.api.WorkbasketPermission;

/**
 * Interface for WorkbasketAccessItem. This interface is used to control access of users to
 * {@linkplain Workbasket Workbaskets}.
 */
public interface WorkbasketAccessItem {

  /**
   * Returns the ID of this WorkbasketAccessItem.
   *
   * @return id
   */
  String getId();

  /**
   * Returns the ID of the referenced {@linkplain Workbasket}.
   *
   * @return the ID of the {@linkplain Workbasket}
   */
  String getWorkbasketId();

  /**
   * Returns the key of the referenced {@linkplain Workbasket}.
   *
   * @return the key of the {@linkplain Workbasket}
   */
  String getWorkbasketKey();

  /**
   * Returns the groupId or userId for which this WorkbasketAccessItem controls access permissions.
   *
   * @return accessId, this is the groupId or userId
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
   * Sets the name of the group or user for which this WorkbasketAccessItem controls access
   * permissions.
   *
   * @param name the name of the group or user for which this WorkbasketAccessItem controls access
   *     permissions
   */
  void setAccessName(String name);

  /**
   * Sets a given permission for the referenced {@linkplain Workbasket}.
   *
   * @param permission the permission which is set
   * @param value the value for the permission
   */
  void setPermission(WorkbasketPermission permission, boolean value);

  /**
   * Returns whether the given permission is permitted or not.
   *
   * @param permission the permission in question
   * @return true, when the given permission is permitted. Otherwise false
   */
  boolean getPermission(WorkbasketPermission permission);

  /**
   * Duplicates this WorkbasketAccessItem without the ID.
   *
   * @return a copy of this WorkbasketAccessItem
   */
  WorkbasketAccessItem copy();
}
