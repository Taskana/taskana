package pro.taskana;

import java.sql.SQLException;
import javax.sql.DataSource;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.TaskanaEngine.ConnectionManagementMode;
import pro.taskana.common.internal.SpringTaskanaEngineImpl;

public class SpringTaskanaEngineConfiguration extends TaskanaEngineConfiguration {

  public SpringTaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String schemaName) {
    super(dataSource, useManagedTransactions, securityEnabled, schemaName);
  }

  public SpringTaskanaEngineConfiguration(
      DataSource dataSource,
      boolean useManagedTransactions,
      boolean securityEnabled,
      String propertiesFileName,
      String propertiesSeparator,
      String schemaName) {
    super(
        dataSource,
        useManagedTransactions,
        securityEnabled,
        propertiesFileName,
        propertiesSeparator,
        schemaName);
  }

  /**
   * This method creates the Spring-based TaskanaEngine without an sqlSessionFactory.
   *
   * @return the TaskanaEngine
   */
  @Override
  public TaskanaEngine buildTaskanaEngine() throws SQLException {
    this.useManagedTransactions = true;
    return new SpringTaskanaEngineImpl(this, ConnectionManagementMode.PARTICIPATE);
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
