package pro.taskana.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final String SQL = "/sql";
    private static final String TEST_DATA = "/sample-data";
    private static final String CLEAR = SQL + TEST_DATA + "/clear-db.sql";
    private static final String CLEAR_HISTORY_EVENTS = SQL + TEST_DATA + "/clear-history-events.sql";
    private static final String TASK = SQL + TEST_DATA + "/task.sql";
    private static final String WORKBASKET = SQL + TEST_DATA + "/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = SQL + TEST_DATA + "/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = SQL + TEST_DATA + "/workbasket-access-list.sql";
    private static final String CLASSIFICATION = SQL + TEST_DATA + "/classification.sql";
    private static final String OBJECT_REFERENCE = SQL + TEST_DATA + "/object-reference.sql";
    private static final String ATTACHMENT = SQL + TEST_DATA + "/attachment.sql";
    private static final String HISTORY_EVENT = SQL + TEST_DATA + "/history-event.sql";
    private static final String CHECK_HISTORY_EVENT_EXIST = SQL + TEST_DATA + "/check-history-event-exist.sql";

    private static final String RELATIVE_DATE_REGEX = "RELATIVE_DATE\\((-?\\d+)\\)";
    private static final Pattern RELATIVE_DATE_PATTERN = Pattern.compile(RELATIVE_DATE_REGEX);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private DataSource dataSource;
    private ScriptRunner runner;

    public SampleDataGenerator(DataSource dataSource) throws SQLException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(dataSource.getConnection().getMetaData().toString());
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
            runner.runScript(
                selectSchemaScript(dataSource.getConnection().getMetaData().getDatabaseProductName(), schemaName));
            runner.setStopOnError(false);
            runner.runScript(getScriptBufferedStream(CLEAR));
        } catch (Exception e) {
            LOGGER.error("caught Exception {}", e);
        }

        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);

        String[] script = this.getScriptList();

        LocalDateTime now = LocalDateTime.now();
        Stream.of(script)
            .map(s -> SampleDataGenerator.parseAndReplace(now, s))
            .map(StringReader::new)
            .map(BufferedReader::new)
            .forEachOrdered(runner::runScript);

        runner.closeConnection();

        LOGGER.trace(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
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
    private static String replaceRelativeTimeFunction(LocalDateTime now, String sql) {
        Matcher m = RELATIVE_DATE_PATTERN.matcher(sql);
        StringBuffer sb = new StringBuffer(sql.length());
        while (m.find()) {
            m.appendReplacement(sb,
                "'" + now.plusDays(Long.parseLong(m.group(1))).format(DATE_TIME_FORMATTER) + "'");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private StringReader selectSchemaScript(String dbProductName, String schemaName) {
        return new StringReader(isPostgreSQL(dbProductName)
            ? "SET search_path TO " + schemaName + ";"
            : "SET SCHEMA " + schemaName + ";");
    }

    /**
     * Create a array with the necessary script.
     * @return a array with the corresponding scripts files
     */
    private String[] getScriptList() {
        ArrayList<String> scriptsList = getDefaultScripts();

        try {
            runner.runScript(getScriptBufferedStream(CHECK_HISTORY_EVENT_EXIST));
            runner.runScript(getScriptBufferedStream(CLEAR_HISTORY_EVENTS));
            scriptsList.add(HISTORY_EVENT);
        } catch (Exception e) {
            LOGGER.error("The HISTORY_EVENTS table is not created");
        }
        return scriptsList.toArray(new String[0]);
    }

    static String parseAndReplace(LocalDateTime now, String script) {
        return replaceRelativeTimeFunction(now,
            getScriptBufferedStream(script).lines().collect(Collectors.joining(System.lineSeparator())));
    }

    static BufferedReader getScriptBufferedStream(String script) {
        return Optional.ofNullable(SampleDataGenerator.class.getResourceAsStream(script)).map(
            inputStream -> new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))).orElse(null);
    }

    static ArrayList<String> getDefaultScripts() {
        String[] script = {WORKBASKET, DISTRIBUTION_TARGETS, CLASSIFICATION, TASK, ATTACHMENT, WORKBASKET_ACCESS_LIST,
            OBJECT_REFERENCE};
        return new ArrayList<>(Arrays.asList(script));
    }

    private static boolean isPostgreSQL(String databaseProductName) {
        return "PostgreSQL".equals(databaseProductName);
    }
}
