package pro.taskana.rest;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationSummaryListResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;

/**
 * Test classification definitions.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
    "devMode=true"})
public class ClassificationDefinitionControllerIntTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);

    String server = "http://127.0.0.1:";

    RestTemplate template;

    HttpEntity<String> request;

    HttpHeaders headers = new HttpHeaders();

    ObjectMapper objMapper = new ObjectMapper();

    @LocalServerPort
    int port;

    @Autowired
    Environment env;

    @Before
    public void before() {
        LOGGER.debug("before");
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void testExportClassifications() {
        ResponseEntity<ClassificationResource[]> response = template.exchange(
            server + port + "/api/v1/classification-definitions?domain=DOMAIN_B",
            HttpMethod.GET, request, ParameterizedTypeReference.forType(ClassificationResource[].class));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 5);
        assertTrue(response.getBody().length <= 7);
        assertThat(response.getBody()[0], instanceOf(ClassificationResource.class));
    }

    @Test
    public void testExportClassificationsFromWrongDomain() {
        ResponseEntity<ClassificationResource[]> response = template.exchange(
            server + port + "/api/v1/classification-definitions?domain=ADdfe",
            HttpMethod.GET, request, ParameterizedTypeReference.forType(ClassificationResource[].class));
        assertEquals(0, response.getBody().length);
    }

    @Test
    public void testImportFilledClassification() throws IOException {
        ClassificationResource classification = new ClassificationResource();
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
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testFailureWhenKeyIsMissing() throws IOException {
        ClassificationResource classification = new ClassificationResource();
        classification.setDomain("DOMAIN_A");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(classification));

        try {
            importRequest(clList);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testFailureWhenDomainIsMissing() throws IOException {
        ClassificationResource classification = new ClassificationResource();
        classification.setKey("one");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(classification));

        try {
            importRequest(clList);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testFailureWhenUpdatingTypeOfExistingClassification() throws IOException {
        ClassificationSummaryResource classification = this.getClassificationWithKeyAndDomain("T6310", "");
        classification.setType("DOCUMENT");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(classification));

        try {
            importRequest(clList);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testImportMultipleClassifications() throws IOException {
        ClassificationResource classification1 = this.createClassification("id1", "ImportKey1", "DOMAIN_A", null, null);
        String c1 = objMapper.writeValueAsString(classification1);

        ClassificationResource classification2 = this.createClassification("id2", "ImportKey2", "DOMAIN_A",
            "CLI:100000000000000000000000000000000016", "T2000");
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
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testImportDuplicateClassification() throws IOException {
        ClassificationResource classification1 = new ClassificationResource();
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
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }
    }

    @Test
    public void testInsertExistingClassificationWithOlderTimestamp() throws IOException {
        ClassificationSummaryResource existingClassification = getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
        existingClassification.setName("first new Name");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(existingClassification));

        ResponseEntity<Void> response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        existingClassification.setName("second new Name");
        clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(existingClassification));

        response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ClassificationSummaryResource testClassification = this.getClassificationWithKeyAndDomain("L110107",
            "DOMAIN_A");
        assertEquals("second new Name", testClassification.getName());
    }

    @Test
    public void testHookExistingChildToNewParent() throws IOException {
        ClassificationResource newClassification = createClassification(
            "new Classification", "newClass", "DOMAIN_A", null, "L11010");
        ClassificationSummaryResource existingClassification = getClassificationWithKeyAndDomain("L110102", "DOMAIN_A");
        existingClassification.setParentId("new Classification");
        existingClassification.setParentKey("newClass");

        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(existingClassification));
        clList.add(objMapper.writeValueAsString(newClassification));

        ResponseEntity<Void> response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ClassificationSummaryResource parentCl = getClassificationWithKeyAndDomain("L11010", "DOMAIN_A");
        ClassificationSummaryResource testNewCl = getClassificationWithKeyAndDomain("newClass", "DOMAIN_A");
        ClassificationSummaryResource testExistingCl = getClassificationWithKeyAndDomain("L110102", "DOMAIN_A");

        assertNotNull(parentCl);
        assertNotNull(testNewCl);
        assertNotNull(testExistingCl);
        assertEquals(testNewCl.getClassificationId(), testExistingCl.getParentId());
        assertEquals(parentCl.getClassificationId(), testNewCl.getParentId());
    }

    @Test
    public void testImportParentAndChildClassification() throws IOException {
        ClassificationResource classification1 = this.createClassification("parentId", "ImportKey6", "DOMAIN_A", null,
            null);
        String c1 = objMapper.writeValueAsString(classification1);

        ClassificationResource classification2 = this.createClassification("childId1", "ImportKey7", "DOMAIN_A", null,
            "ImportKey6");
        String c21 = objMapper.writeValueAsString(classification2);
        classification2 = this.createClassification("childId2", "ImportKey8", "DOMAIN_A", "parentId", null);
        String c22 = objMapper.writeValueAsString(classification2);

        ClassificationResource classification3 = this.createClassification("grandchildId1", "ImportKey9", "DOMAIN_A",
            "childId1", "ImportKey7");
        String c31 = objMapper.writeValueAsString(classification3);
        classification3 = this.createClassification("grandchild2", "ImportKey10", "DOMAIN_A", null, "ImportKey7");
        String c32 = objMapper.writeValueAsString(classification3);

        List<String> clList = new ArrayList<>();
        clList.add(c31);
        clList.add(c32);
        clList.add(c21);
        clList.add(c22);
        clList.add(c1);

        ResponseEntity<Void> response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ClassificationSummaryResource parentCl = getClassificationWithKeyAndDomain("ImportKey6", "DOMAIN_A");
        ClassificationSummaryResource childCl = getClassificationWithKeyAndDomain("ImportKey7", "DOMAIN_A");
        ClassificationSummaryResource grandchildCl = getClassificationWithKeyAndDomain("ImportKey9", "DOMAIN_A");

        assertNotNull(parentCl);
        assertNotNull(childCl);
        assertNotNull(grandchildCl);
        assertEquals(childCl.getClassificationId(), grandchildCl.getParentId());
        assertEquals(parentCl.getClassificationId(), childCl.getParentId());
    }

    @Test
    public void testImportParentAndChildClassificationWithKey() throws IOException {
        ClassificationResource classification1 = createClassification("parent", "ImportKey11", "DOMAIN_A", null, null);
        classification1.setCustom1("parent is correct");
        String parent = objMapper.writeValueAsString(classification1);
        ClassificationResource classification2 = createClassification("wrongParent", "ImportKey11", "DOMAIN_B", null,
            null);
        String wrongParent = objMapper.writeValueAsString(classification2);
        ClassificationResource classification3 = createClassification("child", "ImportKey13", "DOMAIN_A", null,
            "ImportKey11");
        String child = objMapper.writeValueAsString(classification3);

        List<String> clList = new ArrayList<>();
        clList.add(wrongParent);
        clList.add(parent);
        clList.add(child);

        ResponseEntity<Void> response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ClassificationSummaryResource rightParentCl = getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_A");
        ClassificationSummaryResource wrongParentCl = getClassificationWithKeyAndDomain("ImportKey11", "DOMAIN_B");
        ClassificationSummaryResource childCl = getClassificationWithKeyAndDomain("ImportKey13", "DOMAIN_A");

        assertNotNull(rightParentCl);
        assertNotNull(wrongParentCl);
        assertNotNull(childCl);
        assertEquals(rightParentCl.getClassificationId(), childCl.getParentId());
        assertNotEquals(wrongParentCl.getClassificationId(), childCl.getParentId());
    }

    @Test
    public void testChangeParentByImportingExistingClassification() throws IOException, InterruptedException {
        ClassificationSummaryResource child1 = this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
        assertEquals("L11010", child1.getParentKey());
        child1.setParentId("CLI:100000000000000000000000000000000002");
        child1.setParentKey("L10303");
        String withNewParent = objMapper.writeValueAsString(child1);

        ClassificationSummaryResource child2 = this.getClassificationWithKeyAndDomain("L110107", "DOMAIN_A");
        assertEquals("L11010", child2.getParentKey());
        child2.setParentId("");
        child2.setParentKey("");
        String withoutParent = objMapper.writeValueAsString(child2);

        List<String> clList = new ArrayList<>();
        clList.add(withNewParent);
        clList.add(withoutParent);

        ResponseEntity<Void> response = importRequest(clList);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Thread.sleep(10);
        LOGGER.debug("Wait 10 ms to give the system a chance to update");

        ClassificationSummaryResource childWithNewParent = this.getClassificationWithKeyAndDomain("L110105",
            "DOMAIN_A");
        assertEquals(child1.parentKey, childWithNewParent.parentKey);

        ClassificationSummaryResource childWithoutParent = this.getClassificationWithKeyAndDomain("L110107",
            "DOMAIN_A");
        assertEquals(child2.parentId, childWithoutParent.parentId);
        assertEquals(child2.parentKey, childWithoutParent.parentKey);
    }

    @Test
    public void testFailOnImportDuplicates() throws IOException {
        ClassificationSummaryResource classification = this.getClassificationWithKeyAndDomain("L110105", "DOMAIN_A");
        String classificationString = objMapper.writeValueAsString(classification);

        List<String> clList = new ArrayList<>();
        clList.add(classificationString);
        clList.add(classificationString);

        try {
            importRequest(clList);
            fail("Expected http-Status 409");
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }
    }

    private ClassificationResource createClassification(String id, String key, String domain, String parentId,
        String parentKey) {
        ClassificationResource classificationResource = new ClassificationResource();
        classificationResource.setClassificationId(id);
        classificationResource.setKey(key);
        classificationResource.setDomain(domain);
        classificationResource.setParentId(parentId);
        classificationResource.setParentKey(parentKey);
        return classificationResource;
    }

    private ClassificationSummaryResource getClassificationWithKeyAndDomain(String key, String domain) {
        LOGGER.debug("Request classification with key={} in domain={}", key, domain);
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<ClassificationSummaryListResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/api/v1/classifications?key=" + key + "&domain=" + domain,
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
        return response.getBody().getContent().toArray(new ClassificationSummaryResource[1])[0];
    }

    private ResponseEntity<Void> importRequest(List<String> clList) throws IOException {
        LOGGER.debug("Start Import");
        File tmpFile = File.createTempFile("test", ".tmp");
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(tmpFile), StandardCharsets.UTF_8);
        writer.write(clList.toString());
        writer.close();

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        body.add("file", new FileSystemResource(tmpFile));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        String serverUrl = server + port + "/api/v1/classification-definitions";
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.postForEntity(serverUrl, requestEntity, Void.class);
    }

    /**
     * Return a REST template which is capable of dealing with responses in HAL format.
     *
     * @return RestTemplate
     */
    private RestTemplate getRestTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/haljson,*/*"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }
}
