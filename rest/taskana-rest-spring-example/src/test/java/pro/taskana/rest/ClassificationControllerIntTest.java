package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.taskana.rest.resource.ClassificationSummaryResource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"devMode=true"})
@Import(RestConfiguration.class)
public class ClassificationControllerIntTest {

    @LocalServerPort
    int port;

    @Test
    public void testGetAllClassifications() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications", HttpMethod.GET, request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
    }

    @Test
    public void testGetAllClassificationsFilterByCustomAttribute() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications?domain=DOMAIN_A&custom-1-like=RVNR", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertEquals(13, response.getBody().getContent().size());
    }

    @Test
    public void testGetAllClassificationsKeepingFilters() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc", HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {
            });
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc"));
        assertEquals(17, response.getBody().getContent().size());
        assertEquals("A12", response.getBody().getContent().iterator().next().key);
    }

    @Test
    public void testGetSecondPageSortedByKey() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<PagedResources<ClassificationSummaryResource>> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<PagedResources<ClassificationSummaryResource>>() {
            });
        assertEquals(5, response.getBody().getContent().size());
        assertEquals("L1050", response.getBody().getContent().iterator().next().key);
        assertNotNull(response.getBody().getLink(Link.REL_SELF));
        assertTrue(response.getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/v1/classifications?domain=DOMAIN_A&sort-by=key&order=asc&page=2&page-size=5"));
        assertNotNull(response.getBody().getLink("allClassifications"));
        assertTrue(response.getBody()
            .getLink("allClassifications")
            .getHref()
            .endsWith("/v1/classifications"));
        assertNotNull(response.getBody().getLink(Link.REL_FIRST));
        assertNotNull(response.getBody().getLink(Link.REL_LAST));
        assertNotNull(response.getBody().getLink(Link.REL_NEXT));
        assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
    }

    @Test
    public void testCreateClassification() throws IOException {
        String newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        URL url = new URL("http://127.0.0.1:" + port + "/v1/classifications");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();

        newClassification = "{\"classificationId\":\"\",\"category\":\"MANUAL\",\"domain\":\"DOMAIN_A\",\"key\":\"NEW_CLASS_2\",\"name\":\"new classification\",\"type\":\"TASK\"}";
        url = new URL("http://127.0.0.1:" + port + "/v1/classifications");
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json");
        out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
        out.write(newClassification);
        out.flush();
        out.close();
        assertEquals(201, con.getResponseCode());
        con.disconnect();
    }

    @Test
    public void testGetClassificationWithSpecialCharacter() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);
        ResponseEntity<ClassificationSummaryResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {
            });
        assertEquals("Zustimmungserklärung", response.getBody().name);
    }

    @Test(expected = HttpClientErrorException.class)
    public void testDeleteClassification() {
        RestTemplate template = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        HttpEntity<String> request = new HttpEntity<String>(headers);

        ResponseEntity<ClassificationSummaryResource> response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:200000000000000000000000000000000004",
            HttpMethod.DELETE,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {
            });
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        response = template.exchange(
            "http://127.0.0.1:" + port + "/v1/classifications/CLI:200000000000000000000000000000000004",
            HttpMethod.GET,
            request,
            new ParameterizedTypeReference<ClassificationSummaryResource>() {
            });
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
