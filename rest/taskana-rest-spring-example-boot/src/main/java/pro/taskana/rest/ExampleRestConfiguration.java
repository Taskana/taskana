package pro.taskana.rest;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

@Configuration
public class ExampleRestConfiguration {

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("getTaskanaEngine") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(
      DataSource dataSource,
      @Value("${taskana.schemaName:TASKANA}") String schemaName,
      @Value("${generateSampleData:true}") boolean generateSampleData) {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    if (generateSampleData) {
      sampleDataGenerator.generateSampleData();
    }
    return sampleDataGenerator;
  }

  // only required to let the adapter example connect to the same database
  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server inMemoryH2DatabaseaServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9095");
  }
}
