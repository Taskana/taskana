package pro.taskana.workbasket.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
import org.springframework.web.client.HttpStatusCodeException;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;

/** Integration tests for WorkbasketDefinitionController. */
@TaskanaSpringBootTest
class WorkbasketDefinitionControllerIntTest {

  private final ObjectMapper objMapper;
  private final RestHelper restHelper;
  private final DataSource dataSource;

  @Value("${taskana.schemaName:TASKANA}")
  String schemaName;

  @Autowired
  WorkbasketDefinitionControllerIntTest(
      ObjectMapper objMapper, RestHelper restHelper, DataSource dataSource) {
    this.objMapper = objMapper;
    this.restHelper = restHelper;
    this.dataSource = dataSource;
  }

  @BeforeEach
  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @Test
  void testExportWorkbasketFromDomain() {
    ResponseEntity<TaskanaPagedModel<WorkbasketDefinitionRepresentationModel>> response =
        executeExportRequestForDomain("DOMAIN_A");

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().getContent())
        .hasOnlyElementsOfType(WorkbasketDefinitionRepresentationModel.class);

    boolean allAuthorizationsAreEmpty = true;
    boolean allDistributionTargetsAreEmpty = true;
    for (WorkbasketDefinitionRepresentationModel workbasketDefinition :
        response.getBody().getContent()) {
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
    ResponseEntity<TaskanaPagedModel<WorkbasketDefinitionRepresentationModel>> response =
        executeExportRequestForDomain("wrongDomain");
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
    assertThat(response.getBody().getKey()).isSameAs(TaskanaPagedModelKeys.WORKBASKET_DEFINITIONS);
  }

