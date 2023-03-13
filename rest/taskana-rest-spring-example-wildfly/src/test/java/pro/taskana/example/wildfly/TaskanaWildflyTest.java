package pro.taskana.example.wildfly;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.io.File;
import java.util.List;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.task.rest.models.TaskRepresentationModel;

/**
 * This test class is configured to run with postgres DB if you want to run it with h2 it is needed.
 * to change data source configuration at project-defaults.yml.
 */
@RunWith(Arquillian.class)
public class TaskanaWildflyTest extends AbstractAccTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskanaWildflyTest.class);

  @Deployment(testable = false)
  public static Archive<?> createTestArchive() {

    String applicationPropertyFile = "application.properties";
    String dbType = System.getProperty("db.type");
    if (dbType != null && !dbType.isEmpty()) {
      applicationPropertyFile = "application-" + dbType + ".properties";
    }

    LOGGER.info(
        "Running with db.type '{}' and using property file '{}'", dbType, applicationPropertyFile);

    File[] files =
        Maven.resolver()
            .loadPomFromFile("pom.xml")
            .importRuntimeDependencies()
            .resolve()
            .withTransitivity()
            .asFile();

    return ShrinkWrap.create(WebArchive.class, "taskana.war")
        .addPackages(true, "pro.taskana")
        .addAsResource("taskana.properties")
        .addAsResource(applicationPropertyFile, "application.properties")
        .addAsResource("taskana-test.ldif")
        .addAsWebInfResource("int-test-web.xml", "web.xml")
        .addAsWebInfResource("int-test-jboss-web.xml", "jboss-web.xml")
        .addAsLibraries(files);
  }

  @Test
  @RunAsClient
  public void should_ReturnUserInformationForAuthenticatedUser_IfRequested() {
    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/taskana" + RestEndpoints.URL_CURRENT_USER),
            HttpMethod.GET,
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            ParameterizedTypeReference.forType(TaskanaUserInfoRepresentationModel.class));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TaskanaUserInfoRepresentationModel currentUser = response.getBody();
    assertThat(currentUser).isNotNull();
    assertThat(currentUser.getUserId()).isEqualTo("teamlead-1");
    assertThat(currentUser.getGroupIds()).hasSize(4);
    assertThat(currentUser.getRoles()).hasSize(3);
  }

  @Test
  @RunAsClient
  public void should_ReturnUserFromLdap_When_WildcardSearchIsConducted() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/taskana" + RestEndpoints.URL_ACCESS_ID + "?search-for=rig"),
            HttpMethod.GET,
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            new ParameterizedTypeReference<List<AccessIdRepresentationModel>>() {});
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  @RunAsClient
  public void should_ReturnTask_When_Requested() {
    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                "/taskana" + RestEndpoints.URL_TASKS_ID,
                "TKI:000000000000000000000000000000000001"),
            HttpMethod.GET,
            new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1")),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }
}
