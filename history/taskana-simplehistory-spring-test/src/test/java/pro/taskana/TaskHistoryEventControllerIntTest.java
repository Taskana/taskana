package pro.taskana;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
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

import pro.taskana.exceptions.SystemException;
import pro.taskana.rest.resource.TaskHistoryEventListResource;
import pro.taskana.rest.simplehistory.TaskHistoryRestConfiguration;
import pro.taskana.rest.simplehistory.sampledata.SampleDataGenerator;

/**
 * Controller for integration test.
 */
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TaskHistoryRestConfiguration.class},
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskHistoryEventControllerIntTest {

    @Value("${taskana.schemaName:TASKANA}")
    public String schemaName;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskHistoryEventControllerIntTest.class);

    String server = "http://127.0.0.1:";

    RestTemplate template;

    HttpEntity<String> request;

    @LocalServerPort
    int port;

    @Autowired
    private DataSource dataSource;

    @Before
    public void before() {
        template = getRestTemplate();
        SampleDataGenerator sampleDataGenerator;
        try {
            sampleDataGenerator = new SampleDataGenerator(dataSource);
            sampleDataGenerator.generateSampleData(schemaName);
        } catch (SQLException e) {
            throw new SystemException("tried to reset DB and caught Exception " + e, e);
        }
    }

    @Test
    public void testGetAllHistoryEvent() {
        ResponseEntity<TaskHistoryEventListResource> response = template.exchange(
            server + port + "/api/v1/task-history-event", HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskHistoryEventListResource>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(50, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllHistoryEventDescendingOrder() {
        String parameters = "/api/v1/task-history-event?sort-by=business-process-id&order=desc&page-size=3&page=1";
        ResponseEntity<TaskHistoryEventListResource> response = template.exchange(
            server + port + parameters, HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskHistoryEventListResource>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(3, response.getBody().getContent().size());
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
    }

    @Test
    public void testGetSpecificTaskHistoryEvent() {
        ResponseEntity<TaskHistoryEventListResource> response = template.exchange(
            server + port
                + "/api/v1/task-history-event?business-process-id=BPI:01&sort-by=business-process-id&order=asc&page-size=6&page=1",
            HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskHistoryEventListResource>() {

            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertNotNull(response.getBody().getLinks());
        assertNotNull(response.getBody().getMetadata());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    public void testThrowsExceptionIfInvalidFilterIsUsed() {
        try {
            template.exchange(
                server + port + "/api/v1/task-history-event?invalid=BPI:01", HttpMethod.GET, request,
                new ParameterizedTypeReference<TaskHistoryEventListResource>() {
                });
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
        }
    }

    @Test
    public void testGetHistoryEventOfDate() {
        String currentTime = LocalDateTime.now().toString();

        try {
            template.exchange(
                server + port + "/api/v1/task-history-event?created=" + currentTime, HttpMethod.GET, request,
                new ParameterizedTypeReference<TaskHistoryEventListResource>() {
                });
            fail();
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
            assertTrue(e.getResponseBodyAsString().contains(currentTime));
        }

        // correct Format 'yyyy-MM-dd'
        currentTime = currentTime.substring(0, 10);
        ResponseEntity<TaskHistoryEventListResource> response = template.exchange(
            server + port + "/api/v1/task-history-event?created=" + currentTime, HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskHistoryEventListResource>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(25, response.getBody().getContent().size());
    }

    @Test
    public void testGetSecondPageSortedByKey() {
        String parameters = "/api/v1/task-history-event?sort-by=workbasket-key&order=desc&page=2&page-size=2";
        ResponseEntity<TaskHistoryEventListResource> response = template.exchange(
            server + port + parameters, HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskHistoryEventListResource>() {

            });
        assertEquals(2, response.getBody().getContent().size());
        assertEquals("WBI:100000000000000000000000000000000002",
            response.getBody().getContent().iterator().next().getWorkbasketKey());
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(parameters));
        assertNotNull(response.getBody().getLink("allTaskHistoryEvent"));
        assertTrue(response.getBody()
            .getLink("allTaskHistoryEvent")
            .getHref()
            .endsWith("/api/v1/task-history-event"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
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

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }
}
