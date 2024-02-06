package pro.taskana.task.rest;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.rest.test.RestHelper.TEMPLATE;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.sql.DataSource;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.testcontainers.shaded.com.google.common.collect.Lists;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.classification.rest.models.ClassificationSummaryRepresentationModel;
import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.RestHelper;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.rest.models.AttachmentRepresentationModel;
import pro.taskana.task.rest.models.IsReadRepresentationModel;
import pro.taskana.task.rest.models.ObjectReferenceRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel.CustomAttribute;
import pro.taskana.task.rest.models.TaskSummaryCollectionRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryPagedRepresentationModel;
import pro.taskana.task.rest.models.TaskSummaryRepresentationModel;
import pro.taskana.task.rest.models.TransferTaskRepresentationModel;
import pro.taskana.task.rest.routing.IntegrationTestTaskRouter;
import pro.taskana.workbasket.rest.models.WorkbasketSummaryRepresentationModel;

/** Test Task Controller. */
@TaskanaSpringBootTest
class TaskControllerIntTest {

  private static final ParameterizedTypeReference<TaskSummaryPagedRepresentationModel>
      TASK_SUMMARY_PAGE_MODEL_TYPE = new ParameterizedTypeReference<>() {};
  private static final ParameterizedTypeReference<TaskSummaryCollectionRepresentationModel>
      TASK_SUMMARY_COLLECTION_MODEL_TYPE = new ParameterizedTypeReference<>() {};
  private static final ParameterizedTypeReference<Map<String, Object>>
      BULK_RESULT_TASKS_MODEL_TYPE = new ParameterizedTypeReference<>() {};
  private static final ParameterizedTypeReference<TaskRepresentationModel> TASK_MODEL_TYPE =
      ParameterizedTypeReference.forType(TaskRepresentationModel.class);
  private final RestHelper restHelper;
  private final DataSource dataSource;
  private final String schemaName;
  @Autowired TaskanaConfiguration taskanaConfiguration;

  @Autowired
  TaskControllerIntTest(
      RestHelper restHelper,
      DataSource dataSource,
      @Value("${taskana.schemaName:TASKANA}") String schemaName) {
    this.restHelper = restHelper;
    this.dataSource = dataSource;
    this.schemaName = schemaName;
  }

