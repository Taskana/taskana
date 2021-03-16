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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

  private static RestTemplate template;
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
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS),
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(58);
  }

  @Test
  void testGetAllTasksByWorkbasketId() {
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
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

    String parameters =
        String.format(
            "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned=%s&planned="
                + "&planned=%s&planned=%s"
                + "&planned=&planned=%s"
                + "&sort-by=PLANNED",
            firstInstant, secondInstant, thirdInstant, fourthInstant);

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS) + parameters,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSinglePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);
    Instant plannedToInstant = Instant.now().minus(3, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned-from="
                + plannedFromInstant
                + "&planned-until="
                + plannedToInstant
                + "&sort-by=PLANNED",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefinitePlannedTimeInterval() {

    Instant plannedFromInstant = Instant.now().minus(6, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&planned-from="
                + plannedFromInstant
                + "&sort-by=PLANNED",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidPlannedParamsCombination() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&planned=2020-01-22T09:44:47.453Z,,"
                    + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                    + ",2020-01-18T09:44:47.453Z"
                    + "&planned-from=2020-01-19T07:44:47.453Z"
                    + "&sort-by=planned",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
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

    String parameters =
        String.format(
            "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due=%s&due="
                + "&due=%s&due=%s"
                + "&due=&due=%s"
                + "&sort-by=DUE",
            firstInstant, secondInstant, thirdInstant, fourthInstant);

    System.out.println(parameters);
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS) + parameters,
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(22);
  }

  @Test
  void should_ReturnAllTasksByWildcardSearch_For_ProvidedSearchValue() {
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?wildcard-search-value=99"
                + "&wildcard-search-fields=NAME"
                + "&wildcard-search-fields=CUSTOM_3"
                + "&wildcard-search-fields=CUSTOM_4",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(4);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFormatForCustomAttributes() {
    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();

    List<CustomAttribute> customAttributesWithNullKey = new ArrayList<>();
    customAttributesWithNullKey.add(CustomAttribute.of(null, "value"));

    taskRepresentationModel.setCustomAttributes(customAttributesWithNullKey);

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS),
                HttpMethod.POST,
                new HttpEntity<>(taskRepresentationModel, restHelper.getHeadersTeamlead_1()),
                TASK_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Format of custom attributes is not valid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    List<CustomAttribute> customAttributesWithEmptyKey = new ArrayList<>();
    customAttributesWithEmptyKey.add(CustomAttribute.of("", "value"));

    taskRepresentationModel.setCustomAttributes(customAttributesWithEmptyKey);

    httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS),
                HttpMethod.POST,
                new HttpEntity<>(taskRepresentationModel, restHelper.getHeadersTeamlead_1()),
                TASK_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Format of custom attributes is not valid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    List<CustomAttribute> customAttributesWithNullValue = new ArrayList<>();
    customAttributesWithNullValue.add(CustomAttribute.of("key", null));

    taskRepresentationModel.setCustomAttributes(customAttributesWithNullValue);

    httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS),
                HttpMethod.POST,
                new HttpEntity<>(taskRepresentationModel, restHelper.getHeadersTeamlead_1()),
                TASK_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Format of custom attributes is not valid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_DeleteAllTasks_For_ProvidedParams() {
    ResponseEntity<TaskSummaryCollectionRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?task-id=TKI:000000000000000000000000000000000036"
                + "&task-id=TKI:000000000000000000000000000000000037"
                + "&task-id=TKI:000000000000000000000000000000000038"
                + "&custom14=abc",
            HttpMethod.DELETE,
            new HttpEntity<String>(restHelper.getHeadersAdmin()),
            TASK_SUMMARY_COLLECTION_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(3);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidWildcardSearchParameters() {

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS) + "?wildcard-search-value=%rt%",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
                TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);

    ThrowingCallable httpCall2 =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + "?wildcard-search-fields=NAME,CUSTOM_3,CUSTOM_4",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
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

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due-from="
                + dueFromInstant
                + "&due-until="
                + dueToInstant
                + "&sort-by=DUE",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithinSingleIndefiniteDueTimeInterval() {

    Instant dueToInstant = Instant.now().minus(1, ChronoUnit.DAYS);

    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?workbasket-id=WBI:100000000000000000000000000000000001"
                + "&due-until="
                + dueToInstant
                + "&sort-by=DUE",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(6);
  }

  @Test
  void testGetAllTasksByWorkbasketIdWithInvalidDueParamsCombination() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&due=2020-01-22T09:44:47.453Z,,"
                    + "2020-01-19T07:44:47.453Z,2020-01-19T19:44:47.453Z,"
                    + ",2020-01-18T09:44:47.453Z"
                    + "&due-from=2020-01-19T07:44:47.453Z"
                    + "&sort-by=planned",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
                TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksByWorkbasketKeyAndDomain() {
    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersUser_1_2());
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS) + "?workbasket-key=USER-1-2&domain=DOMAIN_A",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(20);
  }

  @Test
  void testGetAllTasksByExternalId() {
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?external-id=ETI:000000000000000000000000000000000003"
                + "&external-id=ETI:000000000000000000000000000000000004",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(2);
  }

  @Test
  void testExceptionIfKeyIsSetButDomainIsMissing() {
    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersUser_1_2());

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS) + "?workbasket-key=USER-1-2",
                HttpMethod.GET,
                request,
                TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("400");
  }

  @Test
  void testGetAllTasksWithAdminRole() {
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(88);
  }

  @Test
  void testGetAllTasksKeepingFilters() {
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?por.type=VNR&por.value=22334455&sort-by=POR_VALUE&order=DESCENDING",
            HttpMethod.GET,
            new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
        .endsWith(
            "/api/v1/tasks?por.type=VNR&por.value=22334455"
                + "&sort-by=POR_VALUE&order=DESCENDING");
  }

  @Test
  void testGetLastPageSortedByPorValue() {

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?state=READY&state=CLAIMED&sort-by=POR_VALUE"
                + "&order=DESCENDING&page-size=5&page=16",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
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

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersTeamlead_1());
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sort-by=DUE&order=DESCENDING",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getContent()).hasSize(58);

    response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?sort-by=DUE&order=DESCENDING&page-size=5&page=5",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
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

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersTeamlead_1());
    ResponseEntity<TaskSummaryPagedRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS)
                + "?por.company=00&por.system=PASystem&por.instance=00&"
                + "por.type=VNR&por.value=22334455&sort-by=POR_TYPE&"
                + "order=ASCENDING&page-size=5&page=2",
            HttpMethod.GET,
            request,
            TASK_SUMMARY_PAGE_MODEL_TYPE);
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
    ResponseEntity<TaskRepresentationModel> response =
        template.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000002"),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            TASK_MODEL_TYPE);

    TaskRepresentationModel repModel = response.getBody();
    assertThat(repModel).isNotNull();
    assertThat(repModel.getAttachments()).isNotEmpty();
    assertThat(repModel.getAttachments()).isNotNull();
  }

  @Test
  void should_ChangeValueOfModified_When_UpdatingTask() {

    ResponseEntity<TaskRepresentationModel> responseGet =
        template.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersTeamlead_1()),
            TASK_MODEL_TYPE);

    final TaskRepresentationModel originalTask = responseGet.getBody();

    ResponseEntity<TaskRepresentationModel> responseUpdate =
        template.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"),
            HttpMethod.PUT,
            new HttpEntity<>(originalTask, restHelper.getHeadersTeamlead_1()),
            TASK_MODEL_TYPE);

    TaskRepresentationModel updatedTask = responseUpdate.getBody();
    assertThat(originalTask).isNotNull();
    assertThat(updatedTask).isNotNull();
    assertThat(originalTask.getModified()).isBefore(updatedTask.getModified());
  }

  @Test
  void testCreateAndDeleteTask() {

    TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
    ResponseEntity<TaskRepresentationModel> responseCreate =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS),
            HttpMethod.POST,
            new HttpEntity<>(taskRepresentationModel, restHelper.getHeadersTeamlead_1()),
            TASK_MODEL_TYPE);
    assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(responseCreate.getBody()).isNotNull();

    String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();

    assertThat(taskIdOfCreatedTask).isNotNull();
    assertThat(taskIdOfCreatedTask).startsWith("TKI:");

    ResponseEntity<TaskRepresentationModel> responseDeleted =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID, taskIdOfCreatedTask),
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
    taskRepresentationModel.setPlanned(now);
    taskRepresentationModel.setDue(now);

    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS),
                HttpMethod.POST,
                new HttpEntity<>(taskRepresentationModel, restHelper.getHeadersUser_1_1()),
                TASK_MODEL_TYPE);
    assertThatThrownBy(httpCall).isInstanceOf(HttpClientErrorException.class);
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
    con.setRequestProperty("Authorization", RestHelper.AUTHORIZATION_TEAMLEAD_1);
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
    con.setRequestProperty("Authorization", RestHelper.AUTHORIZATION_TEAMLEAD_1);
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

    final String claimed_task_id = "TKI:000000000000000000000000000000000026";

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID, claimed_task_id),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);

    // cancel the task
    responseGet =
        template.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID_CANCEL, claimed_task_id),
            HttpMethod.POST,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CANCELLED);
  }

  @Test
  void testCancelClaimTask() {

    final String claimed_task_id = "TKI:000000000000000000000000000000000027";
    final String user_id_of_claimed_task = "user-1-2";

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> getTaskResponse =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID, claimed_task_id),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(getTaskResponse.getBody()).isNotNull();

    TaskRepresentationModel claimedTaskRepresentationModel = getTaskResponse.getBody();
    assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo(user_id_of_claimed_task);

    // cancel claim
    ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID_CLAIM, claimed_task_id),
            HttpMethod.DELETE,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

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
    final String user_id_of_claimed_task = "user-1-1";

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_TASKS_ID, claimed_task_id),
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo(user_id_of_claimed_task);

    // try to cancel claim
    ThrowingCallable httpCall =
        () ->
            template.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS_ID_CLAIM, claimed_task_id),
                HttpMethod.DELETE,
                new HttpEntity<>(restHelper.getHeadersUser_1_2()),
                TASK_MODEL_TYPE);
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
        TEMPLATE.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    assertThat(theTaskRepresentationModel.getOwner()).isNull();

    // set Owner and update Task

    final String anyUserName = "dummyUser";
    theTaskRepresentationModel.setOwner(anyUserName);
    ResponseEntity<TaskRepresentationModel> responseUpdate =
        TEMPLATE.exchange(
            taskUrlString,
            HttpMethod.PUT,
            new HttpEntity<>(theTaskRepresentationModel, restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

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
        TEMPLATE.exchange(
            taskUrlString,
            HttpMethod.GET,
            new HttpEntity<>(restHelper.getHeadersUser_1_2()),
            TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    assertThat(theTaskRepresentationModel.getOwner()).isEqualTo("user-1-1");

    // set Owner and update Task

    final String anyUserName = "dummyuser";
    theTaskRepresentationModel.setOwner(anyUserName);

    ThrowingCallable httpCall =
        () ->
            template.exchange(
                taskUrlString,
                HttpMethod.PUT,
                new HttpEntity<>(theTaskRepresentationModel, restHelper.getHeadersUser_1_2()),
                TASK_MODEL_TYPE);
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("409");
  }

  @Test
  void should_ThrowNotAuthorized_When_UserHasNoAuthorizationOnTask() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000000");

    ThrowingCallable httpCall =
        () ->
            template.exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersUser_b_1()),
                ParameterizedTypeReference.forType(TaskRepresentationModel.class));

    assertThatThrownBy(httpCall)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowException_When_ProvidingInvalidFilterParams() {

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + "?workbasket-id=WBI:100000000000000000000000000000000001"
                    + "&illegalParam=illegal"
                    + "&anotherIllegalParam=stillIllegal"
                    + "&sort-by=NAME&order=DESCENDING&page-size=5&page=2",
                HttpMethod.GET,
                new HttpEntity<String>(restHelper.getHeadersTeamlead_1()),
                TASK_SUMMARY_PAGE_MODEL_TYPE);

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining(
            "Unkown request parameters found: [anotherIllegalParam, illegalParam]")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @TestFactory
  Stream<DynamicTest> should_SetTransferFlagDependentOnRequestBody_When_TransferringTask() {
    Iterator<Boolean> iterator = Arrays.asList(true, false).iterator();

    ThrowingConsumer<Boolean> test =
        setTransferFlag -> {
          ResponseEntity<TaskRepresentationModel> response =
              TEMPLATE.exchange(
                  restHelper.toUrl(
                      RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
                      "TKI:000000000000000000000000000000000003",
                      "WBI:100000000000000000000000000000000006"),
                  HttpMethod.POST,
                  new HttpEntity<>(setTransferFlag.toString(), restHelper.getHeadersAdmin()),
                  TASK_MODEL_TYPE);

          TaskRepresentationModel task = response.getBody();
          assertThat(task).isNotNull();
          assertThat(task.getWorkbasketSummary().getWorkbasketId())
              .isEqualTo("WBI:100000000000000000000000000000000006");
          assertThat(task.isTransferred()).isEqualTo(setTransferFlag);
        };

    return DynamicTest.stream(iterator, c -> "for setTransferFlag: " + c, test);
  }

  @Test
  void should_SetTransferFlagToTrue_When_TransferringWithoutRequestBody() {
    ResponseEntity<TaskRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(
                RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
                "TKI:000000000000000000000000000000000003",
                "WBI:100000000000000000000000000000000006"),
            HttpMethod.POST,
            new HttpEntity<>(restHelper.getHeadersAdmin()),
            TASK_MODEL_TYPE);

    TaskRepresentationModel task = response.getBody();
    assertThat(task).isNotNull();
    assertThat(task.getWorkbasketSummary().getWorkbasketId())
        .isEqualTo("WBI:100000000000000000000000000000000006");
    assertThat(task.isTransferred()).isTrue();
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
