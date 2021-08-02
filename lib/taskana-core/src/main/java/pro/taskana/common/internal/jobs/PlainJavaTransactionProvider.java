package pro.taskana.common.internal.jobs;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

public class PlainJavaTransactionProvider implements TaskanaTransactionProvider {

  private final TaskanaEngine taskanaEngine;
  private final DataSource dataSource;
  private final ConnectionManagementMode defaultConnectionManagementMode;

  public PlainJavaTransactionProvider(TaskanaEngine taskanaEngine, DataSource dataSource) {
    this.taskanaEngine = taskanaEngine;
    this.dataSource = dataSource;
    defaultConnectionManagementMode = taskanaEngine.getConnectionManagementMode();
  }

  @Override
  public <T> T executeInTransaction(Supplier<T> supplier) {
    try (Connection connection = dataSource.getConnection()) {
      taskanaEngine.setConnection(connection);
      final T t = supplier.get();
      connection.commit();
      return t;
    } catch (SQLException ex) {
      throw new SystemException("caught exception", ex);
    } finally {
      taskanaEngine.closeConnection();
      taskanaEngine.setConnectionManagementMode(defaultConnectionManagementMode);
    }
  }
}
