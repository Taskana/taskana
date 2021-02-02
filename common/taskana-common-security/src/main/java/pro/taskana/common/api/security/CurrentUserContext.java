package pro.taskana.common.api.security;

import java.util.List;

/**
 * Provides the context information about the current (calling) user. The context is gathered from
 * the JAAS subject.
 */
public interface CurrentUserContext {

  /**
   * Returns the userid of the current user.
   *
   * @return String the userid. null if there is no JAAS subject.
   */
  public String getUserid();

  /**
   * Returns all groupIds of the current user.
   *
   * @return list containing all groupIds of the current user. Empty if the current user belongs to
   *     no groups or no JAAS Subject set.
   */
  public List<String> getGroupIds();

  /**
   * Returns all accessIds of the current user. This combines the userId and all groupIds of the
   * current user.
   *
   * @return list containing all accessIds of the current user. Empty if there is no JAAS subject.
   */
  public List<String> getAccessIds();
}
