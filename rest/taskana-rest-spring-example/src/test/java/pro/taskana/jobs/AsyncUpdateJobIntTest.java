package pro.taskana.jobs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;
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

import pro.taskana.Classification;
import pro.taskana.Task;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.ClassificationResource;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.assembler.ClassificationResourceAssembler;
import pro.taskana.rest.resource.assembler.TaskResourceAssembler;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT,
    properties = {"devMode=true"})
public class AsyncUpdateJobIntTest {

    @Autowired
    private ClassificationResourceAssembler classificationResourceAssembler;

    @Autowired
    private TaskResourceAssembler taskResourceAssembler;

    @Autowired
    Environment env;

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncUpdateJobIntTest.class);
    String server = "http://127.0.0.1:";
    RestTemplate template;
    HttpEntity<String> request;
    HttpHeaders headers = new HttpHeaders();
    @LocalServerPort
    int port;

    @Before
    public void before() {
        template = getRestTemplate();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        request = new HttpEntity<String>(headers);
    }

    @Test
    public void testUpdateClassificationPrioServiceLevel()
        throws IOException, InterruptedException, InvalidArgumentException {

        // 1st step: get old classification :
        Instant before = Instant.now();

        ResponseEntity<ClassificationResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000003",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationResource>() {

            });

        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        ClassificationResource classification = response.getBody();

        // 2nd step: modify classification and trigger update
        classification.removeLinks();
        classification.setServiceLevel("P5D");
        classification.setPriority(1000);

        String updatedClassification = new JSONObject(classification).toString();

        URL url = new URL(server + port + "/v1/classifications/CLI:100000000000000000000000000000000003");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(updatedClassification);
        out.flush();
        out.close();
        assertEquals(200, con.getResponseCode());
        con.disconnect();

        long delay = 16000;

        LOGGER.info("About to sleep for " + delay / 1000
            + " seconds to give JobScheduler a chance to process the classification change");
        Thread.sleep(delay);
        LOGGER.info("Sleeping ended. Continuing .... ");

        // verify the classification modified timestamp is after 'before'
        ResponseEntity<ClassificationResource> repeatedResponse = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000003",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationResource>() {

            });

        ClassificationResource modifiedClassificationResource = repeatedResponse.getBody();
        Classification modifiedClassification = classificationResourceAssembler.toModel(modifiedClassificationResource);

        assertTrue(!before.isAfter(modifiedClassification.getModified()));

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

        assertTrue("Task " + task.getId() + " has not been refreshed.", !before.isAfter(task.getModified()));
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

}
