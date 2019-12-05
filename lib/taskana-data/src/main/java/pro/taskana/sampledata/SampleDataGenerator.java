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

    private static final String CLEAR = "/sql/clear/clear-db.sql";
    private static final String CLEAR_HISTORY_EVENTS = "/sql/clear/clear-history-events.sql";

    private static final String TASK = "/sql/sample-data/task.sql";
    private static final String WORKBASKET = "/sql/sample-data/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = "/sql/sample-data/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = "/sql/sample-data/workbasket-access-list.sql";
    private static final String CLASSIFICATION = "/sql/sample-data/classification.sql";
    private static final String OBJECT_REFERENCE = "/sql/sample-data/object-reference.sql";
    private static final String ATTACHMENT = "/sql/sample-data/attachment.sql";
    private static final String HISTORY_EVENT = "/sql/sample-data/history-event.sql";
    private static final String CHECK_HISTORY_EVENT_EXIST = "/sql/sample-data/check-history-event-exist.sql";

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

    private StringReader selectSchemaScript(String dbProductName, String schemaName) {
        return new StringReader(isPostgreSQL(dbProductName)
            ? "SET search_path TO " + schemaName + ";"
            : "SET SCHEMA " + schemaName + ";");
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

    static Stream<String> getDefaultScripts() {
        return Stream.of(WORKBASKET, DISTRIBUTION_TARGETS, CLASSIFICATION, TASK, ATTACHMENT, WORKBASKET_ACCESS_LIST,
            OBJECT_REFERENCE);
    }

    private static boolean isPostgreSQL(String databaseProductName) {
        return "PostgreSQL".equals(databaseProductName);
    }
}
