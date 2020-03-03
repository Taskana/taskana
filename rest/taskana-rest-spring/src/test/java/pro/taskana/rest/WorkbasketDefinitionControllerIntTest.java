package pro.taskana.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.sql.DataSource;
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
import pro.taskana.rest.resource.WorkbasketDefinitionResource;
import pro.taskana.sampledata.SampleDataGenerator;

/** Integration tests for WorkbasketDefinitionController. */
@TaskanaSpringBootTest
class WorkbasketDefinitionControllerIntTest {

  private static RestTemplate template;

  @Value("${taskana.schemaName:TASKANA}")
  String schemaName;

  ObjectMapper objMapper = new ObjectMapper();

  @Autowired RestHelper restHelper;

  @Autowired private DataSource dataSource;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @BeforeEach
  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @Test
  void testExportWorkbasketFromDomain() {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        executeExportRequestForDomain("DOMAIN_A");

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().get(0)).isInstanceOf(WorkbasketDefinitionResource.class);

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
    assertThat(allDistributionTargetsAreEmpty).isFalse();
    assertThat(allAuthorizationsAreEmpty).isFalse();
  }

  @Test
  void testExportWorkbasketsFromWrongDomain() {
    ResponseEntity<List<WorkbasketDefinitionResource>> response =
        executeExportRequestForDomain("wrongDomain");
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  void testImportEveryWorkbasketFromDomainA() throws IOException {
    List<WorkbasketDefinitionResource> wbList = executeExportRequestForDomain("DOMAIN_A").getBody();
    for (WorkbasketDefinitionResource w : wbList) {
      expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
    }
  }

  @Test
  void testImportWorkbasketWithoutDistributionTargets() throws IOException {
    WorkbasketDefinitionResource w = executeExportRequestForDomain("DOMAIN_A").getBody().get(0);
    w.setDistributionTargets(new HashSet<>());

    this.expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);

    w.getWorkbasket().setKey("newKey");
    w.getAuthorizations().forEach(authorization -> authorization.setWorkbasketKey("newKey"));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInImportFile() throws IOException {
    List<WorkbasketDefinitionResource> wbList = executeExportRequestForDomain("DOMAIN_A").getBody();

    WorkbasketDefinitionResource w = wbList.get(0);
    w.setDistributionTargets(new HashSet<>());
    String letMeBeYourDistributionTarget = w.getWorkbasket().getWorkbasketId();
    WorkbasketDefinitionResource w2 = wbList.get(1);
    w2.setDistributionTargets(Collections.singleton(letMeBeYourDistributionTarget));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w, "fancyNewId", null);
    w2.setDistributionTargets(Collections.singleton("fancyNewId"));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w, null, "nowImANewWB");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w2, null, "nowImAlsoANewWB");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInSystem() throws IOException {
    List<WorkbasketDefinitionResource> wbList = executeExportRequestForDomain("DOMAIN_A").getBody();

    wbList.removeIf(definition -> definition.getDistributionTargets().isEmpty());
    WorkbasketDefinitionResource w = wbList.get(0);
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);

    changeWorkbasketIdOrKey(w, null, "new");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsNotInSystem() throws IOException {
    List<WorkbasketDefinitionResource> wbList = executeExportRequestForDomain("DOMAIN_A").getBody();

    WorkbasketDefinitionResource w = wbList.get(0);
    w.setDistributionTargets(Collections.singleton("invalidWorkbasketId"));
    try {
      expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.BAD_REQUEST, w);
      fail("Expected http-Status 400");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    w.getWorkbasket().setKey("anotherNewKey");
    try {
      expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.BAD_REQUEST, w);
      fail("Expected http-Status 400");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void testFailOnImportDuplicates() throws IOException {
    WorkbasketDefinitionResource w = executeExportRequestForDomain("DOMAIN_A").getBody().get(0);
    try {
      expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.CONFLICT, w, w);
      fail("Expected http-Status 409");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
  }

  @Test
  void testNoErrorWhenImportWithSameIdButDifferentKeyAndDomain() throws IOException {
    List<WorkbasketDefinitionResource> wbList = executeExportRequestForDomain("DOMAIN_A").getBody();

    WorkbasketDefinitionResource w = wbList.get(0);
    WorkbasketDefinitionResource differentLogicalId = wbList.get(1);
    this.changeWorkbasketIdOrKey(differentLogicalId, w.getWorkbasket().getWorkbasketId(), null);

    // breaks the logic but not the script- should we really allow this case?
    WorkbasketDefinitionResource theDestroyer = wbList.get(2);
    theDestroyer.setDistributionTargets(
        Collections.singleton(differentLogicalId.getWorkbasket().getWorkbasketId()));

    expectStatusWhenExecutingImportRequestOfWorkbaskets(
        HttpStatus.NO_CONTENT, w, differentLogicalId, theDestroyer);
  }

  @Test
  void testErrorWhenImportWithSameAccessIdAndWorkbasket() {
    WorkbasketDefinitionResource w = executeExportRequestForDomain("DOMAIN_A").getBody().get(0);

    String w1String = workbasketToString(w);
    w.getWorkbasket().setKey("new Key for this WB");
    String w2String = workbasketToString(w);
    assertThatThrownBy(
        () ->
            expectStatusWhenExecutingImportRequestOfWorkbaskets(
                    HttpStatus.CONFLICT, Arrays.asList(w1String, w2String)))
        .isInstanceOf(HttpClientErrorException.class);
  }

  private void changeWorkbasketIdOrKey(
      WorkbasketDefinitionResource w, String newId, String newKey) {
    if (newId != null && !newId.isEmpty()) {
      w.getWorkbasket().setWorkbasketId(newId);
      w.getAuthorizations().forEach(auth -> auth.setWorkbasketId(newId));
    }
    if (newKey != null && !newKey.isEmpty()) {
      w.getWorkbasket().setKey(newKey);
      w.getAuthorizations().forEach(auth -> auth.setWorkbasketKey(newKey));
    }
  }

  private ResponseEntity<List<WorkbasketDefinitionResource>> executeExportRequestForDomain(
      String domain) {
    return template.exchange(
        restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS) + "?domain=" + domain,
        HttpMethod.GET,
        restHelper.defaultRequest(),
        new ParameterizedTypeReference<List<WorkbasketDefinitionResource>>() {});
  }

  private void expectStatusWhenExecutingImportRequestOfWorkbaskets(
      HttpStatus expectedStatus, WorkbasketDefinitionResource... workbaskets) throws IOException {
    List<String> workbasketStrings =
        Arrays.stream(workbaskets).map(this::workbasketToString).collect(Collectors.toList());
    expectStatusWhenExecutingImportRequestOfWorkbaskets(expectedStatus, workbasketStrings);
  }

  private void expectStatusWhenExecutingImportRequestOfWorkbaskets(
      HttpStatus expectedStatus, List<String> workbasketStrings) throws IOException {
    File tmpFile = File.createTempFile("test", ".tmp");
    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), UTF_8);
    writer.write(workbasketStrings.toString());
    writer.close();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders headers = restHelper.getHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    body.add("file", new FileSystemResource(tmpFile));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(Mapping.URL_WORKBASKETDEFIITIONS);

    ResponseEntity<Void> responseImport =
        template.postForEntity(serverUrl, requestEntity, Void.class);
    assertThat(responseImport.getStatusCode()).isEqualTo(expectedStatus);
  }

  private String workbasketToString(WorkbasketDefinitionResource workbasketDefinitionResource) {
    try {
      return objMapper.writeValueAsString(workbasketDefinitionResource);
    } catch (JsonProcessingException e) {
      return "";
    }
  }
}
