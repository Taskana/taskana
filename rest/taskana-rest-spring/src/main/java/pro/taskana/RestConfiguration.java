package pro.taskana;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.converter.json.SpringHandlerInstantiator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.monitor.api.MonitorService;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;

/** Configuration for REST service. */
@Configuration
@ComponentScan
@EnableTransactionManagement
public class RestConfiguration {

  @Value("${taskana.schemaName:TASKANA}")
  private String schemaName;

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
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public TaskanaEngine getTaskanaEngine(TaskanaEngineConfiguration taskanaEngineConfiguration) {
    return taskanaEngineConfiguration.buildTaskanaEngine();
  }

  @Bean
  @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
  public TaskanaEngineConfiguration taskanaEngineConfiguration(DataSource dataSource)
      throws SQLException {
    return new SpringTaskanaEngineConfiguration(dataSource, true, true, schemaName);
  }

  @Bean
  public LdapClient ldapClient() {
    return new LdapClient();
  }

  // Needed for injection into jackson deserializer.
  @Bean
  public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
    return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
  }
}
