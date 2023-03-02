package pro.taskana.common.internal;

import java.sql.SQLException;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import pro.taskana.TaskanaConfiguration;

/** This class configures the TaskanaEngine for spring. */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl implements SpringTaskanaEngine {

  public SpringTaskanaEngineImpl(
      TaskanaConfiguration taskanaEngineConfiguration, ConnectionManagementMode mode)
      throws SQLException {
    super(taskanaEngineConfiguration, mode, true);
    this.transactionFactory = new SpringManagedTransactionFactory();
    this.sessionManager = createSqlSessionManager();
  }

  public static SpringTaskanaEngine createTaskanaEngine(
      TaskanaConfiguration taskanaEngineConfiguration,
      ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return new SpringTaskanaEngineImpl(taskanaEngineConfiguration, connectionManagementMode);
  }
}
