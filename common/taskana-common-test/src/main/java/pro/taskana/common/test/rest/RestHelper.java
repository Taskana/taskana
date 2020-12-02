package pro.taskana.common.test.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/** Helps to simplify rest api testing. */
@Component
public class RestHelper {

  public static final String AUTHORIZATION_TEAMLEAD_1 = "Basic dGVhbWxlYWQtMTp0ZWFtbGVhZC0x";
  public static final String AUTHORIZATION_ADMIN = "Basic YWRtaW46YWRtaW4=";
  public static final String AUTHORIZATION_BUSINESSADMIN =
      "Basic YnVzaW5lc3NhZG1pbjpidXNpbmVzc2FkbWlu";
  public static final String AUTHORIZATION_USER_1_1 = "Basic dXNlci0xLTE6dXNlci0xLTE=";
  public static final String AUTHORIZATION_USER_1_2 = "Basic dXNlci0xLTI6dXNlci0xLTI=";
  public static final String AUTHORIZATION_USER_2_1 = "Basic dXNlci0yLTE6dXNlci0yLTE=";
  public static final String AUTHORIZATION_USER_B_1 = "Basic dXNlci1iLTE6dXNlci1iLTE=";

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

  public HttpEntity<String> defaultRequest() {
    return new HttpEntity<>(getHeadersTeamlead_1());
  }

  public HttpHeaders getHeadersTeamlead_1() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_TEAMLEAD_1);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  public HttpHeaders getHeadersAdmin() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_ADMIN);
    headers.add("Content-Type", "application/hal+json");
    return headers;
  }

  public HttpHeaders getHeadersBusinessAdmin() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_BUSINESSADMIN);
    headers.add("Content-Type", "application/hal+json");
    return headers;
  }

  public HttpHeaders getHeadersUser_1_2() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_USER_1_2);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  public HttpHeaders getHeadersUser_1_1() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_USER_1_1);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  public HttpHeaders getHeadersUser_2_1() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_USER_2_1);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  public HttpHeaders getHeadersUser_b_1() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_USER_B_1);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  private int getPort() {
    if (environment != null) {
      return environment.getRequiredProperty("local.server.port", int.class);
    }
    return port;
  }

  /**
   * Return a REST template which is capable of dealing with responses in HAL format.
   *
   * @return RestTemplate
   */
  private static RestTemplate getRestTemplate() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    mapper.registerModule(new Jackson2HalModule());
    mapper
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
