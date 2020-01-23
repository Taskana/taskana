package pro.taskana.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import pro.taskana.impl.WorkbasketAccessItemImpl;
import pro.taskana.rest.resource.WorkbasketDefinitionResource;
import pro.taskana.sampledata.SampleDataGenerator;

/** Integration tests for WorkbasketDefinitionController. */
@TaskanaSpringBootTest
class WorkbasketDefinitionControllerIntTest {

  private static RestTemplate template;

  @Value("${taskana.schemaName:TASKANA}")
  String schemaName;

  @Autowired RestHelper restHelper;

  @Autowired private DataSource dataSource;

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  @BeforeEach
  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
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
    ObjectMapper objMapper = new ObjectMapper();
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
    ObjectMapper objMapper = new ObjectMapper();
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
    ObjectMapper objMapper = new ObjectMapper();
    WorkbasketDefinitionResource wbDef = response.getBody().get(0);
    list.add(objMapper.writeValueAsString(wbDef));
    int i = 1;
    for (WorkbasketAccessItemImpl wbai : wbDef.getAuthorizations()) {
      wbai.setAccessId("user_" + i++);
    }
    wbDef.getWorkbasket().setKey("new Key for this WB");
    list.add(objMapper.writeValueAsString(wbDef));
    ResponseEntity<Void> responseImport = importRequest(list);
    assertEquals(HttpStatus.NO_CONTENT, responseImport.getStatusCode());
  }

  @Test
  void testErrorWhenImportWithSameAccessIdAndWorkbasket() throws IOException {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=DOMAIN_A",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {});

    List<String> list = new ArrayList<>();
    ObjectMapper objMapper = new ObjectMapper();
    WorkbasketDefinitionResource wbDef = response.getBody().get(0);
    list.add(objMapper.writeValueAsString(wbDef));
    wbDef.getWorkbasket().setKey("new Key for this WB");
    list.add(objMapper.writeValueAsString(wbDef));
    Assertions.assertThrows(HttpClientErrorException.class, () -> importRequest(list));
  }

  private ResponseEntity<Void> importRequest(List<String> clList) throws IOException {
    File tmpFile = File.createTempFile("test", ".tmp");
    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), UTF_8);
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
