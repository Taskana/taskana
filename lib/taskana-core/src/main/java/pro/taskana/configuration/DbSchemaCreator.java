package pro.taskana.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.jdbc.SqlRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class create the schema for taskana.
 */
public class DbSchemaCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbSchemaCreator.class);
    private static final String SQL = "/sql";
    private static final String DB_SCHEMA = SQL + "/taskana-schema.sql";
    private static final String DB_SCHEMA_DB2 = SQL + "/taskana-schema-db2.sql";
    private static final String DB_SCHEMA_POSTGRES = SQL + "/taskana-schema-postgres.sql";
    private static final String DB_SCHEMA_DETECTION = SQL + "/schema-detection.sql";
    private static final String DB_SCHEMA_DETECTION_POSTGRES = SQL + "/schema-detection-postgres.sql";

    private DataSource dataSource;
    private String schemaName;
    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter = new PrintWriter(outWriter);
    private StringWriter errorWriter = new StringWriter();
    private PrintWriter errorLogWriter = new PrintWriter(errorWriter);

    public DbSchemaCreator(DataSource dataSource, String schema) {
        super();
        this.dataSource = dataSource;
        this.schemaName = schema;
    }

    private static String selectDbScriptFileName(String dbProductName) {
        return "PostgreSQL".equals(dbProductName)
            ? DB_SCHEMA_POSTGRES
            : "H2".equals(dbProductName) ? DB_SCHEMA : DB_SCHEMA_DB2;
    }

    private static String selectDbSchemaDetectionScript(String dbProductName) {
        return "PostgreSQL".equals(dbProductName) ? DB_SCHEMA_DETECTION_POSTGRES : DB_SCHEMA_DETECTION;
    }

    /**
     * Run all db scripts.
     *
     * @throws SQLException
     *             will be thrown if there will be some incorrect SQL statements invoked.
     */
    public void run() throws SQLException {
        Connection connection = dataSource.getConnection();
        ScriptRunner runner = new ScriptRunner(connection);
        LOGGER.debug(connection.getMetaData().toString());
        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);
        try {
            if (!isSchemaPreexisting(runner, connection.getMetaData().getDatabaseProductName())) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass()
                    .getResourceAsStream(selectDbScriptFileName(connection.getMetaData().getDatabaseProductName()))));
                runner.runScript(getSqlSchemaNameParsed(reader));
            }
        } finally {
            runner.closeConnection();
        }
        LOGGER.debug(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }

    private boolean isSchemaPreexisting(ScriptRunner runner, String productName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass()
                .getResourceAsStream(selectDbSchemaDetectionScript(productName))));
            runner.runScript(getSqlSchemaNameParsed(reader));
        } catch (Exception e) {
            LOGGER.debug("Schema does not exist.");
            return false;
        }
        LOGGER.debug("Schema does exist.");
        return true;
    }

    public boolean isValidSchemaVersion(String expectedVersion) {
        SqlRunner runner = null;
        try {
            Connection connection = dataSource.getConnection();
            connection.setSchema(this.schemaName);
            runner = new SqlRunner(connection);
            LOGGER.debug(connection.getMetaData().toString());

            String query = "select VERSION from TASKANA_SCHEMA_VERSION where "
                + "VERSION = (select max(VERSION) from TASKANA_SCHEMA_VERSION) "
                + "AND VERSION = ?";

            Map<String, Object> queryResult = runner.selectOne(query, expectedVersion);
            if (queryResult == null || queryResult.isEmpty()) {
                LOGGER.error(
                    "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION has not the expected value {}",
                    expectedVersion);
                return false;
            } else {
                LOGGER.debug("Schema version is valid.");
                return true;
            }

        } catch (Exception e) {
            LOGGER.error(
                "Schema version not valid. The VERSION property in table TASKANA_SCHEMA_VERSION has not the expected value {}",
                expectedVersion);
            return false;
        } finally {
            if (runner != null) {
                runner.closeConnection();
            }
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private StringReader getSqlSchemaNameParsed(BufferedReader reader) {

        StringBuffer content = new StringBuffer();
        try {
            String line = "";
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    content.append(line.replaceAll("%schemaName%", schemaName) + System.lineSeparator());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("SchemaName sql parsing failed for schemaName {}", schemaName);
        }
        return new StringReader(content.toString());
    }
}
