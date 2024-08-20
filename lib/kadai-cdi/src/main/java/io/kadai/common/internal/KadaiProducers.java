package io.kadai.common.internal;

import io.kadai.KadaiConfiguration;
import io.kadai.classification.api.ClassificationService;
import io.kadai.common.api.KadaiEngine;
import io.kadai.task.api.TaskService;
import io.kadai.workbasket.api.WorkbasketService;
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

/** TODO. */
@ApplicationScoped
public class KadaiProducers {

  private static final Logger LOGGER = LoggerFactory.getLogger(KadaiProducers.class);

  private static final String KADAI_PROPERTIES = "kadai.properties";

  // initalized during post construct
  private KadaiConfiguration kadaiConfiguration;

  private final KadaiEngine kadaiEngine;

  public KadaiProducers() {
    this.kadaiEngine = null;
  }

  @Inject
  public KadaiProducers(KadaiEngine kadaiEngine) {
    this.kadaiEngine = kadaiEngine;
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
    try (InputStream propertyStream = classloader.getResourceAsStream(KADAI_PROPERTIES)) {
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
      this.kadaiConfiguration =
          new KadaiConfiguration.Builder(dataSource, true, "KADAI", false)
              .initKadaiProperties()
              .build();
    } catch (NamingException | SQLException | IOException e) {
      throw new KadaiCdiStartupException(e);
    }
  }

  @ApplicationScoped
  @Produces
  public KadaiEngine generateTaskEngine() throws SQLException {
    return KadaiEngine.buildKadaiEngine(kadaiConfiguration);
  }

  @ApplicationScoped
  @Produces
  public TaskService generateTaskService() {
    return kadaiEngine.getTaskService();
  }

  @ApplicationScoped
  @Produces
  public ClassificationService generateClassificationService() {
    return kadaiEngine.getClassificationService();
  }

  @ApplicationScoped
  @Produces
  public WorkbasketService generateWorkbasketService() {
    return kadaiEngine.getWorkbasketService();
  }
}
