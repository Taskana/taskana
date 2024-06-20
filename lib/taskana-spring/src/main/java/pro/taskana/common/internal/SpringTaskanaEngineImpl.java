package pro.taskana.common.internal;

import java.sql.SQLException;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import pro.taskana.TaskanaConfiguration;

/** This class configures the TaskanaEngine for spring. */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl implements SpringTaskanaEngine {

  public SpringTaskanaEngineImpl(
      TaskanaConfiguration taskanaConfiguration, ConnectionManagementMode mode)
      throws SQLException {
    super(taskanaConfiguration, mode, new SpringManagedTransactionFactory());
  }

  public static SpringTaskanaEngine createTaskanaEngine(
      TaskanaConfiguration taskanaConfiguration, ConnectionManagementMode connectionManagementMode)
      throws SQLException {
    return new SpringTaskanaEngineImpl(taskanaConfiguration, connectionManagementMode);
  }
}
