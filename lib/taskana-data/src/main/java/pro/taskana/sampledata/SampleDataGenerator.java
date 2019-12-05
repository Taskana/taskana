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

    private static final String SQL_SAMPLE_DATA = "/sql/sample-data";

    private static final String CLEAR = SQL_SAMPLE_DATA + "/clear-db.sql";
    private static final String CLEAR_HISTORY_EVENTS = SQL_SAMPLE_DATA + "/clear-history-events.sql";
    private static final String TASK = SQL_SAMPLE_DATA + "/task.sql";
    private static final String WORKBASKET = SQL_SAMPLE_DATA + "/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = SQL_SAMPLE_DATA + "/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = SQL_SAMPLE_DATA + "/workbasket-access-list.sql";
    private static final String CLASSIFICATION = SQL_SAMPLE_DATA + "/classification.sql";
    private static final String OBJECT_REFERENCE = SQL_SAMPLE_DATA + "/object-reference.sql";
    private static final String ATTACHMENT = SQL_SAMPLE_DATA + "/attachment.sql";
    private static final String HISTORY_EVENT = SQL_SAMPLE_DATA + "/history-event.sql";
    private static final String CHECK_HISTORY_EVENT_EXIST = SQL_SAMPLE_DATA + "/check-history-event-exist.sql";

    static final String RELATIVE_DATE_REGEX = "RELATIVE_DATE\\((-?\\d+)\\)";
    static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile(RELATIVE_DATE_REGEX);
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final DataSource dataSource;
    private final LocalDateTime now;

    public SampleDataGenerator(DataSource dataSource) {
        this(dataSource, LocalDateTime.now());
    }

    public SampleDataGenerator(DataSource dataSource, LocalDateTime now) {
        this.dataSource = dataSource;
        this.now = now;
    }

    public void generateSampleData(String schemaName) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace(connection.getMetaData().toString());
            }

            StringWriter outWriter = new StringWriter();
            StringWriter errorWriter = new StringWriter();

            ScriptRunner runner = getScriptRunner(schemaName, connection, outWriter, errorWriter);

            Stream<String> scriptsList = getDefaultScripts();
            try {
                runner.runScript(getScriptBufferedStream(CHECK_HISTORY_EVENT_EXIST));
                runner.runScript(getScriptBufferedStream(CLEAR_HISTORY_EVENTS));
                scriptsList = Stream.concat(scriptsList, Stream.of(HISTORY_EVENT));
            } catch (Exception e) {
                LOGGER.error("The HISTORY_EVENTS table is not created");
            }

            executeScripts(runner, scriptsList);
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

    private void executeScripts(ScriptRunner runner, Stream<String> scriptsList) {
        scriptsList
            .map(s -> SampleDataGenerator.parseAndReplace(now, s))
            .map(StringReader::new)
            .map(BufferedReader::new)
            .forEachOrdered(runner::runScript);
    }

    private ScriptRunner getScriptRunner(String schemaName, Connection connection, StringWriter outWriter,
        StringWriter errorWriter) throws SQLException {

        PrintWriter logWriter = new PrintWriter(outWriter);
        PrintWriter errorLogWriter = new PrintWriter(errorWriter);
        ScriptRunner runner = new ScriptRunner(connection);

        String databaseProductName = connection.getMetaData().getDatabaseProductName();

        runner.runScript(selectSchemaScript(databaseProductName, schemaName));
        runner.setStopOnError(false);
        runner.runScript(getScriptBufferedStream(CLEAR));

        runner.setStopOnError(true);
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
    static String replaceRelativeTimeFunction(LocalDateTime now, String sql) {
        Matcher m = RELATIVE_DATE_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer(sql.length());
        while (m.find()) {
            long days = Long.parseLong(m.group(1));
            String daysAsStringDate = "'" + now.plusDays(days).format(DATE_TIME_FORMATTER) + "'";
            m.appendReplacement(sb, daysAsStringDate);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private StringReader selectSchemaScript(String dbProductName, String schemaName) {
        return new StringReader(isPostgreSQL(dbProductName)
            ? "SET search_path TO " + schemaName + ";"
            : "SET SCHEMA " + schemaName + ";");
    }

    private static String parseAndReplace(LocalDateTime now, String script) {
        return replaceRelativeTimeFunction(now,
            getScriptBufferedStream(script).lines().collect(Collectors.joining(System.lineSeparator())));
    }

    static BufferedReader getScriptBufferedStream(String script) {
        return Optional.ofNullable(SampleDataGenerator.class.getResourceAsStream(script)).map(
            inputStream -> new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))).orElse(null);
    }

    static Stream<String> getDefaultScripts() {
        return Stream.of(WORKBASKET, DISTRIBUTION_TARGETS, CLASSIFICATION, TASK, ATTACHMENT, WORKBASKET_ACCESS_LIST,
            OBJECT_REFERENCE);
    }

    private static boolean isPostgreSQL(String databaseProductName) {
        return "PostgreSQL".equals(databaseProductName);
    }
}
