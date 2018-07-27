package pro.taskana.sampledata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

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
    private static final String TASK = SQL + TEST_DATA + "/task.sql";
    private static final String WORKBASKET = SQL + TEST_DATA + "/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = SQL + TEST_DATA + "/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = SQL + TEST_DATA + "/workbasket-access-list.sql";
    private static final String CLASSIFICATION = SQL + TEST_DATA + "/classification.sql";
    private static final String OBJECT_REFERENCE = SQL + TEST_DATA + "/object-reference.sql";
    private static final String ATTACHMENT = SQL + TEST_DATA + "/attachment.sql";
    private ScriptRunner runner;

    DataSource dataSource;

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
            runner.runScript(selectSchemaScript(dataSource.getConnection().getMetaData().getDatabaseProductName(), schemaName));
            runner.setStopOnError(false);
            runner.runScript(new BufferedReader(
                new InputStreamReader(this.getClass().getResourceAsStream(CLEAR), StandardCharsets.UTF_8)));
        } catch (Exception e) {
            LOGGER.error("caught Exception {}", e);
        }

        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);

        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(WORKBASKET), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(DISTRIBUTION_TARGETS), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(CLASSIFICATION), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(TASK), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(ATTACHMENT), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(new InputStreamReader(
            this.getClass().getResourceAsStream(WORKBASKET_ACCESS_LIST), StandardCharsets.UTF_8)));
        runner.runScript(new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream(OBJECT_REFERENCE), StandardCharsets.UTF_8)));

        runner.closeConnection();

        LOGGER.trace(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }

    private StringReader selectSchemaScript(String dbProductName, String schemaName) {
        return new StringReader("PostgreSQL".equals(dbProductName) ?
            "SET search_path TO " + schemaName + ";" :
            "SET SCHEMA " + schemaName + ";");
    }

}
