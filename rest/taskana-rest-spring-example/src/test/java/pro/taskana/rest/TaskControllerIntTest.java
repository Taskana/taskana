package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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

import pro.taskana.exceptions.SystemException;
import pro.taskana.rest.resource.TaskSummaryResource;
import pro.taskana.sampledata.SampleDataGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class TaskControllerIntTest {

    @Value("${taskana.schemaName:TASKANA}")
    public String schemaName;

    @LocalServerPort
    int port;

    @Autowired
    private DataSource dataSource;

    public void resetDb() {
        SampleDataGenerator sampleDataGenerator;
        try {
            sampleDataGenerator = new SampleDataGenerator(dataSource);
            sampleDataGenerator.generateSampleData(schemaName);
        } catch (SQLException e) {
            throw new SystemException("tried to reset DB and caught Exception " + e, e);
        }
    }

    @Test
    public void testGetAllTasks() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(25, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllTasksByWorkbasketId() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"); // teamlead_1
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?workbasket-id=WBI:100000000000000000000000000000000001",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(22, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllTasksByWorkbasketKeyAndDomain() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?workbasket-key=USER_1_2&domain=DOMAIN_A",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(20, response.getBody().getContent().size());
    }

    @Test
    public void testExceptionIfKeyIsSetButDomainIsMissing() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
        HttpEntity<String> request = new HttpEntity<String>(headers);
        try {
            ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
                "http://127.0.0.1:" + port + "/v1/tasks?workbasket-key=USER_1_2",
                HttpMethod.GET, request,
                new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
                });
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
        }
    }

    @Test
    public void testGetAllTasksWithAdminRole() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(73, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllTasksKeepingFilters() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?por.type=VNR&por.value=22334455&sortBy=por.value&order=desc",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?por.type=VNR&por.value=22334455&sortBy=por.value&order=desc"));
    }

    @Test
    public void testThrowsExceptionIfInvalidFilterIsUsed() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        try {
            template.exchange(
                "http://127.0.0.1:" + port + "/v1/tasks?invalid=VNR",
                HttpMethod.GET, request,
                new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
                });
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    public void testGetLastPageSortedByPorValue() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port
                + "/v1/tasks?state=READY,CLAIMED&sortBy=por.value&order=desc&page=15&page-size=5",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(1, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=14"));
        assertEquals("TKI:100000000000000000000000000000000000",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?state=READY,CLAIMED&sortBy=por.value&order=desc&page=15&page-size=5"));
        assertNotNull(response.getBody().getLink("allTasks"));
        assertTrue(response.getBody()
            .getLink("allTasks")
            .getHref()
            .endsWith("/v1/tasks"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    public void testGetLastPageSortedByDueWithHiddenTasksRemovedFromResult() {
        resetDb(); // required because ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
                   // tasks and this test depends on the tasks as they are in sampledata
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=due&order=desc", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(25, response.getBody().getContent().size());

        response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=due&order=desc&page=5&page-size=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=5"));
        assertEquals("TKI:000000000000000000000000000000000021",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=due&order=desc&page=5&page-size=5"));
        assertNotNull(response.getBody().getLink("allTasks"));
        assertTrue(response.getBody()
            .getLink("allTasks")
            .getHref()
            .endsWith("/v1/tasks"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    public void testGetQueryByPorSecondPageSortedByType() {
        resetDb(); // required because ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
                   // tasks and this test depends on the tasks as they are in sampledata
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port
                + "/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&por.type=VNR&por.value=22334455&sortBy=por.type&order=asc&page=2&page-size=5",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("TKI:000000000000000000000000000000000013",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(
                "/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&por.type=VNR&por.value=22334455&sortBy=por.type&order=asc&page=2&page-size=5"));
        assertNotNull(response.getBody().getLink("allTasks"));
        assertTrue(response.getBody()
            .getLink("allTasks")
            .getHref()
            .endsWith("/v1/tasks"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }
    
    @Test
    public void testGetTaskWithAttachments() throws IOException {
        URL url = new URL("http://127.0.0.1:" + port + "/v1/tasks/TKI:000000000000000000000000000000000002");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
        assertEquals(200, con.getResponseCode());
        
        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String response = content.toString();
        assertFalse(response.contains("\"attachments\":[]"));
        int start = response.indexOf("created", response.indexOf("created") + 1);
        String createdString = response.substring(start + 10, start + 30);
        assertTrue(createdString.matches("\\d{4}-[01]\\d-[0-3]\\dT[0-2]\\d:[0-5]\\d:[0-5]\\d([+-][0-2]\\d:[0-5]\\d|Z)"));
    }

    @Test
    public void testGetAndUpdateTask() throws IOException {
        URL url = new URL("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        assertEquals(200, con.getResponseCode());

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String originalTask = content.toString();

        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(content.toString());
        out.flush();
        out.close();
        assertEquals(200, con.getResponseCode());
        con.disconnect();

        url = new URL("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        assertEquals(200, con.getResponseCode());

        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String updatedTask = content.toString();

        assertNotEquals(
            originalTask.substring(originalTask.indexOf("modified"), originalTask.indexOf("modified") + 30),
            updatedTask.substring(updatedTask.indexOf("modified"), updatedTask.indexOf("modified") + 30));

    }

    @Test
    public void testCreateAndDeleteTask() throws IOException {
        String taskToCreateJson = "{\"classificationSummaryResource\":{\"key\":\"L11010\"}," +
            "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
            "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

        URL url = new URL("http://127.0.0.1:" + port + "/v1/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(taskToCreateJson);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        // con.disconnect();

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        StringBuffer responsePayload = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            responsePayload.append(inputLine);
        }
        in.close();
        con.disconnect();
        String createdTask = responsePayload.toString();
        String taskIdOfCreatedTask = createdTask.substring(11, 51);
        assertNotNull(taskIdOfCreatedTask);
        assertTrue(taskIdOfCreatedTask.startsWith("TKI:"));

        // delete task again to clean test data
        url = new URL("http://127.0.0.1:" + port + "/v1/tasks/" + taskIdOfCreatedTask);
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4="); // admin
        assertEquals(204, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testCreateTaskWithInvalidParameter() throws IOException {
        String taskToCreateJson = "{\"classificationKey\":\"L11010\"," +
            "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
            "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

        URL url = new URL("http://127.0.0.1:" + port + "/v1/tasks");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(taskToCreateJson);
        out.flush();
        out.close();
        assertEquals(400, con.getResponseCode());
        con.disconnect();

        taskToCreateJson = "{\"classificationSummaryResource\":{\"classificationId\":\"CLI:100000000000000000000000000000000004\"},"
            +
            "\"workbasketSummaryResource\":{\"workbasketId\":\"\"}," +
            "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

        url = new URL("http://127.0.0.1:" + port + "/v1/tasks");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setRequestProperty("Content-Type", "application/json");
        out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(taskToCreateJson);
        out.flush();
        out.close();
        assertEquals(400, con.getResponseCode());
        con.disconnect();

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
        // converter.setSupportedMediaTypes(ImmutableList.of(MediaTypes.HAL_JSON));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }

}
