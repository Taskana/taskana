package pro.taskana.task.rest;

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
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.RestHelper;
import pro.taskana.common.rest.TaskanaSpringBootTest;
import pro.taskana.common.rest.models.TaskanaPagedModel;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test Task Controller. */
@TaskanaSpringBootTest
class TaskControllerIntTest {

  private static final ParameterizedTypeReference<TaskanaPagedModel<TaskSummaryRepresentationModel>>
      TASK_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<TaskanaPagedModel<TaskSummaryRepresentationModel>>() {};
  private static RestTemplate template;

  @Value("${taskana.schemaName:TASKANA}")
  public String schemaName;

  @Autowired RestHelper restHelper;

  @Autowired private DataSource dataSource;

  @BeforeAll
  static void init() {
    template = RestHelper.TEMPLATE;
  }

  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @Test
  void testGetAllTasks() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(25);
  }

  @Test
  void testGetAllTasksByWorkbasketId() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(22);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultiplePlannedTimeIntervals() {

    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
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
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSinglePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);
    Instant plannedToInstant = Instant.now().minus(3, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
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
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefinitePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned-from="
                + plannedFromInstant
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidPlannedParamsCombination() {
    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned=2020-01-22T09:44:47.453Z,,"
                + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                + ",2020-01-18T09:44:47.453Z"
                + "&planned-from=2020-01-19T07:44:47.453Z"
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultipleDueTimeIntervals() {

    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
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
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?wildcard-search-value=%99%"
                + "&wildcard-search-fields=NAME,custom_3,CuStOM_4",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidWildcardSearchParameters() {

    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?wildcard-search-value=%rt%",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    ThrowingCallable httpCall2 =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?wildcard-search-fields=NAME,CUSTOM_3,CUSTOM_4",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall2)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleDueTimeInterval() {

    Instant dueFromInstant = Instant.now().minus(8, ChronoUnit.DAYS);
    Instant dueToInstant = Instant.now().minus(3, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
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
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(9);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefiniteDueTimeInterval() {

    Instant dueToInstant = Instant.now().minus(1, ChronoUnit.DAYS);

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due-until="
                + dueToInstant
                + "&sort-by=due",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidDueParamsCombination() {
    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due=2020-01-22T09:44:47.453Z,,"
                + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                + ",2020-01-18T09:44:47.453Z"
                + "&due-from=2020-01-19T07:44:47.453Z"
                + "&sort-by=planned",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksByWorkbasketKeyAndDomain() {
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2&domain=DOMAIN_A",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(20);
  }

  @Test
  void testGetAllTasksByExternalId() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?external-id=ETI:000000000000000000000000000000000003,"
                + "ETI:000000000000000000000000000000000004",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
  }

  @Test
  void testExceptionIfKeyIsSetButDomainIsMissing() {

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dXNlcl8xXzI6dXNlcl8xXzI="); // user_1_2
    HttpEntity<String> request = new HttpEntity<>(headers);

    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?workbasket-key=USER_1_2",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksWithAdminRole() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(response.getBody().getContent()).hasSize(73);
  }

  @Test
  void testGetAllTasksKeepingFilters() {
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?por.type=VNR&por.value=22334455&sort-by=por.value&order=desc",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF))
        .isNotNull();
    assertThat(
        response
            .getBody()
            .getRequiredLink(IanaLinkRelations.SELF)
            .getHref()
            .endsWith(
                "/api/v1/tasks?por.type=VNR&por.value=22334455&sort-by=por.value&order=desc"))
        .isTrue();
  }

  @Test
  void testThrowsExceptionIfInvalidFilterIsUsed() {
    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?invalid=VNR",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("[invalid]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetLastPageSortedByPorValue() {

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?state=READY,CLAIMED&sort-by=por.value&order=desc&page-size=5&page=14",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(1);
    assertThat(
        response
            .getBody()
            .getRequiredLink(IanaLinkRelations.LAST)
            .getHref()
            .contains("page=14"))
        .isTrue();
    assertThat("TKI:100000000000000000000000000000000000")
        .isEqualTo(response.getBody().getContent().iterator().next().getTaskId());

    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
            response
                .getBody()
                .getRequiredLink(IanaLinkRelations.SELF)
                .getHref()
                .endsWith(
                    "/api/v1/tasks?"
                        + "state=READY,CLAIMED&sort-by=por.value&order=desc&page-size=5&page=14"))
        .isTrue();

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
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

    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(25);

    response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS) + "?sort-by=due&order=desc&page-size=5&page=5",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(5);
    assertThat(
        response.getBody().getRequiredLink(IanaLinkRelations.LAST).getHref().contains("page=5"))
        .isTrue();
    assertThat("TKI:000000000000000000000000000000000023")
        .isEqualTo(response.getBody().getContent().iterator().next().getTaskId());

    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(
        response
            .getBody()
            .getRequiredLink(IanaLinkRelations.SELF)
            .getHref()
            .endsWith("/api/v1/tasks?sort-by=due&order=desc&page-size=5&page=5"))
        .isTrue();

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
  }

  @Test
  void testGetQueryByPorSecondPageSortedByType() {
    resetDb(); // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata

    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
    HttpEntity<String> request = new HttpEntity<>(headers);
    ResponseEntity<TaskanaPagedModel<TaskSummaryRepresentationModel>> response =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS)
                + "?por.company=00&por.system=PASystem&por.instance=00&"
                + "por.type=VNR&por.value=22334455&sort-by=por.type&"
                + "order=asc&page-size=5&page=2",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskSummaryRepresentationModel::getTaskId)
        .containsOnly("TKI:000000000000000000000000000000000013");

    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();

    assertThat(
        response
            .getBody()
            .getRequiredLink(IanaLinkRelations.SELF)
            .getHref()
            .endsWith(
                "/api/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&"
                    + "por.type=VNR&por.value=22334455&sort-by=por.type&order=asc&"
                    + "page-size=5&page=2"))
        .isTrue();

    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
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
    StringBuilder content = new StringBuilder();
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
    TaskRepresentationModel originalTaskObject =
        mapper.readValue(originalTask, TaskRepresentationModel.class);
    TaskRepresentationModel updatedTaskObject =
        mapper.readValue(updatedTask, TaskRepresentationModel.class);

    assertThat(updatedTaskObject.getModified()).isNotEqualTo(originalTaskObject.getModified());
  }

  @Test
  void testCreateAndDeleteTask() {

    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    ResponseEntity<TaskRepresentationModel> responseCreate =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.POST,
            new HttpEntity<>(taskRepresentationModel, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThat(HttpStatus.CREATED).isEqualTo(responseCreate.getStatusCode());
    assertThat(responseCreate.getBody()).isNotNull();

    String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();

    assertThat(taskIdOfCreatedTask).isNotNull();
    assertThat(taskIdOfCreatedTask.startsWith("TKI:")).isTrue();

    ResponseEntity<TaskRepresentationModel> responseDeleted =
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
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    Instant now = Instant.now();
    taskRepresentationModel.setPlanned(now.toString());
    taskRepresentationModel.setDue(now.toString());

    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS),
            HttpMethod.POST,
            new HttpEntity<>(taskRepresentationModel, restHelper.getHeaders()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThatThrownBy(httpCall).isInstanceOf(HttpClientErrorException.class);
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
  void testCancelClaimTask() {

    final String claimed_task_id = "TKI:000000000000000000000000000000000027";
    final String user_id_of_claimed_task = "user_1_2";

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> getTaskResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID, claimed_task_id),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(getTaskResponse.getBody()).isNotNull();

    TaskRepresentationModel claimedTaskRepresentationModel = getTaskResponse.getBody();
    assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo(user_id_of_claimed_task);

    // cancel claim
    ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID_CLAIM, claimed_task_id),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(cancelClaimResponse.getBody()).isNotNull();
    assertThat(cancelClaimResponse.getStatusCode().is2xxSuccessful()).isTrue();

    TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
    assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
  }

  @Test
  void testCancelClaimOfClaimedTaskByAnotherUserShouldThrowException() {

    final String claimed_task_id = "TKI:000000000000000000000000000000000026";
    final String user_id_of_claimed_task = "user_1_1";

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID, claimed_task_id),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo(user_id_of_claimed_task);

    // try to cancel claim
    ThrowingCallable httpCall =
        () -> template.exchange(
            restHelper.toUrl(Mapping.URL_TASKS_ID_CLAIM, claimed_task_id),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  void testUpdateTaskOwnerOfReadyTaskSucceeds() {
    // setup
    final String taskUrlString =
        restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000025");

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        template.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    assertThat(theTaskRepresentationModel.getOwner()).isNull();

    // set Owner and update Task

    final String anyUserName = "dummyUser";
    theTaskRepresentationModel.setOwner(anyUserName);
    ResponseEntity<TaskRepresentationModel> responseUpdate =
        template.exchange(
            taskUrlString,
            HttpMethod.PUT,
            new HttpEntity<>(theTaskRepresentationModel, restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(responseUpdate.getBody()).isNotNull();
    TaskRepresentationModel theUpdatedTaskRepresentationModel = responseUpdate.getBody();
    assertThat(theUpdatedTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    assertThat(theUpdatedTaskRepresentationModel.getOwner()).isEqualTo(anyUserName);
  }

  @Test
  void testUpdateTaskOwnerOfClaimedTaskFails() {
    // setup
    final String taskUrlString =
        restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000026");

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        template.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo("user_1_1");

    // set Owner and update Task

    final String anyUserName = "dummyuser";
    theTaskRepresentationModel.setOwner(anyUserName);

    ThrowingCallable httpCall =
        () -> template.exchange(
            taskUrlString,
            HttpMethod.PUT,
            new HttpEntity<>(theTaskRepresentationModel, restHelper.getHeadersUser_1_2()),
            ParameterizedTypeReference.forType(TaskRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("409");
  }

  private TaskRepresentationModel getTaskResourceSample() {
    ClassificationSummaryRepresentationModel classificationResource =
        new ClassificationSummaryRepresentationModel();
    classificationResource.setKey("L11010");
    WorkbasketSummaryRepresentationModel workbasketSummary =
        new WorkbasketSummaryRepresentationModel();
    workbasketSummary.setWorkbasketId(
        "WBI:100000000000000000000000000000000004");

    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");

    TaskRepresentationModel taskRepresentationModel = new TaskRepresentationModel();
    taskRepresentationModel.setClassificationSummary(classificationResource);
    taskRepresentationModel.setWorkbasketSummary(
        workbasketSummary);
    taskRepresentationModel.setPrimaryObjRef(objectReference);
    return taskRepresentationModel;
  }
}
