package pro.taskana.common.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.rest.test.BaseRestDocTest;

@Disabled
class AccessIdControllerRestDocTest extends BaseRestDocTest {

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

  @Test
  void searchUsersByNameOrAccessIdForRoleTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID_WITH_NAME + "?search-for=user-1&role=user"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
