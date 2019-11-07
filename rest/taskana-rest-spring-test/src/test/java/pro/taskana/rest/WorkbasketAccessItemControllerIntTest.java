package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
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
import pro.taskana.rest.resource.WorkbasketAccessItemListResource;
import pro.taskana.rest.resource.WorkbasketAccessItemPaginatedListResource;

/**
 * Test WorkbasketAccessItemController.
 */
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@TaskanaSpringBootTest
class WorkbasketAccessItemControllerIntTest {

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
    void testGetAllWorkbasketAccessItems() {
        ResponseEntity<WorkbasketAccessItemListResource> response = template.exchange(
            url + port + "/api/v1/workbasket-access-items", HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    void testGetWorkbasketAccessItemsKeepingFilters() {
        String parameters = "/api/v1/workbasket-access-items?sort-by=workbasket-key&order=asc&page=1&page-size=9&access-ids=user_1_1";
        ResponseEntity<WorkbasketAccessItemListResource> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
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
                url + port
                    + "/api/v1/workbasket-access-items/?sort-by=workbasket-key&order=asc&page=1&page-size=9&invalid=user_1_1",
                HttpMethod.GET, request,
                ParameterizedTypeReference.forType(WorkbasketAccessItemListResource.class));
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    void testGetSecondPageSortedByWorkbasketKey() {
        String parameters = "/api/v1/workbasket-access-items?sort-by=workbasket-key&order=asc&page=2&page-size=9&access-ids=user_1_1";
        ResponseEntity<WorkbasketAccessItemPaginatedListResource> response = template.exchange(
            url + port + parameters, HttpMethod.GET, request,
            ParameterizedTypeReference.forType(WorkbasketAccessItemPaginatedListResource.class));
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("user_1_1", response.getBody().getContent().iterator().next().accessId);
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertEquals(9, response.getBody().getMetadata().getSize());
        assertEquals(1, response.getBody().getMetadata().getTotalElements());
        assertEquals(1, response.getBody().getMetadata().getTotalPages());
        assertEquals(1, response.getBody().getMetadata().getNumber());
    }

    @Test
    void testRemoveWorkbasketAccessItemsOfUser() {

        String parameters = "/api/v1/workbasket-access-items/?access-id=user_1_1";
        ResponseEntity<Void> response = template.exchange(
            url + port + parameters, HttpMethod.DELETE, request,
            ParameterizedTypeReference.forType(Void.class));
        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetBadRequestIfTryingToDeleteAccessItemsForGroup() {
        String parameters = "/api/v1/workbasket-access-items?access-id=cn=DevelopersGroup,ou=groups,o=TaskanaTest";
        try {
            ResponseEntity<Void> response = template.exchange(
                url + port + parameters, HttpMethod.DELETE, request,
                ParameterizedTypeReference.forType(Void.class));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
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
