package pro.taskana.rest;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationSummaryListResource;

/**
 * Test general Exception Handling.
 */

@TaskanaSpringBootTest
class GeneralExceptionHandlingTest {

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @Captor
    private ArgumentCaptor<LoggingEvent> captorLoggingEvent;

    @Autowired RestHelper restHelper;

    private static RestTemplate template;

    @BeforeAll
    static void init() {
        template = RestHelper.getRestTemplate();
    }

    @BeforeEach
    void before() {

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
    void testDeleteNonExisitingClassificationExceptionIsLogged() {
        try {
            template.exchange(
                restHelper.toUrl(Mapping.URL_CLASSIFICATIONS_ID, "non-existing-id"), HttpMethod.DELETE,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(ClassificationSummaryListResource.class)
            );
        } catch (Exception ex) {
            verify(mockAppender).doAppend(captorLoggingEvent.capture());
            assertTrue(captorLoggingEvent.getValue()
                .getMessage()
                .contains("The classification \"non-existing-id\" wasn't found"));
        }

    }
}
