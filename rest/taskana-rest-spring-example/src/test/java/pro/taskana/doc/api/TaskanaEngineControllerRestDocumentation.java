package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import pro.taskana.rest.RestConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaskanaEngineControllerRestDocumentation {
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    private FieldDescriptor[] allDomainsFieldDescriptors;
    private FieldDescriptor[] allClassificationCategoriesFieldDescriptors;
    private FieldDescriptor[] allClassificationTypesFieldDescriptors;
    private FieldDescriptor[] currentUserInfoFieldDescriptors;
    
    @Before
    public void setUp() {
        document("{methodName}",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));
        
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(springSecurity())
                .apply(documentationConfiguration(this.restDocumentation)
                        .operationPreprocessors()
                        .withResponseDefaults(prettyPrint())
                        .withRequestDefaults(prettyPrint()))
                        .build(); 
        
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
    public void getAllDomainsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/domains")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllDomainsDocTest",
                responseFields(allDomainsFieldDescriptors)));
    }
    
    @Test
    public void getAllClassificationCategoriesDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/classification-categories")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllClassificationCategoriesDocTest",
                responseFields(allClassificationCategoriesFieldDescriptors)));
    }
    
    @Test
    public void getAllClassificationTypesDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/classification-types")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllClassificationTypesDocTest",
                responseFields(allClassificationTypesFieldDescriptors)));
    }
    
    @Test
    public void getCurrentUserInfo() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/current-user-info")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetCurrentUserInfoDocTest",
                responseFields(currentUserInfoFieldDescriptors)));
    }
}
