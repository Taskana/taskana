package pro.taskana.sampledata;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This class generates sample data for manual testing purposes. */
public class SampleDataGenerator {

  private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataGenerator.class);

  private static final String CACHED_TEST = "TEST";
  private static final String CACHED_SAMPLE = "SAMPLE";
  private static final String CACHED_EVENTSAMPLE = "EVENTSAMPLE";
  private static final String CACHED_MONITOR = "MONITOR";
  private static final String CACHED_CLEARDB = "CLEARDB";
  private static final String CACHED_DROPDB = "DROPDB";
  private static HashMap<String, List<String>> cachedScripts = new HashMap<>();
  private final DataSource dataSource;
  private final ZonedDateTime now;
  /**
   * This value cannot be automatically obtained by connection.getSchema(), because setting not yet
   * existing schema will result into an SQL Exception.
   */
  private final String schema;

  public SampleDataGenerator(DataSource dataSource, String schema) {
    this(dataSource, schema, Instant.now().atZone(ZoneId.of("UTC")));
  }

  public SampleDataGenerator(DataSource dataSource, String schema, ZonedDateTime now) {
    this.dataSource = dataSource;
    this.schema = schema;
    this.now = now;
  }

  public void generateSampleData() {
    LOGGER.debug("entry to generateSampleData()");
    runScripts(
        (runner) -> {
          clearDb();
          Stream<String> scripts;
          String cacheKey;
          // dbtable constants?
          if (tableExists("TASK_HISTORY_EVENT")) {
            scripts = SampleDataProvider.getScriptsWithEvents();
            cacheKey = CACHED_EVENTSAMPLE;
          } else {
            scripts = SampleDataProvider.getSampleDataCreationScripts();
            cacheKey = CACHED_SAMPLE;
          }
          executeAndCacheScripts(scripts, cacheKey);
        });
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("exit from generateSampleData()");
    }
  }

  public void generateTestData() {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("entry to generateTestData()");
    }
    Stream<String> scripts = SampleDataProvider.getTestDataScripts();
    executeAndCacheScripts(scripts, CACHED_TEST);
    LOGGER.debug("exit from generateTestData()");
  }

  public void generateMonitorData() {
    LOGGER.debug("entry to generateMonitorData()");
    Stream<String> scripts = SampleDataProvider.getMonitorDataScripts();
    executeAndCacheScripts(scripts, CACHED_MONITOR);
    LOGGER.debug("exit from generateMonitorData()");
  }

  public void clearDb() {
    LOGGER.debug("entry to clearDb()");
    Stream<String> scripts = SampleDataProvider.getScriptsToClearDatabase();
    executeAndCacheScripts(scripts, CACHED_CLEARDB);
    LOGGER.debug("exit from clearDb()");
  }

  public void dropDb() {
    LOGGER.debug("entry to dropDb()");
    Stream<String> scripts = SampleDataProvider.getScriptsToDropDatabase();
    executeAndCacheScripts(scripts, CACHED_DROPDB);
    LOGGER.debug("exit from dropDb()");
  }

  boolean tableExists(String table) {
    try (Connection connection = dataSource.getConnection()) {
      connection.setSchema(schema);
      SqlRunner runner = new SqlRunner(connection);
      String tableSafe = SqlReplacer.getSanitizedTableName(table);
      String query = "SELECT 1 FROM " + tableSafe + " LIMIT 1;";
      runner.run(query);
      return true;
    } catch (RuntimeSqlException | SQLException e) {
      return false;
    }
  }

  private List<String> parseScripts(Stream<String> scripts) {
    try (Connection connection = dataSource.getConnection()) {
      String dbProductName = connection.getMetaData().getDatabaseProductName();
      return scripts
          .map(script -> SqlReplacer.getScriptAsSql(dbProductName, now, script))
          .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new RuntimeSqlException("Connection to database failed.", e);
    }
  }

  private void runScripts(Consumer<ScriptRunner> consumer) {
    try (Connection connection = dataSource.getConnection()) {
      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(connection.getMetaData().toString());
      }

      StringWriter outWriter = new StringWriter();
      StringWriter errorWriter = new StringWriter();

      ScriptRunner runner = getScriptRunner(connection, outWriter, errorWriter);
      consumer.accept(runner);

      if (LOGGER.isTraceEnabled()) {
        LOGGER.trace(outWriter.toString());
        String trimmedErrorString = errorWriter.toString().trim();
        if (!trimmedErrorString.isEmpty()) {
          LOGGER.error(trimmedErrorString);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeSqlException("Failed to execute script.", e);
    }
  }

  private void executeAndCacheScripts(Stream<String> scripts, String cacheKey) {
    LOGGER.debug("entry to executeAndCacheScripts(scripts = {}, cacheKey = {})", scripts, cacheKey);
    runScripts(
        runner ->
            cachedScripts.computeIfAbsent(cacheKey, key -> parseScripts(scripts)).stream()
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .map(ByteArrayInputStream::new)
                .map(s -> new InputStreamReader(s, StandardCharsets.UTF_8))
                .forEach(runner::runScript));
    LOGGER.debug("exit from executeAndCacheScripts()");
  }

  private ScriptRunner getScriptRunner(
      Connection connection, StringWriter outWriter, StringWriter errorWriter) throws SQLException {

    PrintWriter logWriter = new PrintWriter(outWriter);
    PrintWriter errorLogWriter = new PrintWriter(errorWriter);
    ScriptRunner runner = new ScriptRunner(connection);

    connection.setSchema(schema);
    runner.setLogWriter(logWriter);
    runner.setErrorLogWriter(errorLogWriter);
    return runner;
  }
}
