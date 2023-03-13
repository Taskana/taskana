package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponentsBuilder;
import pro.taskana.task.rest.TaskController;

/** Test RestEndpoints and Linkbuilder. */
class RestEndpointsTest {

  @Test
  void testMapping() {

    String mapUrl = RestEndpoints.URL_DOMAIN;
    String buildUrl = linkTo(methodOn(TaskanaEngineController.class).getDomains()).toString();
    assertThat(buildUrl).isEqualTo(mapUrl);
  }

  @Test
  void testMappingWithVariable() throws Exception {

    String id = "25";

    String mapUrl =
        UriComponentsBuilder.fromPath(RestEndpoints.URL_TASKS_ID).buildAndExpand(id).toUriString();
    String buildUrl = linkTo(methodOn(TaskController.class).getTask(id)).toString();
    assertThat(buildUrl).isEqualTo(mapUrl);
  }
}
