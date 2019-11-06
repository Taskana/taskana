package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * Generate REST Docu for the TaskanaEngineController.
 *
 */
class TaskanaEngineControllerRestDocumentation extends BaseRestDocumentation {

    private FieldDescriptor[] allDomainsFieldDescriptors;
    private FieldDescriptor[] allClassificationCategoriesFieldDescriptors;
    private FieldDescriptor[] allClassificationTypesFieldDescriptors;
    private FieldDescriptor[] currentUserInfoFieldDescriptors;

    @BeforeEach
    void setUp() {

        allDomainsFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("[]").description("An array with the domain-names as strings")
        };

        allClassificationCategoriesFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("[]").description("An array with the classification-categories as strings")
        };

        allClassificationTypesFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("[]").description("An array with the classification-types as strings")
        };

        currentUserInfoFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("userId").description("Id of the current user"),
            fieldWithPath("groupIds").description("An array with the groups the current user is part of as strings"),
            fieldWithPath("roles").description("An array with the roles the current user is granted")
        };
    }

    @Test
    void getAllDomainsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/domains")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetAllDomainsDocTest",
                responseFields(allDomainsFieldDescriptors)));
    }

    @Test
    void getAllClassificationCategoriesDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/classification-categories")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetAllClassificationCategoriesDocTest",
                responseFields(allClassificationCategoriesFieldDescriptors)));
    }

    @Test
    void getAllClassificationTypesDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/classification-types")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetAllClassificationTypesDocTest",
                responseFields(allClassificationTypesFieldDescriptors)));
    }

    @Test
    void getCurrentUserInfo() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/current-user-info")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetCurrentUserInfoDocTest",
                responseFields(currentUserInfoFieldDescriptors)));
    }

    @Test
    void getHistoryProviderIsEnabled() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/history-provider-enabled")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetHistoryProviderIsEnabled"));
    }
}
