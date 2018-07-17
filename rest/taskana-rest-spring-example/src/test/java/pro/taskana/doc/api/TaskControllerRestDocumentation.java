package pro.taskana.doc.api;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pro.taskana.rest.RestConfiguration;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class TaskControllerRestDocumentation {
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    private HashMap<String, String> taskFieldDescriptionsMap = new HashMap<String, String>();
    
    private FieldDescriptor[] allTasksFieldDescriptors;
    private FieldDescriptor[] taskFieldDescriptors;
    private FieldDescriptor[] taskSubsetFieldDescriptors;  
    private FieldDescriptor[] createTaskFieldDescriptors;
    
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
        
        taskFieldDescriptionsMap.put("taskId", "Unique ID");
        taskFieldDescriptionsMap.put("created", "The creation timestamp of the task in the system.");
        taskFieldDescriptionsMap.put("claimed", "The timestamp of the last claim-operation on the task");
        taskFieldDescriptionsMap.put("completed", "The timestamp of the completion of the task");
        taskFieldDescriptionsMap.put("modified", "Timestamp of the last modification of the task");
        taskFieldDescriptionsMap.put("planned", "Planned start of the task. The actual completion of the task should be between PLANNED and DUE.");
        taskFieldDescriptionsMap.put("due", "Timestamp when the task is due. The actual completion of the task should be between PLANNED and DUE.");
        taskFieldDescriptionsMap.put("name", "The name of the task");
        taskFieldDescriptionsMap.put("creator", "");
        taskFieldDescriptionsMap.put("description", "The description of the task");
        taskFieldDescriptionsMap.put("note", "note");
        taskFieldDescriptionsMap.put("priority", "The priority of the task");
        taskFieldDescriptionsMap.put("state", "he state of the task. See (...)");
        taskFieldDescriptionsMap.put("classificationSummaryResource", "The <<classification-subset, Classification>> of the task");
        taskFieldDescriptionsMap.put("workbasketSummaryResource", "The <<workbasket-subset, Workbasket>> of the task");
        taskFieldDescriptionsMap.put("businessProcessId", "");
        taskFieldDescriptionsMap.put("parentBusinessProcessId", "");
        taskFieldDescriptionsMap.put("owner", "The owner of the tasks. The owner is set upon claiming of the task.");
        taskFieldDescriptionsMap.put("primaryObjRef.id", "");
        taskFieldDescriptionsMap.put("primaryObjRef.company", "The company referenced primary object belongs to.");
        taskFieldDescriptionsMap.put("primaryObjRef.system", "The (kind of) system, the object resides in (e.g. SAP, MySystem A, ...) ");
        taskFieldDescriptionsMap.put("primaryObjRef.systemInstance", "The instance of the system, the object resides in.");
        taskFieldDescriptionsMap.put("primaryObjRef.type", "The type of the reference (contract, claim, policy, customer, ...)");
        taskFieldDescriptionsMap.put("primaryObjRef.value", "The value of the primary object reference");
        taskFieldDescriptionsMap.put("customAttributes", "A container for all additional information on the task in JSON representation");
        taskFieldDescriptionsMap.put("attachments", "");
        taskFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
        taskFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
        taskFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
        taskFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
        taskFieldDescriptionsMap.put("custom5", "A custom property with name \"5\"");
        taskFieldDescriptionsMap.put("custom6", "A custom property with name \"6\"");
        taskFieldDescriptionsMap.put("custom7", "A custom property with name \"7\"");
        taskFieldDescriptionsMap.put("custom8", "A custom property with name \"8\"");
        taskFieldDescriptionsMap.put("custom9", "A custom property with name \"9\"");
        taskFieldDescriptionsMap.put("custom10", "A custom property with name \"10\"");
        taskFieldDescriptionsMap.put("custom11", "A custom property with name \"11\"");
        taskFieldDescriptionsMap.put("custom12", "A custom property with name \"12\"");
        taskFieldDescriptionsMap.put("custom13", "A custom property with name \"13\"");
        taskFieldDescriptionsMap.put("custom14", "A custom property with name \"14\"");
        taskFieldDescriptionsMap.put("custom15", "A custom property with name \"15\"");
        taskFieldDescriptionsMap.put("custom16", "A custom property with name \"16\"");
        taskFieldDescriptionsMap.put("read", "Indicator if the task has been read");
        taskFieldDescriptionsMap.put("transferred", "Indicator if the task has been transferred");
        
        allTasksFieldDescriptors = new FieldDescriptor[] {

                subsectionWithPath("_embedded.tasks").description("An Array of <<task-subset, Task-Subsets>>"),
                fieldWithPath("_links").ignored(),
                fieldWithPath("_links.self").ignored(),
                fieldWithPath("_links.self.href").ignored(),
                fieldWithPath("page").ignored()
        };
        
        taskFieldDescriptors = new FieldDescriptor[] {
                
                fieldWithPath("taskId").description(taskFieldDescriptionsMap.get("taskId")),
                fieldWithPath("created").description(taskFieldDescriptionsMap.get("created")),
                fieldWithPath("claimed").description(taskFieldDescriptionsMap.get("claimed")).type("String"),
                fieldWithPath("completed").description(taskFieldDescriptionsMap.get("completed")).type("String"),
                fieldWithPath("modified").description(taskFieldDescriptionsMap.get("modified")).type("String"),
                fieldWithPath("planned").description(taskFieldDescriptionsMap.get("planned")).type("String"),
                fieldWithPath("due").description(taskFieldDescriptionsMap.get("due")).type("String"),
                fieldWithPath("name").description(taskFieldDescriptionsMap.get("name")),
                fieldWithPath("creator").description(taskFieldDescriptionsMap.get("creator")),
                fieldWithPath("description").description(taskFieldDescriptionsMap.get("description")),
                fieldWithPath("note").description(taskFieldDescriptionsMap.get("note")).description("Some custom Note"),
                fieldWithPath("priority").description(taskFieldDescriptionsMap.get("priority")),
                fieldWithPath("state").description(taskFieldDescriptionsMap.get("state")),
                subsectionWithPath("classificationSummaryResource").description(taskFieldDescriptionsMap.get("classificationSummaryResource")),
                subsectionWithPath("workbasketSummaryResource").description(taskFieldDescriptionsMap.get("workbasketSummaryResource")),
                fieldWithPath("businessProcessId").description(taskFieldDescriptionsMap.get("businessProcessId")),
                fieldWithPath("parentBusinessProcessId").description(taskFieldDescriptionsMap.get("parentBusinessProcessId")),
                fieldWithPath("owner").description(taskFieldDescriptionsMap.get("owner")).type("String"),
                fieldWithPath("primaryObjRef.id").description(taskFieldDescriptionsMap.get("primaryObjRef.id")).type("String"),
                fieldWithPath("primaryObjRef.company").description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
                fieldWithPath("primaryObjRef.system").description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
                fieldWithPath("primaryObjRef.systemInstance").description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
                fieldWithPath("primaryObjRef.type").description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
                fieldWithPath("primaryObjRef.value").description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
                fieldWithPath("read").description(taskFieldDescriptionsMap.get("read")),
                fieldWithPath("transferred").description(taskFieldDescriptionsMap.get("transferred")),
                fieldWithPath("customAttributes").description(taskFieldDescriptionsMap.get("customAttributes")),
                fieldWithPath("attachments").description(taskFieldDescriptionsMap.get("attachments")),
                fieldWithPath("custom1").description(taskFieldDescriptionsMap.get("custom1")).type("String"),
                fieldWithPath("custom2").description(taskFieldDescriptionsMap.get("custom2")).type("String"),
                fieldWithPath("custom3").description(taskFieldDescriptionsMap.get("custom3")).type("String"),
                fieldWithPath("custom4").description(taskFieldDescriptionsMap.get("custom4")).type("String"),
                fieldWithPath("custom5").description(taskFieldDescriptionsMap.get("custom5")).type("String"),
                fieldWithPath("custom6").description(taskFieldDescriptionsMap.get("custom6")).type("String"),
                fieldWithPath("custom7").description(taskFieldDescriptionsMap.get("custom7")).type("String"),
                fieldWithPath("custom8").description(taskFieldDescriptionsMap.get("custom8")).type("String"),
                fieldWithPath("custom9").description(taskFieldDescriptionsMap.get("custom9")).type("String"),
                fieldWithPath("custom10").description(taskFieldDescriptionsMap.get("custom10")).type("String"),
                fieldWithPath("custom11").description(taskFieldDescriptionsMap.get("custom11")).type("String"),
                fieldWithPath("custom12").description(taskFieldDescriptionsMap.get("custom12")).type("String"),
                fieldWithPath("custom13").description(taskFieldDescriptionsMap.get("custom13")).type("String"),
                fieldWithPath("custom14").description(taskFieldDescriptionsMap.get("custom14")).type("String"),
                fieldWithPath("custom15").description(taskFieldDescriptionsMap.get("custom15")).type("String"),
                fieldWithPath("custom16").description(taskFieldDescriptionsMap.get("custom16")).type("String"),
                fieldWithPath("_links.self.href").ignored()
        };
        
        taskSubsetFieldDescriptors = new FieldDescriptor[] {

                fieldWithPath("taskId").description(taskFieldDescriptionsMap.get("taskId")),
                fieldWithPath("created").description(taskFieldDescriptionsMap.get("created")),
                fieldWithPath("claimed").description(taskFieldDescriptionsMap.get("claimed")),
                fieldWithPath("completed").description(taskFieldDescriptionsMap.get("completed")).type("String"),
                fieldWithPath("modified").description(taskFieldDescriptionsMap.get("modified")).type("String"),
                fieldWithPath("planned").description(taskFieldDescriptionsMap.get("planned")).type("String"),
                fieldWithPath("due").description(taskFieldDescriptionsMap.get("due")).type("String"),
                fieldWithPath("name").description(taskFieldDescriptionsMap.get("name")),
                fieldWithPath("creator").description(taskFieldDescriptionsMap.get("creator")),
                fieldWithPath("description").ignored(),
                fieldWithPath("note").description(taskFieldDescriptionsMap.get("note")).description("Some custom Note"),
                fieldWithPath("priority").description(taskFieldDescriptionsMap.get("priority")),
                fieldWithPath("state").description(taskFieldDescriptionsMap.get("state")),
                subsectionWithPath("classificationSummaryResource").description(taskFieldDescriptionsMap.get("classificationSummaryResource")),
                subsectionWithPath("workbasketSummaryResource").description(taskFieldDescriptionsMap.get("workbasketSummaryResource")),
                fieldWithPath("businessProcessId").description(taskFieldDescriptionsMap.get("businessProcessId")),
                fieldWithPath("parentBusinessProcessId").description(taskFieldDescriptionsMap.get("parentBusinessProcessId")),
                fieldWithPath("owner").description(taskFieldDescriptionsMap.get("owner")),
                fieldWithPath("primaryObjRef.id").description(taskFieldDescriptionsMap.get("primaryObjRef.id")).type("String"),
                fieldWithPath("primaryObjRef.company").description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
                fieldWithPath("primaryObjRef.system").description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
                fieldWithPath("primaryObjRef.systemInstance").description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
                fieldWithPath("primaryObjRef.type").description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
                fieldWithPath("primaryObjRef.value").description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
                fieldWithPath("read").description(taskFieldDescriptionsMap.get("read")),
                fieldWithPath("transferred").description(taskFieldDescriptionsMap.get("transferred")),
                fieldWithPath("customAttributes").ignored(),
                fieldWithPath("attachments").description(taskFieldDescriptionsMap.get("attachments")),
                fieldWithPath("custom1").description(taskFieldDescriptionsMap.get("custom1")),
                fieldWithPath("custom2").description(taskFieldDescriptionsMap.get("custom2")),
                fieldWithPath("custom3").description(taskFieldDescriptionsMap.get("custom3")),
                fieldWithPath("custom4").description(taskFieldDescriptionsMap.get("custom4")),
                fieldWithPath("custom5").description(taskFieldDescriptionsMap.get("custom5")),
                fieldWithPath("custom6").description(taskFieldDescriptionsMap.get("custom6")),
                fieldWithPath("custom7").description(taskFieldDescriptionsMap.get("custom7")),
                fieldWithPath("custom8").description(taskFieldDescriptionsMap.get("custom8")),
                fieldWithPath("custom9").description(taskFieldDescriptionsMap.get("custom9")),
                fieldWithPath("custom10").description(taskFieldDescriptionsMap.get("custom10")),
                fieldWithPath("custom11").description(taskFieldDescriptionsMap.get("custom11")),
                fieldWithPath("custom12").description(taskFieldDescriptionsMap.get("custom12")),
                fieldWithPath("custom13").description(taskFieldDescriptionsMap.get("custom13")),
                fieldWithPath("custom14").description(taskFieldDescriptionsMap.get("custom14")),
                fieldWithPath("custom15").description(taskFieldDescriptionsMap.get("custom15")),
                fieldWithPath("custom16").description(taskFieldDescriptionsMap.get("custom16")),
                fieldWithPath("_links.self.href").ignored()
        };
        
        createTaskFieldDescriptors = new FieldDescriptor[] {
                
                subsectionWithPath("classificationSummaryResource").description("The new classificationSummaryResource for the task"),
                subsectionWithPath("workbasketSummaryResource").description("The new workbasketSummaryResource for the task"),
                fieldWithPath("primaryObjRef.company").description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
                fieldWithPath("primaryObjRef.system").description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
                fieldWithPath("primaryObjRef.systemInstance").description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
                fieldWithPath("primaryObjRef.type").description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
                fieldWithPath("primaryObjRef.value").description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
                fieldWithPath("created").description(taskFieldDescriptionsMap.get("created")).type("String").optional(),
                fieldWithPath("claimed").description(taskFieldDescriptionsMap.get("claimed")).type("String").optional(),
                fieldWithPath("completed").description(taskFieldDescriptionsMap.get("completed")).type("String").optional(),
                fieldWithPath("modified").description(taskFieldDescriptionsMap.get("modified")).type("String").optional(),
                fieldWithPath("planned").description(taskFieldDescriptionsMap.get("planned")).type("String").optional(),
                fieldWithPath("due").description(taskFieldDescriptionsMap.get("due")).type("String").optional(),
                fieldWithPath("name").description(taskFieldDescriptionsMap.get("name")).type("String").optional(),
                fieldWithPath("creator").description(taskFieldDescriptionsMap.get("creator")).type("String").optional(),
                fieldWithPath("description").description(taskFieldDescriptionsMap.get("description")).type("String").optional(),
                fieldWithPath("note").description(taskFieldDescriptionsMap.get("note")).description("Some custom Note").type("String").optional(),
                fieldWithPath("priority").description(taskFieldDescriptionsMap.get("priority")).type("String").optional(),
                fieldWithPath("state").description(taskFieldDescriptionsMap.get("state")).type("String").optional(),
                fieldWithPath("businessProcessId").description(taskFieldDescriptionsMap.get("businessProcessId")).type("String").optional(),
                fieldWithPath("parentBusinessProcessId").description(taskFieldDescriptionsMap.get("parentBusinessProcessId")).type("String").optional(),
                fieldWithPath("owner").description(taskFieldDescriptionsMap.get("owner")).type("String").optional(),
                fieldWithPath("primaryObjRef.id").description(taskFieldDescriptionsMap.get("primaryObjRef.id")).type("String").optional(),
                fieldWithPath("customAttributes").description(taskFieldDescriptionsMap.get("customAttributes")).type("Object").optional(),
                fieldWithPath("attachments").description(taskFieldDescriptionsMap.get("attachments")).type("Array").optional(),
                fieldWithPath("custom1").description(taskFieldDescriptionsMap.get("custom1")).type("String").optional(),
                fieldWithPath("custom2").description(taskFieldDescriptionsMap.get("custom2")).type("String").optional(),
                fieldWithPath("custom3").description(taskFieldDescriptionsMap.get("custom3")).type("String").optional(),
                fieldWithPath("custom4").description(taskFieldDescriptionsMap.get("custom4")).type("String").optional(),
                fieldWithPath("custom5").description(taskFieldDescriptionsMap.get("custom5")).type("String").optional(),
                fieldWithPath("custom6").description(taskFieldDescriptionsMap.get("custom6")).type("String").optional(),
                fieldWithPath("custom7").description(taskFieldDescriptionsMap.get("custom7")).type("String").optional(),
                fieldWithPath("custom8").description(taskFieldDescriptionsMap.get("custom8")).type("String").optional(),
                fieldWithPath("custom9").description(taskFieldDescriptionsMap.get("custom9")).type("String").optional(),
                fieldWithPath("custom10").description(taskFieldDescriptionsMap.get("custom10")).type("String").optional(),
                fieldWithPath("custom11").description(taskFieldDescriptionsMap.get("custom11")).type("String").optional(),
                fieldWithPath("custom12").description(taskFieldDescriptionsMap.get("custom12")).type("String").optional(),
                fieldWithPath("custom13").description(taskFieldDescriptionsMap.get("custom13")).type("String").optional(),
                fieldWithPath("custom14").description(taskFieldDescriptionsMap.get("custom14")).type("String").optional(),
                fieldWithPath("custom15").description(taskFieldDescriptionsMap.get("custom15")).type("String").optional(),
                fieldWithPath("custom16").description(taskFieldDescriptionsMap.get("custom16")).type("String").optional(),
                fieldWithPath("read").description(taskFieldDescriptionsMap.get("read")).type("Boolean").optional(),
                fieldWithPath("transferred").description(taskFieldDescriptionsMap.get("transferred")).type("Boolean").optional()
        };
    }
    
    @Test
    public void getAllTasksDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/tasks?por.type=VNR&por.value=22334455")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllTasksDocTest",
                responseFields(allTasksFieldDescriptors)));
    }
    
    @Test
    public void getSpecificTaskDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetSpecificTaskDocTest",
                responseFields(taskFieldDescriptors)));
    }
    
    @Test
    public void taskSubSetDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("TaskSubset",
                responseFields(taskSubsetFieldDescriptors)));
    }
    
    @Test
    public void updateTaskDocTest() throws Exception{
        URL url = new URL("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x");
        assertEquals(200, con.getResponseCode());

        BufferedReader in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();
        String originalTask = content.toString();
        String modifiedTask = new String(originalTask.toString());
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .put("http://127.0.0.1:" + port + "/v1/tasks/TKI:100000000000000000000000000000000000")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .contentType("application/json")
                .content(modifiedTask))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("UpdateTaskDocTest",
                requestFields(taskFieldDescriptors),
                responseFields(taskFieldDescriptors)));
    }
    
    @Test
    public void createAndDeleteTaskDocTest() throws Exception {
        
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks")
                .contentType("application/hal+json")
                .content("{\"classificationSummaryResource\":{\"key\":\"L11010\"}," +
                        "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
                        "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("CreateTaskDocTest",
                requestFields(createTaskFieldDescriptors),
                responseFields(taskFieldDescriptors)))
        .andReturn();     
        
        String newId = result.getResponse().getContentAsString().substring(11, 51);
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .delete("http://127.0.0.1:" + port + "/v1/tasks/" + newId)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")) //admin
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteTaskDocTest"));
    }
    
    @Test
    public void claimTaskDocTest() throws Exception {
        
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks")
                .contentType("application/hal+json")
                .content("{\"classificationSummaryResource\":{\"key\":\"L11010\"}," +
                        "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
                        "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("temp"))
        .andReturn();
        
        String newId = result.getResponse().getContentAsString().substring(11, 51);
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks/" + newId + "/claim")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("ClaimTaskDocTest",
                responseFields(taskFieldDescriptors)));
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .delete("http://127.0.0.1:" + port + "/v1/tasks/" + newId)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")) //admin
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteTaskDocTest"));
    }
    
    @Test
    public void completeTaskDocTest() throws Exception {
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks")
                .contentType("application/hal+json")
                .content("{\"classificationSummaryResource\":{\"key\":\"L11010\"}," +
                        "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
                        "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("temp"))
        .andReturn();
        
        String newId = result.getResponse().getContentAsString().substring(11, 51);
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks/" + newId + "/complete")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("CompleteTaskDocTest",
                responseFields(taskFieldDescriptors)));
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .delete("http://127.0.0.1:" + port + "/v1/tasks/" + newId)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")) //admin
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteTaskDocTest"));
    }
    
    @Test
    public void transferTaskDocTest() throws Exception {
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks")
                .contentType("application/hal+json")
                .content("{\"classificationSummaryResource\":{\"key\":\"L11010\"}," +
                        "\"workbasketSummaryResource\":{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"}," +
                        "\"primaryObjRef\":{\"company\":\"MyCompany1\",\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\",\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("TransferTaskDocTest",
                responseFields(taskFieldDescriptors)))
        .andReturn();
       
        String newId = result.getResponse().getContentAsString().substring(11, 51);
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .post("http://127.0.0.1:" + port + "/v1/tasks/" + newId + "/transfer/WBI:100000000000000000000000000000000001")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("TransferTaskDocTest",
                responseFields(taskFieldDescriptors)));
        
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .delete("http://127.0.0.1:" + port + "/v1/tasks/" + newId)
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")) //admin
        .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
