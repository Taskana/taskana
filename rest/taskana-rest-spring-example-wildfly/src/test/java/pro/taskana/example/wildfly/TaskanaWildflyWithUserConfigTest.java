package pro.taskana.example.wildfly;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.io.File;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaUserInfoRepresentationModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.task.rest.models.TaskRepresentationModel;

/**
 * This test class is configured to run with postgres DB. In order to run them on db2, change the
 * datasource.jndi to java:jboss/datasources/ExampleDS and set taskana.schemaName to TASKANA
 */
@RunWith(Arquillian.class)
public class TaskanaWildflyWithUserConfigTest extends AbstractAccTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskanaWildflyWithUserConfigTest.class);

  @Deployment(testable = false)
  public static Archive<?> createTestArchive() {

    String applicationPropertyFile = "application-with-additional-user-config.properties";
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
  public void should_ReturnUserInformation_WhenRelevantUserInUseridHeader() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("user-1-1");
    headers.add("userid", "teamlead-1");
    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/taskana" + RestEndpoints.URL_CURRENT_USER),
            HttpMethod.GET,
            new HttpEntity<>(headers),
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
  public void should_ReturnUserInformation_WhenCurrentUserNotAuthorizedToUseUserFromHeader() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("user-2-1");
    headers.add("userid", "user-1-1");
    ResponseEntity<TaskanaUserInfoRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl("/taskana" + RestEndpoints.URL_CURRENT_USER),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ParameterizedTypeReference.forType(TaskanaUserInfoRepresentationModel.class));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    TaskanaUserInfoRepresentationModel currentUser = response.getBody();
    assertThat(currentUser).isNotNull();
    assertThat(currentUser.getUserId()).isEqualTo("user-2-1");
    assertThat(currentUser.getGroupIds()).hasSize(2);
    assertThat(currentUser.getRoles()).hasSize(1);
  }

  @Test
  @RunAsClient
  public void should_ReturnTask_When_OnlyTheUserInTheUseridHeaderIsAuthorized() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("user-1-1");
    headers.add("userid", "teamlead-1");
    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                "/taskana" + RestEndpoints.URL_TASKS_ID,
                "TKI:000000000000000000000000000000000005"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
  }

  @Test
  @RunAsClient
  public void should_ThrowException_When_RequestingTaskFromUnauthorizedUserInTheAdditionalHeader() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("teamlead-1");
    headers.add("userid", "user-1-1");
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(
                    "/taskana" + RestEndpoints.URL_TASKS_ID,
                    "TKI:000000000000000000000000000000000005"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  @RunAsClient
  public void should_ThrowException_When_NotUsingAdditionalHeaderBecauseCurrentUserNotAuthorized() {
    HttpHeaders headers = RestHelper.generateHeadersForUser("user-2-1");
    headers.add("userid", "teamlead-1");
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(
                    "/taskana" + RestEndpoints.URL_TASKS_ID,
                    "TKI:000000000000000000000000000000000005"),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