  @Test
  void should_UpdateTaskOwnerOfReadyForReviewTask() {
    final String url = restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000104");
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

    // retrieve task from Rest Api
    ResponseEntity<TaskRepresentationModel> responseGet =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

    assertThat(responseGet.getBody()).isNotNull();
    TaskRepresentationModel theTaskRepresentationModel = responseGet.getBody();
    assertThat(theTaskRepresentationModel.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(theTaskRepresentationModel.getOwner()).isNull();

    // set Owner and update Task
    theTaskRepresentationModel.setOwner("dummyUser");
    HttpEntity<TaskRepresentationModel> auth2 =
        new HttpEntity<>(theTaskRepresentationModel, RestHelper.generateHeadersForUser("user-1-2"));

    ResponseEntity<TaskRepresentationModel> responseUpdate =
        TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);

    assertThat(responseUpdate.getBody()).isNotNull();
    TaskRepresentationModel theUpdatedTaskRepresentationModel = responseUpdate.getBody();
    assertThat(theUpdatedTaskRepresentationModel.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    assertThat(theUpdatedTaskRepresentationModel.getOwner()).isEqualTo("dummyUser");
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

  private ObjectReferenceRepresentationModel getObjectReferenceResourceSample() {
    ObjectReferenceRepresentationModel objectReference = new ObjectReferenceRepresentationModel();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");
    return objectReference;
  }

  private AttachmentRepresentationModel getAttachmentResourceSample() {
    AttachmentRepresentationModel attachmentRepresentationModel =
        new AttachmentRepresentationModel();
    attachmentRepresentationModel.setAttachmentId("A11010");
    attachmentRepresentationModel.setObjectReference(getObjectReferenceResourceSample());
    ClassificationSummaryRepresentationModel classificationSummaryRepresentationModel =
        new ClassificationSummaryRepresentationModel();
    classificationSummaryRepresentationModel.setClassificationId(
        "CLI:100000000000000000000000000000000004");
    classificationSummaryRepresentationModel.setKey("L11010");
    attachmentRepresentationModel.setClassificationSummary(
        classificationSummaryRepresentationModel);
    return attachmentRepresentationModel;
  }

  private ObjectReferenceRepresentationModel getSampleSecondaryObjectReference(String suffix) {
    ObjectReferenceRepresentationModel objectReference = new ObjectReferenceRepresentationModel();
    objectReference.setCompany("SecondaryCompany" + suffix);
    objectReference.setSystem("SecondarySystem" + suffix);
    objectReference.setSystemInstance("SecondaryInstance" + suffix);
    objectReference.setType("SecondaryType" + suffix);
    objectReference.setValue("0000000" + suffix);
    return objectReference;
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class GetTasks {

    @Test
    void should_GetAllTasks() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(61);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedWorkbasketId() {
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdWithinMultiplePlannedTimeIntervals() {
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
    void should_GetCustomIntCorrectly_When_GettingTaskWithCustomIntValues() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000025");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      ResponseEntity<TaskRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      TaskRepresentationModel repModel = response.getBody();
      assertThat(repModel).isNotNull();
      assertThat(repModel.getCustomInt1()).isEqualTo(1);
      assertThat(repModel.getCustomInt2()).isEqualTo(2);
      assertThat(repModel.getCustomInt3()).isEqualTo(3);
      assertThat(repModel.getCustomInt4()).isEqualTo(4);
      assertThat(repModel.getCustomInt5()).isEqualTo(5);
      assertThat(repModel.getCustomInt6()).isEqualTo(6);
      assertThat(repModel.getCustomInt7()).isEqualTo(7);
      assertThat(repModel.getCustomInt8()).isEqualTo(8);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldIn() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s=%s",
                        i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldFrom() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-from=%s",
                        i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldFromAndTo() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-from=-1&custom-int-%s-to=123",
                        i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldTo() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-to=%s",
                        i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldNotIn() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-not=25",
                        i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest>
        should_ThrowException_For_SpecifiedWorkbasketIdAndCustomIntFieldWithinIncorrectInterval() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-within=%s"
                            + "&custom-int-%s-within=23"
                            + "&custom-int-%s-within=15",
                        i, i, i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ThrowingCallable httpCall =
                () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThatThrownBy(httpCall)
                .isInstanceOf(HttpStatusCodeException.class)
                .hasMessageContaining(
                    String.format(
                        "provided length of the property 'custom-int-%s-within' is not dividable by"
                            + " 2",
                        i))
                .extracting(HttpStatusCodeException.class::cast)
                .extracting(HttpStatusCodeException::getStatusCode)
                .isEqualTo(HttpStatus.BAD_REQUEST);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest>
        should_ThrowException_For_SpecifiedWorkbasketIdAndCustomIntFieldWithinNullInterval() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-within="
                            + "&custom-int-%s-within=",
                        i, i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ThrowingCallable httpCall =
                () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThatThrownBy(httpCall)
                .isInstanceOf(HttpStatusCodeException.class)
                .hasMessageContaining(
                    String.format(
                        "Each interval in 'custom-int-"
                            + i
                            + "-within' shouldn't consist of two 'null' values",
                        i))
                .extracting(HttpStatusCodeException.class::cast)
                .extracting(HttpStatusCodeException::getStatusCode)
                .isEqualTo(HttpStatus.BAD_REQUEST);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest> should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldWithin() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-within=%s"
                            + "&custom-int-%s-within=15",
                        i, i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest>
        should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldWithinOpenLowerBound() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-within="
                            + "&custom-int-%s-within=%s",
                        i, i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).hasSize(22);
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @TestFactory
    Stream<DynamicTest>
        should_GetAllTasks_For_SpecifiedWorkbasketIdAndCustomIntFieldNotWithinOpenUpperBound() {
      List<Integer> customIntValues = List.of(1, 2, 3, 4, 5, 6, 7, 8);
      ThrowingConsumer<Integer> test =
          i -> {
            String url =
                restHelper.toUrl(RestEndpoints.URL_TASKS)
                    + String.format(
                        "?workbasket-id=WBI:100000000000000000000000000000000001"
                            + "&custom-int-%s-not-within=%s"
                            + "&custom-int-%s-not-within=",
                        i, i, i);
            HttpEntity<Object> auth =
                new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

            ResponseEntity<TaskSummaryPagedRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
            assertThat(response.getBody().getContent()).isEmpty();
          };

      return DynamicTest.stream(customIntValues.iterator(), c -> "customInt" + c, test);
    }

    @Test
    void should_GetAllTasks_For_SpecifiesWorkbasketIdWithinSinglePlannedTimeInterval() {
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdWithinSingleIndefinitePlannedTimeInterval() {
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
    void
        should_ThrowException_When_GettingTasksByWorkbasketIdWithInvalidPlannedParamsCombination() {
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdWithinMultipleDueTimeIntervals() {
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdAndPriorityFromAndUntil() {
      Integer priorityFrom = 2;
      Integer priorityTo = 3;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-from=%s&priority-until=%s",
                  priorityFrom, priorityTo);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(2);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedWorkbasketIdAndMultiplePriorityWithinIntervals() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 0;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-within=%s&priority-within=&priority-within=%s&priority-within=",
                  priorityFrom1, priorityFrom2);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(3);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedWorkbasketIdAndPriorityNotFromAndNotUntil() {
      Integer priorityFrom = 2;
      Integer priorityTo = 3;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-not-from=%s&priority-not-until=%s",
                  priorityFrom, priorityTo);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedWorkbasketIdAndMultiplePriorityNotWithinIntervals() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 1;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-not-within=%s&priority-not-within="
                      + "&priority-not-within=%s&priority-not-within=",
                  priorityFrom1, priorityFrom2);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void should_ThrowException_When_GettingTasksByWorkbasketIdWithPriorityNotWithinAndNotFrom() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 1;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-not-within=%s&priority-not-within="
                      + "&priority-not-from=%s&priority-not-until=",
                  priorityFrom1, priorityFrom2);

      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_ThrowException_When_GettingTasksByWorkbasketIdWithPriorityWithinAndPriorityFrom() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 1;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-within=%s&priority-within=&priority-from=%s&priority-until=",
                  priorityFrom1, priorityFrom2);

      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_ThrowException_When_GettingTasksByWorkbasketIdWithEvenNumberOfPriorityWithin() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 1;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-within=%s&priority-within=&priority-within=%s",
                  priorityFrom1, priorityFrom2);

      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_ThrowException_When_GettingTasksByWorkbasketIdWithEvenNumberOfPriorityNotWithin() {
      Integer priorityFrom1 = 2;
      Integer priorityFrom2 = 1;
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&priority-not-within=%s&priority-not-within=&priority-not-within=%s",
                  priorityFrom1, priorityFrom2);

      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_ReturnAllTasks_For_SpecifiedWorkbasketIdAndClassificationParentKeyIn() {
      String parentKey = "L11010";
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&classification-parent-key=%s",
                  parentKey);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void should_ReturnAllTasks_For_SpecifiedWorkbasketIdAndClassificationParentKeyNotIn() {
      String parentKey = "L11010";
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format(
                  "?workbasket-id=WBI:100000000000000000000000000000000006"
                      + "&classification-parent-key-not=%s",
                  parentKey);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(2);
    }

    @Test
    void should_ReturnAllTasks_For_SpecifiedWorkbasketIdAndClassificationParentKeyLike() {
      String parentKey = "L%";
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format("?classification-parent-key-like=%s", parentKey);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(3);
    }

    @Test
    void should_ReturnAllTasks_For_SpecifiedWorkbasketIdAndClassificationParentKeyNotLike() {
      String parentKey = "L%";
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + String.format("?classification-parent-key-not-like=%s", parentKey);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(89);
    }

    @Test
    void should_ReturnAllTasks_For_ProvidedPrimaryObjectReference() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?por="
              + URLEncoder.encode(
                  "{\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\"}", UTF_8);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(4);
    }

    @Test
    void should_ReturnAllTasks_For_ProvidedSecondaryObjectReferenceByTypeAndValue() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?sor="
              + URLEncoder.encode("{\"type\":\"Type2\",\"value\":\"Value2\"}", UTF_8);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(2);
    }

    @Test
    void should_ReturnAllTasks_For_ProvidedSecondaryObjectReferenceByCompany() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?sor="
              + URLEncoder.encode("{\"company\":\"Company3\"}", UTF_8);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void should_ReturnNoTasks_For_ProvidedNonexistentSecondaryObjectReference() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?sor="
              + URLEncoder.encode("{\"type\":\"Type2\",\"value\":\"Quatsch\"}", UTF_8);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).isEmpty();
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

    @Test
    void should_ThrowException_When_ProvidingInvalidOrder() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?workbasket-id=WBI:100000000000000000000000000000000001"
              + "&sort-by=NAME&order=WRONG";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .hasMessageContaining("\"expectedValues\":[\"ASCENDING\",\"DESCENDING\"]")
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_ThrowNotAuthorized_When_UserHasNoAuthorizationOnTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000000");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-b-1"));

      ThrowingCallable httpCall =
          () ->
              TEMPLATE.exchange(
                  url,
                  HttpMethod.GET,
                  auth,
                  ParameterizedTypeReference.forType(TaskRepresentationModel.class));

      assertThatThrownBy(httpCall)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.FORBIDDEN);
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdWithinSingleDueTimeInterval() {
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
    void should_GetAllTasks_For_SpecifiedWorkbasketIdWithinSingleIndefiniteDueTimeInterval() {
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
    void should_GetAllTasks_For_WorkbasketIdWithInvalidDueParamsCombination() {
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
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedWorkbasketKeyAndDomain() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS) + "?workbasket-key=USER-1-2&domain=DOMAIN_A";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(22);
    }

    @Test
    void should_GetAllTasks_For_SpecifiedExternalId() {
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
    void should_ThrowException_When_KeyIsSetButDomainIsMissing() {
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
    void should_GetAllTasksWithAdminRole() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(92);
    }

    @Test
    void should_KeepFiltersInTheLinkOfTheResponse_When_GettingTasks() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?por-type=VNR&por-value=22334455&sort-by=POR_VALUE&order=DESCENDING";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
          .endsWith(
              "/api/v1/tasks?por-type=VNR&por-value=22334455"
                  + "&sort-by=POR_VALUE&order=DESCENDING");
    }

    @ParameterizedTest
    @CsvSource({
      "owner=user-1-1, 10",
      "owner-is-null, 65",
      "owner-is-null=true, 65",
      "owner-is-null&owner=user-1-1, 75",
      "owner-is-null=TRUE&owner=user-1-1, 75",
      "state=READY&owner-is-null&owner=user-1-1, 56",
      "state=READY&owner-is-null=TrUe&owner=user-1-1, 56",
    })
    void should_ReturnTasksWithVariousOwnerParameters_When_GettingTasks(
        String queryParams, int expectedSize) {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?" + queryParams;
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));
      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat((response.getBody()).getContent()).hasSize(expectedSize);
    }

    @TestFactory
    Stream<DynamicTest> should_ThrowException_When_OwnerIsNullParamNotStrict() {
      List<Pair<String, String>> list =
          List.of(
              Pair.of("When owner-is-null=", "?owner-is-null="),
              Pair.of("When owner-is-null=owner-is-null", "?owner-is-null=owner-is-null"),
              Pair.of(
                  "When owner-is-null=anyValue1,anyValue2", "?owner-is-null=anyValue1,anyValue2"));
      ThrowingConsumer<Pair<String, String>> testOwnerIsNull =
          t -> {
            String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + t.getRight();
            HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

            assertThatThrownBy(
                    () ->
                        TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE))
                .isInstanceOf(HttpStatusCodeException.class)
                .hasMessageContaining(
                    "It is prohibited to use the param owner-is-null with values.");
          };
      return DynamicTest.stream(list.iterator(), Pair::getLeft, testOwnerIsNull);
    }

    @Test
    void should_GetAllTasks_For_GettingLastTaskSummaryPageSortedByPorValue() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?state=READY&state=CLAIMED&sort-by=POR_VALUE"
              + "&order=DESCENDING&page-size=5&page=16";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getContent()).hasSize(5);
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
    void should_SortByOwnerLongName() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?sort-by=OWNER_LONG_NAME"
              + "&order=DESCENDING";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    }

    @Test
    void should_GroupByPor() throws Exception {
      Field useSpecificDb2Taskquery =
          taskanaConfiguration.getClass().getDeclaredField("useSpecificDb2Taskquery");
      useSpecificDb2Taskquery.setAccessible(true);
      useSpecificDb2Taskquery.setBoolean(taskanaConfiguration, true);

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?group-by=POR_VALUE";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(14);
      assertThat(
              response.getBody().getContent().stream()
                  .filter(task -> task.getPrimaryObjRef().getValue().equals("MyValue1"))
                  .map(TaskSummaryRepresentationModel::getGroupByCount)
                  .toArray())
          .containsExactly(6);

      useSpecificDb2Taskquery.setBoolean(taskanaConfiguration, false);
    }

    @Test
    void should_GroupBySor() throws Exception {
      Field useSpecificDb2Taskquery =
          taskanaConfiguration.getClass().getDeclaredField("useSpecificDb2Taskquery");
      useSpecificDb2Taskquery.setAccessible(true);
      useSpecificDb2Taskquery.setBoolean(taskanaConfiguration, true);

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?group-by-sor=Type2";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
      assertThat(
              response.getBody().getContent().stream()
                  .map(TaskSummaryRepresentationModel::getGroupByCount)
                  .toArray())
          .containsExactly(2);

      useSpecificDb2Taskquery.setBoolean(taskanaConfiguration, false);
    }

    @Test
    void testGetLastPageSortedByDueWithHiddenTasksRemovedFromResult() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sort-by=DUE&order=DESCENDING";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getContent()).hasSize(61);

      String url2 =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?sort-by=DUE&order=DESCENDING&page-size=5&page=5";
      response = TEMPLATE.exchange(url2, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getContent()).hasSize(5);
      assertThat(response.getBody().getRequiredLink(IanaLinkRelations.LAST).getHref())
          .contains("page=13");
      assertThat(response.getBody().getContent().iterator().next().getTaskId())
          .isEqualTo("TKI:000000000000000000000000000000000072");
      assertThat(response.getBody().getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getRequiredLink(IanaLinkRelations.SELF).getHref())
          .endsWith("/api/v1/tasks?sort-by=DUE&order=DESCENDING&page-size=5&page=5");
      assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
      assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
      assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    }

    @Test
    void should_GetAllTasks_For_GettingSecondPageFilteredByPorAttributesSortedByType() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS)
              + "?por-company=00&por-system=PASystem&por-instance=00&"
              + "por-type=VNR&por-value=22334455&sort-by=POR_TYPE&"
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
              "/api/v1/tasks?por-company=00&por-system=PASystem&por-instance=00&"
                  + "por-type=VNR&por-value=22334455&sort-by=POR_TYPE&order=ASCENDING&"
                  + "page-size=5&page=2");
      assertThat(response.getBody().getLink(IanaLinkRelations.FIRST)).isNotNull();
      assertThat(response.getBody().getLink(IanaLinkRelations.LAST)).isNotNull();
      assertThat(response.getBody().getLink(IanaLinkRelations.PREV)).isNotNull();
    }

    @Test
    void should_GetAllTasksWithComments_When_FilteringByHasCommentsIsSetToTrue() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?has-comments=true";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent())
          .extracting(TaskSummaryRepresentationModel::getTaskId)
          .containsExactlyInAnyOrder(
              "TKI:000000000000000000000000000000000000",
              "TKI:000000000000000000000000000000000001",
              "TKI:000000000000000000000000000000000002",
              "TKI:000000000000000000000000000000000004",
              "TKI:000000000000000000000000000000000025",
              "TKI:000000000000000000000000000000000026",
              "TKI:000000000000000000000000000000000027");
    }

    @Test
    void should_GetAllTasksWithoutComments_When_FilteringByHasCommentsIsSetToFalse() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?has-comments=false";
      HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getContent())
          .extracting(TaskSummaryRepresentationModel::getTaskId)
          .doesNotContain(
              "TKI:000000000000000000000000000000000000",
              "TKI:000000000000000000000000000000000001",
              "TKI:000000000000000000000000000000000002",
              "TKI:000000000000000000000000000000000004",
              "TKI:000000000000000000000000000000000025",
              "TKI:000000000000000000000000000000000026",
              "TKI:000000000000000000000000000000000027")
          .hasSize(54);
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
    void should_ReturnFilteredTasks_When_GettingTaskWithoutAttachments() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?without-attachment=true";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(85);
    }

    @Test
    void should_ThrowException_When_WithoutAttachmentsIsSetToFalse() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?without-attachment=false";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      assertThatThrownBy(
              () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE))
          .isInstanceOf(HttpStatusCodeException.class)
          .hasMessageContaining(
              "provided value of the property 'without-attachment' must be 'true'");
    }

    @Test
    void should_NotGetEmptyObjectReferencesList_When_GettingTaskWithObjectReferences() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000001");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      TaskRepresentationModel repModel = response.getBody();
      assertThat(repModel).isNotNull();
      assertThat(repModel.getSecondaryObjectReferences()).isNotEmpty();
    }

    @Test
    void should_ReturnFilteredTasks_When_GettingTasksBySecondaryObjectReferenceValue() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sor-value=Value2";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(3);
    }

    @Test
    void should_ReturnFilteredTasks_When_GettingTasksBySecondaryObjectReferenceTypeLike() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sor-type-like=Type";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(3);
    }

    @Test
    void should_ReturnFilteredTasks_When_GettingTasksBySecondaryObjectReferenceValueAndCompany() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS) + "?sor-value=Value2&&sor-company=Company1";
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskSummaryPagedRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_SUMMARY_PAGE_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
      assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void should_GetPriorityCorrectly_When_GettingTaskWithManualPriority() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000070000000000000079");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getPriority())
          .isEqualTo((response.getBody().getManualPriority()))
          .isEqualTo(56);
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
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CreateTasks {

    @Test
    void should_CreateAndDeleteTask() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(
              taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> responseCreate =
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
      assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(responseCreate.getBody()).isNotNull();

      String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();
      assertThat(taskIdOfCreatedTask).startsWith("TKI:");

      String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID_FORCE, taskIdOfCreatedTask);
      HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseDeleted =
          TEMPLATE.exchange(
              url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
      assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void should_CreateTaskWithError_When_SpecifyingAttachmentWrong() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
      AttachmentRepresentationModel attachmentRepresentationModel = getAttachmentResourceSample();
      attachmentRepresentationModel.setTaskId(taskRepresentationModel.getTaskId() + "wrongId");
      taskRepresentationModel.setAttachments(Lists.newArrayList(attachmentRepresentationModel));

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(
              taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_CreateAndDeleteTaskWithSecondaryObjectReferences_When_SpecifyingObjectReferences() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
      ObjectReferenceRepresentationModel obj0 = getSampleSecondaryObjectReference("0");
      obj0.setTaskId(taskRepresentationModel.getTaskId());
      ObjectReferenceRepresentationModel obj1 = getSampleSecondaryObjectReference("1");
      obj1.setTaskId(taskRepresentationModel.getTaskId());
      List<ObjectReferenceRepresentationModel> secondaryObjectReferences = List.of(obj0, obj1);
      taskRepresentationModel.setSecondaryObjectReferences(secondaryObjectReferences);

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(
              taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> responseCreate =
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
      assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(responseCreate.getBody()).isNotNull();
      String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();

      String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID_FORCE, taskIdOfCreatedTask);
      HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseDeleted =
          TEMPLATE.exchange(
              url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
      assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void should_CreateAndDeleteTaskWithManualPriority_When_SpecifyingManualPriority() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
      taskRepresentationModel.setManualPriority(7);

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(
              taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> responseCreate =
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
      assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(responseCreate.getBody()).isNotNull();
      assertThat(responseCreate.getBody().getPriority())
          .isEqualTo((responseCreate.getBody().getManualPriority()))
          .isEqualTo(7);

      String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();
      String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID_FORCE, taskIdOfCreatedTask);
      HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseDeleted =
          TEMPLATE.exchange(
              url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
      assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void should_CreateTaskWithCorrectPriorityAndThenDeleteIt_When_NotSpecifyingManualPriority() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();

      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(
              taskRepresentationModel, RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> responseCreate =
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);
      assertThat(responseCreate.getStatusCode()).isEqualTo(HttpStatus.CREATED);
      assertThat(responseCreate.getBody()).isNotNull();
      // The classification of taskRepresentationModel with the key "L11010" has priority=1
      assertThat(responseCreate.getBody().getPriority()).isEqualTo(1);
      assertThat(responseCreate.getBody().getManualPriority()).isEqualTo(-1);

      String taskIdOfCreatedTask = responseCreate.getBody().getTaskId();
      String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID_FORCE, taskIdOfCreatedTask);
      HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseDeleted =
          TEMPLATE.exchange(
              url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
      assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    /**
     * TSK-926: If Planned and Due Date is provided to create a task and not matching to service
     * level throw an exception One is calculated by other date +- service level.
     */
    @Test
    void should_ThrowException_When_CreatingTaskWithPlannedAndDueDateNotMatchingServiceLevel() {
      TaskRepresentationModel taskRepresentationModel = getTaskResourceSample();
      Instant plannedTime = Instant.parse("2019-09-13T08:44:17.588Z");
      taskRepresentationModel.setPlanned(plannedTime);
      taskRepresentationModel.setDue(plannedTime);
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS);
      HttpEntity<TaskRepresentationModel> auth =
          new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("user-1-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

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
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID_FORCE, responseCreate.getBody().getTaskId());
      HttpEntity<Object> auth2 = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseDeleted =
          TEMPLATE.exchange(
              url2, HttpMethod.DELETE, auth2, ParameterizedTypeReference.forType(Void.class));
      assertThat(responseDeleted.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void should_ThrowException_When_CreatingTaskWithInvalidParameter() throws Exception {
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
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class UpdateTasks {

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
    void should_ThrowError_When_UpdatingTaskWithBadAttachment() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000");
      HttpEntity<Object> httpEntityWithoutBody =
          new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, httpEntityWithoutBody, TASK_MODEL_TYPE);

      final TaskRepresentationModel originalTask = responseGet.getBody();

      AttachmentRepresentationModel attachmentRepresentationModel = getAttachmentResourceSample();
      attachmentRepresentationModel.setTaskId(originalTask.getTaskId() + "wrongId");
      originalTask.setAttachments(Lists.newArrayList(attachmentRepresentationModel));

      HttpEntity<TaskRepresentationModel> httpEntity =
          new HttpEntity<>(originalTask, RestHelper.generateHeadersForUser("teamlead-1"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.PUT, httpEntity, TASK_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.BAD_REQUEST);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class DeleteTasks {

    @Test
    void should_DeleteTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000039");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(responseGet.getBody()).isNotNull();
      assertThat(responseGet.getBody().getState()).isEqualTo(TaskState.COMPLETED);

      ResponseEntity<TaskRepresentationModel> responseDelete =
          TEMPLATE.exchange(url, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .hasMessageContaining(
              "Task with id 'TKI:000000000000000000000000000000000039' was not found.")
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_ForceDeleteTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000026");
      String urlForce =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_FORCE, "TKI:000000000000000000000000000000000026");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(responseGet.getBody()).isNotNull();
      assertThat(responseGet.getBody().getState()).isEqualTo(TaskState.CLAIMED);

      ResponseEntity<TaskRepresentationModel> responseDelete =
          TEMPLATE.exchange(urlForce, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(responseDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .hasMessageContaining(
              "Task with id 'TKI:000000000000000000000000000000000026' was not found.")
          .extracting(HttpStatusCodeException.class::cast)
          .extracting(HttpStatusCodeException::getStatusCode)
          .isEqualTo(HttpStatus.NOT_FOUND);
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
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class UpdateTaskOwnerOfTasks {

    @Test
    void should_UpdateTaskOwnerOfReadyTask() {
      final String url = restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000025");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(responseGet.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = responseGet.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.READY);
      assertThat(taskRepresentationModel.getOwner()).isNull();

      // set Owner and update Task
      taskRepresentationModel.setOwner("dummyUser");
      HttpEntity<TaskRepresentationModel> auth2 =
          new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("user-1-2"));

      ResponseEntity<TaskRepresentationModel> responseUpdate =
          TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);

      assertThat(responseUpdate.getBody()).isNotNull();
      TaskRepresentationModel theUpdatedTaskRepresentationModel = responseUpdate.getBody();
      assertThat(theUpdatedTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
      assertThat(theUpdatedTaskRepresentationModel.getOwner()).isEqualTo("dummyUser");
    }

    @Test
    void should_ThrowException_When_UpdatingTaskOwnerOfClaimedTask() {
      final String url = restHelper.toUrl("/api/v1/tasks/TKI:000000000000000000000000000000000026");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(responseGet.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = responseGet.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-1");

      // set Owner and update Task
      taskRepresentationModel.setOwner("dummyuser");
      HttpEntity<TaskRepresentationModel> auth2 =
          new HttpEntity<>(taskRepresentationModel, RestHelper.generateHeadersForUser("user-1-2"));

      ThrowingCallable httpCall =
          () -> TEMPLATE.exchange(url, HttpMethod.PUT, auth2, TASK_MODEL_TYPE);

      assertThatThrownBy(httpCall)
          .isInstanceOf(HttpStatusCodeException.class)
          .hasMessageContaining("400");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class TransferTasks {
    @TestFactory
    Stream<DynamicTest> should_SetTransferFlagAndOwnerDependentOnBody_When_TransferringTask() {
      Iterator<Pair<Boolean, String>> iterator =
          Arrays.asList(Pair.of(false, "user-1-1"), Pair.of(true, "user-1-1")).iterator();
      String url =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
              "TKI:000000000000000000000000000000000003",
              "WBI:100000000000000000000000000000000006");

      ThrowingConsumer<Pair<Boolean, String>> test =
          pair -> {
            HttpEntity<Object> auth =
                new HttpEntity<>(
                    new TransferTaskRepresentationModel(pair.getLeft(), pair.getRight(), null),
                    RestHelper.generateHeadersForUser("admin"));
            ResponseEntity<TaskRepresentationModel> response =
                TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getWorkbasketSummary().getWorkbasketId())
                .isEqualTo("WBI:100000000000000000000000000000000006");
            assertThat(response.getBody().isTransferred()).isEqualTo(pair.getLeft());
            assertThat(response.getBody().getOwner()).isEqualTo(pair.getRight());
          };

      return DynamicTest.stream(iterator, c -> "for setTransferFlag: " + c, test);
    }

    @Test
    void should_SetTransferFlagToTrueAndOwnerToNull_When_TransferringWithoutRequestBody() {
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
      assertThat(response.getBody().getOwner()).isNull();
    }

    @TestFactory
    Stream<DynamicTest> should_ReturnFailedTasks_When_TransferringTasks() {

      Iterator<Pair<Boolean, String>> iterator =
          Arrays.asList(Pair.of(true, "user-1-1"), Pair.of(false, "user-1-2")).iterator();
      String url =
          restHelper.toUrl(
              RestEndpoints.URL_TRANSFER_WORKBASKET_ID, "WBI:100000000000000000000000000000000006");

      List<String> taskIds =
          Arrays.asList(
              "TKI:000000000000000000000000000000000003",
              "TKI:000000000000000000000000000000000004",
              "TKI:000000000000000000000000000000000039");

      ThrowingConsumer<Pair<Boolean, String>> test =
          pair -> {
            HttpEntity<Object> auth =
                new HttpEntity<>(
                    new TransferTaskRepresentationModel(pair.getLeft(), pair.getRight(), taskIds),
                    RestHelper.generateHeadersForUser("admin"));
            ResponseEntity<Map<String, Object>> response =
                TEMPLATE.exchange(url, HttpMethod.POST, auth, BULK_RESULT_TASKS_MODEL_TYPE);

            assertThat(response.getBody()).isNotNull();
            Map<String, LinkedHashMap> failedTasks =
                (Map<String, LinkedHashMap>) response.getBody().get("tasksWithErrors");
            assertThat(failedTasks).hasSize(1);
            assertThat(failedTasks).containsKey("TKI:000000000000000000000000000000000039");
            String errorName =
                (String) failedTasks.get("TKI:000000000000000000000000000000000039").get("key");
            assertThat(errorName).isEqualTo("TASK_INVALID_STATE");
            LinkedHashMap messageVariables =
                (LinkedHashMap)
                    failedTasks
                        .get("TKI:000000000000000000000000000000000039")
                        .get("messageVariables");
            assertThat((List) messageVariables.get("requiredTaskStates"))
                .containsExactly("READY", "CLAIMED", "READY_FOR_REVIEW", "IN_REVIEW");
            assertThat(messageVariables).containsEntry("taskState", "COMPLETED");
            assertThat(messageVariables)
                .containsEntry("taskId", "TKI:000000000000000000000000000000000039");
          };
      return DynamicTest.stream(iterator, c -> "for setTransferFlag and owner: " + c, test);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class RequestChangesOnTasks {

    @Test
    void should_RequestChangesOnATask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000136");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.IN_REVIEW);
      assertThat(repModel.getOwner()).isEqualTo("user-1-1");

      // request changes
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES,
              "TKI:000000000000000000000000000000000136");
      ResponseEntity<TaskRepresentationModel> requestedChangesResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(requestedChangesResponse.getBody()).isNotNull();
      assertThat(requestedChangesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = requestedChangesResponse.getBody();
      assertThat(repModel.getOwner()).isNull();
      assertThat(repModel.getState()).isEqualTo(TaskState.READY);
    }

    @Test
    void should_ForceRequestChanges_When_CurrentUserIsNotTheOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000100");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(repModel.getOwner()).isEqualTo("user-1-2");

      // request changes
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES_FORCE,
              "TKI:000000000000000000000000000000000100");
      ResponseEntity<TaskRepresentationModel> requestedChangesResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(requestedChangesResponse.getBody()).isNotNull();
      assertThat(requestedChangesResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = requestedChangesResponse.getBody();
      assertThat(repModel.getOwner()).isNull();
      assertThat(repModel.getState()).isEqualTo(TaskState.READY);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class RequestReviewOnTasks {

    @Test
    void should_RequestReviewOnATask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000035");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(repModel.getOwner()).isEqualTo("user-1-1");

      // request review
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW,
              "TKI:000000000000000000000000000000000035");
      ResponseEntity<TaskRepresentationModel> requestReviewResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(requestReviewResponse.getBody()).isNotNull();
      assertThat(requestReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = requestReviewResponse.getBody();
      assertThat(repModel.getOwner()).isNull();
      assertThat(repModel.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    }

    @Test
    void should_ForceRequestReview_When_CurrentUserIsNotTheOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000101");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(repModel.getOwner()).isEqualTo("user-1-2");

      // request review
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW_FORCE,
              "TKI:000000000000000000000000000000000101");
      ResponseEntity<TaskRepresentationModel> requestReviewResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(requestReviewResponse.getBody()).isNotNull();
      assertThat(requestReviewResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = requestReviewResponse.getBody();
      assertThat(repModel.getOwner()).isNull();
      assertThat(repModel.getState()).isEqualTo(TaskState.READY_FOR_REVIEW);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CompleteTasks {
    @Test
    void should_CompleteTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000102");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(repModel.getOwner()).isEqualTo("user-1-2");

      // complete Task
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_COMPLETE, "TKI:000000000000000000000000000000000102");
      ResponseEntity<TaskRepresentationModel> completeResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(completeResponse.getBody()).isNotNull();
      assertThat(completeResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = completeResponse.getBody();
      assertThat(repModel.getOwner()).isEqualTo("user-1-2");
      assertThat(repModel.getState()).isEqualTo(TaskState.COMPLETED);
    }

    @Test
    void should_ForceCompleteTask_When_CurrentUserIsNotTheOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000028");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel repModel = getTaskResponse.getBody();
      assertThat(repModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(repModel.getOwner()).isEqualTo("user-1-1");

      // force complete task
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_COMPLETE_FORCE,
              "TKI:000000000000000000000000000000000028");
      ResponseEntity<TaskRepresentationModel> forceCompleteResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(forceCompleteResponse.getBody()).isNotNull();
      assertThat(forceCompleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      repModel = forceCompleteResponse.getBody();
      assertThat(repModel.getOwner()).isEqualTo("user-1-2");
      assertThat(repModel.getState()).isEqualTo(TaskState.COMPLETED);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CancelTasks {

    @Test
    void should_CancelTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000103");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(responseGet.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = responseGet.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);

      // cancel the task
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CANCEL, "TKI:000000000000000000000000000000000103");
      ResponseEntity<TaskRepresentationModel> cancelResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(cancelResponse.getBody()).isNotNull();
      assertThat(cancelResponse.getBody().getState()).isEqualTo(TaskState.CANCELLED);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class TerminateTasks {
    @Test
    void should_TerminateTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(responseGet.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = responseGet.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);

      // terminate the task
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_TERMINATE, "TKI:000000000000000000000000000000000103");
      ResponseEntity<TaskRepresentationModel> terminateResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(terminateResponse.getBody()).isNotNull();
      assertThat(terminateResponse.getBody().getState()).isEqualTo(TaskState.TERMINATED);
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ClaimTasks {

    @Test
    void should_ClaimTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000033");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel readyTaskRepresentationModel = getTaskResponse.getBody();
      assertThat(readyTaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
      assertThat(readyTaskRepresentationModel.getOwner()).isEqualTo("user-1-2");

      // claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000033");
      ResponseEntity<TaskRepresentationModel> claimResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(claimResponse.getBody()).isNotNull();
      assertThat(claimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel claimedTaskRepresentationModel = claimResponse.getBody();
      assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo("user-1-2");
      assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    }

    @Test
    void should_ForceClaim_When_TaskIsClaimedByDifferentOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000029");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel readyTaskRepresentationModel = getTaskResponse.getBody();
      assertThat(readyTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(readyTaskRepresentationModel.getOwner()).isEqualTo("user-1-2");

      // claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM_FORCE, "TKI:000000000000000000000000000000000029");
      ResponseEntity<TaskRepresentationModel> claimResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(claimResponse.getBody()).isNotNull();
      assertThat(claimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel claimedTaskRepresentationModel = claimResponse.getBody();
      assertThat(claimedTaskRepresentationModel.getOwner()).isEqualTo("user-1-1");
      assertThat(claimedTaskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
    }

    @Test
    void should_SelectAndClaimTasks() {
      String url = restHelper.toUrl(RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM + "?custom14=abc");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

      ResponseEntity<TaskRepresentationModel> response =
          TEMPLATE.exchange(url, HttpMethod.POST, auth, TASK_MODEL_TYPE);

      assertThat(response.getBody()).isNotNull();

      String url2 = restHelper.toUrl(RestEndpoints.URL_TASKS_ID, response.getBody().getTaskId());
      ResponseEntity<TaskRepresentationModel> responseGetTask =
          TEMPLATE.exchange(url2, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(responseGetTask).isNotNull();
      assertThat(responseGetTask.getBody().getOwner()).isEqualTo("admin");
    }
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class CancelClaimTasks {
    @Test
    void should_CancelClaimTask() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000032");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-2");

      // cancel claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000032");
      ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
          TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(cancelClaimResponse.getBody()).isNotNull();
      assertThat(cancelClaimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
      assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    }

    @Test
    void should_KeepOwnerAndOwnerLongName_When_CancelClaimWithKeepOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000000");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-1");

      // cancel claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM + "?keepOwner=true",
              "TKI:000000000000000000000000000000000000");
      ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
          TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(cancelClaimResponse.getBody()).isNotNull();
      assertThat(cancelClaimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
      assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
      assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isEqualTo("user-1-1");
      assertThat(cancelClaimedtaskRepresentationModel.getOwnerLongName())
          .isEqualTo("Mustermann, Max - (user-1-1)");
    }

    @Test
    void should_KeepOwnerAndOwnerLongName_When_ForceCancelClaimWithKeepOwner() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000001");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-1");

      // cancel claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM_FORCE + "?keepOwner=true",
              "TKI:000000000000000000000000000000000001");
      ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
          TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(cancelClaimResponse.getBody()).isNotNull();
      assertThat(cancelClaimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
      assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
      assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isEqualTo("user-1-1");
      assertThat(cancelClaimedtaskRepresentationModel.getOwnerLongName())
          .isEqualTo("Mustermann, Max - (user-1-1)");
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
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-2");

      // force cancel claim
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_CLAIM_FORCE, "TKI:000000000000000000000000000000000027");
      ResponseEntity<TaskRepresentationModel> cancelClaimResponse =
          TEMPLATE.exchange(url2, HttpMethod.DELETE, auth, TASK_MODEL_TYPE);

      assertThat(cancelClaimResponse.getBody()).isNotNull();
      assertThat(cancelClaimResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel cancelClaimedtaskRepresentationModel = cancelClaimResponse.getBody();
      assertThat(cancelClaimedtaskRepresentationModel.getOwner()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getClaimed()).isNull();
      assertThat(cancelClaimedtaskRepresentationModel.getState()).isEqualTo(TaskState.READY);
    }

    @Test
    void should_ThrowException_When_CancelClaimingOfClaimedTaskByAnotherUser() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000026");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> responseGet =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);

      assertThat(responseGet.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = responseGet.getBody();
      assertThat(taskRepresentationModel.getState()).isEqualTo(TaskState.CLAIMED);
      assertThat(taskRepresentationModel.getOwner()).isEqualTo("user-1-1");

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
  }

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class SetTasksRead {
    @Test
    void should_setTaskRead() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000025");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.isRead()).isFalse();

      // set Task read
      HttpEntity<Object> httpEntity =
          new HttpEntity<>(
              new IsReadRepresentationModel(true), RestHelper.generateHeadersForUser("user-1-2"));
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_SET_READ, "TKI:000000000000000000000000000000000025");
      ResponseEntity<TaskRepresentationModel> setReadResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, httpEntity, TASK_MODEL_TYPE);

      assertThat(setReadResponse.getBody()).isNotNull();
      assertThat(setReadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel setReadTaskRepresentationModel = setReadResponse.getBody();
      assertThat(setReadTaskRepresentationModel.isRead()).isTrue();
    }

    @Test
    void should_setTaskUnread() {
      String url =
          restHelper.toUrl(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000027");
      HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-2"));

      // retrieve task from Rest Api
      ResponseEntity<TaskRepresentationModel> getTaskResponse =
          TEMPLATE.exchange(url, HttpMethod.GET, auth, TASK_MODEL_TYPE);
      assertThat(getTaskResponse.getBody()).isNotNull();
      TaskRepresentationModel taskRepresentationModel = getTaskResponse.getBody();
      assertThat(taskRepresentationModel.isRead()).isTrue();

      // set Task unread
      HttpEntity<Object> httpEntity =
          new HttpEntity<>(
              new IsReadRepresentationModel(false), RestHelper.generateHeadersForUser("user-1-2"));
      String url2 =
          restHelper.toUrl(
              RestEndpoints.URL_TASKS_ID_SET_READ, "TKI:000000000000000000000000000000000027");
      ResponseEntity<TaskRepresentationModel> setUnreadResponse =
          TEMPLATE.exchange(url2, HttpMethod.POST, httpEntity, TASK_MODEL_TYPE);

      assertThat(setUnreadResponse.getBody()).isNotNull();
      assertThat(setUnreadResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
      TaskRepresentationModel setReadTaskRepresentationModel = setUnreadResponse.getBody();
      assertThat(setReadTaskRepresentationModel.isRead()).isFalse();
    }
  }
}
