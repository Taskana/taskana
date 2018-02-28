package pro.taskana.sampledata;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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
    private static final String TASK = SQL + TEST_DATA + "/task.sql";
    private static final String WORKBASKET = SQL + TEST_DATA + "/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = SQL + TEST_DATA + "/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = SQL + TEST_DATA + "/workbasket-access-list.sql";
    private static final String CLASSIFICATION = SQL + TEST_DATA + "/classification.sql";
    private static final String OBJECT_REFERENCE = SQL + TEST_DATA + "/object-reference.sql";
    private ScriptRunner runner;

    public SampleDataGenerator(DataSource dataSource) throws SQLException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(dataSource.getConnection().getMetaData().toString());
        }
        runner = new ScriptRunner(dataSource.getConnection());
    }

    public void generateSampleData() throws SQLException {
        StringWriter outWriter = new StringWriter();
        PrintWriter logWriter = new PrintWriter(outWriter);

        StringWriter errorWriter = new StringWriter();
        PrintWriter errorLogWriter = new PrintWriter(errorWriter);

        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);

        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(WORKBASKET)));
        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DISTRIBUTION_TARGETS)));
        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(CLASSIFICATION)));
        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(TASK)));
        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(WORKBASKET_ACCESS_LIST)));
        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(OBJECT_REFERENCE)));

        runner.closeConnection();

        LOGGER.debug(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }

}
