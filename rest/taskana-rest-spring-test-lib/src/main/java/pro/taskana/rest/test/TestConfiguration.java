package pro.taskana.rest.test;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

@SpringBootApplication
@ComponentScan("pro.taskana")
@DependsOn("getTaskanaEngine") // wait for schema to be created BEFORE inserting test data
public class TestConfiguration {

  @Autowired
  public TestConfiguration(
      @Value("${taskana.schemaName:TASKANA}") String schemaName, DataSource dataSource) {
    new SampleDataGenerator(dataSource, schemaName).generateSampleData();
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
