package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
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

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.rest.resource.AccessIdResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {
    "devMode=true"})
public class AccessIdValidationControllerTest {

    @LocalServerPort
    int port;

    @Test
    public void testGetMatches() {
        AccessIdController.setLdapCache(new LdapCacheTestImpl());
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<AccessIdResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/access-ids?searchFor=ali", HttpMethod.GET, request,
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
    public void testBadRequestWhenSearchForIsTooShort() {
        AccessIdController.setLdapCache(new LdapCacheTestImpl());
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        try {
            template.exchange(
                "http://127.0.0.1:" + port + "/v1/access-ids?searchFor=al", HttpMethod.GET, request,
                new ParameterizedTypeReference<List<AccessIdResource>>() {

                });
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("Minimum searchFor length ="));
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

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        return template;
    }

}
