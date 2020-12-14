package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate REST Docu for the TaskanaEngineController. */
class TaskanaEngineControllerRestDocumentation extends BaseRestDocumentation {

  private FieldDescriptor[] allDomainsFieldDescriptors;
  private FieldDescriptor[] allClassificationCategoriesFieldDescriptors;
  private FieldDescriptor[] allClassificationTypesFieldDescriptors;
  private FieldDescriptor[] currentUserInfoFieldDescriptors;

  @BeforeEach
  void setUp() {

    allDomainsFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("[]").description("An array with the domain-names as strings")
        };

    allClassificationCategoriesFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("[]").description("An array with the classification-categories as strings")
        };

    allClassificationTypesFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("[]").description("An array with the classification-types as strings")
        };

    currentUserInfoFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("userId").description("Id of the current user"),
          fieldWithPath("groupIds")
              .description("An array with the groups the current user is part of as strings"),
          fieldWithPath("roles").description("An array with the roles the current user is granted")
        };
  }

  @Test
  void getAllDomainsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(restHelper.toUrl(RestEndpoints.URL_DOMAIN))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllDomainsDocTest", responseFields(allDomainsFieldDescriptors)));
  }

  @Test
  void getAllClassificationCategoriesDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_CATEGORIES))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllClassificationCategoriesDocTest",
                responseFields(allClassificationCategoriesFieldDescriptors)));
  }

  @Test
  void getAllClassificationTypesDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_TYPES))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllClassificationTypesDocTest",
                responseFields(allClassificationTypesFieldDescriptors)));
  }

  @Test
  void getCurrentUserInfo() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(restHelper.toUrl(RestEndpoints.URL_CURRENT_USER))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetCurrentUserInfoDocTest", responseFields(currentUserInfoFieldDescriptors)));
  }

  @Test
  void getHistoryProviderIsEnabled() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_HISTORY_ENABLED))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetHistoryProviderIsEnabled"));
  }
}
