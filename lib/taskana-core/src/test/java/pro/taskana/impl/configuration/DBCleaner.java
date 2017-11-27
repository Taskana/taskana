package pro.taskana.impl.configuration;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pro.taskana.configuration.DbScriptRunner;

/**
 * This class cleans the complete database. Only to be used in Unittest
 */
public class DBCleaner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbScriptRunner.class);
    private static final String DB_CLEAR_SCRIPT = "/sql/clear-db.sql";
    private static final String DB_DROP_TABLES_SCRIPT = "/sql/drop-tables.sql";

    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter = new PrintWriter(outWriter);
    private StringWriter errorWriter = new StringWriter();
    private PrintWriter errorLogWriter = new PrintWriter(errorWriter);


    /**
     * Clears the db.
     * @param dropTables if true drop tables, else clean tables
     * @throws SQLException
     */
    public void clearDb(DataSource dataSource, boolean dropTables) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptRunner runner = new ScriptRunner(connection);
            LOGGER.debug(connection.getMetaData().toString());

            runner.setStopOnError(true);
            runner.setLogWriter(logWriter);
            runner.setErrorLogWriter(errorLogWriter);
            if (dropTables) {
                runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_DROP_TABLES_SCRIPT)));
            } else {
                runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_CLEAR_SCRIPT)));
            }

        } catch (Exception e) {
            LOGGER.error("caught Exception " + e);
        }
        LOGGER.debug(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }
}
