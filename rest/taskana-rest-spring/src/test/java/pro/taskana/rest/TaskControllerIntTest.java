package pro.taskana.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import java.time.temporal.ChronoUnit;
import javax.sql.DataSource;
import org.assertj.core.api.Fail;
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

import pro.taskana.RestHelper;
import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.rest.resource.ClassificationSummaryResource;
import pro.taskana.rest.resource.TaskResource;
import pro.taskana.rest.resource.TaskSummaryListResource;
import pro.taskana.rest.resource.WorkbasketSummaryResource;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.ObjectReference;
import pro.taskana.task.api.TaskState;

// import org.junit.jupiter.api.Assertions;

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
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(25);
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
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(22);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultiplePlannedTimeIntervals() {

    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned="
                + firstInstant
                + ",,"
                + secondInstant
                + ","
                + thirdInstant
                + ","
                + ","
                + fourthInstant
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSinglePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);
    Instant plannedToInstant = Instant.now().minus(3, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned-from="
                + plannedFromInstant
                + "&planned-until="
                + plannedToInstant
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefinitePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned-from="
                + plannedFromInstant
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidPlannedParamsCombination() {
    assertThatThrownBy(
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_TASKS)
                    + "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&planned=2020-01-22T09:44:47.453Z,,"
                    + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                    + ",2020-01-18T09:44:47.453Z"
                    + "&planned-from=2020-01-19T07:44:47.453Z"
                    + "&sort-by=planned",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskSummaryListResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultipleDueTimeIntervals() {

    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due="
                + firstInstant
                + ",,"
                + secondInstant
                + ","
                + thirdInstant
                + ","
                + ","
                + fourthInstant
                + "&sort-by=due",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleDueTimeInterval() {

    Instant dueFromInstant = Instant.now().minus(8, ChronoUnit.DAYS);
    Instant dueToInstant = Instant.now().minus(3, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due-from="
                + dueFromInstant
                + "&due-until="
                + dueToInstant
                + "&sort-by=due",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(9);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefiniteDueTimeInterval() {

    Instant dueToInstant = Instant.now().minus(1, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due-until="
                + dueToInstant
                + "&sort-by=due",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidDueParamsCombination() {
    assertThatThrownBy(
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_TASKS)
                    + "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&due=2020-01-22T09:44:47.453Z,,"
                    + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                    + ",2020-01-18T09:44:47.453Z"
                    + "&due-from=2020-01-19T07:44:47.453Z"
                    + "&sort-by=planned",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(TaskSummaryListResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksByWorkbasketKeyAndDomain() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2&domain=DOMAIN_A",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(20);
  }

  @Test
  void testExceptionIfKeyIsSetButDomainIsMissing() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<>(headers);

    assertThatThrownBy(
        () -> {
          ResponseEntity<TaskSummaryListResource> response =
              template.exchange(
                  restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2",
                  HttpMethod.GET,
                  request,
                  ParameterizedTypeReference.forType(TaskSummaryListResource.class));
        })
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksWithAdminRole() {
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(73);
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
    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getLink(Link.REL_SELF)
                .getHref()
                .endsWith(
                    "/api/v1/tasks?por.type=VNR&por.value=22334455&sort-by=por.value&order=desc"))
        .isTrue();
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    try {
      template.exchange(
          restHelper.toUrl(Mapping.URL_TASKS) + "?invalid=VNR",
          HttpMethod.GET,
          restHelper.defaultRequest(),
          ParameterizedTypeReference.forType(TaskSummaryListResource.class));
      Fail.fail("");
    } catch (HttpClientErrorException e) {
      assertThat(e.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
      assertThat(e.getResponseBodyAsString().contains("[invalid]")).isTrue();
    }
  }

  @Test
  void testGetLastPageSortedByPorValue() {

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?state=READY,CLAIMED&sort-by=por.value&order=desc&page=15&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=14")).isTrue();
    assertThat("TKI:100000000000000000000000000000000000")
        .isEqualTo(response.getBody().getContent().iterator().next().getTaskId());

    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getLink(Link.REL_SELF)
                .getHref()
                .endsWith(
                    "/api/v1/tasks?"
                        + "state=READY,CLAIMED&sort-by=por.value&order=desc&page=15&page-size=5"))
        .isTrue();

    assertThat(response.getBody().getLink(Link.REL_FIRST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_LAST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_PREVIOUS)).isNotNull();
  }

  @Test
  void testGetLastPageSortedByDueWithHiddenTasksRemovedFromResult() {
    resetDb();
    // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    HttpEntity<String> request = new HttpEntity<>(headers);

    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getContent()).hasSize(25);

    response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc&page=5&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getContent()).hasSize(5);
    assertThat(response.getBody().getLink(Link.REL_LAST).getHref().contains("page=5")).isTrue();
    assertThat("TKI:000000000000000000000000000000000023")
        .isEqualTo(response.getBody().getContent().iterator().next().getTaskId());

    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getLink(Link.REL_SELF)
                .getHref()
                .endsWith("/api/v1/tasks?sort-by=due&order=desc&page=5&page-size=5"))
        .isTrue();

    assertThat(response.getBody().getLink(Link.REL_FIRST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_LAST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_PREVIOUS)).isNotNull();
  }

  @Test
  void testGetQueryByPorSecondPageSortedByType() {
    resetDb(); // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<TaskSummaryListResource> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?por.company=00&por.system=PASystem&por.instance=00&"
                + "por.type=VNR&por.value=22334455&sort-by=por.type&"
                + "order=asc&page=2&page-size=5",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(TaskSummaryListResource.class));
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat("TKI:000000000000000000000000000000000013")
        .isEqualTo(response.getBody().getContent().iterator().next().getTaskId());

    assertThat(response.getBody().getLink(Link.REL_SELF)).isNotNull();

    assertThat(
            response
                .getBody()
                .getLink(Link.REL_SELF)
                .getHref()
                .endsWith(
                    "/api/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&"
                        + "por.type=VNR&por.value=22334455&sort-by=por.type&order=asc&"
                        + "page=2&page-size=5"))
        .isTrue();

    assertThat(response.getBody().getLink(Link.REL_FIRST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_LAST)).isNotNull();
    assertThat(response.getBody().getLink(Link.REL_PREVIOUS)).isNotNull();
  }

  @Test
  void testGetTaskWithAttachments() throws IOException {
    final URL url =
        new URL(restHelper.toUrl("/api/v1/tasks/" + "TKI:000000000000000000000000000000000002"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic YWRtaW46YWRtaW4=");
    assertThat(con.getResponseCode()).isEqualTo(200);
    final ObjectMapper objectMapper = new ObjectMapper();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
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
    assertThat(response.contains("\"attachments\":[]")).isFalse();
    assertThat(created.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{3}Z")).isTrue();
  }

  @Test
  void testGetAndUpdateTask() throws IOException {
    URL url = new URL(restHelper.toUrl("/api/v1/tasks/TKI:100000000000000000000000000000000000"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    assertThat(con.getResponseCode()).isEqualTo(200);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
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
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), UTF_8));
    out.write(content.toString());
    out.flush();
    out.close();
    assertThat(con.getResponseCode()).isEqualTo(200);

    con.disconnect();

    url = new URL(restHelper.toUrl("/api/v1/tasks/TKI:100000000000000000000000000000000000"));
    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    assertThat(con.getResponseCode()).isEqualTo(200);

    in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
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

    assertThat(updatedTaskObject.getModified()).isNotEqualTo(originalTaskObject.getModified());
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
    assertThat(HttpStatus.CREATED).isEqualTo(responseCreate.getStatusCode());
    assertThat(responseCreate.getBody()).isNotNull();

    String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();

    assertThat(taskIdOfCreatedTask).isNotNull();
    assertThat(taskIdOfCreatedTask.startsWith("TKI:")).isTrue();

    ResponseEntity<TaskResource> responseDeleted =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID, taskIdOfCreatedTask),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            ParameterizedTypeReference.forType(Void.class));

    assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
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

    assertThatThrownBy(
        () ->
            template.exchange(
                restHelper.toUrl(Mapping.URL_TASKS),
                HttpMethod.POST,
                new HttpEntity<>(taskResource, restHelper.getHeaders()),
                ParameterizedTypeReference.forType(TaskResource.class)))
        .isInstanceOf(HttpClientErrorException.class);
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
    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), UTF_8));
    out.write(taskToCreateJson);
    out.flush();
    out.close();
    assertThat(con.getResponseCode()).isEqualTo(400);

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
    out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), UTF_8));
    out.write(taskToCreateJson2);
    out.flush();
    out.close();
    assertThat(con.getResponseCode()).isEqualTo(400);

    con.disconnect();
  }

  @Test
  void testUpdateTaskOwnerOfReadyTaskSucceeds() {
    // setup
    final String taskUrlString =
        restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000025");

    // retrieve task from Rest Api
    ResponseEntity<TaskResource> responseGet =
        template.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(getHeadersForUser_1_2()),
            ParameterizedTypeReference.forType(TaskResource.class));

    assertThat(responseGet.getBody()).isNotNull();
    TaskResource theTaskResource = responseGet.getBody();
    assertThat(theTaskResource.getState()).isEqualTo(TaskState.READY);
    assertThat(theTaskResource.getOwner() == null);

    // set Owner and update Task

    final String anyUserName = "dummyUser";
    theTaskResource.setOwner(anyUserName);
    ResponseEntity<TaskResource> responseUpdate =
        template.exchange(
            taskUrlString,
            HttpMethod.PUT,
            new HttpEntity<>(theTaskResource, getHeadersForUser_1_2()),
            ParameterizedTypeReference.forType(TaskResource.class));

    assertThat(responseUpdate.getBody()).isNotNull();
    TaskResource theUpdatedTaskResource = responseUpdate.getBody();
    assertThat(theUpdatedTaskResource.getState()).isEqualTo(TaskState.READY);
    assertThat(theUpdatedTaskResource.getOwner()).isEqualTo(anyUserName);
  }

  @Test
  void testUpdateTaskOwnerOfClaimedTaskFails() {
    // setup
    final String taskUrlString =
        restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000026");

    // retrieve task from Rest Api
    ResponseEntity<TaskResource> responseGet =
        template.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(getHeadersForUser_1_2()),
            ParameterizedTypeReference.forType(TaskResource.class));

    assertThat(responseGet.getBody()).isNotNull();
    TaskResource theTaskResource = responseGet.getBody();
    assertThat(theTaskResource.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskResource.getOwner()).isEqualTo("user_1_1");

    // set Owner and update Task

    final String anyUserName = "dummyuser";
    theTaskResource.setOwner(anyUserName);

    assertThatThrownBy(
        () ->
            template.exchange(
                taskUrlString,
                HttpMethod.PUT,
                new HttpEntity<>(theTaskResource, getHeadersForUser_1_2()),
                ParameterizedTypeReference.forType(TaskResource.class)))
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("409");
  }

  private HttpHeaders getHeadersForUser_1_2() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI=");
    headers.add("Content-Type", "application/json");
    return headers;
  }

  private TaskResource getTaskResourceSample() {
    ClassificationSummaryResource classificationResource = new ClassificationSummaryResource();
    classificationResource.setKey("L11010");
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
