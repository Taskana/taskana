package pro.taskana.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    static final String RELATIVE_DATE_REGEX = "RELATIVE_DATE\\((-?\\d+)\\)";
    static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile(RELATIVE_DATE_REGEX);
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final DataSource dataSource;
    private final LocalDateTime now;

    /**
     * This value cannot be automatically obtained by connection.getSchema(),
     * because setting not yet existing schema will result into an SQL Exception.
     */
    private final String schema;

    public SampleDataGenerator(DataSource dataSource, String schema) {
        this(dataSource, schema, LocalDateTime.now());
    }

    public SampleDataGenerator(DataSource dataSource, String schema, LocalDateTime now) {
        this.dataSource = dataSource;
        this.schema = schema;
        this.now = now;
    }

    public void runScripts(Consumer<ScriptRunner> consumer) throws SQLException {
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
        }
    }

    public void generateSampleData() throws SQLException {
        runScripts((runner) -> {
            clearDb(runner);
            executeScripts(runner, SampleDataProvider.getDataProvider(runner));
        });
    }

    private void executeScripts(ScriptRunner runner, Stream<String> scriptsList) {
        scriptsList
            .map(s -> SampleDataGenerator.parseAndReplace(now, s))
            .map(StringReader::new)
            .map(BufferedReader::new)
            .forEachOrdered(runner::runScript);
    }

    public void clearDb(ScriptRunner runner) {
        runner.setStopOnError(false);
        runner.runScript(getScriptBufferedStream(DB_CLEAR_TABLES_SCRIPT));
        runner.setStopOnError(true);
    }

    public void dropDb(ScriptRunner runner) {
        runner.setStopOnError(false);
        runner.runScript(getScriptBufferedStream(DB_DROP_TABLES_SCRIPT));
        runner.setStopOnError(true);
    }

    ScriptRunner getScriptRunner(Connection connection, StringWriter outWriter,
        StringWriter errorWriter) throws SQLException {

        PrintWriter logWriter = new PrintWriter(outWriter);
        PrintWriter errorLogWriter = new PrintWriter(errorWriter);
        ScriptRunner runner = new ScriptRunner(connection);

        connection.setSchema(schema);
        String databaseProductName = connection.getMetaData().getDatabaseProductName();

        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);
        return runner;
    }

    /**
     * This method resolves the custom sql function defined through this regex: {@value RELATIVE_DATE_REGEX}. Its
     * parameter is a digit representing the relative offset of a given starting point date.
     * <p/>
     * Yes, this can be done as an actual sql function, but that'd lead to a little more complexity (and thus we'd have
     * to maintain the code for db compatibility ...) Since we're already replacing the boolean attributes of sql files
     * this addition is not a huge computational cost.
     *
     * @param now
     *            anchor for relative date conversion.
     * @param sql
     *            sql statement which may contain the above declared custom function.
     * @return sql statement with the given function resolved, if the 'sql' parameter contained any.
     */
    static String replaceDatePlaceholder(LocalDateTime now, String sql) {
        Matcher m = RELATIVE_DATE_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer(sql.length());
        while (m.find()) {
            long daysToShift = Long.parseLong(m.group(1));
            String daysAsStringDate = formatToSqlDate(now, daysToShift);
            m.appendReplacement(sb, daysAsStringDate);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private static String formatToSqlDate(LocalDateTime now, long days) {
        return "'" + now.plusDays(days).format(DATE_TIME_FORMATTER) + "'";
    }

    private static String parseAndReplace(LocalDateTime now, String script) {
        return replaceDatePlaceholder(now,
            getScriptBufferedStream(script).lines().collect(Collectors.joining(System.lineSeparator())));
    }

    static BufferedReader getScriptBufferedStream(String script) {
        return Optional.ofNullable(SampleDataGenerator.class.getResourceAsStream(script)).map(
            inputStream -> new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))).orElse(null);
    }

    private static boolean isPostgreSQL(String databaseProductName) {
        return "PostgreSQL".equals(databaseProductName);
    }
}
