package pro.taskana.example.boot;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.configuration.DbSchemaCreator;
import pro.taskana.sampledata.SampleDataGenerator;

@Configuration
public class ExampleRestConfiguration {

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("taskanaEngineConfiguration") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(
      TaskanaEngineConfiguration taskanaEngineConfiguration,
      DataSource dataSource,
      @Value("${generateSampleData:true}") boolean generateSampleData)
      throws SQLException {
    DbSchemaCreator dbSchemaCreator =
        new DbSchemaCreator(dataSource, taskanaEngineConfiguration.getSchemaName());
    dbSchemaCreator.run();
    SampleDataGenerator sampleDataGenerator =
        new SampleDataGenerator(dataSource, taskanaEngineConfiguration.getSchemaName());
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
    return sampleDataGenerator;
  }

  @Bean
  @DependsOn("generateSampleData")
  public TaskanaEngine getTaskanaEngine(TaskanaEngineConfiguration taskanaEngineConfiguration)
      throws SQLException {
    return TaskanaEngine.buildTaskanaEngine(taskanaEngineConfiguration);
  }

  // only required to let the adapter example connect to the same database
  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server inMemoryH2DatabaseaServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9095");
  }
}
