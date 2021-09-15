package pro.taskana.common.test.config;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

import pro.taskana.common.internal.configuration.DB;

public class TestContainerExtension implements AfterAllCallback {

  private final DataSource dataSource;
  private final String schemaName;
  private final JdbcDatabaseContainer<?> container;

  public TestContainerExtension() {
    DB db = getTestDatabase();
    Optional<JdbcDatabaseContainer<?>> container = createDockerContainer(db);
    if (container.isPresent()) {
      this.container = container.get();
      this.container.start();
      dataSource = createDataSource(this.container);
    } else {
      dataSource = createDataSourceForH2();
      this.container = null;
    }
    schemaName = determineSchemaName(db);
  }

  @Override
  public void afterAll(ExtensionContext context) {
    if (container != null) {
      container.stop();
    }
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  public String getSchemaName() {
    return schemaName;
  }

  private DB getTestDatabase() {
    String property = System.getenv("db.type");
    DB db;
    try {
      db = DB.valueOf(property);
    } catch (Exception ex) {
      db = DB.H2;
    }
    return db;
  }

  private static String determineSchemaName(DB db) {
    return db == DB.POSTGRES ? "taskana" : "TASKANA";
  }

  private static DataSource createDataSource(JdbcDatabaseContainer<?> container) {
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            container.getDriverClassName(),
            container.getJdbcUrl(),
            container.getUsername(),
            container.getPassword());
    ds.setPoolTimeToWait(50);
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly
    return ds;
  }

  private static Optional<JdbcDatabaseContainer<?>> createDockerContainer(DB db) {
    switch (db) {
      case DB2:
        return Optional.of(
            new Db2Container(
                    DockerImageName.parse("taskana/db2:11.1")
                        .asCompatibleSubstituteFor("ibmcom/db2"))
                .waitingFor(
                    new LogMessageWaitStrategy()
                        .withRegEx(".*DB2START processing was successful.*")
                        .withStartupTimeout(Duration.of(60, SECONDS)))
                .withUsername("db2inst1")
                .withPassword("db2inst1-pwd")
                .withDatabaseName("TSKDB"));
      case POSTGRES:
        return Optional.of(
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:10"))
                .withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("postgres")
                .withCommand(
                    "/bin/sh",
                    "-c",
                    "localedef -i de_DE -c -f UTF-8 -A /usr/share/locale/locale.alias de_DE.UTF-8 "
                        + "&& export LANG=de_DE.UTF-8 "
                        + "&& ./docker-entrypoint.sh postgres -c fsync=off")
                .waitingFor(
                    new LogMessageWaitStrategy()
                        .withRegEx(".*Datenbanksystem ist bereit, um Verbindungen anzunehmen.*\\s")
                        .withTimes(2)
                        .withStartupTimeout(Duration.of(60, SECONDS))));
      default:
        return Optional.empty();
    }
  }

  private static DataSource createDataSourceForH2() {
    PooledDataSource ds =
        new PooledDataSource(
            Thread.currentThread().getContextClassLoader(),
            "org.h2.Driver",
            "jdbc:h2:mem:"
                + UUID.randomUUID()
                + ";LOCK_MODE=0;"
                + "INIT=CREATE SCHEMA IF NOT EXISTS TASKANA\\;"
                + "SET COLLATION DEFAULT_de_DE ",
            "sa",
            "sa");
    ds.setPoolTimeToWait(50);
    ds.forceCloseAll(); // otherwise, the MyBatis pool is not initialized correctly

    return ds;
  }
}
