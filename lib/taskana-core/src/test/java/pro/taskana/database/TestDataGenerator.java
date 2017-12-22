package pro.taskana.database;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.DbSchemaCreator;

/**
 * Generates the test data for integration and acceptance tests.
 */
public class TestDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbSchemaCreator.class);

    private StringWriter outWriter = new StringWriter();

    private PrintWriter logWriter;

    private StringWriter errorWriter;

    private PrintWriter errorLogWriter;

    public TestDataGenerator() {
        this.logWriter = new PrintWriter(this.outWriter);
        this.errorWriter = new StringWriter();
        this.errorLogWriter = new PrintWriter(this.errorWriter);
    }

    public void generateTestData(DataSource dataSource) throws SQLException {
        ScriptRunner runner = null;
        try {
            Connection connection = dataSource.getConnection();
            LOGGER.debug(connection.getMetaData().toString());
            runner = new ScriptRunner(connection);
            runner.setStopOnError(true);
            runner.setLogWriter(this.logWriter);
            runner.setErrorLogWriter(this.errorLogWriter);
            runner.setStopOnError(true);
            runner.setLogWriter(this.logWriter);
            runner.setErrorLogWriter(this.errorLogWriter);
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream("/sql/task.sql")));
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream("/sql/workbasket.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/distribution-targets.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/classification.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/workbasket-access-list.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/object-reference.sql")));
        } finally {

            runner.closeConnection();
            LOGGER.debug(outWriter.toString());
            if (!errorWriter.toString().trim().isEmpty()) {
                LOGGER.error(errorWriter.toString());
            }
        }
    }
}
