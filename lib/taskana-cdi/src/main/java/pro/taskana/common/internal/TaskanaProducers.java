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

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.task.api.TaskService;
import pro.taskana.workbasket.api.WorkbasketService;

/** The TaskanaProducers ... TODO. */
@ApplicationScoped
public class TaskanaProducers {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaProducers.class);

  private static final String TASKANA_PROPERTIES = "taskana.properties";

  @Inject private TaskanaEngine taskanaEngine;

  private TaskanaEngineConfiguration taskanaEngineConfiguration;

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
      try (Connection connection = dataSource.getConnection()) {
        DatabaseMetaData metaData = connection.getMetaData();
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("---------------> {}", metaData);
        }
      }
      this.taskanaEngineConfiguration =
          new TaskanaEngineConfiguration(dataSource, true, false, "TASKANA");
    } catch (NamingException | SQLException | IOException e) {
      LOGGER.error("Could not start Taskana: ", e);
    }
  }

  @ApplicationScoped
  @Produces
  public TaskanaEngine generateTaskEngine() throws SQLException {
    return taskanaEngineConfiguration.buildTaskanaEngine();
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
