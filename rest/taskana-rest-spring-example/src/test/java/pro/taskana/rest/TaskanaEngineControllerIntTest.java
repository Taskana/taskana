package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.TaskanaRole;
import pro.taskana.rest.resource.TaskanaUserInfoResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"devMode=true"})
public class TaskanaEngineControllerIntTest {

    @LocalServerPort
    int port;

    @Test
    public void testDomains() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<String>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/domains", HttpMethod.GET, request,
            new ParameterizedTypeReference<List<String>>() {
            });
        assertTrue(response.getBody().contains("DOMAIN_A"));
    }

    @Test
    public void testClassificationTypes() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<String>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classification-types", HttpMethod.GET, request,
            new ParameterizedTypeReference<List<String>>() {
            });
        assertTrue(response.getBody().contains("TASK"));
        assertTrue(response.getBody().contains("DOCUMENT"));
        assertFalse(response.getBody().contains("UNKNOWN"));
    }

    @Test
    public void testClassificationCategories() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<List<String>> response = template.exchange(
                "http://127.0.0.1:" + port + "/v1/classification-categories/?type=TASK", HttpMethod.GET, request,
            new ParameterizedTypeReference<List<String>>() {
            });
        assertTrue(response.getBody().contains("MANUAL"));
        assertTrue(response.getBody().contains("EXTERNAL"));
        assertTrue(response.getBody().contains("AUTOMATIC"));
        assertTrue(response.getBody().contains("PROCESS"));
        assertFalse(response.getBody().contains("UNKNOWN"));
    }

    @Test
    public void testGetCurrentUserInfo() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<TaskanaUserInfoResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/current-user-info", HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskanaUserInfoResource>() {
            });
        assertEquals("teamlead_1", response.getBody().getUserId());
        assertTrue(response.getBody().getGroupIds().contains("businessadmin"));
        assertTrue(response.getBody().getRoles().contains(TaskanaRole.BUSINESS_ADMIN));
        assertFalse(response.getBody().getRoles().contains(TaskanaRole.ADMIN));
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
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json, application/json"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }

}
