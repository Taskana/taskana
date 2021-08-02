package pro.taskana.classification.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.exceptions.ClassificationNotFoundException;
import pro.taskana.classification.rest.assembler.ClassificationDefinitionCollectionRepresentationModel;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationCollectionRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationDefinitionRepresentationModel;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Test classification definitions. */
@TaskanaSpringBootTest
@TestMethodOrder(OrderAnnotation.class)
class ClassificationDefinitionControllerIntTest {

  private static final ParameterizedTypeReference<
          ClassificationDefinitionCollectionRepresentationModel>
      CLASSIFICATION_DEFINITION_COLLECTION =
          new ParameterizedTypeReference<
              ClassificationDefinitionCollectionRepresentationModel>() {};

  private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);

  private final RestHelper restHelper;
  private final ObjectMapper mapper;
  private final ClassificationService classificationService;
  private final ClassificationRepresentationModelAssembler classificationAssembler;

  @Autowired
  ClassificationDefinitionControllerIntTest(
      RestHelper restHelper,
      ObjectMapper mapper,
      ClassificationService classificationService,
      ClassificationRepresentationModelAssembler classificationAssembler) {
    this.restHelper = restHelper;
    this.mapper = mapper;
    this.classificationService = classificationService;
    this.classificationAssembler = classificationAssembler;
  }

  @Test
  @Order(1) // since the import tests adds Classifications this has to be tested first.
  void should_ExportAllClassifications_When_ExportIsRequested() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS) + "?domain=DOMAIN_B";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));
    ResponseEntity<ClassificationDefinitionCollectionRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_DEFINITION_COLLECTION);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(ClassificationDefinitionRepresentationModel::getClassification)
        .extracting(ClassificationRepresentationModel::getClassificationId)
        .containsExactlyInAnyOrder(
            "CLI:200000000000000000000000000000000015",
            "CLI:200000000000000000000000000000000017",
            "CLI:200000000000000000000000000000000018",
            "CLI:200000000000000000000000000000000003",
            "CLI:200000000000000000000000000000000004");
  }

  @Test
  void should_NotContainAnyLinks_When_ExportIsRequested() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS) + "?domain=DOMAIN_B";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<ClassificationDefinitionCollectionRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_DEFINITION_COLLECTION);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isPresent();
    assertThat(response.getBody().getContent())
        .extracting(ClassificationDefinitionRepresentationModel::getClassification)
        .extracting(ClassificationRepresentationModel::getLinks)
        .extracting(Links::isEmpty)
        .containsOnly(true);
  }

  @Test
  void should_ExportNothing_When_DomainIsUnknown() {
    String url = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS) + "?domain=ADdfe";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<ClassificationDefinitionCollectionRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, CLASSIFICATION_DEFINITION_COLLECTION);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).isEmpty();
  }

  @Test
  void should_CreateNewClassification_When_ImportContainsUnknownClassification() throws Exception {
    String key = "key drelf";
    String domain = "DOMAIN_A";
    assertThatThrownBy(() -> classificationService.getClassification(key, domain))
        .isInstanceOf(ClassificationNotFoundException.class);

    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setClassificationId("classificationId_");
    classification.setKey(key);
    classification.setDomain(domain);
    classification.setParentId("CLI:100000000000000000000000000000000016");
    classification.setParentKey("T2000");
    classification.setCategory("MANUAL");
    classification.setType("TASK");
    classification.setIsValidInDomain(true);
    classification.setCreated(Instant.parse("2016-05-12T10:12:12.12Z"));
    classification.setModified(Instant.parse("2018-05-12T10:12:12.12Z"));
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

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    assertThatCode(() -> classificationService.getClassification(key, domain))
        .doesNotThrowAnyException();
  }

  @Test
  void should_ThrowError_When_KeyIsMissingForNewClassification() {
    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setDomain("DOMAIN_A");
    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification));

    assertThatThrownBy(() -> importRequest(clList))
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ThrowError_When_DomainIsMissingForNewClassification() {
    ClassificationRepresentationModel classification = new ClassificationRepresentationModel();
    classification.setKey("one");
    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification));

    assertThatThrownBy(() -> importRequest(clList))
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ThrowError_When_ImportModifiesTypeOfExistingClassification() throws Exception {
    ClassificationRepresentationModel classification =
        getClassificationWithKeyAndDomain("T6310", "");
    classification.setType("DOCUMENT");
    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification));

    assertThatThrownBy(() -> importRequest(clList))
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(e -> (HttpStatusCodeException) e)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ThrowError_When_ImportContainsDuplicateClassification() {
    ClassificationRepresentationModel classification1 = new ClassificationRepresentationModel();
    classification1.setClassificationId("id1");
    classification1.setKey("ImportKey3");
    classification1.setDomain("DOMAIN_A");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification1, classification1));

    assertThatThrownBy(() -> importRequest(clList))
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(e -> (HttpStatusCodeException) e)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void should_CreateMultipleClassifications_When_ImportContainsMultipleClassifications()
      throws Exception {
    String key1 = "ImportKey1";
    String key2 = "ImportKey2";
    String domain = "DOMAIN_A";

    assertThatThrownBy(() -> classificationService.getClassification(key1, domain))
        .isInstanceOf(ClassificationNotFoundException.class);
    assertThatThrownBy(() -> classificationService.getClassification(key2, domain))
        .isInstanceOf(ClassificationNotFoundException.class);

    ClassificationRepresentationModel classification1 =
        this.createClassification("id1", "ImportKey1", "DOMAIN_A", null, null);

    ClassificationRepresentationModel classification2 =
        this.createClassification(
            "id2", "ImportKey2", "DOMAIN_A", "CLI:100000000000000000000000000000000016", "T2000");
    classification2.setCategory("MANUAL");
    classification2.setType("TASK");
    classification2.setIsValidInDomain(true);
    classification2.setCreated(Instant.parse("2016-05-12T10:12:12.12Z"));
    classification2.setModified(Instant.parse("2018-05-12T10:12:12.12Z"));
    classification2.setName("name");
    classification2.setDescription("description");
    classification2.setPriority(4);
    classification2.setServiceLevel("P2D");
    classification2.setApplicationEntryPoint("entry1");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(classification1, classification2));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    assertThatCode(() -> classificationService.getClassification(key1, domain))
        .doesNotThrowAnyException();
    assertThatCode(() -> classificationService.getClassification(key2, domain))
        .doesNotThrowAnyException();
  }

  @Test
  void should_IgnoreModifiedTimestamp_When_ImportingClassification() throws Exception {
    ClassificationRepresentationModel existingClassification =
        getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    existingClassification.setName("first new Name");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(existingClassification));

    // first request. Everything normal here
    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    existingClassification.setName("second new Name");
    clList = new ClassificationCollectionRepresentationModel(List.of(existingClassification));

    // second request. NOTE: we did not update the modified timestamp of the classification
    // we want to import -> import should still work
    response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationRepresentationModel testClassification =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(testClassification.getName()).isEqualTo("second new Name");
  }

  @Test
  void should_KeepParentLink_When_ImportingClassificationsWhichWereLinked() throws Exception {
    ClassificationRepresentationModel parent =
        this.createClassification("parentId", "ImportKey6", "DOMAIN_A", null, null);
    ClassificationRepresentationModel child1 =
        this.createClassification("childId1", "ImportKey7", "DOMAIN_A", null, "ImportKey6");
    ClassificationRepresentationModel child2 =
        this.createClassification("childId2", "ImportKey8", "DOMAIN_A", "parentId", null);
    ClassificationRepresentationModel grandChild1 =
        this.createClassification(
            "grandchildId1", "ImportKey9", "DOMAIN_A", "childId1", "ImportKey7");
    ClassificationRepresentationModel grandChild2 =
        this.createClassification("grandchild2", "ImportKey10", "DOMAIN_A", null, "ImportKey7");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(
            List.of(parent, child1, child2, grandChild1, grandChild2));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationRepresentationModel parentCl =
        getClassificationWithKeyAndDomain("ImportKey6", "DOMAIN_A");
    ClassificationRepresentationModel childCl =
        getClassificationWithKeyAndDomain("ImportKey7", "DOMAIN_A");
    ClassificationRepresentationModel grandchildCl =
        getClassificationWithKeyAndDomain("ImportKey9", "DOMAIN_A");

    assertThat(parentCl).isNotNull();
    assertThat(childCl).isNotNull();
    assertThat(grandchildCl).isNotNull();
    assertThat(grandchildCl.getParentId()).isEqualTo(childCl.getClassificationId());
    assertThat(childCl.getParentId()).isEqualTo(parentCl.getClassificationId());
  }

  @Test
  void should_LinkParentClassification_When_OnlyParentKeyIsDefinedInImport() throws Exception {
    ClassificationRepresentationModel parent =
        createClassification("parent", "ImportKey11", "DOMAIN_A", null, null);
    parent.setCustom1("parent is correct");
    ClassificationRepresentationModel wrongParent =
        createClassification("wrongParent", "ImportKey11", "DOMAIN_B", null, null);
    ClassificationRepresentationModel child =
        createClassification("child", "ImportKey13", "DOMAIN_A", null, "ImportKey11");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(parent, wrongParent, child));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

    ClassificationRepresentationModel rightParentCl =
        getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_A");
    ClassificationRepresentationModel wrongParentCl =
        getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_B");
    ClassificationRepresentationModel childCl =
        getClassificationWithKeyAndDomain("ImportKey13", "DOMAIN_A");

    assertThat(rightParentCl).isNotNull();
    assertThat(wrongParentCl).isNotNull();
    assertThat(childCl).isNotNull();
    assertThat(childCl.getParentId()).isEqualTo(rightParentCl.getClassificationId());
    assertThat(childCl.getParentId()).isNotEqualTo(wrongParentCl.getClassificationId());
  }

  @Test
  void should_OverrideExistingParentLinks_When_ImportLinksExistingClassificationsDifferently()
      throws Exception {
    ClassificationRepresentationModel child1 =
        this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
    assertThat(child1.getParentKey()).isEqualTo("L11010");
    child1.setParentId("CLI:100000000000000000000000000000000002");
    child1.setParentKey("L10303");

    ClassificationRepresentationModel child2 =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(child2.getParentKey()).isEqualTo("L11010");
    child2.setParentId("");
    child2.setParentKey("");

    ClassificationCollectionRepresentationModel clList =
        new ClassificationCollectionRepresentationModel(List.of(child1, child2));

    ResponseEntity<Void> response = importRequest(clList);
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    Thread.sleep(10);
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Wait 10 ms to give the system a chance to update");
    }

    ClassificationRepresentationModel childWithNewParent =
        this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
    assertThat(childWithNewParent.getParentKey()).isEqualTo(child1.getParentKey());

    ClassificationRepresentationModel childWithoutParent =
        this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
    assertThat(childWithoutParent.getParentId()).isEqualTo(child2.getParentId());
    assertThat(childWithoutParent.getParentKey()).isEqualTo(child2.getParentKey());
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
    classificationRepresentationModel.setServiceLevel("P1D");
    return classificationRepresentationModel;
  }

  private ClassificationRepresentationModel getClassificationWithKeyAndDomain(
      String key, String domain) throws Exception {
    return classificationAssembler.toModel(classificationService.getClassification(key, domain));
  }

  private ResponseEntity<Void> importRequest(ClassificationCollectionRepresentationModel clList)
      throws Exception {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Start Import");
    }
    File tmpFile = File.createTempFile("test", ".tmp");
    try (FileOutputStream out = new FileOutputStream(tmpFile);
        OutputStreamWriter writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
      mapper.writeValue(writer, clList);
    }
    MultiValueMap<String, FileSystemResource> body = new LinkedMultiValueMap<>();
    body.add("file", new FileSystemResource(tmpFile));

    HttpHeaders headers = RestHelper.generateHeadersForUser("businessadmin");
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<?> requestEntity = new HttpEntity<>(body, headers);
    String serverUrl = restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS);

    return TEMPLATE.postForEntity(serverUrl, requestEntity, Void.class);
  }
}
