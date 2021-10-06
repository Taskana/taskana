package pro.taskana.task.internal.jobs.helper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.sql.DataSource;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.util.CheckedConsumer;

/** Run low level SQL Statements reusing the taskana datasource. */
public class SqlConnectionRunner {

  private final DataSource dataSource;

  public SqlConnectionRunner(TaskanaEngine taskanaEngine) {
    this(
        Objects.requireNonNull(taskanaEngine, "Taskana engine may not be null")
            .getConfiguration()
            .getDatasource());
  }

  public SqlConnectionRunner(DataSource dataSource) {
    this.dataSource = Objects.requireNonNull(dataSource, "Datasource may not be null.");
  }

  /**
   * Run custom queries on a given connection. Please check for committing changes.
   *
   * @param consumer consumes a connection.
   * @throws SystemException will pass on any checked SQLException as a runtime SystemException
   */
  public void runWithConnection(CheckedConsumer<Connection, SQLException> consumer) {
    try (Connection connection = getConnection()) {
      consumer.accept(connection);
    } catch (SQLException e) {
      throw new SystemException("SQL error while running low level SQL", e);
    }
  }

  public Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }
}
