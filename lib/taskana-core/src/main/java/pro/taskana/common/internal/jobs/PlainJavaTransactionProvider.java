package pro.taskana.common.internal.jobs;

import java.sql.SQLException;
import java.util.function.Supplier;
import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSession;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.transaction.TaskanaTransactionProvider;

/**
 * This implementation of {@linkplain TaskanaTransactionProvider} is used when executing our
 * background jobs. Use this class together with {@linkplain JobRunner}.
 */
public class PlainJavaTransactionProvider implements TaskanaTransactionProvider {

  private final TaskanaEngine taskanaEngine;
  private final SqlSession sqlSession;
  private final DataSource dataSource;
  private final ConnectionManagementMode defaultConnectionManagementMode;

  public PlainJavaTransactionProvider(TaskanaEngine taskanaEngine, DataSource dataSource) {
    this.taskanaEngine = taskanaEngine;
    this.dataSource = dataSource;
    sqlSession = taskanaEngine.getSqlSession();
    defaultConnectionManagementMode = taskanaEngine.getConnectionManagementMode();
  }

  @Override
  public <T> T executeInTransaction(Supplier<T> supplier) {
    try {
      taskanaEngine.setConnection(dataSource.getConnection());
      final T t = supplier.get();
      sqlSession.commit();
      taskanaEngine.closeConnection();
      taskanaEngine.setConnectionManagementMode(defaultConnectionManagementMode);
      return t;
    } catch (SQLException e) {
      throw new SystemException("caught exception", e);
    }
  }
}
