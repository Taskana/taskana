package io.kadai.example.wildfly;

import io.kadai.sampledata.SampleDataGenerator;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class KadaiWildflyConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(KadaiWildflyConfiguration.class);

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }

  @Bean
  @DependsOn("getKadaiEngine") // generate sample data after schema was inserted
  public SampleDataGenerator generateSampleData(
      DataSource dataSource,
      @Value("${kadai.schemaName:KADAI}") String schemaName,
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
      LOGGER.debug(
          "KADAI is using datasource '{}' from application.properties.",
          properties.getProperty("datasource.jndi"));
      dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
      return dataSource;
    } catch (Exception e) {
      LOGGER.error(
          "Caught exception when attempting to start Kadai with Datasource "
              + "from Jndi. Using default H2 datasource. ",
          e);
      throw e;
    }
  }

  @Bean
  public AdditionalUserProperties getAdditionalUserProperties(Environment env) {
    AdditionalUserProperties properties = new AdditionalUserProperties();
    properties.setAuthorizedUsers(List.of(env.getProperty("authorizedUsers", "").split("\\|")));
    properties.setEnableUserIdHeader(env.getProperty("enableUserIdHeader", Boolean.TYPE, false));
    return properties;
  }
}
