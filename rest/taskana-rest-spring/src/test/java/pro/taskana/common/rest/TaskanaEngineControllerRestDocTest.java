package pro.taskana.common.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.models.CustomAttributesRepresentationModel;
import pro.taskana.rest.test.BaseRestDocTest;

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

  @Test
  void getCustomConfigurationAttributesDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CUSTOM_ATTRIBUTES))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void setCustomConfigurationAttributesDocTest() throws Exception {
    CustomAttributesRepresentationModel customAttributes2 =
        new CustomAttributesRepresentationModel(
            Map.of(
                "filter",
                "{ \"Tasks with state READY\": { \"state\": [\"READY\"]}, "
                    + "\"Tasks with state CLAIMED\": {\"state\": [\"CLAIMED\"] }}",
                "schema",
                Map.of(
                    "Filter",
                    Map.of(
                        "displayName",
                        "Filter for Task-Priority-Report",
                        "members",
                        Map.of(
                            "filter",
                            Map.of("displayName", "Filter values", "type", "json", "min", "1"))))));

    mockMvc
        .perform(
            put(RestEndpoints.URL_CUSTOM_ATTRIBUTES)
                .content(objectMapper.writeValueAsString(customAttributes2)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
