package pro.taskana.task.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.sampledata.SampleDataGenerator;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel.CustomAttribute;
import pro.taskana.task.rest.models.TaskSummaryCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryPagedRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.task.rest.routing.IntegrationTestTaskRouter;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test Task Controller. */
@TaskanaSpringBootTest
class TaskControllerIntTest {

  private static final ParameterizedTypeReference<TaskSummaryPagedRepresentationModel>
      TASK_SUMMARY_PAGE_MODEL_TYPE =
          new ParameterizedTypeReference<TaskSummaryPagedRepresentationModel>() {};

  private static final ParameterizedTypeReference<TaskSummaryCollectionRepresentationModel>
      TASK_SUMMARY_COLLECTION_MODEL_TYPE =
          new ParameterizedTypeReference<TaskSummaryCollectionRepresentationModel>() {};

  private static final ParameterizedTypeReference<TaskRepresentationModel> TASK_MODEL_TYPE =
      ParameterizedTypeReference.forType(TaskRepresentationModel.class);

  private final RestHelper restHelper;
  private final DataSource dataSource;

  private final String schemaName;

  @Autowired
  TaskControllerIntTest(
      RestHelper restHelper,
      DataSource dataSource,
      @Value("${taskana.schemaName:TASKANA}") String schemaName) {
    this.restHelper = restHelper;
    this.dataSource = dataSource;
    this.schemaName = schemaName;
  }

  void resetDb() {
    SampleDataGenerator sampleDataGenerator = new SampleDataGenerator(dataSource, schemaName);
    sampleDataGenerator.generateSampleData();
  }

  @Test
  void testGetAllTasks() {
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(57);
  }

