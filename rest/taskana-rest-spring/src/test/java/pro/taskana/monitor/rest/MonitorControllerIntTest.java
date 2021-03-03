package pro.taskana.monitor.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.monitor.rest.models.ReportRepresentationModel;

/** Test MonitorController. */
@TaskanaSpringBootTest
class MonitorControllerIntTest {

  static RestTemplate template = RestHelper.TEMPLATE;
  @Autowired RestHelper restHelper;

  @Test
  void
      should_ReturnAllOpenTasksByState_When_QueryingForSpecificWorkbasketAndReadyAndClaimedState() {

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_MONITOR_TASKS_STATUS_REPORT)
                + "?workbasket-ids=WBI:100000000000000000000000000000000007&states=READY,CLAIMED",
            HttpMethod.GET,
            request,
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

    HttpEntity<String> request = new HttpEntity<>(restHelper.getHeadersAdmin());
    ResponseEntity<ReportRepresentationModel> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_MONITOR_TASKS_STATUS_REPORT)
                + "?workbasket-ids=WBI:100000000000000000000000000000000007"
                + "&states=READY&priority-minimum=1",
            HttpMethod.GET,
            request,
            ParameterizedTypeReference.forType(ReportRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat((response.getBody()).getLink(IanaLinkRelations.SELF)).isNotNull();

    int[] tasksInStateReady = response.getBody().getSumRow().get(0).getCells();
    // should be 2 READY
    int[] expectedTasksPerState = new int[] {2};
    assertThat(tasksInStateReady).isEqualTo(expectedTasksPerState);
  }
}
