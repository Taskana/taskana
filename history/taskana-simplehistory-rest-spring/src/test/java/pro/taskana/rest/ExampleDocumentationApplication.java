package pro.taskana.rest;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.simplehistory.rest.TaskHistoryRestConfiguration;
import pro.taskana.simplehistory.rest.sampledata.SampleDataGenerator;

/** Example Application to create the documentation. */
@SpringBootApplication
@ComponentScan(basePackages = "pro.taskana.rest.simplehistory")
@Import({TaskHistoryRestConfiguration.class})
public class ExampleDocumentationApplication {

  @Value("${taskana.schemaName:TASKANA}")
  private String schemaName;

  @Autowired private SampleDataGenerator sampleDataGenerator;

  public static void main(String[] args) {
    SpringApplication.run(ExampleDocumentationApplication.class, args);
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "datasource")
  public DataSourceProperties dataSourceProperties() {
    DataSourceProperties props = new DataSourceProperties();
    props.setUrl(
        "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS "
            + schemaName);
    return props;
  }

  @Bean
  public DataSource dataSource(DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().build();
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("getTaskanaEngine") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(DataSource dataSource) throws SQLException {
    sampleDataGenerator = new SampleDataGenerator(dataSource);
    sampleDataGenerator.generateSampleData(schemaName);
    return sampleDataGenerator;
  }
}
