package pro.taskana;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.resource.TaskHistoryEventListResource;
import pro.taskana.rest.simplehistory.TaskHistoryRestConfiguration;
import pro.taskana.rest.simplehistory.sampledata.SampleDataGenerator;

/** Controller for integration test. */
@EnableAutoConfiguration
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {TaskHistoryRestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskHistoryEventControllerIntTest {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TaskHistoryEventControllerIntTest.class);

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  String server = "http://127.0.0.1:";

  RestTemplate template;

  HttpEntity<String> request;

  @LocalServerPort int port;

  @Autowired private DataSource dataSource;

  @BeforeEach
  public void beforeEach() {
    template = getRestTemplate();
    SampleDataGenerator sampleDataGenerator;
    try {
      sampleDataGenerator = new SampleDataGenerator(dataSource);
      sampleDataGenerator.generateSampleData(schemaName);
    } catch (SQLException e) {
      throw new SystemException("tried to reset DB and caught Exception " + e, e);
    }
  }

  @Test
  public void testGetAllHistoryEvent() {
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + "/api/v1/task-history-event",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(50);
  }

  @Test
  public void testGetAllHistoryEventDescendingOrder() {
    String parameters =
        "/api/v1/task-history-event?sort-by=business-process-id&order=desc&page-size=3&page=1";
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + parameters,
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
    assertThat(response.getBody().getLink(Link.REL_SELF).getHref().endsWith(parameters)).isTrue();
  }

  @Test
  public void testGetSpecificTaskHistoryEvent() {
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server
                + port
                + "/api/v1/task-history-event?business-process-id=BPI:01"
                + "&sort-by=business-process-id&order=asc&page-size=6&page=1",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));

    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getLinks()).isNotNull();
    assertThat(response.getBody().getMetadata()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
  }

  @Test
  public void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              server + port + "/api/v1/task-history-event?invalid=BPI:01",
              HttpMethod.GET,
              request,
              ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void testGetHistoryEventOfDate() {
    String currentTime = LocalDateTime.now().toString();
    final String finalCurrentTime = currentTime;
    ThrowingCallable httpCall =
        () -> {
          template.exchange(
              server + port + "/api/v1/task-history-event?created=" + finalCurrentTime,
              HttpMethod.GET,
              request,
              ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
        };
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining(currentTime)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    // correct Format 'yyyy-MM-dd'
    currentTime = currentTime.substring(0, 10);
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + "/api/v1/task-history-event?created=" + currentTime,
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(25);
  }

  @Test
  public void testGetSecondPageSortedByKey() {
    String parameters =
        "/api/v1/task-history-event?sort-by=workbasket-key&order=desc&page=2&page-size=2";
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + parameters,
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));

    assertThat(response.getBody().getContent()).hasSize(2);
    assertThat(response.getBody().getContent().iterator().next().getWorkbasketKey())
        .isEqualTo("WBI:100000000000000000000000000000000002");
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_SELF).getHref().endsWith(parameters)).isTrue();
    assertThat(response.getBody().getLink("allTaskHistoryEvent")).isNotNull();
    assertThat(
            response
                .getBody()
                .getLink("allTaskHistoryEvent")
                .getHref()
                .endsWith("/api/v1/task-history-event"))
        .isTrue();
    assertThat(response.getBody().getLink(Link.REL_FIRST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_LAST)).isNotNull();
  }

  /**
   * Return a REST template which is capable of dealing with responses in HAL format.
   *
   * @return RestTemplate
   */
  private RestTemplate getRestTemplate() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.registerModule(new Jackson2HalModule());

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
    converter.setObjectMapper(mapper);

    return new RestTemplate(Collections.singletonList(converter));
  }
}
