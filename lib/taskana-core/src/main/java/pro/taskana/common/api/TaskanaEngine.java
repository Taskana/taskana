package pro.taskana.common.api;

import java.sql.SQLException;

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
   * Returns the {@linkplain TaskService} used for operations on all {@linkplain
   * pro.taskana.task.api.models.Task Tasks}.
   *
   * @return the {@linkplain TaskService}
   */
  TaskService getTaskService();

  /**
   * Returns the {@linkplain MonitorService} used for monitoring {@linkplain
   * pro.taskana.task.api.models.Task Tasks}.
   *
   * @return the {@linkplain MonitorService}
   */
  MonitorService getMonitorService();

  /**
   * Returns the {@linkplain WorkbasketService} used for operations on all {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbaskets}.
   *
   * @return the {@linkplain pro.taskana.workbasket.api.models.Workbasket Workbasket}
   */
  WorkbasketService getWorkbasketService();

  /**
   * Returns the {@linkplain ClassificationService} used for operations on all {@linkplain
   * pro.taskana.classification.api.models.Classification Classifications}.
   *
   * @return the {@linkplain ClassificationService}
   */
  ClassificationService getClassificationService();

  /**
   * Returns the {@linkplain JobService} which can be used for all {@linkplain ScheduledJob Job}
   * operations.
   *
   * @return the {@linkplain JobService}
   */
  JobService getJobService();

  /**
   * Returns the Taskana configuration.
   *
   * @return the {@linkplain TaskanaEngineConfiguration TaskanaConfiguration}
   */
  TaskanaEngineConfiguration getConfiguration();

  /**
   * Returns the {@linkplain WorkingDaysToDaysConverter} used to compute holidays.
   *
   * @return the {@linkplain WorkingDaysToDaysConverter}
   */
  WorkingDaysToDaysConverter getWorkingDaysToDaysConverter();

  /**
   * Checks if the History plugin is enabled.
   *
   * @return true if the {@linkplain pro.taskana.spi.history.api.TaskanaHistory TaskanaHistory} is
   *     enabled. Otherwise false.
   */
  boolean isHistoryEnabled();

  /**
   * Sets the connection management mode.
   *
   * @param mode the connection management mode valid values are:
   *     <ul>
   *       <li>PARTICIPATE - Taskana participates in global transaction. This is the default mode.
   *       <li>AUTOCOMMIT - Taskana commits each API call separately
   *       <li>EXPLICIT - commit processing is managed explicitly by the client
   *     </ul>
   */
  void setConnectionManagementMode(ConnectionManagementMode mode);

  /**
   * Sets the connection to be used by Taskana in mode CONNECTION_MANAGED_EXTERNALLY.
   *
   * <p>If this Api is called, Taskana uses the connection passed by the client for all subsequent
   * API calls until the client resets this connection. Control over commit and rollback of the
   * connection is the responsibility of the client. In order to close the connection,
   * closeConnection() or setConnection(null) has to be called.
   *
   * @param connection - {@linkplain java.sql.Connection Connection} that is controlled by the
   *     client
   * @throws SQLException if a database access error occurs
   */
  void setConnection(java.sql.Connection connection) throws SQLException;

  /**
   * Closes the client's connection, sets it to null and switches to mode PARTICIPATE.
   *
   * <p>Only applicable in mode EXPLICIT. Has the same effect as setConnection(null).
   */
  void closeConnection();

  /**
   * Checks whether the current user is member of one of the roles specified.
   *
   * @param roles the roles that are checked for membership of the current user
   * @return true if the current user is a member of at least one of the specified groups
   */
  boolean isUserInRole(TaskanaRole... roles);

  /**
   * Checks whether current user is member of any of the specified roles.
   *
   * @param roles the roles that are checked for membership of the current user
   * @throws NotAuthorizedException if the current user is not member of any specified role
   */
  void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException;

  /**
   * Returns the {@linkplain CurrentUserContext} class.
   *
   * @return the {@linkplain CurrentUserContext}
   */
  CurrentUserContext getCurrentUserContext();

  /**
   * Connection management mode.
   *
   * <p>Controls the connection handling of Taskana
   *
   * <ul>
   *   <li>PARTICIPATE - Taskana participates in global transaction. This is the default mode.
   *   <li>AUTOCOMMIT - Taskana commits each API call separately
   *   <li>EXPLICIT - commit processing is managed explicitly by the client
   * </ul>
   */
  enum ConnectionManagementMode {
    PARTICIPATE,
    AUTOCOMMIT,
    EXPLICIT
  }
}
