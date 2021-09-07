package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.SpringTaskanaEngineConfiguration;
import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.TaskService;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketService;

/** Configuration for REST service. */
@Configuration
@ComponentScan("pro.taskana")
@EnableTransactionManagement
public class RestConfiguration {

  private final String schemaName;

  public RestConfiguration(@Value("${taskana.schemaName:TASKANA}") String schemaName) {
    this.schemaName = schemaName;
  }

  @Bean
  public ClassificationService getClassificationService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getClassificationService();
  }

  @Bean
  public TaskService getTaskService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getTaskService();
  }

  @Bean
  public MonitorService getMonitorService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getMonitorService();
  }

  @Bean
  public WorkbasketService getWorkbasketService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getWorkbasketService();
  }

  @Bean
  public UserService getUserService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getUserService();
  }

  @Bean
  @ConditionalOnMissingBean(TaskanaEngine.class)
  public TaskanaEngine getTaskanaEngine(TaskanaEngineConfiguration taskanaEngineConfiguration)
      throws SQLException {
    return taskanaEngineConfiguration.buildTaskanaEngine();
  }

  @Bean
  @ConditionalOnMissingBean(TaskanaEngineConfiguration.class)
  public TaskanaEngineConfiguration taskanaEngineConfiguration(DataSource dataSource) {
    return new SpringTaskanaEngineConfiguration(dataSource, true, true, schemaName);
  }

  // Needed for injection into jackson deserializer.
  @Bean
  public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
    return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
  }
}
