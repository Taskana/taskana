package io.kadai.common.internal;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiCdiTestRestController;
import io.kadai.KadaiEjb;
import io.kadai.RestApplication;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import org.apache.commons.io.FileUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class KadaiProducersTest {

  private static final String REST_TEST_URL = "http://127.0.0.1:8080/kadai/rest/test";

  private static final HttpClient HTTP_CLIENT =
      HttpClient.newBuilder()
          .version(HttpClient.Version.HTTP_1_1)
          .connectTimeout(Duration.ofSeconds(10))
          .build();

  @Deployment(testable = false)
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
    ejbModule.addClasses(KadaiProducers.class, KadaiEjb.class, KadaiCdiStartupException.class);
    ejbModule.addAsResource("kadai.properties");
    deployment.addAsModule(ejbModule);

    WebArchive webArchive =
        ShrinkWrap.create(WebArchive.class, "kadai.war")
            .addClasses(KadaiCdiTestRestController.class, RestApplication.class)
            .addAsWebInfResource("META-INF/beans.xml", "beans.xml")
            .addAsWebInfResource("int-test-jboss-web.xml", "jboss-web.xml");
    deployment.addAsModule(webArchive);

    deployment.addAsManifestResource("META-INF/beans.xml", "beans.xml");
    return deployment;
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    // Delete Kadai folder if exists
    Path kadaiH2Data = Path.of(System.getProperty("user.home"), "kadai-h2-data");
    if (Files.exists(kadaiH2Data)) {
      FileUtils.forceDelete(kadaiH2Data.toFile());
    }
  }

  @Test
  public void testCommit() throws Exception {
    HttpRequest getRequest = HttpRequest.newBuilder(new URI(REST_TEST_URL)).GET().build();

    HttpResponse<String> response =
        HTTP_CLIENT.send(getRequest, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(200);
    assertThat(countTasksByName("startTask")).isEqualTo(1);
  }

  @Test
  public void testRollback() throws Exception {
    HttpRequest postRequest =
        HttpRequest.newBuilder(new URI(REST_TEST_URL))
            .POST(HttpRequest.BodyPublishers.ofString("null"))
            .build();

    HttpResponse<String> response =
        HTTP_CLIENT.send(postRequest, HttpResponse.BodyHandlers.ofString());

    assertThat(response.statusCode()).isEqualTo(204);
    assertThat(countTasksByName("triggerRollback")).isZero();
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
    try (Connection conn = getConnection()) {
      try (PreparedStatement statement =
          conn.prepareStatement("SELECT COUNT(ID) FROM KADAI.TASK WHERE NAME = ?")) {
        statement.setString(1, taskName);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
          resultCount = rs.getInt(1);
        }
      }
    }
    return resultCount;
  }
}
