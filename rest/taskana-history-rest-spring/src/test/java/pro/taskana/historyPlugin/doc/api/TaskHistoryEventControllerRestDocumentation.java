package pro.taskana.historyPlugin.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

import pro.taskana.historyPlugin.config.TaskHistoryRestConfiguration;
import pro.taskana.rest.RestConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RestConfiguration.class, TaskHistoryRestConfiguration.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskHistoryEventControllerRestDocumentation {

    @LocalServerPort
    int port;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private HashMap<String, String> taskHistoryEventFieldDescriptionsMap = new HashMap<String, String>();

    private FieldDescriptor[] allTaskHistoryEventFieldDescriptors;
    private FieldDescriptor[] taskHistoryEventFieldDescriptors;

    @Before
    public void setUp() {
        document("{methodName}",
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()));

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
            .apply(documentationConfiguration(this.restDocumentation)
                .operationPreprocessors()
                .withResponseDefaults(prettyPrint())
                .withRequestDefaults(prettyPrint()))
            .build();

        taskHistoryEventFieldDescriptionsMap.put("_embedded.taskHistoryId","Unique ID");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.businessProcessId","The id of the business process");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.parentBusinessProcessId","The id of the parent business process");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.taskId","The id of the task");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.eventType","The type of the event");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.created","The time was created");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.userId","The id of the user");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.domain","Domain");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.workbasketKey","The key of workbasket");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.porCompany","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.porSystem","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.porInstance","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.porValue","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.porType","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.taskClassificationKey","The key of classification task");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.taskClassificationCategory","The category of classification");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.attachmentClassificationKey","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.comment","");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.oldValue","The old value");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.newValue","The new value");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.custom1","A custom property with name \"1\"");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.custom2","A custom property with name \"2\"");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.custom3","A custom property with name \"3\"");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.custom4","A custom property with name \"4\"");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.oldData","The old data");
        taskHistoryEventFieldDescriptionsMap.put("_embedded.newData","The new data");
        taskHistoryEventFieldDescriptionsMap.put("_links.self.href","The links of this task history event");
        taskHistoryEventFieldDescriptionsMap.put("_links.allTaskHistoryEvent.href","Link to all task history event");
        taskHistoryEventFieldDescriptionsMap.put("_links.first.href","Link to the first result");
        taskHistoryEventFieldDescriptionsMap.put("_links.last.href","Link to the last result");

        allTaskHistoryEventFieldDescriptors = new FieldDescriptor[] {
            subsectionWithPath("_embedded.taskHistoryEventResourceList").description("An array of Task history event"),
            fieldWithPath("_links.self.href").ignored(),
            fieldWithPath("page").ignored()
        };

        taskHistoryEventFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("_embedded.taskHistoryEventResourceList[].taskHistoryId").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.taskHistoryId")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].businessProcessId").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.businessProcessId")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].parentBusinessProcessId").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.parentBusinessProcessId")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].taskId").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.taskId")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].eventType").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.eventType")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].created").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.created")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].userId").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.userId")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].domain").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.domain")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].workbasketKey").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.workbasketKey")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].porCompany").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.porCompany")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].porSystem").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.porSystem")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].porInstance").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.porInstance")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].porValue").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.porValue")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].porType").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.porType")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].taskClassificationKey").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.taskClassificationKey")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].taskClassificationCategory").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.taskClassificationCategory")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].attachmentClassificationKey").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.attachmentClassificationKey")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].comment").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.comment")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].oldValue").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.oldValue")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].newValue").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.newValue")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].custom1").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.custom1")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].custom2").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.custom2")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].custom3").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.custom3")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].custom4").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.custom4")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].oldData").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.oldData")),
            fieldWithPath("_embedded.taskHistoryEventResourceList[].newData").description(taskHistoryEventFieldDescriptionsMap.get("_embedded.newData")),
            fieldWithPath("_links.self.href").ignored(),
            fieldWithPath("page").ignored()
        };
    }

    @Test
    public void getAllTaskHistoryEventDocTest() throws Exception {
        this.mockMvc.perform(
            RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/task-history-event")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetAllTaskHistoryEventDocTest",
                responseFields(allTaskHistoryEventFieldDescriptors)));
    }

    @Test
    public void getSpecificTaskHistoryEventDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get(
            "http://127.0.0.1:" + port + "/v1/task-history-event?business-process-id=BPI:02")
            .accept("application/hal+json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetSpecificTaskHistoryEventDocTest",
                responseFields(taskHistoryEventFieldDescriptors)));
    }
}
