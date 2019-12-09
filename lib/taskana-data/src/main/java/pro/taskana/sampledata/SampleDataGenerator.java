package pro.taskana.sampledata;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class generates sample data for manual testing purposes.
 */
public class SampleDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(SampleDataGenerator.class);

    private static final String DB_CLEAR_TABLES_SCRIPT = "/sql/clear/clear-db.sql";
    private static final String DB_DROP_TABLES_SCRIPT = "/sql/clear/drop-tables.sql";
    private static final String CHECK_HISTORY_EVENT_EXIST = "/sql/sample-data/check-history-event-exist.sql";
    public static final String CACHED_TEST = "TEST";
    public static final String CACHED_SAMPLE = "SAMPLE";
    public static final String CACHED_EVENTSAMPLE = "EVENTSAMPLE";
    public static final String CACHED_MONITOR = "MONITOR";

    private final DataSource dataSource;
    private final LocalDateTime now;

    /**
     * This value cannot be automatically obtained by connection.getSchema(),
     * because setting not yet existing schema will result into an SQL Exception.
     */
    private final String schema;

    private static HashMap<String, List<String>> cachedScripts = new HashMap<String, List<String>>();

    public SampleDataGenerator(DataSource dataSource, String schema) {
        this(dataSource, schema, LocalDateTime.now());
    }

    public SampleDataGenerator(DataSource dataSource, String schema, LocalDateTime now) {
        this.dataSource = dataSource;
        this.schema = schema;
        this.now = now;
    }

    public void runScripts(Consumer<ScriptRunner> consumer) {
        try (Connection connection = dataSource.getConnection()) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(connection.getMetaData().toString());
            }

            StringWriter outWriter = new StringWriter();
            StringWriter errorWriter = new StringWriter();

            ScriptRunner runner = getScriptRunner(connection, outWriter, errorWriter);
            consumer.accept(runner);
            runner.closeConnection();

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(outWriter.toString());
                String trimmedErrorString = errorWriter.toString().trim();
                if (!trimmedErrorString.isEmpty()) {
                    LOGGER.error(trimmedErrorString);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute script.", e);
        }
    }

    public void generateSampleData() throws SQLException {
        runScripts((runner) -> {
            clearDb(runner);
            Stream<String> scripts;
            String cacheKey;
            try {
                //TODO find a better method of testing if a table exists
                runner.runScript(SQLReplacer.getScriptBufferedStream(CHECK_HISTORY_EVENT_EXIST));
                scripts = SampleDataProvider.getScriptsWithEvents();
                cacheKey = CACHED_SAMPLE;
            } catch (Exception e) {
                scripts = SampleDataProvider.getDefaultScripts();
                cacheKey = CACHED_EVENTSAMPLE;
            }
            cacheAndExecute(scripts, cacheKey);
        });
    }

    public List<String> parseScripts(Stream<String> scripts) {
        try (Connection connection = dataSource.getConnection()) {
            String dbProductName = connection.getMetaData().getDatabaseProductName();
            return scripts.map(script -> SQLReplacer.getScriptAsSql(dbProductName, now, script))
                .collect(Collectors.toList());
        } catch (SQLException e) {
            throw new RuntimeException("Connection to database failed.", e);
        }
    }

    public void generateTestData() {
        Stream<String> scripts = SampleDataProvider.getTestDataScripts();
        cacheAndExecute(scripts, CACHED_TEST);
    }

    public void generateMonitorData() {
        Stream<String> scripts = SampleDataProvider.getMonitorDataScripts();
        cacheAndExecute(scripts, CACHED_MONITOR);
    }

    public void clearDb(ScriptRunner runner) {
        runner.setStopOnError(false);
        runner.runScript(SQLReplacer.getScriptBufferedStream(DB_CLEAR_TABLES_SCRIPT));
        runner.setStopOnError(true);
    }

    private void cacheAndExecute(Stream<String> scripts, String cacheKey) {
        if (!cachedScripts.containsKey(cacheKey)) {
            cachedScripts.put(cacheKey, parseScripts(scripts));
        }
        runScripts(runner -> cachedScripts.get(cacheKey).stream()
            .map(s -> s.getBytes(StandardCharsets.UTF_8))
            .map(ByteArrayInputStream::new)
            .map(InputStreamReader::new)
            .forEach(runner::runScript));
    }

    public void dropDb(ScriptRunner runner) {
        runner.setStopOnError(false);
        runner.runScript(SQLReplacer.getScriptBufferedStream(DB_DROP_TABLES_SCRIPT));
        runner.setStopOnError(true);
    }

    ScriptRunner getScriptRunner(Connection connection, StringWriter outWriter,
        StringWriter errorWriter) throws SQLException {

        PrintWriter logWriter = new PrintWriter(outWriter);
        PrintWriter errorLogWriter = new PrintWriter(errorWriter);
        ScriptRunner runner = new ScriptRunner(connection);

        connection.setSchema(schema);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);
        return runner;
    }

}
