package io.kadai.common.api;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.internal.KadaiEngineImpl;
import io.kadai.common.internal.workingtime.WorkingTimeCalculatorImpl;
import io.kadai.monitor.api.MonitorService;
import io.kadai.task.api.TaskService;
import io.kadai.task.api.models.Task;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketService;
import java.sql.SQLException;
import java.util.function.Supplier;
import org.apache.ibatis.transaction.TransactionFactory;

/** The KadaiEngine represents an overall set of all needed services. */
public interface KadaiEngine {
  String MINIMAL_KADAI_SCHEMA_VERSION = "7.1.0";

  /**
   * Returns a {@linkplain TaskService} initialized with the current KadaiEngine. {@linkplain
   * TaskService} can be used for operations on all {@linkplain Task Tasks}.
   *
   * @return an instance of {@linkplain TaskService}
   */
  TaskService getTaskService();

  /**
   * Returns a {@linkplain MonitorService} initialized with the current KadaiEngine. {@linkplain
   * MonitorService} can be used for monitoring {@linkplain Task Tasks}.
   *
   * @return an instance of {@linkplain MonitorService}
   */
  MonitorService getMonitorService();

  /**
   * Returns a {@linkplain WorkbasketService} initialized with the current KadaiEngine. The
   * {@linkplain WorkbasketService} can be used for operations on all {@linkplain
   * io.kadai.workbasket.api.models.Workbasket Workbaskets}.
   *
   * @return an instance of {@linkplain WorkbasketService}
   */
  WorkbasketService getWorkbasketService();

  /**
   * Returns a {@linkplain ClassificationService} initialized with the current KadaiEngine. The
   * {@linkplain ClassificationService} can be used for operations on all {@linkplain
   * io.kadai.classification.api.models.Classification Classifications}.
   *
   * @return an instance of {@linkplain ClassificationService}
   */
  ClassificationService getClassificationService();

  /**
   * Returns a {@linkplain JobService} initialized with the current KadaiEngine. The {@linkplain
   * JobService} can be used for all operations on {@linkplain
   * io.kadai.common.internal.jobs.KadaiJob KadaiJobs}.
   *
   * @return an instance of {@linkplain JobService}
   */
  JobService getJobService();

  /**
   * Returns a {@linkplain UserService} initialized with the current KadaiEngine. The {@linkplain
   * UserService} can be used for all operations on {@linkplain io.kadai.user.api.models.User
   * Users}.
   *
   * @return an instance of {@linkplain UserService}
   */
  UserService getUserService();

  /**
   * Returns a {@linkplain ConfigurationService} initialized with the current KadaiEngine. The
   * {@linkplain ConfigurationService} can be used to manage custom configuration options.
   *
   * @return an instance of {@linkplain ConfigurationService}
   */
  ConfigurationService getConfigurationService();

  /**
   * Returns the {@linkplain KadaiConfiguration configuration} of the KadaiEngine.
   *
   * @return {@linkplain KadaiConfiguration configuration}
   */
  KadaiConfiguration getConfiguration();

  /**
   * This method creates the {@linkplain KadaiEngine} with {@linkplain
   * ConnectionManagementMode#PARTICIPATE}.
   *
   * @see KadaiEngine#buildKadaiEngine(KadaiConfiguration, ConnectionManagementMode)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  static KadaiEngine buildKadaiEngine(KadaiConfiguration configuration) throws SQLException {
    return buildKadaiEngine(configuration, ConnectionManagementMode.PARTICIPATE, null);
  }

  /**
   * Builds an {@linkplain KadaiEngine} based on {@linkplain KadaiConfiguration} and
   * SqlConnectionMode.
   *
   * @param configuration complete kadaiConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @return a {@linkplain KadaiEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static KadaiEngine buildKadaiEngine(
      KadaiConfiguration configuration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return buildKadaiEngine(configuration, connectionManagementMode, null);
  }

  /**
   * Builds an {@linkplain KadaiEngine} based on {@linkplain KadaiConfiguration}, SqlConnectionMode
   * and TransactionFactory.
   *
   * @param configuration complete kadaiConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @param transactionFactory the TransactionFactory
   * @return a {@linkplain KadaiEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static KadaiEngine buildKadaiEngine(
      KadaiConfiguration configuration,
      ConnectionManagementMode connectionManagementMode,
      TransactionFactory transactionFactory)
      throws SQLException {
    return KadaiEngineImpl.createKadaiEngine(
        configuration, connectionManagementMode, transactionFactory);
  }

  /**
   * Returns the {@linkplain WorkingTimeCalculator} of the KadaiEngine. The {@linkplain
   * WorkingTimeCalculator} is used to add or subtract working time from Instants according to a
   * working time schedule or to calculate the working time between Instants.
   *
   * @return {@linkplain WorkingTimeCalculatorImpl}
   */
  WorkingTimeCalculator getWorkingTimeCalculator();

