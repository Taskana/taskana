package pro.taskana.simplehistory.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.rest.test.BaseRestDocTest;

@Disabled
class TaskHistoryEventControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getAllTaskHistoryEventsDocTest() throws Exception {
    mockMvc
        .perform(get(HistoryRestEndpoints.URL_HISTORY_EVENTS + "?page=1&page-size=3"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getSpecificTaskHistoryEventDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                HistoryRestEndpoints.URL_HISTORY_EVENTS_ID,
                "THI:000000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
