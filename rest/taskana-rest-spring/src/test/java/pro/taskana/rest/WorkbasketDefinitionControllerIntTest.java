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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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

/**
 * Integration tests for WorkbasketDefinitionController.
 */
@TaskanaSpringBootTest
class WorkbasketDefinitionControllerIntTest {

  private static RestTemplate template;
  @Value("${taskana.schemaName:TASKANA}")
  String schemaName;
  ObjectMapper objMapper = new ObjectMapper();

  @Autowired
  RestHelper restHelper;

  @Autowired
  private DataSource dataSource;

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
    ResponseEntity<List<WorkbasketDefinitionResource>> response = getExportForDomain("DOMAIN_A");

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
    ResponseEntity<List<WorkbasketDefinitionResource>> response = getExportForDomain("wrongDomain");
    assertEquals(0, response.getBody().size());
  }

  @Test
  void testImportEveryWorkbasketFromDomainA() throws IOException {
    List<WorkbasketDefinitionResource> wbList = getExportForDomain("DOMAIN_A").getBody();
    for (WorkbasketDefinitionResource w : wbList) {
      importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w));
    }
  }

  @Test
  void testImportWorkbasketWithoutDistributionTargets() throws IOException {
    WorkbasketDefinitionResource w = getExportForDomain("DOMAIN_A").getBody().get(0);
    w.setDistributionTargets(new HashSet<>());

    this.importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w));

    w.getWorkbasket().setKey("newKey");
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w));
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInImportFile() throws IOException {
    List<WorkbasketDefinitionResource> wbList = getExportForDomain("DOMAIN_A").getBody();

    WorkbasketDefinitionResource w = wbList.get(0);
    w.setDistributionTargets(new HashSet<>());
    String letMeBeYourDistributionTarget = w.getWorkbasket().workbasketId;
    WorkbasketDefinitionResource w2 = wbList.get(1);
    w2.setDistributionTargets(Collections.singleton(letMeBeYourDistributionTarget));
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w),
        objMapper.writeValueAsString(w2));

    w.getWorkbasket().setWorkbasketId("fancyNewId");
    w2.setDistributionTargets(Collections.singleton("fancyNewId"));
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w),
        objMapper.writeValueAsString(w2));

    w.getWorkbasket().setKey("nowImANewWB");
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w),
        objMapper.writeValueAsString(w2));

    w2.getWorkbasket().setKey("nowImAlsoANewWB");
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w),
        objMapper.writeValueAsString(w2));
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInSystem() throws IOException {
    List<WorkbasketDefinitionResource> wbList = getExportForDomain("DOMAIN_A").getBody();

    wbList.removeIf(definition -> definition.getDistributionTargets().isEmpty());
    WorkbasketDefinitionResource w = wbList.get(0);
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w));

    w.getWorkbasket().setKey("new");
    importRequest(HttpStatus.NO_CONTENT, objMapper.writeValueAsString(w));
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsNotInSystem() throws IOException {
    List<WorkbasketDefinitionResource> wbList = getExportForDomain("DOMAIN_A").getBody();

    WorkbasketDefinitionResource w = wbList.get(0);
    w.setDistributionTargets(Collections.singleton("invalidWorkbasketId"));
    try {
      importRequest(HttpStatus.BAD_REQUEST, objMapper.writeValueAsString(w));
      fail("Expected http-Status 400");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }

    w.getWorkbasket().setKey("anotherNewKey");
    try {
      importRequest(HttpStatus.BAD_REQUEST, objMapper.writeValueAsString(w));
      fail("Expected http-Status 400");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }
  }

  @Test
  void testFailOnImportDuplicates() throws IOException {
    WorkbasketDefinitionResource w = getExportForDomain("DOMAIN_A").getBody().get(0);
    try {
      importRequest(HttpStatus.CONFLICT, objMapper.writeValueAsString(w),
          objMapper.writeValueAsString(w));
      fail("Expected http-Status 409");
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
    }
  }

  @Test
  void testNoErrorWhenImportWithSameIdButDifferentKeyAndDomain() throws IOException {
    List<WorkbasketDefinitionResource> wbList = getExportForDomain("DOMAIN_A").getBody();

    String wbAsItIs = objMapper.writeValueAsString(wbList.get(0));
    WorkbasketDefinitionResource differentLogicalId = wbList.get(0);
    differentLogicalId.getWorkbasket().setKey("new Key for this WB");

    // breaks the logic - should we really allow this case?
    WorkbasketDefinitionResource theDestroyer = wbList.get(1);
    theDestroyer.setDistributionTargets(
        Collections.singleton(differentLogicalId.getWorkbasket().workbasketId));

    importRequest(HttpStatus.NO_CONTENT, wbAsItIs,
        objMapper.writeValueAsString(differentLogicalId),
        objMapper.writeValueAsString(theDestroyer));
  }

  private ResponseEntity<List<WorkbasketDefinitionResource>> getExportForDomain(String domain) {
    return template.exchange(
        restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=" + domain,
        HttpMethod.GET,
        restHelper.defaultRequest(),
        new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {
        });
    int i = 1;
    for (WorkbasketAccessItemImpl wbai : wbDef.getAuthorizations()) {
      wbai.setAccessId("user_" + i++);
    }
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

  private void importRequest(HttpStatus expectedStatus, String... workbasketStrings)
      throws IOException {
    File tmpFile = File.createTempFile("test", ".tmp");
    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), UTF_8);
    writer.write(Arrays.asList(workbasketStrings).toString());
    writer.close();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders headers = restHelper.getHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    body.add("file", new FileSystemResource(tmpFile));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS);

    ResponseEntity<Void> responseImport = template
                                              .postForEntity(serverUrl, requestEntity, Void.class);
    assertEquals(expectedStatus, responseImport.getStatusCode());
  static class WorkbasketDefinitionListResource extends ArrayList<WorkbasketDefinitionResource> {

    private static final long serialVersionUID = 1L;
  }
}
