package pro.taskana.impl.configuration;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
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

    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter = new PrintWriter(outWriter);
    private StringWriter errorWriter = new StringWriter();
    private PrintWriter errorLogWriter = new PrintWriter(errorWriter);


    /**
     * Clears the db.
     * @throws SQLException
     */
    public void clearDb(DataSource dataSource) throws SQLException {
        ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
        LOGGER.debug(dataSource.getConnection().getMetaData().toString());

        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);

        runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_CLEAR_SCRIPT)));

        runner.closeConnection();

        LOGGER.debug(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }
}
