package pro.taskana.monitor.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.BaseRestDocTest;

class MonitorControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getTaskStatusReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TASKS_STATUS_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getWorkbasketReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_MONITOR_TASKS_WORKBASKET_REPORT
                    + "?daysInPast=4&states=READY,CLAIMED,COMPLETED"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getClassificationReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TASKS_CLASSIFICATION_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getTimestampReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
