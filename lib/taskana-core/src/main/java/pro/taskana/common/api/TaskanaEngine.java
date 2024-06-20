package pro.taskana.common.api;

import java.sql.SQLException;
import java.util.function.Supplier;
import org.apache.ibatis.transaction.TransactionFactory;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.workingtime.WorkingTimeCalculatorImpl;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketService;

/** The TaskanaEngine represents an overall set of all needed services. */
public interface TaskanaEngine {
  String MINIMAL_TASKANA_SCHEMA_VERSION = "7.0.0";

  /**
   * Returns a {@linkplain TaskService} initialized with the current TaskanaEngine. {@linkplain
   * TaskService} can be used for operations on all {@linkplain Task Tasks}.
   *
   * @return an instance of {@linkplain TaskService}
   */
  TaskService getTaskService();

  /**
   * Returns a {@linkplain MonitorService} initialized with the current TaskanaEngine. {@linkplain
   * MonitorService} can be used for monitoring {@linkplain Task Tasks}.
   *
   * @return an instance of {@linkplain MonitorService}
   */
  MonitorService getMonitorService();

  /**
   * Returns a {@linkplain WorkbasketService} initialized with the current TaskanaEngine. The
   * {@linkplain WorkbasketService} can be used for operations on all {@linkplain
   * pro.taskana.workbasket.api.models.Workbasket Workbaskets}.
   *
   * @return an instance of {@linkplain WorkbasketService}
   */
  WorkbasketService getWorkbasketService();

  /**
   * Returns a {@linkplain ClassificationService} initialized with the current TaskanaEngine. The
   * {@linkplain ClassificationService} can be used for operations on all {@linkplain
   * pro.taskana.classification.api.models.Classification Classifications}.
   *
   * @return an instance of {@linkplain ClassificationService}
   */
  ClassificationService getClassificationService();

  /**
   * Returns a {@linkplain JobService} initialized with the current TaskanaEngine. The {@linkplain
   * JobService} can be used for all operations on {@linkplain
   * pro.taskana.common.internal.jobs.TaskanaJob TaskanaJobs}.
   *
   * @return an instance of {@linkplain JobService}
   */
  JobService getJobService();

  /**
   * Returns a {@linkplain UserService} initialized with the current TaskanaEngine. The {@linkplain
   * UserService} can be used for all operations on {@linkplain pro.taskana.user.api.models.User
   * Users}.
   *
   * @return an instance of {@linkplain UserService}
   */
  UserService getUserService();

  /**
   * Returns a {@linkplain ConfigurationService} initialized with the current TaskanaEngine. The
   * {@linkplain ConfigurationService} can be used to manage custom configuration options.
   *
   * @return an instance of {@linkplain ConfigurationService}
   */
  ConfigurationService getConfigurationService();

  /**
   * Returns the {@linkplain TaskanaConfiguration configuration} of the TaskanaEngine.
   *
   * @return {@linkplain TaskanaConfiguration configuration}
   */
  TaskanaConfiguration getConfiguration();

  /**
   * This method creates the {@linkplain TaskanaEngine} with {@linkplain
   * ConnectionManagementMode#PARTICIPATE}.
   *
   * @see TaskanaEngine#buildTaskanaEngine(TaskanaConfiguration, ConnectionManagementMode)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  static TaskanaEngine buildTaskanaEngine(TaskanaConfiguration configuration) throws SQLException {
    return buildTaskanaEngine(configuration, ConnectionManagementMode.PARTICIPATE, null);
  }

  /**
   * Builds an {@linkplain TaskanaEngine} based on {@linkplain TaskanaConfiguration} and
   * SqlConnectionMode.
   *
   * @param configuration complete taskanaConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @return a {@linkplain TaskanaEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static TaskanaEngine buildTaskanaEngine(
      TaskanaConfiguration configuration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return buildTaskanaEngine(configuration, connectionManagementMode, null);
  }

  /**
   * Builds an {@linkplain TaskanaEngine} based on {@linkplain TaskanaConfiguration},
   * SqlConnectionMode and TransactionFactory.
   *
   * @param configuration complete taskanaConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @param transactionFactory the TransactionFactory
   * @return a {@linkplain TaskanaEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static TaskanaEngine buildTaskanaEngine(
      TaskanaConfiguration configuration,
      ConnectionManagementMode connectionManagementMode,
      TransactionFactory transactionFactory)
      throws SQLException {
    return TaskanaEngineImpl.createTaskanaEngine(
        configuration, connectionManagementMode, transactionFactory);
  }

  /**
   * Returns the {@linkplain WorkingTimeCalculator} of the TaskanaEngine. The {@linkplain
   * WorkingTimeCalculator} is used to add or subtract working time from Instants according to a
   * working time schedule or to calculate the working time between Instants.
   *
   * @return {@linkplain WorkingTimeCalculatorImpl}
   */
  WorkingTimeCalculator getWorkingTimeCalculator();

