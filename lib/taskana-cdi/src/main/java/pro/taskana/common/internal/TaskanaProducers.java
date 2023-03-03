/*-
 * #%L
 * pro.taskana:taskana-cdi
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
package pro.taskana.common.internal;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;

/** TODO. */
@ApplicationScoped
public class TaskanaProducers {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaProducers.class);

  private static final String TASKANA_PROPERTIES = "taskana.properties";

  @Inject private TaskanaEngine taskanaEngine;

  private TaskanaConfiguration taskanaConfiguration;

  @PostConstruct
  public void init() {
    // Load Properties and get Datasource via Context
    // Load DataSource via Container
    Context ctx;
    DataSource dataSource;
    ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    try (InputStream propertyStream = classloader.getResourceAsStream(TASKANA_PROPERTIES)) {
      Properties properties = new Properties();
      ctx = new InitialContext();
      properties.load(propertyStream);
      dataSource = (DataSource) ctx.lookup(properties.getProperty("datasource.jndi"));
      if (LOGGER.isDebugEnabled()) {
        try (Connection connection = dataSource.getConnection()) {
          DatabaseMetaData metaData = connection.getMetaData();
          LOGGER.debug("---------------> {}", metaData);
        }
      }
      this.taskanaConfiguration =
          new TaskanaConfiguration.Builder(dataSource, true, "TASKANA", false)
              .initTaskanaProperties()
              .build();
    } catch (NamingException | SQLException | IOException e) {
      LOGGER.error("Could not start Taskana: ", e);
    }
  }

  @ApplicationScoped
  @Produces
  public TaskanaEngine generateTaskEngine() throws SQLException {
    return TaskanaEngine.buildTaskanaEngine(taskanaConfiguration);
  }

  @ApplicationScoped
  @Produces
  public TaskService generateTaskService() {
    return taskanaEngine.getTaskService();
  }

  @ApplicationScoped
  @Produces
  public ClassificationService generateClassificationService() {
    return taskanaEngine.getClassificationService();
  }

  @ApplicationScoped
  @Produces
  public WorkbasketService generateWorkbasketService() {
    return taskanaEngine.getWorkbasketService();
  }
}
