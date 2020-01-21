package configuration;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Set up the database's writer and generates data for tests. */
public class DbWriter {

  private static final Logger LOGGER = LoggerFactory.getLogger(DbWriter.class);
  private static final String INSERTVALUES = "/sql/history-events.sql";
  private StringWriter outWriter = new StringWriter();
  private PrintWriter logWriter;
  private StringWriter errorWriter;
  private PrintWriter errorLogWriter;

  public DbWriter() {
    this.logWriter = new PrintWriter(this.outWriter);
    this.errorWriter = new StringWriter();
    this.errorLogWriter = new PrintWriter(this.errorWriter);
  }

  public void generateTestData(DataSource dataSource) throws SQLException {
    ScriptRunner runner = null;
    try (Connection connection = dataSource.getConnection()) {
      runner = configScriptRunner(connection);
      runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(INSERTVALUES)));
    } finally {
      LOGGER.debug(outWriter.toString());
      if (!errorWriter.toString().trim().isEmpty()) {
        LOGGER.error(errorWriter.toString());
      }
    }
  }

  public void clearDB(DataSource dataSource) throws SQLException {
    ScriptRunner runner = null;
    try (Connection connection = dataSource.getConnection()) {
      runner = configScriptRunner(connection);
      runner.runScript(new StringReader("DELETE FROM HISTORY_EVENTS;"));
    } finally {
      LOGGER.debug(outWriter.toString());
      if (!errorWriter.toString().trim().isEmpty()) {
        LOGGER.error(errorWriter.toString());
      }
    }
  }

  private ScriptRunner configScriptRunner(Connection connection) throws SQLException {
    LOGGER.debug(connection.getMetaData().toString());
    ScriptRunner runner = new ScriptRunner(connection);
    runner.setStopOnError(true);
    runner.setLogWriter(this.logWriter);
    runner.setErrorLogWriter(this.errorLogWriter);
    runner.setStopOnError(true);
    runner.setLogWriter(this.logWriter);
    runner.setErrorLogWriter(this.errorLogWriter);
    return runner;
  }
}
