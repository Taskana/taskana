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
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.RuntimeSqlException;
import org.apache.ibatis.jdbc.ScriptRunner;

import pro.taskana.common.internal.configuration.DB;

/** This class generates sample data for manual testing purposes. */
@Slf4j
public class SampleDataGenerator {

  private static final String CACHED_TEST = "TEST";
  private static final String CACHED_SAMPLE = "SAMPLE";
  private static final String CACHED_MONITOR = "MONITOR";
  private static final String CACHED_CLEAR_DB = "CLEARDB";
  private static final String CACHED_DROP_DB = "DROP_DB";
  private static final HashMap<String, List<String>> CACHED_SCRIPTS = new HashMap<>();
  private final DataSource dataSource;
  private final ZonedDateTime now;
  // This value cannot be automatically obtained by connection.getSchema(), because setting not yet
  // existing schema will result into an SQL Exception.
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
    clearDb();
    Stream<String> scripts = SampleDataProvider.getSampleDataCreationScripts();
    executeAndCacheScripts(scripts, CACHED_SAMPLE);
  }

  public void generateTestData() {
    Stream<String> scripts = SampleDataProvider.getTestDataScripts();
    executeAndCacheScripts(scripts, CACHED_TEST);
  }

  public void generateMonitorData() {
    Stream<String> scripts = SampleDataProvider.getMonitorDataScripts();
    executeAndCacheScripts(scripts, CACHED_MONITOR);
  }

  public void clearDb() {
    Stream<String> scripts = SampleDataProvider.getScriptsToClearDatabase();
    executeAndCacheScripts(scripts, CACHED_CLEAR_DB);
  }

  public void dropDb() {
    Stream<String> scripts = SampleDataProvider.getScriptsToDropDatabase();
    executeAndCacheScripts(scripts, CACHED_DROP_DB);
  }

  private List<String> parseScripts(Stream<String> scripts) {
    try (Connection connection = dataSource.getConnection()) {
      String dbProductId = DB.getDatabaseProductId(connection);
      return scripts
          .map(script -> SqlReplacer.getScriptAsSql(dbProductId, now, script))
          .collect(Collectors.toList());
    } catch (SQLException e) {
      throw new RuntimeSqlException("Connection to database failed.", e);
    }
  }

  private void runScripts(Consumer<ScriptRunner> consumer) {
    try (Connection connection = dataSource.getConnection()) {
      if (log.isDebugEnabled()) {
        log.debug(
            "Generating sample data for database of type '{}' with url '{}' and schema '{}'.",
            DB.getDatabaseProductName(connection),
            connection.getMetaData().getURL(),
            schema);
      }

      StringWriter outWriter = new StringWriter();
      StringWriter errorWriter = new StringWriter();

      ScriptRunner runner = getScriptRunner(connection, outWriter, errorWriter);
      consumer.accept(runner);

      if (log.isTraceEnabled()) {
        log.trace(outWriter.toString());
      }
      if (log.isErrorEnabled()) {
        String trimmedErrorString = errorWriter.toString().trim();
        if (!trimmedErrorString.isEmpty()) {
          log.error(trimmedErrorString);
        }
      }
    } catch (SQLException e) {
      throw new RuntimeSqlException("Failed to execute script.", e);
    }
  }

  private void executeAndCacheScripts(Stream<String> scripts, String cacheKey) {
    runScripts(
        runner ->
            CACHED_SCRIPTS.computeIfAbsent(cacheKey, key -> parseScripts(scripts)).stream()
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .map(ByteArrayInputStream::new)
                .map(s -> new InputStreamReader(s, StandardCharsets.UTF_8))
                .forEach(runner::runScript));
  }

  private ScriptRunner getScriptRunner(
      Connection connection, StringWriter outWriter, StringWriter errorWriter) throws SQLException {

    PrintWriter logWriter = new PrintWriter(outWriter);
    PrintWriter errorLogWriter = new PrintWriter(errorWriter);
    ScriptRunner runner = new ScriptRunner(connection);

    connection.setSchema(schema);
    runner.setLogWriter(logWriter);
    runner.setErrorLogWriter(errorLogWriter);
    runner.setStopOnError(true);
    return runner;
  }
}
