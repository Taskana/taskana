package pro.taskana.common.api.security;

import java.util.List;

/**
 * The CurrentUserContext provides the context information about the current (calling) user. The
 * context is gathered from the JAAS subject.
 */
public interface CurrentUserContext {

  /**
   * Returns the userid of the current user.
   *
   * @return the userid of the user; null if there is no JAAS subject
   */
  public String getUserid();

  /**
   * Returns all groupIds of the current user.
   *
   * @return list containing all groupIds of the current user; empty if the current user belongs to
   *     no groups or if no JAAS Subject is set
   */
  public List<String> getGroupIds();

  /**
   * Returns all accessIds of the current user. This combines the userId and all groupIds of the
   * current user.
   *
   * @return list containing all accessIds of the current user; empty if there is no JAAS subject;
   */
  public List<String> getAccessIds();
}
