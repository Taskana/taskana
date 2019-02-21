package pro.taskana.impl.configuration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class cleans the complete database. Only to be used in Unittest
 */
public class DBCleaner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBCleaner.class);
    private static final String DB_CLEAR_SCRIPT = "/sql/clear-db.sql";
    private static final String DB_DROP_TABLES_SCRIPT = "/sql/drop-tables.sql";

    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter = new PrintWriter(outWriter);
    private StringWriter errorWriter = new StringWriter();
    private PrintWriter errorLogWriter = new PrintWriter(errorWriter);

    /**
     * Clears the db.
     *
     * @param dropTables
     *            if true drop tables, else clean tables
     */
    public void clearDb(DataSource dataSource, boolean dropTables) {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            LOGGER.debug(connection.getMetaData().toString());

            runner.setStopOnError(false);
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errorLogWriter);
            BufferedReader reader;
            if (dropTables) {
                reader = new BufferedReader((new InputStreamReader(this.getClass().getResourceAsStream(DB_DROP_TABLES_SCRIPT))));
            } else {
                reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(DB_CLEAR_SCRIPT)));
            }
            boolean isPostgres = "PostgreSQL".equals(connection.getMetaData().getDatabaseProductName());
            runner.runScript(addSchemaAndPotentialToLowerCase(reader, isPostgres));
        } catch (Exception e) {
            LOGGER.error("caught Exception " + e);
        }
        LOGGER.debug(outWriter.toString());
        String errorMsg = errorWriter.toString().trim();

        if (!errorMsg.isEmpty() && errorMsg.indexOf("SQLCODE=-204, SQLSTATE=42704") == -1) {
            LOGGER.error(errorWriter.toString());
        }
    }

    private StringReader addSchemaAndPotentialToLowerCase(BufferedReader reader, boolean isPostgres) {
        StringBuffer content = new StringBuffer();
        if (isPostgres) {
            content.append("SET search_path TO " + TaskanaEngineConfigurationTest.getSchemaName().toLowerCase() + ";"
                        + System.lineSeparator());
        } else {
            content.append("SET SCHEMA " +  TaskanaEngineConfigurationTest.getSchemaName().toUpperCase() + ";" + System.lineSeparator());
        }
        try {
            String line = "";
            while (line != null) {
                line = reader.readLine();
                if (line != null) {
                    if (isPostgres) {
                        content.append(line.toLowerCase() + System.lineSeparator());
                    } else {
                        content.append(line + System.lineSeparator());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("Transformation to lower case failed");
        }
        return new StringReader(content.toString());
    }
}
