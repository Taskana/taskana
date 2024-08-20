package io.kadai.monitor.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kadai.common.rest.RestEndpoints;
import io.kadai.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import io.kadai.monitor.rest.models.ReportRepresentationModel;
import io.kadai.monitor.rest.models.ReportRepresentationModel.RowRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

/** Test MonitorController. */
@KadaiSpringBootTest
class MonitorControllerIntTest {

  private final RestHelper restHelper;
  private final ObjectMapper objectMapper;

  @Autowired
  MonitorControllerIntTest(RestHelper restHelper, ObjectMapper objectMapper) {
    this.restHelper = restHelper;
    this.objectMapper = objectMapper;
  }

  @Test
  void should_ReturnAllOpenTasksByState_When_QueryingForAWorkbasketAndReadyAndClaimedState() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT)
            + "?workbasket-id=WBI:100000000000000000000000000000000007"
            + "&state=READY&state=CLAIMED";
    HttpEntity<String> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    int totalOpenTasks = response.getBody().getSumRow().get(0).getTotal();
    assertThat(totalOpenTasks).isEqualTo(15);
    int[] tasksPerState = response.getBody().getSumRow().get(0).getCells();
    // should be 2 READY, 13 CLAIMED
    int[] expectedTasksPerState = new int[] {2, 13};
    assertThat(tasksPerState).isEqualTo(expectedTasksPerState);
  }

  @Test
  void should_ReturnAllOpenTasksByState_When_QueryingForSpecificWbAndStateReadyAndMinimumPrio() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url
                + "?workbasket-id=WBI:100000000000000000000000000000000007"
                + "&state=READY&priority-minimum=1",
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();
    int[] tasksInStateReady = response.getBody().getSumRow().get(0).getCells();
    // should be 2 READY
    assertThat(tasksInStateReady).isEqualTo(new int[] {2});
  }

  @Test
  void should_ApplyAllFiltersAndComputeReport_When_QueryingForAWorkbasketReport() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_REPORT)
            + "?workbasket-id=WBI:100000000000000000000000000000000015"
            + "&custom-3=abcd"
            + "&custom-3=abbd"
            + "&custom-3-not-in=abbb"
            + "&custom-4=defg"
            + "&custom-5=important";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    ReportRepresentationModel report = response.getBody();

    assertThat(report).isNotNull();

    assertThat(report.getSumRow())
        .extracting(RowRepresentationModel::getCells)
        .containsExactly(new int[] {0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0});
  }

  @Test
  void should_ApplyStateFilterAndComputeReport_When_QueryingForAWorkbasketReport() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_REPORT)
            + "?workbasket-id=WBI:100000000000000000000000000000000008"
            + "&state=READY"
            + "&state=CLAIMED";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    ReportRepresentationModel report = response.getBody();
    assertThat(report).isNotNull();
    assertThat(report.getSumRow())
        .extracting(RowRepresentationModel::getCells)
        // expecting 4 tasks due tomorrow (RELATIVE_DATE(1))
        .containsExactly(new int[] {0, 0, 0, 0, 0, 0, 0, 4, 0, 0, 0, 0, 0});
  }

  @Test
  void should_ComputeWorkbasketPriorityReport_When_QueryingForAWorkbasketPriorityReport() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT)
            + "?workbasket-type=TOPIC&workbasket-type=GROUP";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    ReportRepresentationModel report = response.getBody();

    assertThat(report).isNotNull();

    assertThat(report.getSumRow()).extracting(RowRepresentationModel::getTotal).containsExactly(28);
  }

  @Test
  void should_ApplyComplexFilterCombination_When_QueryingForAWorkbasketPriorityReport() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT)
            + "?workbasket-type=TOPIC&workbasket-type=GROUP"
            + "&state=READY&state=CLAIMED"
            + "&custom-6=074&custom-6=075"
            + "&custom-7-not-in=20";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    ReportRepresentationModel report = response.getBody();
    assertThat(report).isNotNull();
    assertThat(report.getSumRow())
        .extracting(RowRepresentationModel::getCells)
        .containsExactly(new int[] {2, 0, 1});
  }

  @Test
  void should_DetectPriorityColumnHeader_When_HeaderIsPassedAsQueryParameter() throws Exception {
    PriorityColumnHeaderRepresentationModel columnHeader =
        new PriorityColumnHeaderRepresentationModel(10, 20);

    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT)
            + "?columnHeader="
            + URLEncoder.encode(
                objectMapper.writeValueAsString(columnHeader), StandardCharsets.UTF_8);

    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    ReportRepresentationModel report = response.getBody();
    assertThat(report).isNotNull();
    assertThat(report.getMeta().getHeader()).containsExactly("10 - 20");
  }

  @Test
  void should_ReturnBadRequest_When_PriorityColumnHeaderIsNotAValidJson() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT)
            + "?columnHeader=invalidJson";

    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("monitor"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThatThrownBy(httpCall).isInstanceOf(BadRequest.class);
  }

  @Test
  void should_ComputeClassificationCategoryReport() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_CLASSIFICATION_CATEGORY_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    String[][] descArray = {{"AUTOMATIC"}, {"EXTERN"}, {"MANUAL"}};
    assertThat(response.getBody()).isNotNull();
    assertThat(
            response.getBody().getRows().stream()
                .map(RowRepresentationModel::getDesc)
                .collect(Collectors.toList()))
        .hasSize(3)
        .containsExactlyInAnyOrder(descArray);
  }

  @Test
  void should_ComputeClassificationReport() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_CLASSIFICATION_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
    assertThat(
            response.getBody().getRows().stream()
                .map(RowRepresentationModel::getDesc)
                .collect(Collectors.toList()))
        .hasSize(6);
  }

  @Test
  void should_ComputeDetailedClassificationReport() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_DETAILED_CLASSIFICATION_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void should_ComputeTaskStatusReport() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void should_ComputeTimestampReport() {
    String url = restHelper.toUrl(RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT);
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    assertThat(response.getBody()).isNotNull();
  }

  @Test
  void should_ComputeTaskCustomFieldValueReport() {
    String url =
        restHelper.toUrl(
            RestEndpoints.URL_MONITOR_TASK_CUSTOM_FIELD_VALUE_REPORT + "?custom-field=CUSTOM_14");
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("admin"));

    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));

    String[][] descArray = {{"abc"}, {"dde"}, {"ert"}};

    assertThat(response.getBody()).isNotNull();
    assertThat(
            response.getBody().getRows().stream()
                .map(RowRepresentationModel::getDesc)
                .collect(Collectors.toList()))
        .hasSize(3)
        .containsExactlyInAnyOrder(descArray);
  }
}
