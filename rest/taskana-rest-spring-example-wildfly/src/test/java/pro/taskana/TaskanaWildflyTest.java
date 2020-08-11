package pro.taskana;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
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

import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
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
    HttpEntity<String> httpEntity = new HttpEntity<>(getHeadersTeamlead_1());
    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        getRestTemplate()
            .exchange(
                "http://127.0.0.1:" + "8080" + "/taskana/api/v1/current-user-info",
                HttpMethod.GET,
                httpEntity,
                ParameterizedTypeReference.forType(TaskanaUserInfoRepresentationModel.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    TaskanaUserInfoRepresentationModel currentUser = response.getBody();
    assertEquals("teamlead-1", currentUser.getUserId());
    assertEquals(4, currentUser.getGroupIds().size());
    assertEquals(3, currentUser.getRoles().size());
  }

  @Test
  @RunAsClient
  public void should_ReturnUserFromLdap_WhenWildcardSearchIsConducted() {
    HttpEntity<String> httpEntity = new HttpEntity<>(getHeadersTeamlead_1());
    ResponseEntity<AccessIdListResource> response =
        getRestTemplate()
            .exchange(
                "http://127.0.0.1:8080/taskana/api/v1/access-ids?search-for=rig",
                HttpMethod.GET,
                httpEntity,
                ParameterizedTypeReference.forType(AccessIdListResource.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    AccessIdListResource accessIdList = response.getBody();
    assertEquals(2, accessIdList.size());
  }

  @Test
  @RunAsClient
  public void should_ReturnTask_WhenRequested() {
    HttpEntity<String> httpEntity = new HttpEntity<>(getHeadersTeamlead_1());
    ResponseEntity<TaskRepresentationModel> response =
        getRestTemplate()
            .exchange(
                "http://127.0.0.1:8080/taskana/api/v1/tasks/TKI:000000000000000000000000000000000001",
                HttpMethod.GET,
                httpEntity,
                ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody()).isNotNull();
  }

  static class AccessIdListResource extends ArrayList<AccessIdRepresentationModel> {
    private static final long serialVersionUID = 1L;
  }
}
