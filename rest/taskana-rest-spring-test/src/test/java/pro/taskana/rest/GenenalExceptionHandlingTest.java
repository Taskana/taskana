package pro.taskana.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.ldap.LdapCacheTestImpl;
import pro.taskana.rest.resource.ClassificationSummaryListResource;

/**
 * Test general Exception Handling.
 */

@TaskanaSpringBootTest
class GenenalExceptionHandlingTest {

    String server = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    int port;
    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @BeforeEach
    void before() {
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
        final Logger logger = (Logger) LoggerFactory.getLogger(TaskanaRestExceptionHandler.class);
        logger.addAppender(mockAppender);
    }

    // Always have this teardown otherwise we can stuff up our expectations. Besides, it's
    // good coding practise
    @AfterEach
    void teardown() {
        final Logger logger = (Logger) LoggerFactory.getLogger(TaskanaRestExceptionHandler.class);
        logger.detachAppender(mockAppender);
    }

    @Test
    void testAccessIdValidationMinimunValueExceptionIsLogged() {
        try {

            AccessIdController.setLdapCache(new LdapCacheTestImpl());
            template.exchange(
                server + port + "/api/v1/access-ids?search-for=al", HttpMethod.GET, request,
                ParameterizedTypeReference.forType(List.class));
        } catch (Exception ex) {
            verify(mockAppender).doAppend(captorLoggingEvent.capture());
            assertTrue(
                captorLoggingEvent.getValue().getMessage().contains("is too short. Minimum searchFor length = "));
        }
    }

    @Test
    void testDeleteNonExisitingClassificationExceptionIsLogged() {
        try {
            template.exchange(
                server + port + "/api/v1/classifications/non-existing-id", HttpMethod.DELETE, request,
                ParameterizedTypeReference.forType(ClassificationSummaryListResource.class));
        } catch (Exception ex) {
            verify(mockAppender).doAppend(captorLoggingEvent.capture());
            assertTrue(captorLoggingEvent.getValue()
                .getMessage()
                .contains("The classification \"non-existing-id\" wasn't found"));
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
