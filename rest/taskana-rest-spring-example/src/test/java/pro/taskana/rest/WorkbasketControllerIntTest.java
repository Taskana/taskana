package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
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

import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class WorkbasketControllerIntTest {

    String url = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    @LocalServerPort
    int port;

    @Before
    public void before() {
        template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void testGetAllWorkbaskets() {
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            url + port + "/v1/workbaskets", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    public void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            url + port + "/v1/workbaskets?required-permission=OPEN", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(3, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllWorkbasketsKeepingFilters() {
        String parameters = "/v1/workbaskets?type=PERSONAL&sort-by=key&order=desc";
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
    }

    @Test
    public void testThrowsExceptionIfInvalidFilterIsUsed() {
        try {
            template.exchange(
                url + port + "/v1/workbaskets?invalid=PERSONAL", HttpMethod.GET, request,
                new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
                });
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    public void testGetSecondPageSortedByKey() {

        String parameters = "/v1/workbaskets?sort-by=key&order=desc&page=2&page-size=5";
        ResponseEntity<PagedResources<WorkbasketSummaryResource>> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<WorkbasketSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("USER_1_1", response.getBody().getContent().iterator().next().key);
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
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

    @Test
    public void testRemoveWorkbasketAsDistributionTarget() {
        String parameters = "/v1/workbaskets/distribution-targets/WBI:100000000000000000000000000000000007";
        ResponseEntity<?> response = template.exchange(
            url + port + parameters, HttpMethod.DELETE, request,
            Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<Resources<DistributionTargetResource>> response2 = template.exchange(
            url + port + "/v1/workbaskets/WBI:100000000000000000000000000000000002/distribution-targets",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<Resources<DistributionTargetResource>>() {
            });
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Iterator<DistributionTargetResource> iterator = response2.getBody().getContent().iterator();
        while (iterator.hasNext()) {
            assertNotEquals("WBI:100000000000000000000000000000000007", iterator.next().workbasketId);
        }
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
