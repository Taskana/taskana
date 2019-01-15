package pro.taskana.rest.sampledata;

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
    private static final String TEST_DATA = "/sql.sample-data";
    private static final String CLEAR =  TEST_DATA + "/clear-db.sql";
    private static final String HISTORY_EVENT =  TEST_DATA + "/history-event.sql";
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
            new InputStreamReader(this.getClass().getResourceAsStream(HISTORY_EVENT), StandardCharsets.UTF_8)));

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
