package pro.taskana.rest;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.rest.resource.WorkbasketDefinition;

/**
 * Test workbasket definitions.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkbasketDefinitionControllerIntTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassificationController.class);
    private String server = "http://127.0.0.1:";
    RestTemplate template;
    private HttpEntity<String> request;
    private HttpHeaders headers = new HttpHeaders();

    @LocalServerPort
    int port;

    @Before
    public void before() {
        LOGGER.debug("before");
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void exportWorkbasketFromDomain() {
        ResponseEntity<List<WorkbasketDefinition>> response = template.exchange(
            this.server + this.port + "/v1/workbasket-definitions?domain=DOMAIN_A",
            HttpMethod.GET, request, new ParameterizedTypeReference<List<WorkbasketDefinition>>() {

            });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody().get(0), instanceOf(WorkbasketDefinition.class));
        assertEquals(11, response.getBody().size());
    }

    @Test
    public void exportWorkbasketFromWrongDomain() {
        ResponseEntity<List<WorkbasketDefinition>> response = template.exchange(
            this.server + this.port + "/v1/workbasket-definitions?domain=wrongDomain",
            HttpMethod.GET, request, new ParameterizedTypeReference<List<WorkbasketDefinition>>() {

            });
        assertEquals(0, response.getBody().size());
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
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/haljson,*/*"));
        converter.setObjectMapper(mapper);

        return new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
    }

}