  /**
   * Checks if the {@linkplain pro.taskana.spi.history.api.TaskanaHistory TaskanaHistory} plugin is
   * enabled.
   *
   * @return true if the history is enabled; otherwise false
   */
  boolean isHistoryEnabled();

  /**
   * Returns the {@linkplain ConnectionManagementMode ConnectionManagementMode} of the
   * TaskanaEngine.
   *
   * @return {@linkplain ConnectionManagementMode ConnectionManagementMode}
   */
  ConnectionManagementMode getConnectionManagementMode();

  /**
   * Sets {@linkplain ConnectionManagementMode ConnectionManagementMode} of the TaskanaEngine.
   *
   * @param mode the valid values for the {@linkplain ConnectionManagementMode} are:
   *     <ul>
   *       <li>{@linkplain ConnectionManagementMode#PARTICIPATE PARTICIPATE} - taskana participates
   *           in global transaction; this is the default mode
   *       <li>{@linkplain ConnectionManagementMode#AUTOCOMMIT AUTOCOMMIT} - taskana commits each
   *           API call separately
   *       <li>{@linkplain ConnectionManagementMode#EXPLICIT EXPLICIT} - commit processing is
   *           managed explicitly by the client
   *     </ul>
   */
  void setConnectionManagementMode(ConnectionManagementMode mode);

  /**
   * Set the {@code Connection} to be used by TASKANA in mode {@linkplain
   * ConnectionManagementMode#EXPLICIT EXPLICIT}. If this API is called, TASKANA uses the {@code
   * Connection} passed by the client for all subsequent API calls until the client resets this
   * {@code Connection}. Control over commit and rollback of the {@code Connection} is the
   * responsibility of the client. In order to close the {@code Connection}, {@code
   * closeConnection()} or {@code setConnection(null)} has to be called.
   *
   * @param connection - The {@code java.sql.Connection} that is controlled by the client
   * @throws SQLException if a database access error occurs
   */
  void setConnection(java.sql.Connection connection) throws SQLException;

  /**
   * Closes the client's connection, sets it to null and switches to mode {@linkplain
   * ConnectionManagementMode#PARTICIPATE PARTICIPATE}. Only applicable in mode {@linkplain
   * ConnectionManagementMode#EXPLICIT EXPLICIT}. Has the same effect as {@code
   * setConnection(null)}.
   */
  void closeConnection();

  /**
   * Check whether the current user is member of one of the {@linkplain TaskanaRole TaskanaRoles}
   * specified.
   *
   * @param roles The {@linkplain TaskanaRole TaskanaRoles} that are checked for membership of the
   *     current user
   * @return true if the current user is a member of at least one of the specified {@linkplain
   *     TaskanaRole TaskanaRole}
   */
  boolean isUserInRole(TaskanaRole... roles);

  /**
   * Checks whether current user is member of any of the specified {@linkplain TaskanaRole
   * TaskanaRoles}.
   *
   * @param roles The {@linkplain TaskanaRole TaskanaRoles} that are checked for membership of the
   *     current user
   * @throws NotAuthorizedException If the current user is not member of any specified {@linkplain
   *     TaskanaRole TaskanaRole}
   */
  void checkRoleMembership(TaskanaRole... roles) throws NotAuthorizedException;

  /**
   * Executes a given {@code Supplier} with admin privileges and thus skips further permission
   * checks. With great power comes great responsibility.
   *
   * @param supplier will be executed with admin privileges
   * @param <T> defined with the return value of the {@code Supplier}
   * @return output from {@code Supplier}
   */
  <T> T runAsAdmin(Supplier<T> supplier);

  /**
   * Executes a given {@code Runnable} with admin privileges and thus skips further permission
   * checks. With great power comes great responsibility.
   *
   * @see #runAsAdmin(Supplier)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  default void runAsAdmin(Runnable runnable) {
    runAsAdmin(
        () -> {
          runnable.run();
          return null;
        });
  }

  /**
   * Returns the {@linkplain CurrentUserContext} of the TaskanaEngine.
   *
   * @return {@linkplain CurrentUserContext}
   */
  CurrentUserContext getCurrentUserContext();

  /**
   * Connection management mode. Controls the connection handling of taskana
   *
   * <ul>
   *   <li>{@linkplain ConnectionManagementMode#PARTICIPATE PARTICIPATE} - taskana participates * in
   *       global transaction; this is the default mode *
   *   <li>{@linkplain ConnectionManagementMode#AUTOCOMMIT AUTOCOMMIT} - taskana commits each * API
   *       call separately *
   *   <li>{@linkplain ConnectionManagementMode#EXPLICIT EXPLICIT} - commit processing is * managed
   *       explicitly by the client
   * </ul>
   */
  enum ConnectionManagementMode {
    PARTICIPATE,
    AUTOCOMMIT,
    EXPLICIT
  }
}
