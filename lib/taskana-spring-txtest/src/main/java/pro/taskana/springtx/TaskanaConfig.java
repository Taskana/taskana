package pro.taskana.springtx;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.ClassificationService;
import pro.taskana.TaskService;
import pro.taskana.TaskanaEngine;
import pro.taskana.WorkbasketService;
import pro.taskana.configuration.SpringTaskanaEngineConfiguration;

/**
 * Class to set /load configuration for Taskana Library
 *
 * @author v101536 (Kilian Burkhardt)
 */
@Configuration
@EnableTransactionManagement()
public class TaskanaConfig {

    @Profile("inmemorydb")
    @Configuration
    @PropertySource("classpath:postkorb-inmemorydb.properties")
    static class InmemoryDBProperties {
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "postkorb.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        DataSource dataSource = properties.initializeDataSourceBuilder().build();
        // if TaskanaEngineImpl runs with SpringManagedTransactionFactory, then
        // there is no need to wrap the dataSource into TransactionAwareDataSourceProxy ...
        // return new TransactionAwareDataSourceProxy(dataSource);
        return dataSource;
    }

    @Bean
    public DataSourceTransactionManager transactionManager(DataSource dataSource) {
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
        transactionManager.setDataSource(dataSource);
        return transactionManager;
    }

    @Bean
    public SpringTaskanaEngineConfiguration taskanaEngineConfiguration(DataSource dataSource) {
        SpringTaskanaEngineConfiguration taskanaEngineConfiguration = new SpringTaskanaEngineConfiguration(false);
        taskanaEngineConfiguration.setDataSource(dataSource);
        return taskanaEngineConfiguration;
    }

    @Bean
    public TaskanaEngine taskanaEngine(SpringTaskanaEngineConfiguration taskanaEngineConfiguration)
        throws SQLException {
        TaskanaEngine taskanaEngine = taskanaEngineConfiguration.buildTaskanaEngine();
        // taskanaEngine.setConnectionManagementMode(TaskanaEngine.ConnectionManagementMode.EXPLICIT);
        return taskanaEngine;
    }

    @Bean
    public WorkbasketService workbasketService(TaskanaEngine taskanaEngine) {
        return taskanaEngine.getWorkbasketService();
    }

    @Bean
    public TaskService taskService(TaskanaEngine taskanaEngine) {
        return taskanaEngine.getTaskService();
    }

    @Bean
    public ClassificationService classificationService(TaskanaEngine taskanaEngine) {
        return taskanaEngine.getClassificationService();
    }

}
