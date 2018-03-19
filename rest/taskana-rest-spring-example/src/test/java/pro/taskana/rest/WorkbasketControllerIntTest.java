package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
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

import pro.taskana.rest.resource.WorkbasketSummaryResource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(RestConfiguration.class)
public class WorkbasketControllerIntTest {

    @LocalServerPort
    int port;

    @Test
    public void testGetAllWorkbaskets() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/workbaskets", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    public void testGetAllWorkbasketsKeepingFilters() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/workbaskets?type=PERSONAL&sortBy=key&order=desc", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/workbaskets?type=PERSONAL&sortBy=key&order=desc"));
    }

    @Test
    public void testGetSecondPageSortedByKey() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/workbaskets?sortBy=key&order=desc&page=2&pagesize=5", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("USER_1_1", response.getBody().getContent().iterator().next().key);
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/workbaskets?sortBy=key&order=desc&page=2&pagesize=5"));
        assertNotNull(response.getBody().getLink("allWorkbaskets"));
        assertTrue(response.getBody()
            .getLink("allWorkbaskets")
            .getHref()
            .endsWith("/v1/workbaskets"));
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
