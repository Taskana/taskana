package pro.taskana.common.test.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Helps to simplify rest api testing. */
@Component
public class RestHelper {

  public static final RestTemplate TEMPLATE = getRestTemplate();

  private Environment environment;
  private int port;

  @Autowired
  public RestHelper(Environment environment) {
    this.environment = environment;
  }

  public RestHelper(int port) {
    this.port = port;
  }

  public String toUrl(String relativeUrl, Object... uriVariables) {
    return UriComponentsBuilder.fromPath(relativeUrl)
        .scheme("http")
        .host("127.0.0.1")
        .port(getPort())
        .build(false)
        .expand(uriVariables)
        .toString();
  }

  public static HttpHeaders generateHeadersForUser(String user) {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", encodeUserAndPasswordAsBasicAuth(user));
    headers.add("Content-Type", MediaTypes.HAL_JSON_VALUE);
    return headers;
  }

  public static String encodeUserAndPasswordAsBasicAuth(String user) {
    String toEncode = user + ":" + user;
    return "Basic " + Base64.getEncoder().encodeToString(toEncode.getBytes(StandardCharsets.UTF_8));
  }

  private int getPort() {
    return Optional.ofNullable(environment)
        .map(e -> e.getRequiredProperty("local.server.port", int.class))
        .orElse(port);
  }

  /**
   * Return a REST template which is capable of dealing with responses in HAL format.
   *
   * @return RestTemplate
   */
  private static RestTemplate getRestTemplate() {
    ObjectMapper mapper =
        new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new Jackson2HalModule())
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setSupportedMediaTypes(Collections.singletonList(MediaTypes.HAL_JSON));
    converter.setObjectMapper(mapper);

    RestTemplate template = new RestTemplate();
    // important to add first to ensure priority
    template.getMessageConverters().add(0, converter);
    return template;
  }
}
