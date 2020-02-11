package pro.taskana;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Helps to simplify rest api testing. */
@Component
public class RestHelper {

  public static final RestTemplate template = getRestTemplate();
  @Autowired Environment environment;

  public String toUrl(String relativeUrl, Object... uriVariables) {
    return UriComponentsBuilder.fromPath(relativeUrl)
        .scheme("http")
        .host("127.0.0.1")
        .port(environment.getProperty("local.server.port"))
        .build(false)
        .expand(uriVariables)
        .toString();
  }

  public HttpEntity<String> defaultRequest() {
    return new HttpEntity<>(getHeaders());
  }

  public HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    headers.add("Content-Type", "application/json");
    return headers;
  }

  public HttpHeaders getHeadersAdmin() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic YWRtaW46YWRtaW4="); // admin:admin
    headers.add("Content-Type", "application/hal+json");
    return headers;
  }

  public HttpHeaders getHeadersBusinessAdmin() {
    HttpHeaders headers = new HttpHeaders();
    // businessadmin:businessadmin
    headers.add("Authorization", "Basic YnVzaW5lc3NhZG1pbjpidXNpbmVzc2FkbWlu");
    headers.add("Content-Type", "application/hal+json");
    return headers;
  }

  /**
   * Return a REST template which is capable of dealing with responses in HAL format.
   *
   * @return RestTemplate
   */
  public static RestTemplate getRestTemplate() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.registerModule(new Jackson2HalModule());

    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
    converter.setObjectMapper(mapper);

    RestTemplate template = new RestTemplate();
    // important to add first to ensure priority
    template.getMessageConverters().add(0, converter);
    return template;
  }
}
