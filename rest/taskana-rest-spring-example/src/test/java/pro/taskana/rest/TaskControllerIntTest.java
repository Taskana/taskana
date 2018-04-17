package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
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
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
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

import pro.taskana.rest.resource.TaskSummaryResource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(RestConfiguration.class)
public class TaskControllerIntTest {

    @LocalServerPort
    int port;

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
        assertEquals(23, response.getBody().getContent().size());
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
        assertEquals(71, response.getBody().getContent().size());
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
    public void testGetLastPageSortedByDue() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=por.value&order=desc&page=15&page-size=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(1, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=15"));
        assertEquals("TKI:100000000000000000000000000000000000",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=por.value&order=desc&page=15&page-size=5"));
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
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=due&order=desc", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(23, response.getBody().getContent().size());

        response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=por.value&order=desc&page=5&page-size=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(3, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=5"));
        assertEquals("TKI:000000000000000000000000000000000023",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=por.value&order=desc&page=5&page-size=5"));
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
