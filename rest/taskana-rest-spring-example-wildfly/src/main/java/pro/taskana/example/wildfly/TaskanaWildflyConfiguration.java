package pro.taskana.example.wildfly;

import java.io.InputStream;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

@Configuration
public class TaskanaWildflyConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaWildflyConfiguration.class);

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "datasource")
  public DataSourceProperties dataSourceProperties(
      @Value("${taskana.schemaName:TASKANA}") String schemaName) {
    DataSourceProperties props = new DataSourceProperties();
    props.setUrl(
        "jdbc:h2:mem:taskana;IGNORECASE=TRUE;LOCK_MODE=0;INIT=CREATE SCHEMA IF NOT EXISTS "
            + schemaName);
    return props;
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

  @Bean
  public DataSource dataSource(DataSourceProperties dsProperties) {
    // First try to load Properties and get Datasource via jndi lookup
    Context ctx;
    DataSource dataSource;
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    try (InputStream propertyStream = classloader.getResourceAsStream("application.properties")) {
      Properties properties = new Properties();
      ctx = new InitialContext();
      properties.load(propertyStream);
      dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
      return dataSource;
    } catch (Exception e) {
      LOGGER.error(
          "Caught exception when attempting to start Taskana with Datasource "
              + "from Jndi. Using default H2 datasource. ",
          e);
      return dsProperties.initializeDataSourceBuilder().build();
    }
  }
}
