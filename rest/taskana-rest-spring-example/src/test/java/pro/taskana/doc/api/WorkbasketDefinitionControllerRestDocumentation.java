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
import org.junit.Ignore;
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
public class WorkbasketDefinitionControllerRestDocumentation {
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    private FieldDescriptor[] workbasketdefinitionsFieldDescriptors;
    
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

        
        workbasketdefinitionsFieldDescriptors = new FieldDescriptor[] {
                subsectionWithPath("[]").description("An array of <<workbasket, workbaskets>>")
        };
    }
    
    @Test
    public void getAllWorkbasketDefinitions() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/workbasketdefinitions")
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllWorkbasktdefinitionsDocTest",
                responseFields(workbasketdefinitionsFieldDescriptors)));
    }
    
    @Test
    public void importWorkbasketdefinition() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/workbasketdefinitions/import")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .contentType("application/json")
                .content("["
                        + "{"
                        + "\"distributionTargets\":[], "
                        + "\"authorizations\":[], "
                        + "\"workbasketResource\":"
                        + "{\"name\":\"ich\", \"key\":\"neuerKeyXy\", \"domain\":\"DOMAIN_A\", \"type\":\"GROUP\","
                        + "\"created\":\"2018-02-01T11:00:00Z\", \"modified\":\"2018-02-01T11:00:00Z\", \"workbasketId\":\"gibtsNed\"}"
                        + "}"
                        + "]"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("ImportWorkbasketdefinitions",
                requestFields(subsectionWithPath("[]").description("An array of <<workbasket, workbaskets>>"))));
    }
}
