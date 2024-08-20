package io.kadai.rest.test;

import io.kadai.sampledata.SampleDataGenerator;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication
@ComponentScan("io.kadai")
@DependsOn("getKadaiEngine") // wait for schema to be created BEFORE inserting test data
public class TestConfiguration {

  @Autowired
  public TestConfiguration(
      @Value("${kadai.schemaName:KADAI}") String schemaName, DataSource dataSource) {
    new SampleDataGenerator(dataSource, schemaName).generateSampleData();
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
