package pro.taskana.simplehistory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.spi.history.api.TaskanaHistory;

/** The TaskanaHistoryEngine represents an overall set of all needed services. */
public interface TaskanaHistoryEngine {
  /**
   * The TaskanaHistory can be used for operations on all history events.
   *
   * @return the HistoryService
   */
  TaskanaHistory getTaskanaHistoryService();

  /**
   * check whether the current user is member of one of the roles specified.
   *
   * @param roles The roles that are checked for membership of the current user
   * @return true if the current user is a member of at least one of the specified groups
   */
  boolean isUserInRole(TaskanaRole... roles);

  /**
   * Checks whether current user is member of any of the specified roles.
   *
   * @param roles The roles that are checked for membership of the current user
   * @throws NotAuthorizedException If the current user is not member of any specified role
   */
  void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException;
}
