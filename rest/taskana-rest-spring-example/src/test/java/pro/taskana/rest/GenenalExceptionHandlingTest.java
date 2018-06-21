package pro.taskana.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import ch.qos.logback.classic.Logger;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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

import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.rest.resource.AccessIdResource;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.assembler.ClassificationResourceAssembler;
import pro.taskana.rest.resource.assembler.TaskResourceAssembler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
    "devMode=true"})
public class GenenalExceptionHandlingTest {

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @Autowired
    private TaskResourceAssembler taskResourceAssembler;

    String server = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    int port;
    @Mock
    private Appender mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Before
    public void before() {
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
        final Logger logger = (Logger) LoggerFactory.getLogger(TaskanaRestExceptionHandler.class);
        logger.addAppender(mockAppender);
    }

    //Always have this teardown otherwise we can stuff up our expectations. Besides, it's
    //good coding practise
    @After
    public void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(TaskanaRestExceptionHandler.class);
        logger.detachAppender(mockAppender);
    }

    @Test
    public void testAccessIdValidationMinimunValueExceptionIsLogged() {
        try {

            AccessIdController.setLdapCache(new LdapCacheTestImpl());
            template.exchange(
                server + port + "/v1/access-ids?searchFor=al", HttpMethod.GET, request,
                new ParameterizedTypeReference<List<AccessIdResource>>() {

                });
        } catch (Exception ex) {
            verify(mockAppender).doAppend(captorLoggingEvent.capture());
            assertTrue(
                captorLoggingEvent.getValue().getMessage().contains("is too short. Minimum searchFor length = "));
        }
    }

    @Test
    public void testDeleteNonExisitingClassificationExceptionIsLogged() {
        try {
            template.exchange(
                server + port + "/v1/classifications/non-existing-id", HttpMethod.DELETE, request,
                new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {

                });
        } catch (Exception ex) {
            verify(mockAppender).doAppend(captorLoggingEvent.capture());
            assertTrue(captorLoggingEvent.getValue()
                .getMessage()
                .contains("The classification \"non-existing-id\" wasn't found"));
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
