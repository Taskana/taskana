package pro.taskana.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collections;
import java.util.Date;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.Task;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.SystemException;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskSummaryResource;
import pro.taskana.rest.resource.assembler.TaskResourceAssembler;
import pro.taskana.sampledata.SampleDataGenerator;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class TaskCleanupJobIntTest {

    @Autowired
    private TaskResourceAssembler taskResourceAssembler;

    @Autowired
    private DataSource dataSource;

    @Autowired
    Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskCleanupJobIntTest.class);
    String server = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    int port;

    @Before
    public void before() {
        resetDb();
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void testCleanupCompletedTasksJob()
        throws InterruptedException {

        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<TaskSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(71, response.getBody().getContent().size());

        // wait until the next trigger time + 2 seconds to give JobScheduler a chance to run
        String cron = env.getProperty("taskana.jobscheduler.async.cron");
        CronTrigger trigger = new CronTrigger(cron);
        TriggerContext context = new TriggerContext() {

            @Override
            public Date lastScheduledExecutionTime() {
                return null;
            }

            @Override
            public Date lastActualExecutionTime() {
                return null;
            }

            @Override
            public Date lastCompletionTime() {
                return null;
            }
        };
        Date now = new Date();
        long delay = trigger.nextExecutionTime(context).getTime() - now.getTime() + 2000;

        LOGGER.info("About to sleep for " + delay / 1000
            + " seconds to give JobScheduler a chance to process the task cleanup");
        Thread.sleep(delay);
        LOGGER.info("Sleeping ended. Continuing .... ");

        // verify the 25 completed tasks have been deleted.
        template = getRestTemplate();
        headers = new HttpHeaders();
        headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // Role Admin
        request = new HttpEntity<String>(headers);
        response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<TaskSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        // TODO
        assertEquals(66, response.getBody().getContent().size());
    }

    private void verifyTaskIsModifiedAfter(String taskId, Instant before) throws InvalidArgumentException {
        RestTemplate admTemplate = getRestTemplate();
        HttpHeaders admHeaders = new HttpHeaders();
        admHeaders.add("Authorization", "Basic YWRtaW46YWRtaW4=");  // admin:admin

        HttpEntity<String> admRequest = new HttpEntity<String>(admHeaders);

        ResponseEntity<TaskResource> taskResponse = admTemplate.exchange(
            "http://127.0.0.1:" + port + "/v1/tasks/" + taskId,
            HttpMethod.GET,
            admRequest,
            new ParameterizedTypeReference<TaskResource>() {

            });

        TaskResource taskResource = taskResponse.getBody();
        Task task = taskResourceAssembler.toModel(taskResource);

        assertTrue(!before.isAfter(task.getModified()));
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

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>> singletonList(converter));
        return template;
    }

    public void resetDb() {
        SampleDataGenerator sampleDataGenerator;
        try {
            sampleDataGenerator = new SampleDataGenerator(dataSource);
            sampleDataGenerator.generateSampleData();
        } catch (SQLException e) {
            throw new SystemException("tried to reset DB and caught Exception " + e, e);
        }
    }

}
