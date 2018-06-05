package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
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
public class MonitorControllerRestDocumentation {

    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    private FieldDescriptor[] taskStatusReportFieldDescriptors;
    private FieldDescriptor[] countByStateFieldDescriptors;
    
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
        
        taskStatusReportFieldDescriptors = new FieldDescriptor[] {
                fieldWithPath("meta").description("Object holding metainfo on the report"),
                fieldWithPath("meta.name").description("Name of the report"),
                fieldWithPath("meta.date").description("Date of the report creation"),
                fieldWithPath("meta.header").description("Column-headers of the report"),
                fieldWithPath("meta.rowDesc").description("Descriptions for the rows the report"),
                fieldWithPath("meta.totalDesc").description("Description for the report itself"),
                subsectionWithPath("rows").description("Object holding the rows of the report.\n"
                        + "For the exact structure please check the example response above"),
                fieldWithPath("sumRow").description("Object holding the sums in the columns over all rows"),
                subsectionWithPath("sumRow.cells").description("Contains the accumulated numbers over all columns defined in meta.header.\n"
                        + "For the exact structure please check the example response above"),
                fieldWithPath("sumRow.total").description("Total number of tasks"),
                fieldWithPath("_links.self.href").ignored()
        };
        
        countByStateFieldDescriptors = new FieldDescriptor[] {
                fieldWithPath("[]..state").description("The state the tasks are in"),
                fieldWithPath("[]..counter").description("Number of tasks in the corresponding state")
        };
    }
    
    @Test
    public void getTaskStatusReport() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/monitor/taskStatusReport")
                .header("Authorization", "Basic YWRtaW46YWRtaW4="))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetTaskStatusReportDocTest",
                responseFields(taskStatusReportFieldDescriptors)));
    }
    
    @Test
    public void getCountByState() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/monitor/countByState?states=READY,CLAIMED,COMPLETED")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetCountByStateDocTest",
                responseFields(countByStateFieldDescriptors)));
    }
}
