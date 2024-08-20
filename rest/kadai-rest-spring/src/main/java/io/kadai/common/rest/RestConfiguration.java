package io.kadai.common.rest;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.common.api.ConfigurationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.common.api.security.CurrentUserContext;
import io.kadai.common.internal.SpringKadaiEngine;
import io.kadai.monitor.api.MonitorService;
import io.kadai.task.api.TaskService;
import io.kadai.user.api.UserService;
import io.kadai.workbasket.api.WorkbasketService;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/** Configuration for REST service. */
@Configuration
@ComponentScan("io.kadai")
@EnableTransactionManagement
public class RestConfiguration {

  private final String schemaName;

  public RestConfiguration(@Value("${kadai.schemaName:KADAI}") String schemaName) {
    this.schemaName = schemaName;
  }

  @Bean
  public ClassificationService getClassificationService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getClassificationService();
  }

  @Bean
  public TaskService getTaskService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getTaskService();
  }

  @Bean
  public MonitorService getMonitorService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getMonitorService();
  }

  @Bean
  public WorkbasketService getWorkbasketService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getWorkbasketService();
  }

  @Bean
  public UserService getUserService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getUserService();
  }

  @Bean
  public ConfigurationService configurationService(KadaiEngine kadaiEngine) {
    return kadaiEngine.getConfigurationService();
  }

  @Bean
  public CurrentUserContext currentUserContext(KadaiEngine kadaiEngine) {
    return kadaiEngine.getCurrentUserContext();
  }

  @Bean
  @ConditionalOnMissingBean(KadaiEngine.class)
  public KadaiEngine getKadaiEngine(KadaiConfiguration kadaiConfiguration)
      throws SQLException {
    return SpringKadaiEngine.buildKadaiEngine(kadaiConfiguration);
  }

  @Bean
  @ConditionalOnMissingBean(KadaiConfiguration.class)
  public KadaiConfiguration kadaiConfiguration(
      DataSource dataSource,
      @Qualifier("kadaiPropertiesFileName") String propertiesFileName,
      @Qualifier("kadaiPropertiesDelimiter") String delimiter) {
    return new KadaiConfiguration.Builder(dataSource, true, schemaName)
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

  // Needed for injection into jackson deserializer.
  @Bean
  public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
    return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
  }
}
