package pro.taskana.classification.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
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

import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.RestHelper;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.common.rest.models.TaskanaPagedModel;

/** Test classification definitions. */
@TaskanaSpringBootTest
class ClassificationDefinitionControllerIntTest {

  private static final ParameterizedTypeReference<
          TaskanaPagedModel<ClassificationSummaryRepresentationModel>>
      CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<
              TaskanaPagedModel<ClassificationSummaryRepresentationModel>>() {};
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);
  private static RestTemplate template;
  @Autowired RestHelper restHelper;
  private ObjectMapper objMapper = new ObjectMapper();

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  @Test
  void testExportClassifications() {
    ResponseEntity<ClassificationRepresentationModel[]> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONDEFINITION) + "?domain=DOMAIN_B",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel[].class));
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().length >= 5).isTrue();
    assertThat(response.getBody().length <= 7).isTrue();
    assertThat(response.getBody()[0]).isInstanceOf(ClassificationRepresentationModel.class);
  }

  @Test
  void testExportClassificationsFromWrongDomain() {
    ResponseEntity<ClassificationRepresentationModel[]> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONDEFINITION) + "?domain=ADdfe",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(ClassificationRepresentationModel[].class));
    assertThat(response.getBody()).isEmpty();
  }

  @Test
  void testImportFilledClassification() throws IOException {
    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setClassificationId("classificationId_");
    classification.setKey("key drelf");
    classification.setParentId("CLI:100000000000000000000000000000000016");
    classification.setParentKey("T2000");
    classification.setCategory("MANUAL");
    classification.setType("TASK");
    classification.setDomain("DOMAIN_A");
    classification.setIsValidInDomain(true);
    classification.setCreated("2016-05-12T10:12:12.12Z");
    classification.setModified("2018-05-12T10:12:12.12Z");
    classification.setName("name");
    classification.setDescription("description");
    classification.setPriority(4);
    classification.setServiceLevel("P2D");
    classification.setApplicationEntryPoint("entry1");
    classification.setCustom1("custom");
    classification.setCustom2("custom");
    classification.setCustom3("custom");
    classification.setCustom4("custom");
    classification.setCustom5("custom");
    classification.setCustom6("custom");
    classification.setCustom7("custom");
    classification.setCustom8("custom");

    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(classification));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void testFailureWhenKeyIsMissing() throws IOException {
    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setDomain("DOMAIN_A");
    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(classification));

    try {
      importRequest(clList);
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void testFailureWhenDomainIsMissing() throws IOException {
    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setKey("one");
    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(classification));

    try {
      importRequest(clList);
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void testFailureWhenUpdatingTypeOfExistingClassification() throws IOException {
    ClassificationSummaryRepresentationModel classification =
        this.getClassificationWithKeyAndDomain("T6310", "");
    classification.setType("DOCUMENT");
    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(classification));

    try {
      importRequest(clList);
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Test
  void testImportMultipleClassifications() throws IOException {
    ClassificationRepresentationModel classification1 =
        this.createClassification("id1", "ImportKey1", "DOMAIN_A", null, null);
    final String c1 = objMapper.writeValueAsString(classification1);

    ClassificationRepresentationModel classification2 =
        this.createClassification(
            "id2", "ImportKey2", "DOMAIN_A", "CLI:100000000000000000000000000000000016", "T2000");
    classification2.setCategory("MANUAL");
    classification2.setType("TASK");
    classification2.setIsValidInDomain(true);
    classification2.setCreated("2016-05-12T10:12:12.12Z");
    classification2.setModified("2018-05-12T10:12:12.12Z");
    classification2.setName("name");
    classification2.setDescription("description");
    classification2.setPriority(4);
    classification2.setServiceLevel("P2D");
    classification2.setApplicationEntryPoint("entry1");
    String c2 = objMapper.writeValueAsString(classification2);

    List<String> clList = new ArrayList<>();
    clList.add(c1);
    clList.add(c2);

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void testImportDuplicateClassification() throws IOException {
    ClassificationRepresentationModel classification1 = new ClassificationRepresentationModel();
    classification1.setClassificationId("id1");
    classification1.setKey("ImportKey3");
    classification1.setDomain("DOMAIN_A");
    String c1 = objMapper.writeValueAsString(classification1);

    List<String> clList = new ArrayList<>();
    clList.add(c1);
    clList.add(c1);

    try {
      importRequest(clList);
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
  }

  @Test
  void testInsertExistingClassificationWithOlderTimestamp() throws IOException {
    ClassificationSummaryRepresentationModel existingClassification =
        getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    existingClassification.setName("first new Name");
    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(existingClassification));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    existingClassification.setName("second new Name");
    clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(existingClassification));

    response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationSummaryRepresentationModel testClassification =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(testClassification.getName()).isEqualTo("second new Name");
  }

  @Test
  void testHookExistingChildToNewParent() throws IOException {
    final ClassificationRepresentationModel newClassification =
        createClassification("new Classification", "newClass", "DOMAIN_A", null, "L11010");
    ClassificationSummaryRepresentationModel existingClassification =
        getClassificationWithKeyAndDomain("L110102", "DOMAIN_A");
    existingClassification.setParentId("new Classification");
    existingClassification.setParentKey("newClass");

    List<String> clList = new ArrayList<>();
    clList.add(objMapper.writeValueAsString(existingClassification));
    clList.add(objMapper.writeValueAsString(newClassification));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationSummaryRepresentationModel parentCl =
        getClassificationWithKeyAndDomain("L11010", "DOMAIN_A");
    ClassificationSummaryRepresentationModel testNewCl =
        getClassificationWithKeyAndDomain("newClass", "DOMAIN_A");
    ClassificationSummaryRepresentationModel testExistingCl =
        getClassificationWithKeyAndDomain("L110102", "DOMAIN_A");

    assertThat(parentCl).isNotNull();
    assertThat(testNewCl).isNotNull();
    assertThat(testExistingCl).isNotNull();
    assertThat(testExistingCl.getParentId()).isEqualTo(testNewCl.getClassificationId());
    assertThat(testNewCl.getParentId()).isEqualTo(parentCl.getClassificationId());
  }

  @Test
  void testImportParentAndChildClassification() throws IOException {
    ClassificationRepresentationModel classification1 =
        this.createClassification("parentId", "ImportKey6", "DOMAIN_A", null, null);
    final String c1 = objMapper.writeValueAsString(classification1);

    ClassificationRepresentationModel classification2 =
        this.createClassification("childId1", "ImportKey7", "DOMAIN_A", null, "ImportKey6");
    final String c21 = objMapper.writeValueAsString(classification2);
    classification2 =
        this.createClassification("childId2", "ImportKey8", "DOMAIN_A", "parentId", null);
    final String c22 = objMapper.writeValueAsString(classification2);

    ClassificationRepresentationModel classification3 =
        this.createClassification(
            "grandchildId1", "ImportKey9", "DOMAIN_A", "childId1", "ImportKey7");
    final String c31 = objMapper.writeValueAsString(classification3);
    classification3 =
        this.createClassification("grandchild2", "ImportKey10", "DOMAIN_A", null, "ImportKey7");
    final String c32 = objMapper.writeValueAsString(classification3);

    List<String> clList = new ArrayList<>();
    clList.add(c31);
    clList.add(c32);
    clList.add(c21);
    clList.add(c22);
    clList.add(c1);

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationSummaryRepresentationModel parentCl =
        getClassificationWithKeyAndDomain("ImportKey6", "DOMAIN_A");
    ClassificationSummaryRepresentationModel childCl =
        getClassificationWithKeyAndDomain("ImportKey7", "DOMAIN_A");
    ClassificationSummaryRepresentationModel grandchildCl =
        getClassificationWithKeyAndDomain("ImportKey9", "DOMAIN_A");

    assertThat(parentCl).isNotNull();
    assertThat(childCl).isNotNull();
    assertThat(grandchildCl).isNotNull();
    assertThat(grandchildCl.getParentId()).isEqualTo(childCl.getClassificationId());
    assertThat(childCl.getParentId()).isEqualTo(parentCl.getClassificationId());
  }

  @Test
  void testImportParentAndChildClassificationWithKey() throws IOException {
    ClassificationRepresentationModel classification1 =
        createClassification("parent", "ImportKey11", "DOMAIN_A", null, null);
    classification1.setCustom1("parent is correct");
    String parent = objMapper.writeValueAsString(classification1);
    ClassificationRepresentationModel classification2 =
        createClassification("wrongParent", "ImportKey11", "DOMAIN_B", null, null);
    String wrongParent = objMapper.writeValueAsString(classification2);
    ClassificationRepresentationModel classification3 =
        createClassification("child", "ImportKey13", "DOMAIN_A", null, "ImportKey11");
    String child = objMapper.writeValueAsString(classification3);

    List<String> clList = new ArrayList<>();
    clList.add(wrongParent);
    clList.add(parent);
    clList.add(child);

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationSummaryRepresentationModel rightParentCl =
        getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_A");
    ClassificationSummaryRepresentationModel wrongParentCl =
        getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_B");
    ClassificationSummaryRepresentationModel childCl =
        getClassificationWithKeyAndDomain("ImportKey13", "DOMAIN_A");

    assertThat(rightParentCl).isNotNull();
    assertThat(wrongParentCl).isNotNull();
    assertThat(childCl).isNotNull();
    assertThat(childCl.getParentId()).isEqualTo(rightParentCl.getClassificationId());
    assertThat(childCl.getParentId()).isNotEqualTo(wrongParentCl.getClassificationId());
  }

  @Test
  void testChangeParentByImportingExistingClassification()
      throws IOException, InterruptedException {
    ClassificationSummaryRepresentationModel child1 =
        this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
    assertThat(child1.getParentKey()).isEqualTo("L11010");
    child1.setParentId("CLI:100000000000000000000000000000000002");
    child1.setParentKey("L10303");
    final String withNewParent = objMapper.writeValueAsString(child1);

    ClassificationSummaryRepresentationModel child2 =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(child2.getParentKey()).isEqualTo("L11010");
    child2.setParentId("");
    child2.setParentKey("");
    String withoutParent = objMapper.writeValueAsString(child2);

    List<String> clList = new ArrayList<>();
    clList.add(withNewParent);
    clList.add(withoutParent);

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Thread.sleep(10);
    LOGGER.debug("Wait 10 ms to give the system a chance to update");

    ClassificationSummaryRepresentationModel childWithNewParent =
        this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
    assertThat(childWithNewParent.getParentKey()).isEqualTo(child1.getParentKey());

    ClassificationSummaryRepresentationModel childWithoutParent =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(childWithoutParent.getParentId()).isEqualTo(child2.getParentId());
    assertThat(childWithoutParent.getParentKey()).isEqualTo(child2.getParentKey());
  }

  @Test
  void testFailOnImportDuplicates() throws IOException {
    ClassificationSummaryRepresentationModel classification =
        this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
    String classificationString = objMapper.writeValueAsString(classification);

    List<String> clList = new ArrayList<>();
    clList.add(classificationString);
    clList.add(classificationString);

    assertThatThrownBy(() -> importRequest(clList))
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  private ClassificationRepresentationModel createClassification(
      String id, String key, String domain, String parentId, String parentKey) {
    ClassificationRepresentationModel classificationRepresentationModel =
        new ClassificationRepresentationModel();
    classificationRepresentationModel.setClassificationId(id);
    classificationRepresentationModel.setKey(key);
    classificationRepresentationModel.setDomain(domain);
    classificationRepresentationModel.setParentId(parentId);
    classificationRepresentationModel.setParentKey(parentKey);
    return classificationRepresentationModel;
  }

  private ClassificationSummaryRepresentationModel getClassificationWithKeyAndDomain(
      String key, String domain) {
    LOGGER.debug("Request classification with key={} in domain={}", key, domain);
    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeaders());
    ResponseEntity<TaskanaPagedModel<ClassificationSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_CLASSIFICATIONS) + "?key=" + key + "&domain=" + domain,
            HttpMethod.GET,
            request,
            CLASSIFICATION_SUMMARY_PAGE_MODEL_TYPE);
    return response
        .getBody()
        .getContent()
        .toArray(new ClassificationSummaryRepresentationModel[1])[0];
  }

  private ResponseEntity<Void> importRequest(List<String> clList) throws IOException {
    LOGGER.debug("Start Import");
    File tmpFile = File.createTempFile("test", ".tmp");
    OutputStreamWriter writer =
        new OutputStreamWriter(new FileOutputStream(tmpFile), StandardCharsets.UTF_8);
    writer.write(clList.toString());
    writer.close();

    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

    HttpHeaders headers = restHelper.getHeaders();
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);
    body.add("file", new FileSystemResource(tmpFile));

    HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(Mapping.URL_CLASSIFICATIONDEFINITION);

    return template.postForEntity(serverUrl, requestEntity, Void.class);
  }
}
