package pro.taskana.common.internal.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class create the schema for taskana. */
public class DbSchemaCreator {

  private static final Logger LOGGER = LoggerFactory.getLogger(DbSchemaCreator.class);
  private static final String SQL = "/sql";

  private static final String DB_SCHEMA_H2 = SQL + "/h2/taskana-schema-h2.sql";
  private static final String DB_SCHEMA_DETECTION_H2 = SQL + "/h2/schema-detection-h2.sql";

  private static final String DB_SCHEMA_DB2 = SQL + "/db2/taskana-schema-db2.sql";
  private static final String DB_SCHEMA_DETECTION_DB2 = SQL + "/db2/schema-detection-db2.sql";

  private static final String DB_SCHEMA_POSTGRES = SQL + "/postgres/taskana-schema-postgres.sql";
  private static final String DB_SCHEMA_DETECTION_POSTGRES =
      SQL + "/postgres/schema-detection-postgres.sql";

  private final String schemaName;
  private final StringWriter outWriter = new StringWriter();
  private final PrintWriter logWriter = new PrintWriter(outWriter);
  private final StringWriter errorWriter = new StringWriter();
  private final PrintWriter errorLogWriter = new PrintWriter(errorWriter);
  private DataSource dataSource;

  public DbSchemaCreator(DataSource dataSource, String schema) {
    super();
    this.dataSource = dataSource;
    this.schemaName = schema;
  }

  /**
   * Run all db scripts.
   *
   * @throws SQLException will be thrown if there will be some incorrect SQL statements invoked.
   */
  public void run() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      LOGGER.debug(
          "Using database of type {} with url '{}'",
          connection.getMetaData().getDatabaseProductName(),
          connection.getMetaData().getURL());
      ScriptRunner runner = getScriptRunnerInstance(connection);

      if (!isSchemaPreexisting(connection)) {
        String scriptPath =
            selectDbScriptFileName(connection.getMetaData().getDatabaseProductName());
        InputStream resourceAsStream = DbSchemaCreator.class.getResourceAsStream(scriptPath);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
        runner.runScript(getSqlSchemaNameParsed(reader));
      }
    }
    LOGGER.debug(outWriter.toString());
    if (!errorWriter.toString().trim().isEmpty()) {
      LOGGER.error(errorWriter.toString());
    }
  }

  public boolean isValidSchemaVersion(String expectedVersion) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setSchema(this.schemaName);
      SqlRunner runner = new SqlRunner(connection);
      LOGGER.debug(connection.getMetaData().toString());

      String query =
          "select VERSION from TASKANA_SCHEMA_VERSION where "
              + "VERSION = (select max(VERSION) from TASKANA_SCHEMA_VERSION) "
              + "AND VERSION = ?";

      Map<String, Object> queryResult = runner.selectOne(query, expectedVersion);
      if (queryResult == null || queryResult.isEmpty()) {
        LOGGER.error(
            "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION "
                + "has not the expected value {}",
            expectedVersion);
        return false;
      } else {
        LOGGER.debug("Schema version is valid.");
        return true;
      }

    } catch (RuntimeSqlException | SQLException e) {
      LOGGER.error(
          "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION "
              + "has not the expected value {}",
          expectedVersion);
      return false;
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private static String selectDbScriptFileName(String dbProductName) {
    return DB.isPostgreSql(dbProductName)
        ? DB_SCHEMA_POSTGRES
        : DB.isH2(dbProductName) ? DB_SCHEMA_H2 : DB_SCHEMA_DB2;
  }

  private static String selectDbSchemaDetectionScript(String dbProductName) {

    if (DB.isPostgreSql(dbProductName)) {
      return DB_SCHEMA_DETECTION_POSTGRES;
    }
    return DB.isH2(dbProductName) ? DB_SCHEMA_DETECTION_H2 : DB_SCHEMA_DETECTION_DB2;
  }

  private ScriptRunner getScriptRunnerInstance(Connection connection) {
    ScriptRunner runner = new ScriptRunner(connection);
    runner.setStopOnError(true);
    runner.setLogWriter(logWriter);
    runner.setErrorLogWriter(errorLogWriter);
    return runner;
  }

  private boolean isSchemaPreexisting(Connection connection) {
    ScriptRunner runner = getScriptRunnerInstance(connection);
    StringWriter errorWriter = new StringWriter();
    runner.setErrorLogWriter(new PrintWriter(errorWriter));
    try {
      String scriptPath =
          selectDbSchemaDetectionScript(connection.getMetaData().getDatabaseProductName());
      InputStream resourceAsStream = DbSchemaCreator.class.getResourceAsStream(scriptPath);
      BufferedReader reader =
          new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
      runner.runScript(getSqlSchemaNameParsed(reader));
    } catch (RuntimeSqlException | SQLException e) {
      LOGGER.debug("Schema does not exist.");
      if (!errorWriter.toString().trim().isEmpty()) {
        LOGGER.debug(errorWriter.toString());
      }
      return false;
    }
    LOGGER.debug("Schema does exist.");
    return true;
  }

  private StringReader getSqlSchemaNameParsed(BufferedReader reader) {

    StringBuilder content = new StringBuilder();
    try {
      String line = "";
      while (line != null) {
        line = reader.readLine();
        if (line != null) {
          content.append(line.replace("%schemaName%", schemaName)).append(System.lineSeparator());
        }
      }
    } catch (IOException e) {
      LOGGER.error("SchemaName sql parsing failed for schemaName {}", schemaName, e);
    }
    return new StringReader(content.toString());
  }
}
