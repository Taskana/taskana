package pro.taskana.common.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.test.BaseRestDocTest;

class TaskanaEngineControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getAllDomainsDocTest() throws Exception {
    mockMvc.perform(get(RestEndpoints.URL_DOMAIN)).andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getClassificationCategoriesDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATION_CATEGORIES))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getClassificationTypesDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATION_TYPES))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getClassificationCategoriesByTypeMapDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATION_CATEGORIES_BY_TYPES))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getCurrentUserInfoDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CURRENT_USER))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getHistoryProviderIsEnabledDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_HISTORY_ENABLED))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getCurrentVersionDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_VERSION))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
