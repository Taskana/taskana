package pro.taskana.common.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.test.BaseRestDocTest;

public class AccessIdControllerRestDocTest extends BaseRestDocTest {

  @Test
  void searchForAccessIdDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID + "?search-for=max"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getGroupsForAccessIdDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID_GROUPS + "?access-id=teamlead-1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
