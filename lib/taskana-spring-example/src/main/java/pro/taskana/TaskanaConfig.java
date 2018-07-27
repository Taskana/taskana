package pro.taskana;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
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
 */
@Configuration
@EnableTransactionManagement()
public class TaskanaConfig {

    @Value("${taskana.schemaName:TASKANA}")
    private String schemaName;

    @Profile("inmemorydb")
    @Configuration
    @PropertySource("classpath:customdb.properties")
    static class InmemoryDBProperties {
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "customdb.datasource")
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
    public SpringTaskanaEngineConfiguration taskanaEngineConfiguration(DataSource dataSource) throws SQLException {
        SpringTaskanaEngineConfiguration taskanaEngineConfiguration = new SpringTaskanaEngineConfiguration(dataSource,
            true, false, schemaName);
        return taskanaEngineConfiguration;
    }

    @Bean
    public TaskanaEngine taskanaEngine(SpringTaskanaEngineConfiguration taskanaEngineConfiguration) {
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

    @Bean
    public ExampleBootstrap exampleBootstrap() {
      return new ExampleBootstrap() ;
    }
}
