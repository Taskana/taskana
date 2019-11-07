package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.DistributionTargetListResource;
import pro.taskana.rest.resource.DistributionTargetResource;
import pro.taskana.rest.resource.WorkbasketSummaryListResource;

/**
 * Test WorkbasketController.
 */

@TaskanaSpringBootTest
class WorkbasketControllerIntTest {

    @Autowired RestHelper restHelper;

    private static RestTemplate template;

    @BeforeAll
    static void init() {
        template = RestHelper.getRestTemplate();
    }

    @Test
    void testGetAllWorkbaskets() {
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET), HttpMethod.GET, restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    void testGetAllWorkbasketsBusinessAdminHasOpenPermission() {
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + "?required-permission=OPEN", HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(3, response.getBody().getContent().size());
    }

    @Test
    void testGetAllWorkbasketsKeepingFilters() {
        String parameters = "?type=PERSONAL&sort-by=key&order=desc";
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + parameters, HttpMethod.GET, restHelper.defaultRequest(),
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
                restHelper.toUrl(Mapping.URL_WORKBASKET) + "?invalid=PERSONAL", HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(WorkbasketSummaryListResource.class));
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    void testGetSecondPageSortedByKey() {

        String parameters = "?sort-by=key&order=desc&page=2&page-size=5";
        ResponseEntity<WorkbasketSummaryListResource> response = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET) + parameters, HttpMethod.GET, restHelper.defaultRequest(),
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
        ResponseEntity<?> response = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_DISTRIBUTION_ID), HttpMethod.DELETE, restHelper.defaultRequest(),
            Void.class,
            "WBI:100000000000000000000000000000000007");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        ResponseEntity<DistributionTargetListResource> response2 = template.exchange(
            restHelper.toUrl(Mapping.URL_WORKBASKET_ID_DISTRIBUTION), HttpMethod.GET, restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(DistributionTargetListResource.class),
            "WBI:100000000000000000000000000000000002");
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        Iterator<DistributionTargetResource> iterator = response2.getBody().getContent().iterator();
        while (iterator.hasNext()) {
            assertNotEquals("WBI:100000000000000000000000000000000007", iterator.next().getWorkbasketId());
        }
    }
}