  /**
   * Checks if the {@linkplain io.kadai.spi.history.api.KadaiHistory KadaiHistory} plugin is
   * enabled.
   *
   * @return true if the history is enabled; otherwise false
   */
  boolean isHistoryEnabled();

  /**
   * Returns the {@linkplain ConnectionManagementMode ConnectionManagementMode} of the KadaiEngine.
   *
   * @return {@linkplain ConnectionManagementMode ConnectionManagementMode}
   */
  ConnectionManagementMode getConnectionManagementMode();

  /**
   * Sets {@linkplain ConnectionManagementMode ConnectionManagementMode} of the KadaiEngine.
   *
   * @param mode the valid values for the {@linkplain ConnectionManagementMode} are:
   *     <ul>
   *       <li>{@linkplain ConnectionManagementMode#PARTICIPATE PARTICIPATE} - kadai participates in
   *           global transaction; this is the default mode
   *       <li>{@linkplain ConnectionManagementMode#AUTOCOMMIT AUTOCOMMIT} - kadai commits each API
   *           call separately
   *       <li>{@linkplain ConnectionManagementMode#EXPLICIT EXPLICIT} - commit processing is
   *           managed explicitly by the client
   *     </ul>
   */
  void setConnectionManagementMode(ConnectionManagementMode mode);

  /**
   * Set the {@code Connection} to be used by KADAI in mode {@linkplain
   * ConnectionManagementMode#EXPLICIT EXPLICIT}. If this API is called, KADAI uses the {@code
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
   * Check whether the current user is member of one of the {@linkplain KadaiRole KadaiRoles}
   * specified.
   *
   * @param roles The {@linkplain KadaiRole KadaiRoles} that are checked for membership of the
   *     current user
   * @return true if the current user is a member of at least one of the specified {@linkplain
   *     KadaiRole KadaiRole}
   */
  boolean isUserInRole(KadaiRole... roles);

  /**
   * Checks whether current user is member of any of the specified {@linkplain KadaiRole
   * KadaiRoles}.
   *
   * @param roles The {@linkplain KadaiRole KadaiRoles} that are checked for membership of the
   *     current user
   * @throws NotAuthorizedException If the current user is not member of any specified {@linkplain
   *     KadaiRole KadaiRole}
   */
  void checkRoleMembership(KadaiRole... roles) throws NotAuthorizedException;

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
   * Returns the {@linkplain CurrentUserContext} of the KadaiEngine.
   *
   * @return {@linkplain CurrentUserContext}
   */
  CurrentUserContext getCurrentUserContext();

  /** Clears the cache of the underlying local SQL session. */
  void clearSqlSessionCache();

  /**
   * Connection management mode. Controls the connection handling of kadai
   *
   * <ul>
   *   <li>{@linkplain ConnectionManagementMode#PARTICIPATE PARTICIPATE} - kadai participates * in
   *       global transaction; this is the default mode *
   *   <li>{@linkplain ConnectionManagementMode#AUTOCOMMIT AUTOCOMMIT} - kadai commits each * API
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
