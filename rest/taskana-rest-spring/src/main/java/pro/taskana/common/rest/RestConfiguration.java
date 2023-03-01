/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.rest;

import com.fasterxml.jackson.databind.cfg.HandlerInstantiator;
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

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.ConfigurationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.api.security.CurrentUserContext;
import pro.taskana.common.internal.SpringTaskanaEngine;
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
  public ConfigurationService configurationService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getConfigurationService();
  }

  @Bean
  public CurrentUserContext currentUserContext(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getCurrentUserContext();
  }

  @Bean
  @ConditionalOnMissingBean(TaskanaEngine.class)
  public TaskanaEngine getTaskanaEngine(TaskanaConfiguration taskanaConfiguration)
      throws SQLException {
    return SpringTaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
  }

  @Bean
  @ConditionalOnMissingBean(TaskanaConfiguration.class)
  public TaskanaConfiguration taskanaConfiguration(
      DataSource dataSource,
      @Qualifier("taskanaPropertiesFileName") String propertiesFileName,
      @Qualifier("taskanaPropertiesDelimiter") String delimiter) {
    return new TaskanaConfiguration.Builder(dataSource, true, schemaName)
        .initTaskanaProperties(propertiesFileName, delimiter)
        .build();
  }

  @Bean
  public String taskanaPropertiesFileName() {
    return "/taskana.properties";
  }

  @Bean
  public String taskanaPropertiesDelimiter() {
    return "|";
  }

  // Needed for injection into jackson deserializer.
  @Bean
  public HandlerInstantiator handlerInstantiator(ApplicationContext context) {
    return new SpringHandlerInstantiator(context.getAutowireCapableBeanFactory());
  }
}
