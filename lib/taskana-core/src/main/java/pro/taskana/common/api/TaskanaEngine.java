package pro.taskana.common.api;

import java.sql.SQLException;
import java.util.function.Supplier;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;

/** The TaskanaEngine represents an overall set of all needed services. */
public interface TaskanaEngine {

  /**
   * The TaskService can be used for operations on all Tasks.
   *
   * @return the TaskService
   */
  TaskService getTaskService();

  /**
   * The MonitorService can be used for monitoring Tasks.
   *
   * @return the MonitorService
   */
  MonitorService getMonitorService();

  /**
   * The WorkbasketService can be used for operations on all Workbaskets.
   *
   * @return the WorbasketService
   */
  WorkbasketService getWorkbasketService();

  /**
   * The ClassificationService can be used for operations on all Categories.
   *
   * @return the ClassificationService
   */
  ClassificationService getClassificationService();

  /**
   * The JobService can be user for all job operations.
   *
   * @return the JobService
   */
  JobService getJobService();

  /**
   * The Taskana configuration.
   *
   * @return the TaskanaConfiguration
   */
  TaskanaEngineConfiguration getConfiguration();

  /**
   * The WorkingDaysToDaysConverter used to compute holidays.
   *
   * @return the converter
   */
  WorkingDaysToDaysConverter getWorkingDaysToDaysConverter();

  /**
   * Checks if the history plugin is enabled.
   *
   * @return true if the history is enabled. Otherwise false.
   */
  boolean isHistoryEnabled();

  /**
   * sets the connection management mode.
   *
   * @param mode the connection management mode Valid values are:
   *     <ul>
   *       <li>PARTICIPATE - taskana participates in global transaction. This is the default mode.
   *       <li>AUTOCOMMIT - taskana commits each API call separately
   *       <li>EXPLICIT - commit processing is managed explicitly by the client
   *     </ul>
   */
  void setConnectionManagementMode(ConnectionManagementMode mode);

  /**
   * Set the connection to be used by taskana in mode CONNECTION_MANAGED_EXTERNALLY. If this Api is
   * called, taskana uses the connection passed by the client for all subsequent API calls until the
   * client resets this connection. Control over commit and rollback of the connection is the
   * responsibility of the client. In order to close the connection, closeConnection() or
   * setConnection(null) has to be called.
   *
   * @param connection - The java.sql.Connection that is controlled by the client
   * @throws SQLException if a database access error occurs
   */
  void setConnection(java.sql.Connection connection) throws SQLException;

  /**
   * Closes the client's connection, sets it to null and switches to mode PARTICIPATE. Only
   * applicable in mode EXPLICIT. Has the same effect as setConnection(null).
   */
  void closeConnection();

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

  /**
   * This method is supposed to skip further permission checks if we are already in a secured
   * environment. With great power comes great responsibility.
   *
   * @param supplier will be executed with admin privileges
   * @param <T> defined with the supplier return value
   * @return output from supplier
   */
  <T> T runAsAdmin(Supplier<T> supplier);


  /**
   * Returns the CurrentUserContext class.
   *
   * @return the CurrentUserContext
   */
  CurrentUserContext getCurrentUserContext();

  /**
   * Connection management mode. Controls the connection handling of taskana
   *
   * <ul>
   *   <li>PARTICIPATE - taskana participates in global transaction. This is the default mode
   *   <li>AUTOCOMMIT - taskana commits each API call separately
   *   <li>EXPLICIT - commit processing is managed explicitly by the client
   * </ul>
   */
  enum ConnectionManagementMode {
    PARTICIPATE,
    AUTOCOMMIT,
    EXPLICIT
  }
}
