package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.DistributionTargetListResource;
import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.WorkbasketSummaryListResource;

/**
 * Test WorkbasketController.
 */

@TaskanaSpringBootTest
class WorkbasketControllerIntTest {

    String url = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    @LocalServerPort
    int port;

    @BeforeEach
    void before() {
        template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    void testGetAllWorkbaskets() {
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            url + port + "/api/v1/workbaskets", HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            url + port + "/api/v1/workbaskets?required-permission=OPEN", HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(3, response.getBody().getContent().size());
    }

    @Test
    void testGetAllWorkbasketsKeepingFilters() {
        String parameters = "/api/v1/workbaskets?type=PERSONAL&sort-by=key&order=desc";
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
    }

    @Test
    void testThrowsExceptionIfInvalidFilterIsUsed() {
        try {
            template.exchange(
                url + port + "/api/v1/workbaskets?invalid=PERSONAL", HttpMethod.GET, request,
                ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    void testGetSecondPageSortedByKey() {

        String parameters = "/api/v1/workbaskets?sort-by=key&order=desc&page=2&page-size=5";
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("USER_1_1", response.getBody().getContent().iterator().next().getKey());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_NEXT));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    void testRemoveWorkbasketAsDistributionTarget() {
        String parameters = "/api/v1/workbaskets/distribution-targets/WBI:100000000000000000000000000000000007";
        ResponseEntity<?> response = template.exchange(
            url + port + parameters, HttpMethod.DELETE, request,
            Void.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<DistributionTargetListResource> response2 = template.exchange(
            url + port + "/api/v1/workbaskets/WBI:100000000000000000000000000000000002/distribution-targets",
            HttpMethod.GET, request,
            ParameterizedTypeReference.forType(DistributionTargetListResource.class));
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Iterator<DistributionTargetResource> iterator = response2.getBody().getContent().iterator();
        while (iterator.hasNext()) {
            assertNotEquals("WBI:100000000000000000000000000000000007", iterator.next().getWorkbasketId());
        }
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
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        return template;
    }

}
