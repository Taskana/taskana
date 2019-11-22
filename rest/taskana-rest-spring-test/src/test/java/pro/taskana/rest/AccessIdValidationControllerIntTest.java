package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.rest.resource.AccessIdResource;

/**
 * Test AccessIdValidation.
 */
@TaskanaSpringBootTest
class AccessIdValidationControllerIntTest {

    @Autowired RestHelper restHelper;

    static RestTemplate template;

    @BeforeAll
    static void init() {
        template = RestHelper.getRestTemplate();
    }

    @Test
    void testGetMatches() {
        AccessIdController.setLdapCache(new LdapCacheTestImpl());
        HttpEntity<String> request = new HttpEntity<String>(restHelper.getHeaders());
        ResponseEntity<List<AccessIdResource>> response = template.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=ali",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<List<AccessIdResource>>() {

            });
        List<AccessIdResource> body = response.getBody();
        assertNotNull(body);
        assertTrue(3 == body.size());
        List<String> expectedIds = new ArrayList<>(Arrays.asList("Tralisch, Thea", "Bert, Ali", "Mente, Ali"));
        for (AccessIdResource accessId : body) {
            assertTrue(expectedIds.contains(accessId.getName()));
        }
    }

    @Test
    void testBadRequestWhenSearchForIsTooShort() {
        AccessIdController.setLdapCache(new LdapCacheTestImpl());
        HttpEntity<String> request = new HttpEntity<String>(restHelper.getHeaders());
        try {
            template.exchange(
                restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=al",
                HttpMethod.GET,
                request,
                ParameterizedTypeReference.forType(List.class));
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Minimum searchFor length ="));
        }

    }

}
