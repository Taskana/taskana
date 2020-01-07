package pro.taskana.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

/** Test Mapping and Linkbuilder. */
class MappingTest {

  @Test
  void testMapping() throws Exception {

    String mapUrl = Mapping.URL_TASKS;
    String buildUrl =
        linkTo(methodOn(TaskController.class).getTasks(new LinkedMultiValueMap<>())).toString();
    Assertions.assertEquals(mapUrl, buildUrl);
  }

  @Test
  void testMappingWithVariable() throws Exception {

    String id = "25";

    String mapUrl =
        UriComponentsBuilder.fromPath(Mapping.URL_TASKS_ID).buildAndExpand(id).toUriString();
    String buildUrl = linkTo(methodOn(TaskController.class).getTask(id)).toString();
    Assertions.assertEquals(mapUrl, buildUrl);
  }
}
