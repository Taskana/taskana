package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
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
public class ClassificationDefinitionControllerRestDocumentation {
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    private FieldDescriptor[] classificationdefinitionsFieldDescriptors;
    
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
        
        classificationdefinitionsFieldDescriptors = new FieldDescriptor[] {
                subsectionWithPath("[]").description("An array of <<classification, classifications>>")
        };
    }
    
    @Test
    public void getAllClassificationdefinitions() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/classificationdefinitions")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllClassificationdefinitionsDocTest",
                responseFields(classificationdefinitionsFieldDescriptors)));
    }
    
    @Test
    public void importClassificationdefinition() throws Exception {
        String definitionString = "[{\"key\":\"Key0815\", \"domain\":\"DOMAIN_B\"}]";
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/classificationdefinitions/import")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .contentType("application/json")
                .content(definitionString))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("ImportClassificationdefinitions",
                requestFields(subsectionWithPath("[]").description("An array of <<classification-definitions, classifications>>"))));
    }
}
