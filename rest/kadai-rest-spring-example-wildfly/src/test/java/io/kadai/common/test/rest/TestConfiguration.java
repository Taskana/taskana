package io.kadai.common.test.rest;

import io.kadai.common.internal.configuration.DB;
import io.kadai.sampledata.SampleDataGenerator;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(TestConfiguration.class);

  @Autowired
  public TestConfiguration(
      @Value("${kadai.schemaName:KADAI}") String schemaName, DataSource dataSource) {
    if (LOGGER.isDebugEnabled()) {
      try (Connection connection = dataSource.getConnection()) {
        LOGGER.debug(
            "Using database of type {} with url '{}'",
            DB.getDB(connection).dbProductName,
            connection.getMetaData().getURL());
      } catch (SQLException e) {
        LOGGER.error(e.getMessage(), e);
      }
    }
    new SampleDataGenerator(dataSource, schemaName).generateSampleData();
  }

  @Bean
  public PlatformTransactionManager txManager(DataSource dataSource) {
    return new DataSourceTransactionManager(dataSource);
  }
}
