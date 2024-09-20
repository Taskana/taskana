package io.kadai.example;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.internal.SpringKadaiEngine;
import io.kadai.task.api.TaskService;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketService;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Class to set /load configuration for Kadai Library. */
@Configuration
@EnableTransactionManagement()
public class KadaiConfig {

  @Value("${kadai.schemaName:TASKANA}")
  private String schemaName;

  @Bean
  @Primary
  @ConfigurationProperties(prefix = "customdb.datasource")
  public DataSourceProperties dataSourceProperties() {
    return new DataSourceProperties();
  }

  @Bean
  @Primary
  public DataSource dataSource(DataSourceProperties properties) {
    return properties.initializeDataSourceBuilder().build();
  }

  @Bean
  public DataSourceTransactionManager transactionManager(DataSource dataSource) {
    DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
    transactionManager.setDataSource(dataSource);
    return transactionManager;
  }

  @Bean
  public KadaiConfiguration kadaiConfiguration(
      DataSource dataSource,
      @Qualifier("kadaiPropertiesFileName") String propertiesFileName,
      @Qualifier("kadaiPropertiesDelimiter") String delimiter) {
    return new KadaiConfiguration.Builder(dataSource, true, schemaName, false)
        .initKadaiProperties(propertiesFileName, delimiter)
        .build();
  }

  @Bean
  public String kadaiPropertiesFileName() {
    return "/kadai.properties";
  }

  @Bean
  public String kadaiPropertiesDelimiter() {
    return "|";
  }

  @Bean
  public KadaiEngine kadaiEngine(KadaiConfiguration kadaiConfiguration) throws SQLException {
    return SpringKadaiEngine.buildKadaiEngine(kadaiConfiguration);
  }

  @Bean
  public WorkbasketService workbasketService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getWorkbasketService();
  }

  @Bean
  public TaskService taskService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getTaskService();
  }

  @Bean
  public ClassificationService classificationService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getClassificationService();
  }

  @Bean
  public UserService userService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getUserService();
  }

  @Bean
  public ExampleBootstrap exampleBootstrap(TaskService taskService, KadaiEngine kadaiEngine) {
    return new ExampleBootstrap(taskService, kadaiEngine);
  }

  @Profile("inmemorydb")
  @Configuration
  @PropertySource("classpath:customdb.properties")
  static class InmemoryDbProperties {}
}
