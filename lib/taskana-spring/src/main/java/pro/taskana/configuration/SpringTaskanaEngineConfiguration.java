package pro.taskana.configuration;

import java.sql.SQLException;

import javax.sql.DataSource;

import pro.taskana.SpringTaskanaEngineImpl;
import pro.taskana.TaskanaEngine;

/**
 * This class configures the TaskanaEngineConfiguration for spring
 */
public class SpringTaskanaEngineConfiguration extends TaskanaEngineConfiguration {

    public SpringTaskanaEngineConfiguration(DataSource dataSource, boolean useManagedTransactions,
        boolean securityEnabled) throws SQLException {
        super(dataSource, useManagedTransactions, securityEnabled);
    }

    public SpringTaskanaEngineConfiguration(DataSource dataSource, boolean useManagedTransactions,
        boolean securityEnabled, String propertiesFileName, String propertiesSeparator) throws SQLException {
        super(dataSource, useManagedTransactions, securityEnabled, propertiesFileName, propertiesSeparator);
    }

    /**
     * This method creates the Spring-based TaskanaEngine without an sqlSessionFactory
     *
     * @return the TaskanaEngine
     */
    @Override
    public TaskanaEngine buildTaskanaEngine() {
        this.useManagedTransactions = true;
        return new SpringTaskanaEngineImpl(this);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}
