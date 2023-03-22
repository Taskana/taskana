package pro.taskana.testapi;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.util.Optional;
import javax.sql.DataSource;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.testcontainers.containers.Db2Container;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import pro.taskana.common.internal.configuration.DB;

public class DockerContainerCreator {

  private DockerContainerCreator() {
    throw new IllegalStateException("Utility class");
  }

  public static Optional<JdbcDatabaseContainer<?>> createDockerContainer(DB db) {
    switch (db) {
      case ORACLE:
        return Optional.of(
            new OracleContainer("gvenzl/oracle-xe:18-slim-faststart")
                .withDatabaseName("taskana")
                .withUsername("TEST_USER")
                .withPassword("testPassword")
                .withEnv("TZ", "Europe/Berlin"));
      case DB2:
        return Optional.of(
            new Db2Container(
                    DockerImageName.parse("taskana/db2:11.5")
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
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:14.7"))
                .withUsername("postgres")
                .withPassword("postgres")
                .withDatabaseName("postgres")
                .withCommand(
                    "/bin/sh",
                    "-c",
                    "localedef -i de_DE -c -f UTF-8 -A /usr/share/locale/locale.alias de_DE.UTF-8 "
                        + "&& export LANG=de_DE.UTF-8 "
                        + "&& /usr/local/bin/docker-entrypoint.sh postgres -c fsync=off")
                .waitingFor(
                    new LogMessageWaitStrategy()
                        .withRegEx(".*Datenbanksystem ist bereit, um Verbindungen anzunehmen.*\\s")
                        .withTimes(2)
                        .withStartupTimeout(Duration.of(60, SECONDS))));
      default:
        return Optional.empty();
    }
  }

  public static DataSource createDataSource(JdbcDatabaseContainer<?> container) {
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
}
