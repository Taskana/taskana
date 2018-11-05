package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Request;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.Classification;
import pro.taskana.exceptions.*;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;

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
        		server + port + "/v1/classificationdefinitions?domain=DOMAIN_B",
        		HttpMethod.GET, request, new ParameterizedTypeReference<ClassificationResource[]>() {
				}
                );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 5);
        assertTrue(response.getBody().length <= 7);
    }

	@Test
	public void testExportClassificationsFromWrongDomain() {
        ResponseEntity<ClassificationResource[]> response = template.exchange(
        		server + port + "/v1/classificationdefinitions?domain=ADdfe",
        		HttpMethod.GET, request, new ParameterizedTypeReference<ClassificationResource[]>() {
				}
                );
        assertEquals(0, response.getBody().length);
	}
	
    @Test
    public void testImportClassification() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        List<String> clList = new ArrayList<>();
        clList.add(newClassification);
        
        HttpURLConnection con = importRequest(clList);
        assertEquals(200, con.getResponseCode());
    }

    @Test
    public void testImportFilledClassification() throws IOException {
        ClassificationResource classification = new ClassificationResource();
        classification.setClassificationId("classificationId_");
        classification.setKey("key droelf");
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

        HttpURLConnection con = importRequest(clList);
        assertEquals(200, con.getResponseCode());
    }

    @Test
    public void testFailureWhenKeyIsMissing() throws IOException {
        ClassificationResource classification = new ClassificationResource();
        classification.setDomain("");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(classification));

        HttpURLConnection con = importRequest(clList);
        assertEquals(400, con.getResponseCode());
    }

    @Test
    public void testFailureWhenDomainIsMissing() throws IOException {
        ClassificationResource classification = new ClassificationResource();
        classification.setKey("one");
        List<String> clList = new ArrayList<>();
        clList.add(objMapper.writeValueAsString(classification));

        HttpURLConnection con = importRequest(clList);
        assertEquals(404, con.getResponseCode());
    }

    @Test
    public void testImportMultipleClassifications() throws IOException, InterruptedException {
        ClassificationResource classification1 = new ClassificationResource();
        classification1.setClassificationId("Id1");
        classification1.setKey("ImportKey1");
        classification1.setDomain("DOMAIN_A");
        String c1 = objMapper.writeValueAsString(classification1);
        ClassificationResource classification2 = new ClassificationResource();
        classification2.setClassificationId("Id2");
        classification2.setKey("ImportKey2");
        classification2.setDomain("DOMAIN_A");
        String c2 = objMapper.writeValueAsString(classification2);

        List<String> clList = new ArrayList<>();
        clList.add(c1);
        clList.add(c2);

        HttpURLConnection con = importRequest(clList);
        assertEquals(200, con.getResponseCode());
    }

    @Test
    public void testImportDuplicateClassification() throws IOException, InterruptedException {
        ClassificationResource classification1 = new ClassificationResource();
        classification1.setClassificationId("id1");
        classification1.setKey("ImportKey3");
        classification1.setDomain("DOMAIN_A");
        String c1 = objMapper.writeValueAsString(classification1);

        List<String> clList = new ArrayList<>();
        clList.add(c1);
        clList.add(c1);

        HttpURLConnection con = importRequest(clList);
        assertEquals(409, con.getResponseCode()); //Conflict
    }

    @Test
    public void testImportParentAndChildClassification() throws IOException, InterruptedException {
    	// Level 1
        ClassificationResource classification1 = new ClassificationResource();
        classification1.setClassificationId("parentId");
        classification1.setKey("ImportKey6");
        classification1.setDomain("DOMAIN_A");
        String c1 = objMapper.writeValueAsString(classification1);
        
        // Level 2
        ClassificationResource classification2 = new ClassificationResource();
        classification2.setClassificationId("childId1");
        classification2.setKey("ImportKey7");
        classification2.setDomain("DOMAIN_A");
        classification2.setParentId("parentId");
        classification2.setParentKey("ImportKey6");
        String c21 = objMapper.writeValueAsString(classification2);
        classification2.setClassificationId("childId2");
        classification2.setKey("ImportKey8");
        classification2.setDomain("DOMAIN_A");
        classification2.setParentId("parentId");
        classification2.setParentKey("ImportKey6");
        String c22 = objMapper.writeValueAsString(classification2);

        // Level 3
        ClassificationResource classification3 = new ClassificationResource();
        classification3.setClassificationId("grandchildId1");
        classification3.setKey("ImportKey9");
        classification3.setDomain("DOMAIN_A");
        classification3.setParentId("childId1");
        classification3.setParentKey("ImportKey7");
        String c31 = objMapper.writeValueAsString(classification3);
        classification3.setClassificationId("grandchildId2");
        classification3.setKey("ImportKey10");
        classification3.setDomain("DOMAIN_A");
        classification3.setParentId("childId1");
        classification3.setParentKey("ImportKey7");
        String c32 = objMapper.writeValueAsString(classification3);

        List<String> clList = new ArrayList<>();
        clList.add(c31);
        clList.add(c32);
        clList.add(c21);
        clList.add(c22);
        clList.add(c1);

        HttpURLConnection con = importRequest(clList);
        assertEquals(200, con.getResponseCode());
    }

    @Test
    public void testImportParentAndChildClassificationWithKey() throws IOException, InterruptedException {
        ClassificationResource classification1 = new ClassificationResource();
        classification1.setClassificationId("parent");
        classification1.setKey("ImportKey11");
        classification1.setDomain("DOMAIN_A");
        String parent = objMapper.writeValueAsString(classification1);
        ClassificationResource classification2 = new ClassificationResource();
        classification2.setClassificationId("wrongParent");
        classification2.setKey("ImportKey11");
        classification2.setDomain("DOMAIN_B");
        String wrongParent = objMapper.writeValueAsString(classification2);
        ClassificationResource classification3 = new ClassificationResource();
        classification3.setClassificationId("child");
        classification3.setKey("ImportKey13");
        classification3.setDomain("DOMAIN_A");
        classification3.setParentKey("ImportKey11");
        String child = objMapper.writeValueAsString(classification3);

        List<String> clList = new ArrayList<>();
        clList.add(wrongParent);
        clList.add(parent);
        clList.add(child);

        HttpURLConnection con = importRequest(clList);
        assertEquals(200, con.getResponseCode());
    }

    private HttpURLConnection importRequest (List<String> classificationList) throws IOException {
        URL url = new URL(server + port + "/v1/classificationdefinitions/import");
    	HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(classificationList.toString());
        out.flush();
        out.close();
        return con;
    }

    /**
     * Return a REST template which is capable of dealing with responses in HAL format
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

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        return template;
    }
}
