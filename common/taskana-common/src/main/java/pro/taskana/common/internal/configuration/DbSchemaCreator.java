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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;

import pro.taskana.common.internal.util.ComparableVersion;

/** This class create the schema for taskana. */
@Slf4j
public class DbSchemaCreator {

  private static final String SQL = "/sql";

  private static final String DB_SCHEMA_H2 = SQL + "/h2/taskana-schema-h2.sql";
  private static final String DB_SCHEMA_DETECTION_H2 = SQL + "/h2/schema-detection-h2.sql";
  private static final String DB_SCHEMA_DB2 = SQL + "/db2/taskana-schema-db2.sql";
  private static final String DB_SCHEMA_DETECTION_DB2 = SQL + "/db2/schema-detection-db2.sql";
  private static final String DB_SCHEMA_ORACLE = SQL + "/oracle/taskana-schema-oracle.sql";
  private static final String DB_SCHEMA_DETECTION_ORACLE =
      SQL + "/oracle/schema-detection-oracle.sql";
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
    this.dataSource = dataSource;
    this.schemaName = schema;
  }

  /**
   * Run all db scripts.
   *
   * @return true when schema was created, false when no schema created because already existing
   * @throws SQLException will be thrown if there will be some incorrect SQL statements invoked.
   */
  public boolean run() throws SQLException {
    try (Connection connection = dataSource.getConnection()) {
      if (log.isDebugEnabled()) {
        log.debug(
            "Using database of type {} with url '{}'",
            DB.getDatabaseProductName(connection),
            connection.getMetaData().getURL());
      }
      String dbProductId = DB.getDatabaseProductId(connection);

      ScriptRunner runner = getScriptRunnerInstance(connection);

      if (!isSchemaPreexisting(connection, dbProductId)) {
        String scriptPath = selectDbScriptFileName(dbProductId);
        InputStream resourceAsStream = DbSchemaCreator.class.getResourceAsStream(scriptPath);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8));
        runner.runScript(getSqlSchemaNameParsed(reader));
        return true;
      }
    }
    if (log.isDebugEnabled()) {
      log.debug(outWriter.toString());
    }
    if (!errorWriter.toString().trim().isEmpty()) {
      log.error(errorWriter.toString());
    }
    return false;
  }

  public boolean isValidSchemaVersion(String expectedMinVersion) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setSchema(this.schemaName);
      SqlRunner runner = new SqlRunner(connection);
      if (log.isDebugEnabled()) {
        log.debug("{}", connection.getMetaData());
      }

      String query =
          "select VERSION from TASKANA_SCHEMA_VERSION where "
              + "VERSION = (select max(VERSION) from TASKANA_SCHEMA_VERSION) ";

      Map<String, Object> queryResult = runner.selectOne(query);

      ComparableVersion actualVersion = ComparableVersion.of((String) queryResult.get("VERSION"));
      ComparableVersion minVersion = ComparableVersion.of(expectedMinVersion);

      if (actualVersion.compareTo(minVersion) < 0) {
        log.error(
            "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION "
                + "has not the expected min value {}",
            expectedMinVersion);
        return false;
      } else {
        if (log.isDebugEnabled()) {
          log.debug("Schema version is valid.");
        }
        return true;
      }

    } catch (RuntimeSqlException | SQLException e) {
      log.error(
          "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION "
              + "has not the expected min value {}",
          expectedMinVersion);
      return false;
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public void setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  private static String selectDbScriptFileName(String dbProductId) {

    switch (DB.getDbForId(dbProductId)) {
      case DB2:
        return DB_SCHEMA_DB2;
      case ORACLE:
        return DB_SCHEMA_ORACLE;
      case POSTGRES:
        return DB_SCHEMA_POSTGRES;
      default:
        return DB_SCHEMA_H2;
    }
  }

  private static String selectDbSchemaDetectionScript(String dbProductId) {

    switch (DB.getDbForId(dbProductId)) {
      case DB2:
        return DB_SCHEMA_DETECTION_DB2;
      case ORACLE:
        return DB_SCHEMA_DETECTION_ORACLE;
      case POSTGRES:
        return DB_SCHEMA_DETECTION_POSTGRES;
      default:
        return DB_SCHEMA_DETECTION_H2;
    }
  }

  private ScriptRunner getScriptRunnerInstance(Connection connection) {
    ScriptRunner runner = new ScriptRunner(connection);
    runner.setStopOnError(true);
    runner.setLogWriter(logWriter);
    runner.setErrorLogWriter(errorLogWriter);
    return runner;
  }

  private boolean isSchemaPreexisting(Connection connection, String dbProductId) {
    ScriptRunner runner = getScriptRunnerInstance(connection);
    runner.setErrorLogWriter(errorLogWriter);

    String scriptPath = selectDbSchemaDetectionScript(dbProductId);
    try (InputStream resource = DbSchemaCreator.class.getResourceAsStream(scriptPath);
        InputStreamReader inputReader = new InputStreamReader(resource, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(inputReader)) {
      runner.runScript(getSqlSchemaNameParsed(reader));
    } catch (RuntimeSqlException | IOException e) {
      if (log.isDebugEnabled()) {
        log.debug("Schema does not exist.");
        if (!errorWriter.toString().trim().isEmpty()) {
          log.debug(errorWriter.toString());
        }
      }
      return false;
    }
    if (log.isDebugEnabled()) {
      log.debug("Schema does exist.");
    }
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
      log.error("SchemaName sql parsing failed for schemaName {}", schemaName, e);
    }
    return new StringReader(content.toString());
  }
}
