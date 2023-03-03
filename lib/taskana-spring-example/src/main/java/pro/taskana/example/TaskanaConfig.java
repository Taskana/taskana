/*-
 * #%L
 * pro.taskana:taskana-spring-example
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
package pro.taskana.example;

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

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.internal.SpringTaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.user.api.UserService;
import pro.taskana.workbasket.api.WorkbasketService;

/** Class to set /load configuration for Taskana Library. */
@Configuration
@EnableTransactionManagement()
public class TaskanaConfig {

  @Value("${taskana.schemaName:TASKANA}")
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
  public TaskanaConfiguration taskanaConfiguration(
      DataSource dataSource,
      @Qualifier("taskanaPropertiesFileName") String propertiesFileName,
      @Qualifier("taskanaPropertiesDelimiter") String delimiter) {
    return new TaskanaConfiguration.Builder(dataSource, true, schemaName, false)
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

  @Bean
  public TaskanaEngine taskanaEngine(TaskanaConfiguration taskanaConfiguration)
      throws SQLException {
    return SpringTaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
  }

  @Bean
  public WorkbasketService workbasketService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getWorkbasketService();
  }

  @Bean
  public TaskService taskService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getTaskService();
  }

  @Bean
  public ClassificationService classificationService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getClassificationService();
  }

  @Bean
  public UserService userService(TaskanaEngine taskanaEngine) {
    return taskanaEngine.getUserService();
  }

  @Bean
  public ExampleBootstrap exampleBootstrap() {
    return new ExampleBootstrap();
  }

  @Profile("inmemorydb")
  @Configuration
  @PropertySource("classpath:customdb.properties")
  static class InmemoryDbProperties {}
}
