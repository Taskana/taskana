package pro.taskana;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.simplehistory.rest.TaskHistoryRestConfiguration;
import pro.taskana.simplehistory.rest.resource.TaskHistoryEventListResource;
import pro.taskana.simplehistory.rest.resource.TaskHistoryEventResource;

/** Controller for integration test. */
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = {TaskHistoryRestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskHistoryEventControllerIntTest {

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  String server = "http://127.0.0.1:";

  RestTemplate template = getRestTemplate();

  HttpEntity<String> request;

  @LocalServerPort int port;

  @Test
  void testGetAllHistoryEvent() {
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + "/api/v1/task-history-event",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(45);
  }

  @Test
  void testGetAllHistoryEventDescendingOrder() {
    String parameters =
        "/api/v1/task-history-event?sort-by=business-process-id&order=desc&page-size=3&page=1";
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server + port + parameters,
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(parameters))
        .isTrue();
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithoutDetails_When_ListIsQueried() {
    ResponseEntity<TaskHistoryEventListResource> response =
        template.exchange(
            server
                + port
                + "/api/v1/task-history-event?business-process-id=BPI:01"
                + "&sort-by=business-process-id&order=asc&page-size=6&page=1",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));

    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLinks()).isNotNull();
    assertThat(response.getBody().getMetadata()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent().stream().findFirst().get().getDetails()).isNull();
  }

  @Test
  void should_ReturnSpecificTaskHistoryEventWithDetails_When_SingleEventIsQueried() {
    ResponseEntity<TaskHistoryEventResource> response =
        template.exchange(
            server + port + "/api/v1/task-history-event/HEI:000000000000000000000000000000000000",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskHistoryEventResource.class));

    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getLinks()).isNotNull();
    assertThat(response.getBody().getDetails()).isNotNull();
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
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
  void testGetHistoryEventOfDate() {
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
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(23);
  }

  @Test
  void testGetSecondPageSortedByKey() {
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
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(parameters))
        .isTrue();
    assertThat(response.getBody().getLink("allTaskHistoryEvent")).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink("allTaskHistoryEvent")
                .getHref()
                .endsWith("/api/v1/task-history-event"))
        .isTrue();
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
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
