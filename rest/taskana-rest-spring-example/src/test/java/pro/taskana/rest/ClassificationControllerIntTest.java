package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.Collections;

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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.Task;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.assembler.ClassificationResourceAssembler;
import pro.taskana.rest.resource.assembler.TaskResourceAssembler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class ClassificationControllerIntTest {

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @Autowired
    private TaskResourceAssembler taskResourceAssembler;

    @Autowired
    Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationControllerIntTest.class);
    String server = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    int port;

    @Before
    public void before() {
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void testGetAllClassifications() {
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            server + port + "/v1/classifications", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    public void testGetAllClassificationsFilterByCustomAttribute() {
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            server + port + "/v1/classifications?domain=DOMAIN_A&custom-1-like=RVNR", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(13, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllClassificationsKeepingFilters() {
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            server + port + "/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc"));
        assertEquals(17, response.getBody().getContent().size());
        assertEquals("A12", response.getBody().getContent().iterator().next().key);
    }

    @Test
    public void testGetSecondPageSortedByKey() {
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            server + port + "/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

            });
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("L1050", response.getBody().getContent().iterator().next().key);
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5"));
        assertNotNull(response.getBody().getLink("allClassifications"));
        assertTrue(response.getBody()
            .getLink("allClassifications")
            .getHref()
            .endsWith("/v1/classifications"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_NEXT));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    public void testCreateClassification() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        URL url = new URL(server + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();

        newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_2\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        url = new URL(server + port + "/v1/classifications");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testCreateClassificationWithParentId() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P1\",\"name\":\"new classification\",\"type\":\"TASK\",\"parentId\":\"CLI:200000000000000000000000000000000015\"}";
        URL url = new URL(server + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testCreateClassificationWithParentKey() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\",\"type\":\"TASK\",\"parentKey\":\"T2100\"}";
        URL url = new URL(server + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testCreateClassificationWithParentKeyInDOMAIN_AShouldCreateAClassificationInRootDomain()
        throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_P2\",\"name\":\"new classification\",\"type\":\"TASK\",\"parentKey\":\"T2100\"}";

        URL url = new URL(server + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();

        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            server + port + "/v1/classifications", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        boolean foundClassificationCreated = false;
        for (ClassificationSummaryResource classification : response.getBody().getContent()) {
            if ("NEW_CLASS_P2".equals(classification.getKey()) && "".equals(classification.getDomain())
                && "T2100".equals(classification.getParentKey())) {
                foundClassificationCreated = true;
            }
        }

        assertEquals(true, foundClassificationCreated);
    }

    @Test
    public void testReturn400IfCreateClassificationWithIncompatibleParentIdAndKey() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_B\",\"key\":\"NEW_CLASS_P3\",\"name\":\"new classification\",\"type\":\"TASK\",\"parentId\":\"CLI:200000000000000000000000000000000015\",\"parentKey\":\"T2000\"}";
        URL url = new URL(server + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(400, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testCreateClassificationWithClassificationIdReturnsError400() throws IOException {
        String newClassification = "{\"classificationId\":\"someId\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        URL url = new URL("http://127.0.0.1:" + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(400, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testGetClassificationWithSpecialCharacter() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<ClassificationSummaryResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {

            });
        assertEquals("Zustimmungserklärung", response.getBody().name);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testDeleteClassification() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<ClassificationSummaryResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:200000000000000000000000000000000004",
            HttpMethod.DELETE,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {

            });
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:200000000000000000000000000000000004",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {

            });
    }

    private void verifyTaskIsModifiedAfter(String taskId, Instant before) throws InvalidArgumentException {
        RestTemplate admTemplate = getRestTemplate();
        HttpHeaders admHeaders = new HttpHeaders();
        admHeaders.add("Authorization", "Basic YWRtaW46YWRtaW4=");  // admin:admin

        HttpEntity<String> admRequest = new HttpEntity<String>(admHeaders);

        ResponseEntity<TaskResource> taskResponse = admTemplate.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks/" + taskId,
            HttpMethod.GET,
            admRequest,
            new ParameterizedTypeReference<TaskResource>() {

            });

        TaskResource taskResource = taskResponse.getBody();
        Task task = taskResourceAssembler.toModel(taskResource);

        assertTrue(!before.isAfter(task.getModified()));
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
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }

}
