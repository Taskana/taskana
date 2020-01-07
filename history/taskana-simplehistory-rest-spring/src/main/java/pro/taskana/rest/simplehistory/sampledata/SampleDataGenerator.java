package pro.taskana.rest.simplehistory.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.DB;

/** This class generates sample data for manual testing purposes. */
public class SampleDataGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataGenerator.class);
  private static final String TEST_DATA = "/sql.sample-data";
  private static final String CLEAR = TEST_DATA + "/clear-db.sql";
  private static final String HISTORY_EVENT = TEST_DATA + "/history-event.sql";
  DataSource dataSource;
  String dbProductName;
  private ScriptRunner runner;

  public SampleDataGenerator(DataSource dataSource) throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      dbProductName = connection.getMetaData().getDatabaseProductName();
      if (LOGGER.isTraceEnabled()) {
        String msg = connection.getMetaData().toString();
        LOGGER.trace(msg);
      }
    }
    this.dataSource = dataSource;

    runner = new ScriptRunner(dataSource.getConnection());
  }

  public void generateSampleData(String schemaName) {
    StringWriter outWriter = new StringWriter();
    PrintWriter logWriter = new PrintWriter(outWriter);

    StringWriter errorWriter = new StringWriter();
    PrintWriter errorLogWriter = new PrintWriter(errorWriter);
    try {
      runner.runScript(selectSchemaScript(dbProductName, schemaName));
      runner.setStopOnError(false);
      runner.runScript(
          new BufferedReader(
              new InputStreamReader(
                  this.getClass().getResourceAsStream(CLEAR), StandardCharsets.UTF_8)));
    } catch (Exception e) {
      LOGGER.error("caught Exception {}", e, e);
    }

    runner.setStopOnError(true);
    runner.setLogWriter(logWriter);
    runner.setErrorLogWriter(errorLogWriter);

    runner.runScript(
        new BufferedReader(
            new InputStreamReader(
                this.getClass().getResourceAsStream(HISTORY_EVENT), StandardCharsets.UTF_8)));

    runner.closeConnection();

    LOGGER.trace(outWriter.toString());
    if (!errorWriter.toString().trim().isEmpty()) {
      LOGGER.error(errorWriter.toString());
    }
  }

  private StringReader selectSchemaScript(String dbProductName, String schemaName) {
    return new StringReader(
        DB.isPostgreSQL(dbProductName)
            ? "SET search_path TO " + schemaName + ";"
            : "SET SCHEMA " + schemaName + ";");
  }
}
