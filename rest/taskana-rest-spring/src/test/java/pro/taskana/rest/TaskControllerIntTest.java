package pro.taskana.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import javax.sql.DataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.ObjectReference;
import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskSummaryListResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;
import pro.taskana.sampledata.SampleDataGenerator;

/** Test Task Controller. */
@TaskanaSpringBootTest
class TaskControllerIntTest {

  private static RestTemplate template;

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  @Autowired RestHelper restHelper;
  @Autowired private DataSource dataSource;

  @BeforeAll
  static void init() {
    template = RestHelper.getRestTemplate();
  }

  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @Test
  void testGetAllTasks() {
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertEquals(25, response.getBody().getContent().size());
  }

  @Test
  void testGetAllTasksByWorkbasketId() {
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertEquals(22, response.getBody().getContent().size());
  }

  @Test
  void testGetAllTasksByWorkbasketKeyAndDomain() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2&domain=DOMAIN_A",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertEquals(20, response.getBody().getContent().size());
  }

  @Test
  void testExceptionIfKeyIsSetButDomainIsMissing() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<String>(headers);
    try {
      ResponseEntity<TaskSummaryListResource> response =
          template.exchange(
              restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2",
              HttpMethod.GET,
              request,
              ParameterizedTypeReference.forType(TaskSummaryListResource.class));
      fail();
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
    }
  }

  @Test
  void testGetAllTasksWithAdminRole() {
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertEquals(73, response.getBody().getContent().size());
  }

  @Test
  void testGetAllTasksKeepingFilters() {
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?por.type=VNR&por.value=22334455&sort-by=por.value&order=desc",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(
                "/api/v1/tasks?por.type=VNR&por.value=22334455&sort-by=por.value&order=desc"));
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    try {
      template.exchange(
          restHelper.toUrl(Mapping.URL_TASKS) + "?invalid=VNR",
          HttpMethod.GET,
          restHelper.defaultRequest(),
          ParameterizedTypeReference.forType(TaskSummaryListResource.class));
      fail();
    } catch (HttpClientErrorException e) {
      assertEquals(HttpStatus.BAD_REQUEST, e.getStatusCode());
      assertTrue(e.getResponseBodyAsString().contains("[invalid]"));
    }
  }

  @Test
  void testGetLastPageSortedByPorValue() {

    HttpEntity<String> request = new HttpEntity<String>(restHelper.getHeadersAdmin());
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?state=READY,CLAIMED&sort-by=por.value&order=desc&page=15&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertEquals(1, response.getBody().getContent().size());
    assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=14"));
    assertEquals(
        "TKI:100000000000000000000000000000000000",
        response.getBody().getContent().iterator().next().getTaskId());
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(
                "/api/v1/tasks?"
                    + "state=READY,CLAIMED&sort-by=por.value&order=desc&page=15&page-size=5"));
    assertNotNull(response.getBody().getLink(Link.REL_FIRST));
    assertNotNull(response.getBody().getLink(Link.REL_LAST));
    assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
  }

  @Test
  void testGetLastPageSortedByDueWithHiddenTasksRemovedFromResult() {
    resetDb();
    // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    HttpEntity<String> request = new HttpEntity<String>(headers);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertEquals(25, response.getBody().getContent().size());

    response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc&page=5&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertEquals(5, response.getBody().getContent().size());
    assertTrue(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=5"));
    assertEquals(
        "TKI:000000000000000000000000000000000023",
        response.getBody().getContent().iterator().next().getTaskId());
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith("/api/v1/tasks?sort-by=due&order=desc&page=5&page-size=5"));
    assertNotNull(response.getBody().getLink(Link.REL_FIRST));
    assertNotNull(response.getBody().getLink(Link.REL_LAST));
    assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
  }

  @Test
  void testGetQueryByPorSecondPageSortedByType() {
    resetDb(); // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    HttpEntity<String> request = new HttpEntity<String>(headers);
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?por.company=00&por.system=PASystem&por.instance=00&"
                + "por.type=VNR&por.value=22334455&sort-by=por.type&"
                + "order=asc&page=2&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertEquals(1, response.getBody().getContent().size());
    assertEquals(
        "TKI:000000000000000000000000000000000013",
        response.getBody().getContent().iterator().next().getTaskId());
    assertNotNull(response.getBody().getLink(Link.REL_SELF));
    assertTrue(
        response
            .getBody()
            .getLink(Link.REL_SELF)
            .getHref()
            .endsWith(
                "/api/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&"
                    + "por.type=VNR&por.value=22334455&sort-by=por.type&order=asc&"
                    + "page=2&page-size=5"));
    assertNotNull(response.getBody().getLink(Link.REL_FIRST));
    assertNotNull(response.getBody().getLink(Link.REL_LAST));
    assertNotNull(response.getBody().getLink(Link.REL_PREVIOUS));
  }

  @Test
  void testGetTaskWithAttachments() throws IOException {
    final URL url =
        new URL(restHelper.toUrl("/api/v1/tasks/" + "TKI:000000000000000000000000000000000002"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
    assertEquals(200, con.getResponseCode());
    final ObjectMapper objectMapper = new ObjectMapper();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    String response = content.toString();
    JsonNode jsonNode = objectMapper.readTree(response);
    String created = jsonNode.get("created").asText();
    assertFalse(response.contains("\"attachments\":[]"));
    assertTrue(created.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z"));
  }

  @Test
  void testGetAndUpdateTask() throws IOException {
    URL url = new URL(restHelper.toUrl("/api/v1/tasks/TKI:100000000000000000000000000000000000"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    assertEquals(200, con.getResponseCode());

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    final String originalTask = content.toString();

    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("PUT");
    con.setDoOutput(true);
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    con.setRequestProperty("Content-Type", "application/json");
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
    out.write(content.toString());
    out.flush();
    out.close();
    assertEquals(200, con.getResponseCode());
    con.disconnect();

    url = new URL(restHelper.toUrl("/api/v1/tasks/TKI:100000000000000000000000000000000000"));
    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    assertEquals(200, con.getResponseCode());

    in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    content = new StringBuffer();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    String updatedTask = content.toString();
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    TaskResource originalTaskObject = mapper.readValue(originalTask, TaskResource.class);
    TaskResource updatedTaskObject = mapper.readValue(updatedTask, TaskResource.class);

    assertNotEquals(originalTaskObject.getModified(), updatedTaskObject.getModified());
  }

  @Test
  void testCreateAndDeleteTask() {

    TaskResource taskResource = getTaskResourceSample();
    ResponseEntity<TaskResource> responseCreate =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.POST,
            new HttpEntity<>(taskResource, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(TaskResource.class));
    assertEquals(responseCreate.getStatusCode(), HttpStatus.CREATED);
    assertNotNull(responseCreate.getBody());

    String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();
    assertNotNull(taskIdOfCreatedTask);
    assertTrue(taskIdOfCreatedTask.startsWith("TKI:"));

    ResponseEntity<TaskResource> responseDeleted =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID, taskIdOfCreatedTask),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(Void.class));

    assertEquals(HttpStatus.NO_CONTENT, responseDeleted.getStatusCode());
  }

  /**
   * TSK-926: If Planned and Due Date is provided to create a task and not matching to service level
   * throw an exception One is calculated by other other date +- service level.
   */
  @Test
  void testCreateWithPlannedAndDueDate() {
    TaskResource taskResource = getTaskResourceSample();
    Instant now = Instant.now();
    taskResource.setPlanned(now.toString());
    taskResource.setDue(now.toString());

    HttpClientErrorException ex =
        Assertions.assertThrows(
            HttpClientErrorException.class,
            () ->
                template.exchange(
                    restHelper.toUrl(Mapping.URL_TASKS),
                    HttpMethod.POST,
                    new HttpEntity<>(taskResource, restHelper.getHeaders()),
                    ParameterizedTypeReference.forType(TaskResource.class)));
  }

  @Test
  void testCreateTaskWithInvalidParameter() throws IOException {
    final String taskToCreateJson =
        "{\"classificationKey\":\"L11010\","
            + "\"workbasketSummaryResource\":"
            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
            + "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\","
            + "\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

    URL url = new URL(restHelper.toUrl(Mapping.URL_TASKS));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    con.setRequestProperty("Content-Type", "application/json");
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
    out.write(taskToCreateJson);
    out.flush();
    out.close();
    assertEquals(400, con.getResponseCode());
    con.disconnect();

    final String taskToCreateJson2 =
        "{\"classificationSummaryResource\":"
            + "{\"classificationId\":\"CLI:100000000000000000000000000000000004\"},"
            + "\"workbasketSummaryResource\":{\"workbasketId\":\"\"},"
            + "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\","
            + "\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

    url = new URL(restHelper.toUrl(Mapping.URL_TASKS));
    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    con.setRequestProperty("Content-Type", "application/json");
    out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream()));
    out.write(taskToCreateJson2);
    out.flush();
    out.close();
    assertEquals(400, con.getResponseCode());
    con.disconnect();
  }

  private TaskResource getTaskResourceSample() {
    ClassificationSummaryResource classificationResource = new ClassificationSummaryResource();
    classificationResource.key = "L11010";
    WorkbasketSummaryResource workbasketSummaryResource = new WorkbasketSummaryResource();
    workbasketSummaryResource.setWorkbasketId("WBI:100000000000000000000000000000000004");

    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");

    TaskResource taskResource = new TaskResource();
    taskResource.setClassificationSummaryResource(classificationResource);
    taskResource.setWorkbasketSummaryResource(workbasketSummaryResource);
    taskResource.setPrimaryObjRef(objectReference);
    return taskResource;
  }
}
