package io.kadai.example;

import static org.awaitility.Awaitility.await;
import static org.awaitility.Durations.ONE_HUNDRED_MILLISECONDS;
import static org.awaitility.Durations.TWO_SECONDS;
import static org.hamcrest.CoreMatchers.equalTo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.io.FileUtils;
import org.h2.jdbc.JdbcSQLNonTransientConnectionException;
import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ExampleBootstrapTest {

  @Deployment(testable = false, order = 10)
  public static Archive<?> createDeployment() throws Exception {
    EnterpriseArchive deployment = ShrinkWrap.create(EnterpriseArchive.class, "kadai.ear");

    File[] libs =
        Maven.resolver()
            .loadPomFromFile("pom.xml")
            .importCompileAndRuntimeDependencies()
            .resolve()
            .withTransitivity()
            .asFile();
    deployment.addAsLibraries(libs);

    JavaArchive ejbModule = ShrinkWrap.create(JavaArchive.class, "kadai.jar");
    ejbModule.addClasses(KadaiEjb.class, ExampleBootstrap.class, ExampleStartupException.class);
    ejbModule.addAsResource("kadai.properties");
    deployment.addAsModule(ejbModule);

    deployment.addAsManifestResource("META-INF/beans.xml", "beans.xml");
    return deployment;
  }

  @BeforeClass
  public static void cleanKadaiH2DataFolder() throws IOException {
    // Delete Kadai folder if exists
    Path kadaiH2Data = Path.of(System.getProperty("user.home"), "kadai-h2-data");
    if (Files.exists(kadaiH2Data)) {
      FileUtils.forceDelete(kadaiH2Data.toFile());
    }
  }

  @Test
  public void should_count_tasks_after_example_cdi_application_was_deployed() throws Exception {
    // this test method is started that fast that the commit
    // from io.kadai.example.ExampleBootstrap.init is not completed
    // so we need to wait here a bit
    // https://www.baeldung.com/awaitility-testing
    await()
        .atLeast(ONE_HUNDRED_MILLISECONDS)
        .atMost(TWO_SECONDS)
        .with()
        .pollInterval(ONE_HUNDRED_MILLISECONDS)
        .until(
            () -> {
              try {
                return countTasksByName("BootstrapTask");
              } catch (JdbcSQLSyntaxErrorException | JdbcSQLNonTransientConnectionException e) {
                // ignore this Exception, because in the beginning the schema is not created
                return 0;
              }
            },
            equalTo(1));
  }

  private Connection getConnection() throws Exception {
    return DriverManager.getConnection(
        "jdbc:h2:~/kadai-h2-data/testdb;NON_KEYWORDS=KEY,VALUE;AUTO_SERVER=TRUE;"
            + "IGNORECASE=TRUE;LOCK_MODE=0",
        "sa",
        "sa");
  }

  private int countTasksByName(String taskName) throws Exception {

    Class.forName("org.h2.Driver");
    int resultCount = 0;
    try (Connection conn = getConnection();
        PreparedStatement statement =
            conn.prepareStatement("SELECT COUNT(ID) FROM KADAI.TASK WHERE NAME = ?")) {
      statement.setString(1, taskName);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        resultCount = rs.getInt(1);
      }
    }
    return resultCount;
  }
}
