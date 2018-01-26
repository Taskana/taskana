package pro.taskana.configuration;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class create the schema for taskana.
 */
public class DbSchemaCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DbSchemaCreator.class);
    private static final String SQL = "/sql";
    private static final String DB_SCHEMA = SQL + "/taskana-schema.sql";
    private static final String DB_SCHEMA_DETECTION = SQL + "/schema-detection.sql";
    private DataSource dataSource;
    private StringWriter outWriter = new StringWriter();
    private PrintWriter logWriter = new PrintWriter(outWriter);
    private StringWriter errorWriter = new StringWriter();
    private PrintWriter errorLogWriter = new PrintWriter(errorWriter);

    public DbSchemaCreator(DataSource dataSource) {
        super();
        this.dataSource = dataSource;
    }

    /**
     * Run all db scripts.
     *
     * @throws SQLException
     *             TODO
     */
    public void run() throws SQLException {
        Connection connection = dataSource.getConnection();
        ScriptRunner runner = new ScriptRunner(connection);
        LOGGER.debug(connection.getMetaData().toString());

        runner.setStopOnError(true);
        runner.setLogWriter(logWriter);
        runner.setErrorLogWriter(errorLogWriter);

        if (!isSchemaPreexisting(runner)) {
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_SCHEMA)));
        }
        runner.closeConnection();

        LOGGER.debug(outWriter.toString());
        if (!errorWriter.toString().trim().isEmpty()) {
            LOGGER.error(errorWriter.toString());
        }
    }

    private boolean isSchemaPreexisting(ScriptRunner runner) {
        try {
            runner.runScript(new InputStreamReader(this.getClass().getResourceAsStream(DB_SCHEMA_DETECTION)));
        } catch (Exception e) {
            LOGGER.debug("Schema does not exist.");
            return false;
        }
        LOGGER.debug("Schema does exist.");
        return true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
