package io.kadai.common.internal;

import io.kadai.KadaiConfiguration;
import java.sql.SQLException;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

/** This class configures the KadaiEngine for spring. */
public class SpringKadaiEngineImpl extends KadaiEngineImpl implements SpringKadaiEngine {

  public SpringKadaiEngineImpl(KadaiConfiguration kadaiConfiguration, ConnectionManagementMode mode)
      throws SQLException {
    super(kadaiConfiguration, mode, new SpringManagedTransactionFactory());
  }

  public static SpringKadaiEngine createKadaiEngine(
      KadaiConfiguration kadaiConfiguration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return new SpringKadaiEngineImpl(kadaiConfiguration, connectionManagementMode);
  }
}
