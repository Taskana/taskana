package pro.taskana.database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

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
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/classification.sql")));
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream("/sql/workbasket.sql")));
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream("/sql/task.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/workbasket-access-list.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/distribution-targets.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/object-reference.sql")));
            runner.runScript(
                new InputStreamReader(this.getClass().getResourceAsStream("/sql/attachment.sql")));
        } finally {

            runner.closeConnection();
            LOGGER.debug(outWriter.toString());
            if (!errorWriter.toString().trim().isEmpty()) {
                LOGGER.error(errorWriter.toString());
            }
        }
    }

    public void generateMonitoringTestData(DataSource dataSource) throws IOException, SQLException {
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
            runner.runScript(
                new InputStreamReader(
                    new ByteArrayInputStream(
                        generateMonitoringSqlData().getBytes(StandardCharsets.UTF_8.name()))));
        } finally {

            runner.closeConnection();
            LOGGER.debug(outWriter.toString());
            if (!errorWriter.toString().trim().isEmpty()) {
                LOGGER.error(errorWriter.toString());
            }
        }
    }

    private String generateMonitoringSqlData() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
            new InputStreamReader(this.getClass().getResourceAsStream("/sql/monitor-sample-data.sql")));
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sql = new StringBuilder();
        String line;

        List<Integer> ages = Arrays.asList(-70000, -14000, -2800, -1400, -1400, -700, -700, -35, -28, -28, -14, -14,
            -14, -14, -14, -14, -14, -14, -14, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, 0, 0, 0, 0, 7, 7, 7, 7, 7, 7,
            7, 14, 14, 14, 14, 21, 210, 210, 28000, 700000);
        int i = 0;
        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("dueDate")) {
                line = line.replace("dueDate", "\'" + now.plusDays(ages.get(i)).format(formatter) + "\' ");
                i++;
            }
            sql.append(line).append("\n");
        }
        bufferedReader.close();
        return sql.toString();
    }

}