  @Test
  void testImportEveryWorkbasketFromDomainA() throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> wbList =
        executeExportRequestForDomain("DOMAIN_A").getBody();
    assertThat(wbList).isNotNull();
    for (WorkbasketDefinitionRepresentationModel w : wbList.getContent()) {
      expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
    }
  }

  @Test
  void testImportWorkbasketWithoutDistributionTargets() throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> pagedModel =
        executeExportRequestForDomain("DOMAIN_A").getBody();
    assertThat(pagedModel).isNotNull();
    WorkbasketDefinitionRepresentationModel w = pagedModel.getContent().iterator().next();
    w.setDistributionTargets(new HashSet<>());
    this.expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
    w.getWorkbasket().setKey("newKey");
    w.getAuthorizations().forEach(authorization -> authorization.setWorkbasketKey("newKey"));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInImportFile() throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> wbList =
        executeExportRequestForDomain("DOMAIN_A").getBody();
    assertThat(wbList).isNotNull();
    Iterator<WorkbasketDefinitionRepresentationModel> iterator = wbList.getContent().iterator();

    WorkbasketDefinitionRepresentationModel w = iterator.next();
    w.setDistributionTargets(new HashSet<>());
    String letMeBeYourDistributionTarget = w.getWorkbasket().getWorkbasketId();
    WorkbasketDefinitionRepresentationModel w2 = iterator.next();
    w2.setDistributionTargets(Set.of(letMeBeYourDistributionTarget));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w, "fancyNewId", null);
    w2.setDistributionTargets(Set.of("fancyNewId"));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w, null, "nowImANewWB");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);

    this.changeWorkbasketIdOrKey(w2, null, "nowImAlsoANewWB");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w, w2);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsInSystem() throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> wbList =
        executeExportRequestForDomain("DOMAIN_A").getBody();

    assertThat(wbList).isNotNull();
    List<WorkbasketDefinitionRepresentationModel> content = new ArrayList<>(wbList.getContent());

    content.removeIf(definition -> definition.getDistributionTargets().isEmpty());
    WorkbasketDefinitionRepresentationModel w = content.iterator().next();
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);

    changeWorkbasketIdOrKey(w, null, "new");
    expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.NO_CONTENT, w);
  }

  @Test
  void testImportWorkbasketWithDistributionTargetsNotInSystem() {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> wbList =
        executeExportRequestForDomain("DOMAIN_A").getBody();

    assertThat(wbList).isNotNull();
    WorkbasketDefinitionRepresentationModel w = wbList.getContent().iterator().next();
    w.setDistributionTargets(Set.of("invalidWorkbasketId"));
    ThrowingCallable httpCall =
        () -> expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.BAD_REQUEST, w);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(e -> (HttpClientErrorException) e)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);

    w.getWorkbasket().setKey("anotherNewKey");

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(e -> (HttpClientErrorException) e)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testFailOnImportDuplicates() {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> pagedModel =
        executeExportRequestForDomain("DOMAIN_A").getBody();

    assertThat(pagedModel).isNotNull();
    WorkbasketDefinitionRepresentationModel w = pagedModel.getContent().iterator().next();
    ThrowingCallable httpCall =
        () -> expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.CONFLICT, w, w);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(e -> (HttpClientErrorException) e)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testNoErrorWhenImportWithSameIdButDifferentKeyAndDomain() throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> wbList =
        executeExportRequestForDomain("DOMAIN_A").getBody();

    assertThat(wbList).isNotNull();
    Iterator<WorkbasketDefinitionRepresentationModel> iterator = wbList.getContent().iterator();
    WorkbasketDefinitionRepresentationModel w = iterator.next();
    WorkbasketDefinitionRepresentationModel differentLogicalId = iterator.next();
    this.changeWorkbasketIdOrKey(differentLogicalId, w.getWorkbasket().getWorkbasketId(), null);

    // breaks the logic but not the script- should we really allow this case?
    WorkbasketDefinitionRepresentationModel theDestroyer = iterator.next();
    theDestroyer.setDistributionTargets(
        Set.of(differentLogicalId.getWorkbasket().getWorkbasketId()));

    expectStatusWhenExecutingImportRequestOfWorkbaskets(
        HttpStatus.NO_CONTENT, w, differentLogicalId, theDestroyer);
  }

  @Test
  void testErrorWhenImportWithSameAccessIdAndWorkbasket() {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> pagedModel =
        executeExportRequestForDomain("DOMAIN_A").getBody();

    assertThat(pagedModel).isNotNull();
    WorkbasketDefinitionRepresentationModel w = pagedModel.getContent().iterator().next();

    ThrowingCallable httpCall =
        () -> {
          expectStatusWhenExecutingImportRequestOfWorkbaskets(HttpStatus.CONFLICT, w, w);
        };
    assertThatThrownBy(httpCall).isInstanceOf(HttpClientErrorException.class);
  }

  private void changeWorkbasketIdOrKey(
      WorkbasketDefinitionRepresentationModel w, String newId, String newKey) {
    if (newId != null && !newId.isEmpty()) {
      w.getWorkbasket().setWorkbasketId(newId);
      w.getAuthorizations().forEach(auth -> auth.setWorkbasketId(newId));
    }
    if (newKey != null && !newKey.isEmpty()) {
      w.getWorkbasket().setKey(newKey);
      w.getAuthorizations().forEach(auth -> auth.setWorkbasketKey(newKey));
    }
  }

  private ResponseEntity<TaskanaPagedModel<WorkbasketDefinitionRepresentationModel>>
      executeExportRequestForDomain(String domain) {
    return TEMPLATE.exchange(
        restHelper.toUrl(RestEndpoints.URL_WORKBASKET_DEFINITIONS) + "?domain=" + domain,
        HttpMethod.GET,
        restHelper.defaultRequest(),
        new ParameterizedTypeReference<
            TaskanaPagedModel<WorkbasketDefinitionRepresentationModel>>() {});
  }

  private void expectStatusWhenExecutingImportRequestOfWorkbaskets(
      HttpStatus expectedStatus, WorkbasketDefinitionRepresentationModel... workbaskets)
      throws Exception {
    TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> pagedModel =
        new TaskanaPagedModel<>(
            TaskanaPagedModelKeys.WORKBASKET_DEFINITIONS, Arrays.asList(workbaskets));
    expectStatusWhenExecutingImportRequestOfWorkbaskets(expectedStatus, pagedModel);
  }

  private void expectStatusWhenExecutingImportRequestOfWorkbaskets(
      HttpStatus expectedStatus,
      TaskanaPagedModel<WorkbasketDefinitionRepresentationModel> pageModel)
      throws Exception {
    File tmpFile = File.createTempFile("test", ".tmp");
    try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), UTF_8)) {
      objMapper.writeValue(writer, pageModel);
    }
    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
    HttpHeaders headers = restHelper.getHeadersBusinessAdmin();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    body.add("file", new FileSystemResource(tmpFile));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(RestEndpoints.URL_WORKBASKET_DEFINITIONS);

    ResponseEntity<Void> responseImport =
        TEMPLATE.postForEntity(serverUrl, requestEntity, Void.class);
    assertThat(responseImport.getStatusCode()).isEqualTo(expectedStatus);
  }
}
