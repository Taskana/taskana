package io.kadai.common.internal;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiEngine;
import java.sql.SQLException;

public interface SpringKadaiEngine extends KadaiEngine {

  /**
   * This method creates the {@linkplain SpringKadaiEngine} with {@linkplain
   * ConnectionManagementMode#PARTICIPATE }.
   *
   * @see SpringKadaiEngine#buildKadaiEngine(KadaiConfiguration, ConnectionManagementMode)
   */
  @SuppressWarnings("checkstyle:JavadocMethod")
  static SpringKadaiEngine buildKadaiEngine(KadaiConfiguration configuration) throws SQLException {
    return SpringKadaiEngine.buildKadaiEngine(configuration, ConnectionManagementMode.PARTICIPATE);
  }

  /**
   * Builds an {@linkplain SpringKadaiEngine} based on {@linkplain KadaiConfiguration} and
   * SqlConnectionMode.
   *
   * @param configuration complete kadaiConfig to build the engine
   * @param connectionManagementMode connectionMode for the SqlSession
   * @return a {@linkplain SpringKadaiEngineImpl}
   * @throws SQLException when the db schema could not be initialized
   */
  static SpringKadaiEngine buildKadaiEngine(
      KadaiConfiguration configuration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return SpringKadaiEngineImpl.createKadaiEngine(configuration, connectionManagementMode);
  }
}