  @Test
  void testGetAllTasksByWorkbasketId() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(22);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultiplePlannedTimeIntervals() {
    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + String.format(
                "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&planned=%s&planned="
                    + "&planned=%s&planned=%s"
                    + "&planned=&planned=%s"
                    + "&sort-by=PLANNED",
                firstInstant, secondInstant, thirdInstant, fourthInstant);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSinglePlannedTimeInterval() {
    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);
    Instant plannedToInstant = Instant.now().minus(3, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&planned-from="
            + plannedFromInstant
            + "&planned-until="
            + plannedToInstant
            + "&sort-by=PLANNED";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefinitePlannedTimeInterval() {
    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&planned-from="
            + plannedFromInstant
            + "&sort-by=PLANNED";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidPlannedParamsCombination() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&planned=2020-01-22T09:44:47.453Z,,"
            + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
            + ",2020-01-18T09:44:47.453Z"
            + "&planned-from=2020-01-19T07:44:47.453Z"
            + "&sort-by=planned";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinMultipleDueTimeIntervals() {
    Instant firstInstant = Instant.now().minus(7, ChronoUnit.DAYS);
    Instant secondInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant thirdInstant = Instant.now().minus(10, ChronoUnit.DAYS);
    Instant fourthInstant = Instant.now().minus(11, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + String.format(
                "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&due=%s&due="
                    + "&due=%s&due=%s"
                    + "&due=&due=%s"
                    + "&sort-by=DUE",
                firstInstant, secondInstant, thirdInstant, fourthInstant);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(22);
  }

  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?wildcard-search-value=99"
            + "&wildcard-search-fields=NAME"
            + "&wildcard-search-fields=CUSTOM_3"
            + "&wildcard-search-fields=CUSTOM_4";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @TestFactory
  Stream<DynamicTest> should_ThrowException_When_ProvidingInvalidFormatForCustomAttributes() {
    Iterator<CustomAttribute> iterator =
        Arrays.asList(
                CustomAttribute.of(null, "value"),
                CustomAttribute.of("", "value"),
                CustomAttribute.of("key", null))
            .iterator();

    ThrowingConsumer<CustomAttribute> test =
        customAttribute -> {
          TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
          taskRepresentationModel.setCustomAttributes(List.of(customAttribute));
          String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
          HttpEntity<TaskRepresentationModel> auth =
              new HttpEntity<>(
                  taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

          ThrowingCallable httpCall =
              () -> TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
          assertThatThrownBy(httpCall)
              .isInstanceOf(HttpStatusCodeException.class)
              .hasMessageContaining("Format of custom attributes is not valid")
              .extracting(HttpStatusCodeException.class::cast)
              .extracting(HttpStatusCodeException::getStatusCode)
              .isEqualTo(HttpStatus.BAD_REQUEST);
        };

    return DynamicTest.stream(iterator, c -> "customAttribute: '" + c.getKey() + "'", test);
  }

  @Test
  void should_DeleteAllTasks_For_ProvidedParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?task-id=TKI:000000000000000000000000000000000036"
            + "&task-id=TKI:000000000000000000000000000000000037"
            + "&task-id=TKI:000000000000000000000000000000000038"
            + "&custom14=abc";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskSummaryCollectionRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.DELETE, auth, TASK_SUMMARY_COLLECTION_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidWildcardSearchParameters() {
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?wildcard-search-value=%rt%";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);

    String url2 =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?wildcard-search-fields=NAME,CUSTOM_3,CUSTOM_4";
    ThrowingCallable httpCall2 =
        () -> TEMPLATE.exchange(url2, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall2)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleDueTimeInterval() {
    Instant dueFromInstant = Instant.now().minus(8, ChronoUnit.DAYS);
    Instant dueToInstant = Instant.now().minus(3, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&due-from="
            + dueFromInstant
            + "&due-until="
            + dueToInstant
            + "&sort-by=DUE";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefiniteDueTimeInterval() {
    Instant dueToInstant = Instant.now().minus(1, ChronoUnit.DAYS);
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&due-until="
            + dueToInstant
            + "&sort-by=DUE";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidDueParamsCombination() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&due=2020-01-22T09:44:47.453Z,,"
            + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
            + ",2020-01-18T09:44:47.453Z"
            + "&due-from=2020-01-19T07:44:47.453Z"
            + "&sort-by=planned";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllTasksByWorkbasketKeyAndDomain() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS) + "?workbasket-key=USER-1-2&domain=DOMAIN_A";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(20);
  }

  @Test
  void testGetAllTasksByExternalId() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?external-id=ETI:000000000000000000000000000000000003"
            + "&external-id=ETI:000000000000000000000000000000000004";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
  }

  @Test
  void testExceptionIfKeyIsSetButDomainIsMissing() {
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?workbasket-key=USER-1-2";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testGetAllTasksWithAdminRole() {
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(88);
  }

  @Test
  void testGetAllTasksKeepingFilters() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?por.type=VNR&por.value=22334455&sort-by=POR_VALUE&order=DESCENDING";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(
            "/api/v1/tasks?por.type=VNR&por.value=22334455"
                + "&sort-by=POR_VALUE&order=DESCENDING");
  }

  @Test
  void testGetLastPageSortedByPorValue() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?state=READY&state=CLAIMED&sort-by=POR_VALUE"
            + "&order=DESCENDING&page-size=5&page=16";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(3);
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.LAST).getHref())
        .contains("page=16");
    assertThat(response.getBody().getContent().iterator().next().getTaskId())
        .isEqualTo("TKI:000000000000000000000000000000000064");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(
            "/api/v1/tasks?state=READY&state=CLAIMED"
                + "&sort-by=POR_VALUE&order=DESCENDING&page-size=5&page=16");
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
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sort-by=DUE&order=DESCENDING";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(57);

    String url2 =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?sort-by=DUE&order=DESCENDING&page-size=5&page=5";
    response = TEMPLATE.exchange(url2, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(5);
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.LAST).getHref())
        .contains("page=12");
    assertThat(response.getBody().getContent().iterator().next().getTaskId())
        .isEqualTo("TKI:000000000000000000000000000000000073");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith("/api/v1/tasks?sort-by=DUE&order=DESCENDING&page-size=5&page=5");
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
  }

