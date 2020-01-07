package pro.taskana.rest;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.WorkbasketDefinitionResource;

/** Integration tests for WorkbasketDefinitionController. */
@TaskanaSpringBootTest
class WorkbasketDefinitionControllerIntTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);
  private static RestTemplate template;
  @Autowired RestHelper restHelper;
  private ObjectMapper objMapper = new ObjectMapper();

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  @Test
  void testExportWorkbasketFromDomain() {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=DOMAIN_A",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {});

    assertNotNull(response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertThat(response.getBody().get(0), instanceOf(WorkbasketDefinitionResource.class));

    boolean allAuthorizationsAreEmpty = true;
    boolean allDistributionTargetsAreEmpty = true;
    for (WorkbasketDefinitionResource workbasketDefinition : response.getBody()) {
      if (allAuthorizationsAreEmpty && !workbasketDefinition.getAuthorizations().isEmpty()) {
        allAuthorizationsAreEmpty = false;
      }
      if (allDistributionTargetsAreEmpty
          && !workbasketDefinition.getDistributionTargets().isEmpty()) {
        allDistributionTargetsAreEmpty = false;
      }
      if (!allAuthorizationsAreEmpty && !allDistributionTargetsAreEmpty) {
        break;
      }
    }
    assertFalse(allDistributionTargetsAreEmpty);
    assertFalse(allAuthorizationsAreEmpty);
  }

  @Test
  void testExportWorkbasketsFromWrongDomain() {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=wrongDomain",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));
    assertEquals(0, response.getBody().size());
  }

  @Test
  void testImportWorkbasket() throws IOException {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=DOMAIN_A",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(List.class));

    List<String> list = new ArrayList<>();
    list.add(objMapper.writeValueAsString(response.getBody().get(0)));
    ResponseEntity<Void> responseImport = importRequest(list);
    assertEquals(HttpStatus.NO_CONTENT, responseImport.getStatusCode());
  }

  @Test
  void testFailOnImportDuplicates() throws IOException {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=DOMAIN_A",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {});

    List<String> list = new ArrayList<>();
    list.add(objMapper.writeValueAsString(response.getBody().get(0)));
    list.add(objMapper.writeValueAsString(response.getBody().get(0)));
    try {
      importRequest(list);
      fail("Expected http-Status 409");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }
  }

  @Test
  void testNoErrorWhenImportWithSameIdButDifferentKeyAndDomain() throws IOException {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=DOMAIN_A",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {});

    List<String> list = new ArrayList<>();
    WorkbasketDefinitionResource wbDef = response.getBody().get(0);
    list.add(objMapper.writeValueAsString(wbDef));
    wbDef.getWorkbasket().setKey("new Key for this WB");
    list.add(objMapper.writeValueAsString(wbDef));
    ResponseEntity<Void> responseImport = importRequest(list);
    assertEquals(HttpStatus.NO_CONTENT, responseImport.getStatusCode());
  }

  private ResponseEntity<Void> importRequest(List<String> clList) throws IOException {
    File tmpFile = File.createTempFile("test", ".tmp");
    FileWriter writer = new FileWriter(tmpFile);
    writer.write(clList.toString());
    writer.close();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders headers = restHelper.getHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    body.add("file", new FileSystemResource(tmpFile));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS);

    return template.postForEntity(serverUrl, requestEntity, Void.class);
  }
}
