package pro.taskana.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.AccessIdResource;

@TaskanaSpringBootTest
class AccessIdControllerIntTest {

  private static RestTemplate template;

  @Autowired RestHelper restHelper;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testQueryGroupsByDn() {
    ResponseEntity<List<AccessIdResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID)
                + "?search-for=cn=ksc-users,cn=groups,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).hasSize(1);
  }

  @Test
  void testQueryGroupsByCn() {
    ResponseEntity<List<AccessIdResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=ksc",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertThat(response.getBody()).hasSize(1);
  }

  @Test
  void testGetMatches() {
    ResponseEntity<List<AccessIdResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=user",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));

    List<AccessIdResource> body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body).hasSize(3);
    assertThat(body)
        .extracting(AccessIdResource::getName)
        .containsExactlyInAnyOrder("Tralisch, Thea", "Bert, Ali", "Mente, Ali");
  }

  @Test
  void testBadRequestWhenSearchForIsTooShort() {
    assertThatThrownBy(
        () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=al",
                    HttpMethod.GET,
                    restHelper.defaultRequest(),
                    ParameterizedTypeReference.forType(List.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Minimum searchFor length =")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  static class AccessIdListResource extends ArrayList<AccessIdResource> {
    private static final long serialVersionUID = 1L;
  }
}
