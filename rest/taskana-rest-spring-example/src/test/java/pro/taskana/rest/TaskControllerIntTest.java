package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Ignore;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
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
        assertEquals(22, response.getBody().getContent().size());
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
        assertEquals(70, response.getBody().getContent().size());
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
    @Ignore
    public void testGetLastPageSortedByDue() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=due&order=desc&page=14&pageSize=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=14"));
        assertEquals("TKI:000000000000000000000000000000000004",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=due&order=desc&page=14&pageSize=5"));
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
    @Ignore
    public void testGetLastPageSortedByDueWithHiddenTasksRemovedFromResult() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks?sortBy=due&order=desc&page=14&pageSize=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(2, response.getBody().getContent().size());
        assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=14"));
        assertEquals("TKI:000000000000000000000000000000000005",
            response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=due&order=desc&page=2&pageSize=5"));
        assertNotNull(response.getBody().getLink("allTasks"));
        assertTrue(response.getBody()
            .getLink("allTasks")
            .getHref()
            .endsWith("/v1/tasks"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_NEXT));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    @Ignore
    public void testGetQueryByPorSecondPageSortedByType() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port
                + "/v1/tasks?porCompany=00&porSystem=PASystem&porInstance=00&porType=VNR&porValue=22334455&sortBy=porType&order=asc&page=2&pageSize=5",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("USER_1_1", response.getBody().getContent().iterator().next().getTaskId());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/tasks?sortBy=due&order=desc&page=2&pageSize=5"));
        assertNotNull(response.getBody().getLink("allTasks"));
        assertTrue(response.getBody()
            .getLink("allTasks")
            .getHref()
            .endsWith("/v1/tasks"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_NEXT));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
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
