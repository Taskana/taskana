package pro.taskana;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collections;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.rest.resource.TaskanaUserInfoResource;

/**
 * This test class is configured to run with postgres DB if you want to run it with h2 it is needed.
 * to change data source configuration at project-defaults.yml.
 */
@RunWith(Arquillian.class)
public class TaskanaWildflyTest {

    @Deployment(testable = false)
    public static Archive<?> createTestArchive() {

        File[] files = Maven.resolver()
            .loadPomFromFile("pom.xml")
            .importRuntimeDependencies()
            .resolve().withTransitivity()
            .asFile();

        return ShrinkWrap.create(WebArchive.class, "taskana.war")
            .addPackages(true, "pro.taskana")
            .addAsResource("taskana.properties")
            .addAsResource("application.properties")
            .addAsResource("project-defaults.yml")
            .addAsLibraries(files);
    }

    @Test
    @RunAsClient
    public void shouldGetStatusOK() {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<TaskanaUserInfoResource> response = getRestTemplate().exchange(
            "http://127.0.0.1:" + "8090" + "/v1/current-user-info", HttpMethod.GET, request,
            new ParameterizedTypeReference<TaskanaUserInfoResource>() {

            });
        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    private RestTemplate getRestTemplate() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/json"));
        converter.setObjectMapper(mapper);

        RestTemplate template = new RestTemplate(Collections.<HttpMessageConverter<?>>singletonList(converter));
        return template;
    }
}
