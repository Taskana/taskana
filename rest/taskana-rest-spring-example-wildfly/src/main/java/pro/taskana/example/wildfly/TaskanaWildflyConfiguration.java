package pro.taskana.example.wildfly;

import java.io.InputStream;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import pro.taskana.sampledata.SampleDataGenerator;

@Configuration
@Slf4j
public class TaskanaWildflyConfiguration {

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

  @Bean
  public DataSource dataSource() throws Exception {
    // First try to load Properties and get Datasource via jndi lookup
    Context ctx;
    DataSource dataSource;
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    try (InputStream propertyStream = classloader.getResourceAsStream("application.properties")) {
      Properties properties = new Properties();
      ctx = new InitialContext();
      properties.load(propertyStream);
      log.debug(
          "TASKANA is using datasource '{}' from application.properties.",
          properties.getProperty("datasource.jndi"));
      dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
      return dataSource;
    } catch (Exception e) {
      log.error(
          "Caught exception when attempting to start Taskana with Datasource "
              + "from Jndi. Using default H2 datasource. ",
          e);
      throw e;
    }
  }
}
