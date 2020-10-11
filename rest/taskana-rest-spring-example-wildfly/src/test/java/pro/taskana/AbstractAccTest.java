package pro.taskana;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collections;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.simplehistory.rest.models.TaskHistoryEventListResource;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

public class AbstractAccTest {

  protected static final String DEPENDENCY_VERSION = "4.1.1-SNAPSHOT";
  private static final String AUTHORIZATION_TEAMLEAD_1 = "Basic dGVhbWxlYWQtMTp0ZWFtbGVhZC0x";

  /**
   * Return a REST template which is capable of dealing with responses in HAL format.
   *
   * @return RestTemplate
   */
  protected static RestTemplate getRestTemplate() {
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

  protected HttpHeaders getHeadersTeamlead_1() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", AUTHORIZATION_TEAMLEAD_1);
    headers.add("Content-Type", "application/json");
    return headers;
  }

  protected TaskRepresentationModel getTaskResourceSample() {
    ClassificationSummaryRepresentationModel classificationResource =
        new ClassificationSummaryRepresentationModel();
    classificationResource.setKey("L11010");
    WorkbasketSummaryRepresentationModel workbasketSummary =
        new WorkbasketSummaryRepresentationModel();
    workbasketSummary.setWorkbasketId("WBI:100000000000000000000000000000000004");

    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");

    TaskRepresentationModel taskRepresentationModel = new TaskRepresentationModel();
    taskRepresentationModel.setClassificationSummary(classificationResource);
    taskRepresentationModel.setWorkbasketSummary(workbasketSummary);
    taskRepresentationModel.setPrimaryObjRef(objectReference);
    return taskRepresentationModel;
  }

  protected ResponseEntity<TaskHistoryEventListResource> performGetHistoryEventsRestCall() {

    HttpEntity<TaskHistoryEventListResource> httpEntity = new HttpEntity<>(getHeadersTeamlead_1());

    ResponseEntity<TaskHistoryEventListResource> response =
        getRestTemplate()
            .exchange(
                "http://127.0.0.1:" + "8080" + "/taskana/api/v1/task-history-event",
                HttpMethod.GET,
                httpEntity,
                ParameterizedTypeReference.forType(TaskHistoryEventListResource.class));

    return response;
  }

  protected ResponseEntity<TaskRepresentationModel> performCreateTaskRestCall() {

    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    ResponseEntity<TaskRepresentationModel> responseCreateTask =
        getRestTemplate()
            .exchange(
                "http://127.0.0.1:" + "8080" + "/taskana/api/v1/tasks",
                HttpMethod.POST,
                new HttpEntity<>(taskRepresentationModel, getHeadersTeamlead_1()),
                ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    return responseCreateTask;
  }

  protected String parseServerLog() throws Exception {

    // TO-DO: make log4j log into rollingFile from log4j.xml
    File file = new File("target/wildfly-13.0.0.Final/standalone/log/server.log");

    BufferedReader br = new BufferedReader(new FileReader(file));

    String str;
    StringBuilder stringBuilder = new StringBuilder();
    while ((str = br.readLine()) != null) {
      stringBuilder.append(str);
    }
    return stringBuilder.toString();
  }

}
