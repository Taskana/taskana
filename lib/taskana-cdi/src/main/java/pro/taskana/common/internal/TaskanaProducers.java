package pro.taskana.common.internal;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.Startup;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
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

  // initalized during post construct
  private TaskanaConfiguration taskanaConfiguration;

  private final TaskanaEngine taskanaEngine;

  public TaskanaProducers() {
    this.taskanaEngine = null;
  }

  @Inject
  public TaskanaProducers(TaskanaEngine taskanaEngine) {
    this.taskanaEngine = taskanaEngine;
  }

  /**
   * The parameter `@Observes Startup` makes sure that the dependency injection framework calls this
   * method on system startup. And to do that, it needs to call `@PostConstruct start()` first.
   *
   * @param startup just the startup event
   */
  @SuppressWarnings("unused")
  private void forceEagerInitialization(@Observes Startup startup) {
    LOGGER.info("startup={}", startup);
  }

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
      throw new TaskanaCdiStartupException(e);
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
