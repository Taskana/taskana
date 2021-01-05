package pro.taskana.common.internal;

import java.sql.SQLException;
import javax.annotation.PostConstruct;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;

import pro.taskana.SpringTaskanaEngineConfiguration;

/** This class configures the TaskanaEngine for spring. */
public class SpringTaskanaEngineImpl extends TaskanaEngineImpl {

  public SpringTaskanaEngineImpl(SpringTaskanaEngineConfiguration taskanaEngineConfiguration)
      throws SQLException {
    super(taskanaEngineConfiguration);
  }

  @PostConstruct
  public void init() {
    this.transactionFactory = new SpringManagedTransactionFactory();
    this.sessionManager = createSqlSessionManager();
  }
}
