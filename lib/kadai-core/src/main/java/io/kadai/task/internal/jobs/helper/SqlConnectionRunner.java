package io.kadai.task.internal.jobs.helper;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.exceptions.SystemException;
import io.kadai.common.internal.util.CheckedConsumer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/** Run low level SQL Statements reusing the kadai datasource. */
public class SqlConnectionRunner {

  private final KadaiEngine kadaiEngine;

  public SqlConnectionRunner(KadaiEngine kadaiEngine) {
    this.kadaiEngine = Objects.requireNonNull(kadaiEngine, "KadaiEngine may not be null");
  }

  /**
   * Run custom queries on a given connection. Please check for committing changes.
   *
   * @param consumer consumes a connection.
   * @throws SystemException will pass on any checked SQLException as a runtime SystemException
   */
  public void runWithConnection(CheckedConsumer<Connection, SQLException> consumer) {

    try (Connection connection = getConnection()) {
      connection.setSchema(kadaiEngine.getConfiguration().getSchemaName());
      consumer.accept(connection);
    } catch (SQLException e) {
      throw new SystemException("SQL error while running low level SQL", e);
    }
  }

  public Connection getConnection() throws SQLException {
    return kadaiEngine.getConfiguration().getDataSource().getConnection();
  }
}
