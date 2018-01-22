package pro.taskana.configuration;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaEngine;
import pro.taskana.impl.TaskanaEngineImpl;

/**
 * This central class creates the TaskanaEngine and holds all the information about DB and Security.
 */
public class TaskanaEngineConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaEngineConfiguration.class);

    private static final String USER_NAME = "sa";
    private static final String USER_PASSWORD = "sa";
    private static final String JDBC_H2_MEM_TASKANA = "jdbc:h2:mem:taskana";
    private static final String H2_DRIVER = "org.h2.Driver";
    protected DataSource dataSource;
    protected DbSchemaCreator dbScriptRunner;

    // global switch to enable JAAS based authentication and Taskana
    // authorizations
    protected boolean securityEnabled;
    protected boolean useManagedTransactions;

    public TaskanaEngineConfiguration() {
    }

    public TaskanaEngineConfiguration(DataSource dataSource, boolean useManagedTransactions)
        throws SQLException {
        this(dataSource, useManagedTransactions, true);
    }

    public TaskanaEngineConfiguration(DataSource dataSource, boolean useManagedTransactions,
        boolean securityEnabled) throws SQLException {
        this.useManagedTransactions = useManagedTransactions;

        if (dataSource != null) {
            this.dataSource = dataSource;
        } else {
            // use default In Memory datasource
            this.dataSource = createDefaultDataSource();
        }
        dbScriptRunner = new DbSchemaCreator(this.dataSource);
        dbScriptRunner.run();

        this.securityEnabled = securityEnabled;
    }

    public static DataSource createDefaultDataSource() {
        LOGGER.warn("No datasource is provided. A inmemory db is used: "
            + "'org.h2.Driver', 'jdbc:h2:mem:taskana', 'sa', 'sa'");
        return createDatasource(H2_DRIVER, JDBC_H2_MEM_TASKANA, USER_NAME, USER_PASSWORD);
    }

    /**
     * This method creates the TaskanaEngine without an sqlSessionFactory.
     *
     * @return the TaskanaEngine
     */
    public TaskanaEngine buildTaskanaEngine() {
        return new TaskanaEngineImpl(this);
    }

    /**
     * This method creates a PooledDataSource, if the needed properties are provided.
     *
     * @param driver
     *            the name of the jdbc driver
     * @param jdbcUrl
     *            the url to which the jdbc driver connects
     * @param username
     *            the user name for database access
     * @param password
     *            the password for database access
     * @return DataSource
     */
    public static DataSource createDatasource(String driver, String jdbcUrl, String username, String password) {
        return new PooledDataSource(driver, jdbcUrl, username, password);
    }

    public boolean isSecurityEnabled() {
        return this.securityEnabled;
    }

    public DataSource getDatasource() {
        return this.dataSource;
    }

    public boolean getUseManagedTransactions() {
        return this.useManagedTransactions;
    }

    /**
     * Helper method to determine whether all access ids (user Id and group ids) should be used in lower case.
     *
     * @return true if all access ids should be used in lower case, false otherwise
     */
    public static boolean shouldUseLowerCaseForAccessIds() {
        return true;
    }
}
