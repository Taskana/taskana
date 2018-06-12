package pro.taskana.database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.configuration.DbSchemaCreator;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * Generates the test data for integration and acceptance tests.
 */
public class TestDataGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbSchemaCreator.class);
    private static final String SQL = "/sql";
    private static final String TASK = SQL + "/task.sql";
    private static final String WORKBASKET = SQL + "/workbasket.sql";
    private static final String DISTRIBUTION_TARGETS = SQL + "/distribution-targets.sql";
    private static final String WORKBASKET_ACCESS_LIST = SQL + "/workbasket-access-list.sql";
    private static final String CLASSIFICATION = SQL + "/classification.sql";
    private static final String OBJECT_REFERENCE = SQL + "/object-reference.sql";
    private static final String ATTACHMENT = SQL + "/attachment.sql";
    private static final String MONITOR_SAMPLE_DATA = SQL + "/monitor-sample-data.sql";
    private static SQLReplacer sqlReplacer;

    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter;
    private StringWriter errorWriter;
    private PrintWriter errorLogWriter;

    public TestDataGenerator() {
        this.logWriter = new PrintWriter(this.outWriter);
        this.errorWriter = new StringWriter();
        this.errorLogWriter = new PrintWriter(this.errorWriter);
    }

    public void generateTestData(DataSource dataSource) throws SQLException, IOException {
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

            if (sqlReplacer == null) {
                sqlReplacer = new SQLReplacer(connection.getMetaData().getDatabaseProductName());
            }

            Stream.of(sqlReplacer.classificationSql, sqlReplacer.workbasketSql, sqlReplacer.taskSql,
                sqlReplacer.workbasketAccessListSql, sqlReplacer.distributionTargetSql, sqlReplacer.objectReferenceSql,
                sqlReplacer.attachmentSql)
                .map(s -> s.getBytes(StandardCharsets.UTF_8))
                .map(ByteArrayInputStream::new)
                .map(InputStreamReader::new)
                .forEach(runner::runScript);

        } finally {
            if (runner != null) {
                runner.closeConnection();
            }
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

            if (sqlReplacer == null) {
                sqlReplacer = new SQLReplacer(connection.getMetaData().getDatabaseProductName());
            }

            runner.runScript(
                new InputStreamReader(
                    new ByteArrayInputStream(
                        sqlReplacer.monitoringTestDataSql.getBytes(StandardCharsets.UTF_8))));
        } finally {
            if (runner != null) {
                runner.closeConnection();
            }
            LOGGER.debug(outWriter.toString());
            if (!errorWriter.toString().trim().isEmpty()) {
                LOGGER.error(errorWriter.toString());
            }
        }
    }

    /**
     * This class replaces boolean values with int values if the database is db2.
     */
    private static final class SQLReplacer {

        private String classificationSql;
        private String workbasketSql;
        private String taskSql;
        private String workbasketAccessListSql;
        private String distributionTargetSql;
        private String objectReferenceSql;
        private String attachmentSql;
        private String monitoringTestDataSql;

        private SQLReplacer(String dbProductName) throws IOException {
            boolean isDb2 = TaskanaEngineImpl.isDb2(dbProductName);
            classificationSql = parseAndReplace(getClass().getResourceAsStream(CLASSIFICATION), isDb2);
            workbasketSql = parseAndReplace(getClass().getResourceAsStream(WORKBASKET), isDb2);
            taskSql = parseAndReplace(getClass().getResourceAsStream(TASK), isDb2);
            workbasketAccessListSql = parseAndReplace(getClass().getResourceAsStream(WORKBASKET_ACCESS_LIST), isDb2);
            distributionTargetSql = parseAndReplace(getClass().getResourceAsStream(DISTRIBUTION_TARGETS), isDb2);
            objectReferenceSql = parseAndReplace(getClass().getResourceAsStream(OBJECT_REFERENCE), isDb2);
            attachmentSql = parseAndReplace(getClass().getResourceAsStream(ATTACHMENT), isDb2);
            monitoringTestDataSql = generateMonitoringSqlData(isDb2);
        }

        private String parseAndReplace(InputStream stream, boolean replace) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sql = new StringBuilder();
            String line;
            if (replace) {
                while ((line = bufferedReader.readLine()) != null) {
                    sql.append(line.replaceAll("true|TRUE", "1").replaceAll("false|FALSE", "0")).append("\n");
                }
            } else {
                while ((line = bufferedReader.readLine()) != null) {
                    sql.append(line).append("\n");
                }
            }
            return sql.toString();
        }

        private String generateMonitoringSqlData(boolean replace) throws IOException {
            String rawSql = parseAndReplace(getClass().getResourceAsStream(MONITOR_SAMPLE_DATA), replace);

            BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(rawSql.getBytes(StandardCharsets.UTF_8))));
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            StringBuilder sql = new StringBuilder();
            String line;

            List<Integer> ages = Arrays.asList(-70000, -14000, -2800, -1400, -1400, -700, -700, -35, -28, -28, -14, -14,
                -14, -14, -14, -14, -14, -14, -14, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, -7, 0, 0, 0, 0, 7, 7, 7, 7,
                7, 7,
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

}
