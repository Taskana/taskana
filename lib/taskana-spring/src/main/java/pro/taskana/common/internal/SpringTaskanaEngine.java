package pro.taskana.common.internal;

import java.sql.SQLException;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaEngine;

public interface SpringTaskanaEngine extends TaskanaEngine {

  /**
   * This method creates the {@linkplain SpringTaskanaEngine} with {@linkplain
   * ConnectionManagementMode#PARTICIPATE }.
   *
   * @see SpringTaskanaEngine#buildTaskanaEngine(TaskanaConfiguration, ConnectionManagementMode)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  static SpringTaskanaEngine buildTaskanaEngine(TaskanaConfiguration configuration)
      throws SQLException {
    return SpringTaskanaEngine.buildTaskanaEngine(
        configuration, ConnectionManagementMode.PARTICIPATE);
  }

  /**
   * Builds an {@linkplain SpringTaskanaEngine} based on {@linkplain TaskanaConfiguration} and
   * SqlConnectionMode.
   *
   * @param configuration complete taskanaConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @return a {@linkplain SpringTaskanaEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static SpringTaskanaEngine buildTaskanaEngine(
      TaskanaConfiguration configuration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return SpringTaskanaEngineImpl.createTaskanaEngine(configuration, connectionManagementMode);
  }
}