  @Test
  void testGetQueryByPorSecondPageSortedByType() {
    resetDb(); // required because
    // ClassificationControllerIntTest.testGetQueryByPorSecondPageSortedByType changes
    // tasks and this test depends on the tasks as they are in sampledata
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?por.company=00&por.system=PASystem&por.instance=00&"
            + "por.type=VNR&por.value=22334455&sort-by=POR_TYPE&"
            + "order=ASCENDING&page-size=5&page=2";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent())
        .extracting(TaskSummaryRepresentationModel::getTaskId)
        .containsExactlyInAnyOrder("TKI:000000000000000000000000000000000013");
    assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(
            "/api/v1/tasks?por.company=00&por.system=PASystem&por.instance=00&"
                + "por.type=VNR&por.value=22334455&sort-by=POR_TYPE&order=ASCENDING&"
                + "page-size=5&page=2");
    assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
    assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
  }

  @Test
  void should_NotGetEmptyAttachmentList_When_GettingTaskWithAttachment() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000002");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    TaskRepresentationModel repModel = response.getBody();
    assertThat(repModel).isNotNull();
    assertThat(repModel.getAttachments()).isNotEmpty();
  }

  @Test
  void should_ReturnReceivedDate_When_GettingTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000024");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(TaskSummaryRepresentationModel::getReceived)
        .isNotNull();
  }

  @Test
  void should_ChangeValueOfReceived_When_UpdatingTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000");
    HttpEntity<Object> httpEntityWithoutBody =
        new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, httpEntityWithoutBody, TASK_MODEL_TYPE);

    final TaskRepresentationModel originalTask = responseGet.getBody();
    Instant expectedReceived = Instant.parse("2019-09-13T08:44:17.588Z");
    originalTask.setReceived(expectedReceived);
    HttpEntity<TaskRepresentationModel> httpEntity =
        new HttpEntity<>(originalTask, RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskRepresentationModel> responseUpdate =
        TEMPLATE.exchange(url, HttpMethod.PUT, httpEntity, TASK_MODEL_TYPE);

    TaskRepresentationModel updatedTask = responseUpdate.getBody();
    assertThat(updatedTask).isNotNull();
    assertThat(updatedTask.getReceived()).isEqualTo(expectedReceived);
  }

  @Test
  void should_ChangeValueOfModified_When_UpdatingTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    final TaskRepresentationModel originalTask = responseGet.getBody();
    HttpEntity<TaskRepresentationModel> auth2 =
        new HttpEntity<>(originalTask, RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskRepresentationModel> responseUpdate =
        TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);

    TaskRepresentationModel updatedTask = responseUpdate.getBody();
    assertThat(originalTask).isNotNull();
    assertThat(updatedTask).isNotNull();
    assertThat(originalTask.getModified()).isBefore(updatedTask.getModified());
  }

  @Test
  void testCreateAndDeleteTask() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
    HttpEntity<TaskRepresentationModel> auth =
        new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<TaskRepresentationModel> responseCreate =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
    assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseCreate.getBody()).isNotNull();

    String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();
    assertThat(taskIdOfCreatedTask).startsWith("TKI:");

    String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID, taskIdOfCreatedTask);
    HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskRepresentationModel> responseDeleted =
        TEMPLATE.exchange(
            url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
    assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  /**
   * TSK-926: If Planned and Due Date is provided to create a task and not matching to service level
   * throw an exception One is calculated by other other date +- service level.
   */
  @Test
  void testCreateWithPlannedAndDueDate() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    Instant plannedTime = Instant.parse("2019-09-13T08:44:17.588Z");
    taskRepresentationModel.setPlanned(plannedTime);
    taskRepresentationModel.setDue(plannedTime);
    String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
    HttpEntity<TaskRepresentationModel> auth =
        new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall).isInstanceOf(HttpStatusCodeException.class);
  }

  @Test
  void should_RouteCreatedTask_When_CreatingTaskWithoutWorkbasketInformation() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    taskRepresentationModel.setWorkbasketSummary(null);

    String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
    HttpEntity<TaskRepresentationModel> auth =
        new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("user-1-1"));
    ResponseEntity<TaskRepresentationModel> responseCreate =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

    assertThat(responseCreate.getBody().getWorkbasketSummary().getWorkbasketId())
        .isEqualTo(IntegrationTestTaskRouter.DEFAULT_ROUTING_TARGET);

    String url2 =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, responseCreate.getBody().getTaskId());
    HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskRepresentationModel> responseDeleted =
        TEMPLATE.exchange(
            url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
    assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void testCreateTaskWithInvalidParameter() throws Exception {
    final String taskToCreateJson =
        "{\"classificationKey\":\"L11010\","
            + "\"workbasketSummaryResource\":"
            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
            + "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\","
            + "\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}";

    URL url = new URL(restHelper.toUrl(RestEndpoints.URL_TASKS));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setRequestProperty("Authorization", RestHelper.encodeUserAndPasswordAsBasicAuth("admin"));
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

    url = new URL(restHelper.toUrl(RestEndpoints.URL_TASKS));
    con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("POST");
    con.setDoOutput(true);
    con.setRequestProperty("Authorization", RestHelper.encodeUserAndPasswordAsBasicAuth("admin"));
    con.setRequestProperty("Content-Type", "application/json");
    out = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), UTF_8));
    out.write(taskToCreateJson2);
    out.flush();
    out.close();
    assertThat(con.getResponseCode()).isEqualTo(400);

    con.disconnect();
  }

  @Test
  void should_CancelTask_when_CallingCancelEndpoint() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000026");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);

    // cancel the task
    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_CANCEL, "TKI:000000000000000000000000000000000026");
    responseGet = TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    assertThat(responseGet.getBody().getState()).isEqualTo(TaskState.CANCELLED);
  }

  @Test
  void testCancelClaimTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000027");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> getTaskResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
    assertThat(getTaskResponse.getBody()).isNotNull();
    TaskRepresentationModel claimedTaskRepresentationModel = getTaskResponse.getBody();
    assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo("user-1-2");

    // cancel claim
    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000027");
    ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
        TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

    assertThat(cancelClaimResponse.getBody()).isNotNull();
    assertThat(cancelClaimResponse.getStatusCode().is2xxSuccessful()).isTrue();
    TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
    assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
  }

  @Test
  void should_ForceCancelClaim_When_TaskIsClaimedByDifferentOwner() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000027");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> getTaskResponse =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
    assertThat(getTaskResponse.getBody()).isNotNull();
    TaskRepresentationModel claimedTaskRepresentationModel = getTaskResponse.getBody();
    assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo("user-1-2");

    // force cancel claim
    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_CLAIM_FORCE, "TKI:000000000000000000000000000000000027");
    ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
        TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

    assertThat(cancelClaimResponse.getBody()).isNotNull();
    assertThat(cancelClaimResponse.getStatusCode().is2xxSuccessful()).isTrue();
    TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
    assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
    assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
  }

  @Test
  void testCancelClaimOfClaimedTaskByAnotherUserShouldThrowException() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000026");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo("user-1-1");

    // try to cancel claim
    String url2 =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000026");
    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void testUpdateTaskOwnerOfReadyTaskSucceeds() {
    final String url = restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000025");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    assertThat(theTaskRepresentationModel.getOwner()).isNull();

    // set Owner and update Task
    theTaskRepresentationModel.setOwner("dummyUser");
    HttpEntity<TaskRepresentationModel> auth2 =
        new HttpEntity<>(theTaskRepresentationModel, RestHelper.generateHeadersForUser("user-1-2"));

    ResponseEntity<TaskRepresentationModel> responseUpdate =
        TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);

    assertThat(responseUpdate.getBody()).isNotNull();
    TaskRepresentationModel theUpdatedTaskRepresentationModel = responseUpdate.getBody();
    assertThat(theUpdatedTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    assertThat(theUpdatedTaskRepresentationModel.getOwner()).isEqualTo("dummyUser");
  }

  @Test
  void testUpdateTaskOwnerOfClaimedTaskFails() {
    final String url = restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000026");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo("user-1-1");

    // set Owner and update Task
    theTaskRepresentationModel.setOwner("dummyuser");
    HttpEntity<TaskRepresentationModel> auth2 =
        new HttpEntity<>(theTaskRepresentationModel, RestHelper.generateHeadersForUser("user-1-2"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("400");
  }

  @Test
  void should_ThrowNotAuthorized_When_UserHasNoAuthorizationOnTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000000");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-b-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(
              url,
              HttpMethod.GET,
              auth,
              ParameterizedTypeReference.forType(TaskRepresentationModel.class));
        };

    assertThatThrownBy(httpCall)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS)
            + "?workbasket-id=WBI:100000000000000000000000000000000001"
            + "&illegalParam=illegal"
            + "&anotherIllegalParam=stillIllegal"
            + "&sort-by=NAME&order=DESCENDING&page-size=5&page=2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining(
            "Unknown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @TestFactory
  Stream<DynamicTest> should_SetTransferFlagDependentOnRequestBody_When_TransferringTask() {
    Iterator<Boolean> iterator = Arrays.asList(true, false).iterator();
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
            "TKI:000000000000000000000000000000000003",
            "WBI:100000000000000000000000000000000006");

    ThrowingConsumer<Boolean> test =
        setTransferFlag -> {
          HttpEntity<String> auth =
              new HttpEntity<>(
                  setTransferFlag.toString(), RestHelper.generateHeadersForUser("admin"));
          ResponseEntity<TaskRepresentationModel> response =
              TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

          assertThat(response.getBody()).isNotNull();
          assertThat(response.getBody().getWorkbasketSummary().getWorkbasketId())
              .isEqualTo("WBI:100000000000000000000000000000000006");
          assertThat(response.getBody().isTransferred()).isEqualTo(setTransferFlag);
        };

    return DynamicTest.stream(iterator, c -> "for setTransferFlag: " + c, test);
  }

  @Test
  void should_SetTransferFlagToTrue_When_TransferringWithoutRequestBody() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
            "TKI:000000000000000000000000000000000003",
            "WBI:100000000000000000000000000000000006");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getWorkbasketSummary().getWorkbasketId())
        .isEqualTo("WBI:100000000000000000000000000000000006");
    assertThat(response.getBody().isTransferred()).isTrue();
  }

  private TaskRepresentationModel getTaskResourceSample() {
    ClassificationSummaryRepresentationModel classificationResource =
        new ClassificationSummaryRepresentationModel();
    classificationResource.setKey("L11010");
    WorkbasketSummaryRepresentationModel workbasketSummary =
        new WorkbasketSummaryRepresentationModel();
    workbasketSummary.setWorkbasketId("WBI:100000000000000000000000000000000004");

    ObjectReferenceRepresentationModel objectReference = new ObjectReferenceRepresentationModel();
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
}
