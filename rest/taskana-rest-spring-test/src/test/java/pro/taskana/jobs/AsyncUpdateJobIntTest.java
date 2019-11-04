package pro.taskana.jobs;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import pro.taskana.Classification;
import pro.taskana.Task;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.ClassificationResourceAssembler;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskResourceAssembler;

/**
 * Test async updates.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class AsyncUpdateJobIntTest {

    private static final String CLASSIFICATION_ID = "CLI:100000000000000000000000000000000003";

    @Autowired
    ClassificationResourceAssembler classificationResourceAssembler;

    @Autowired
    TaskResourceAssembler taskResourceAssembler;

    @Autowired
    JobScheduler jobScheduler;

    @Autowired
    Environment env;

    @LocalServerPort
    int port;

    private String server;

    private RestTemplate template;

    @Before
    public void before() {
        template = getRestTemplate();
        server = "http://127.0.0.1:" + port;
    }

    @Test
    public void testUpdateClassificationPrioServiceLevel()
        throws IOException, InvalidArgumentException {

        // 1st step: get old classification :
        Instant before = Instant.now();
        ObjectMapper mapper = new ObjectMapper();

        ResponseEntity<ClassificationResource> response = template.exchange(
            server + "/api/v1/classifications/{classificationId}",
            HttpMethod.GET,
            new HttpEntity<String>(getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class),
            CLASSIFICATION_ID);

        assertNotNull(response.getBody());
        ClassificationResource classification = response.getBody();
        assertNotNull(classification.getLink(Link.REL_SELF));

        // 2nd step: modify classification and trigger update
        classification.removeLinks();
        classification.setServiceLevel("P5D");
        classification.setPriority(1000);

        template.put(
            server + "/api/v1/classifications/{classificationId}",
            new HttpEntity<>(mapper.writeValueAsString(classification), getHeaders()),
            CLASSIFICATION_ID);

        //trigger jobs twice to refresh all entries. first entry on the first call and follow up on the seconds call
        jobScheduler.triggerJobs();
        jobScheduler.triggerJobs();

        // verify the classification modified timestamp is after 'before'
        ResponseEntity<ClassificationResource> repeatedResponse = template.exchange(
            server + "/api/v1/classifications/{classificationId}",
            HttpMethod.GET,
            new HttpEntity<String>(getHeaders()),
            ParameterizedTypeReference.forType(ClassificationResource.class),
            CLASSIFICATION_ID);

        assertNotNull(repeatedResponse.getBody());

        ClassificationResource modifiedClassificationResource = repeatedResponse.getBody();
        Classification modifiedClassification = classificationResourceAssembler.toModel(modifiedClassificationResource);

        assertFalse(before.isAfter(modifiedClassification.getModified()));

        List<String> affectedTasks = new ArrayList<>(
            Arrays.asList("TKI:000000000000000000000000000000000003", "TKI:000000000000000000000000000000000004",
                "TKI:000000000000000000000000000000000005", "TKI:000000000000000000000000000000000006",
                "TKI:000000000000000000000000000000000007", "TKI:000000000000000000000000000000000008",
                "TKI:000000000000000000000000000000000009", "TKI:000000000000000000000000000000000010",
                "TKI:000000000000000000000000000000000011", "TKI:000000000000000000000000000000000012",
                "TKI:000000000000000000000000000000000013", "TKI:000000000000000000000000000000000014",
                "TKI:000000000000000000000000000000000015", "TKI:000000000000000000000000000000000016",
                "TKI:000000000000000000000000000000000017", "TKI:000000000000000000000000000000000018",
                "TKI:000000000000000000000000000000000019", "TKI:000000000000000000000000000000000020",
                "TKI:000000000000000000000000000000000021", "TKI:000000000000000000000000000000000022",
                "TKI:000000000000000000000000000000000023", "TKI:000000000000000000000000000000000024",
                "TKI:000000000000000000000000000000000025", "TKI:000000000000000000000000000000000026",
                "TKI:000000000000000000000000000000000027", "TKI:000000000000000000000000000000000028",
                "TKI:000000000000000000000000000000000029", "TKI:000000000000000000000000000000000030",
                "TKI:000000000000000000000000000000000031", "TKI:000000000000000000000000000000000032",
                "TKI:000000000000000000000000000000000033", "TKI:000000000000000000000000000000000034",
                "TKI:000000000000000000000000000000000035", "TKI:000000000000000000000000000000000100",
                "TKI:000000000000000000000000000000000101", "TKI:000000000000000000000000000000000102",
                "TKI:000000000000000000000000000000000103"));
        for (String taskId : affectedTasks) {
            verifyTaskIsModifiedAfter(taskId, before);
        }

    }

    private void verifyTaskIsModifiedAfter(String taskId, Instant before)
        throws InvalidArgumentException {
        RestTemplate admTemplate = getRestTemplate();
        HttpHeaders admHeaders = new HttpHeaders();
        admHeaders.add("Authorization", "Basic YWRtaW46YWRtaW4=");  // admin:admin

        HttpEntity<String> admRequest = new HttpEntity<>(admHeaders);

        ResponseEntity<TaskResource> taskResponse = admTemplate.exchange(
            server + "/api/v1/tasks/{taskId}",
            HttpMethod.GET,
            admRequest,
            ParameterizedTypeReference.forType(TaskResource.class), taskId);

        TaskResource taskResource = taskResponse.getBody();
        Task task = taskResourceAssembler.toModel(taskResource);

        assertFalse("Task " + task.getId() + " has not been refreshed.", before.isAfter(task.getModified()));
    }

    /**
     * Return a REST template which is capable of dealing with responses in HAL format.
     *
     * @return RestTemplate
     */
    private RestTemplate getRestTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.registerModule(new Jackson2HalModule());

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate();
        template.getMessageConverters().clear();
        template.getMessageConverters().add(new StringHttpMessageConverter());
        template.getMessageConverters().add(converter);
        return template;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        headers.add("Content-Type", "application/hal+json");
        return headers;
    }

}
