package pro.taskana.rest;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

public class ExampleRestConfiguration {

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("getTaskanaEngine") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(DataSource dataSource) {
    return new SampleDataGenerator(dataSource, schemaName);
  }
}
