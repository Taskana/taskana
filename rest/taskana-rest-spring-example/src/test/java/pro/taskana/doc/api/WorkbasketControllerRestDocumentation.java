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
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import pro.taskana.rest.RestConfiguration;
import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class WorkbasketControllerRestDocumentation {
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;
    
    // HashMaps to store the field descriptions centrally for multiple uses 
    private HashMap<String, String> workbasketFieldDescriptionsMap = new HashMap<String, String>();
    private HashMap<String, String> accessItemFieldDescriptionsMap = new HashMap<String, String>();
    
    private FieldDescriptor[] allWorkbasketsFieldDescriptors;
    private FieldDescriptor[] workbasketFieldDescriptors;
    private FieldDescriptor[] workbasketSubsetFieldDescriptors;
    private FieldDescriptor[] allWorkbasketAccessItemsFieldDescriptors;
    private FieldDescriptor[] accessItemFieldDescriptors;
    private FieldDescriptor[] allDistributionTargetsFieldDescriptors;
    private FieldDescriptor[] createWorkbasketFieldDescriptors;
    
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
        
        workbasketFieldDescriptionsMap.put("workbasketId", "Unique ID");
        workbasketFieldDescriptionsMap.put("key", "");
        workbasketFieldDescriptionsMap.put("name", "The name of the workbasket");
        workbasketFieldDescriptionsMap.put("domain", "");
        workbasketFieldDescriptionsMap.put("type", "");
        workbasketFieldDescriptionsMap.put("description", "The description of the workbasket");
        workbasketFieldDescriptionsMap.put("owner", "The owner of the workbasket. The owner is responsible for the on-time completion of all tasks in the workbasket.");
        workbasketFieldDescriptionsMap.put("orgLevel1", "The first Org Level (the top one)\nThe Org Level is an association with an org hierarchie level in the organization. The values are used for monitoring and statistical purposes and should reflect the responsibility of the tasks in the workbasket.");
        workbasketFieldDescriptionsMap.put("orgLevel2", "The second Org Level");
        workbasketFieldDescriptionsMap.put("orgLevel3", "The third Org Level");
        workbasketFieldDescriptionsMap.put("orgLevel4", "The fourth Org Level (the lowest one).");
        workbasketFieldDescriptionsMap.put("created", "The creation timestamp of the workbasket in the system.");
        workbasketFieldDescriptionsMap.put("modified", "Timestamp of the last modification of the workbasket");
        workbasketFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
        workbasketFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
        workbasketFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
        workbasketFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
        workbasketFieldDescriptionsMap.put("_links.distributionTargets.href", "The Distribution-Targets of the workbasket");
        workbasketFieldDescriptionsMap.put("_links.removeDistributionTargets.href", "Link to remove all distribution-targets from the workbasket");
        workbasketFieldDescriptionsMap.put("_links.accessItems.href", "The Access-Items of the workbasket");
        workbasketFieldDescriptionsMap.put("_links.allWorkbaskets.href", "Link to all workbaskets");
        
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessItemId", "Unique ID");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.workbasketId", "The workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessId", "The access id, this ACL entry refers to. This could be either a userid or a full qualified group id (both lower case)");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessName", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permRead", "The permission to read the information about the workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permOpen", "The permission to view the content (the tasks) of a workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permAppend", "The permission to add tasks to the workbasket (required for creation and tranferring of tasks)");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permTransfer", "The permission to transfer tasks (out of the current workbasket)");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permDistribute", "The permission to distribute tasks from the workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom1", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom2", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom3", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom4", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom5", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom6", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom7", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom8", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom9", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom10", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom11", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permCustom12", "");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems._links.workbasket.href", "Link to the workbasket");     
        
        allWorkbasketsFieldDescriptors = new FieldDescriptor[] {
                
                subsectionWithPath("_embedded.workbaskets").description("An Array of <<workbasket-subset, Workbasket-Subsets>>"),
                fieldWithPath("_links.self.href").ignored(),
                fieldWithPath("page").ignored()
        };
        
        workbasketFieldDescriptors = new FieldDescriptor[] {

                fieldWithPath("workbasketId").description(workbasketFieldDescriptionsMap.get("workbasketId")),
                fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
                fieldWithPath("created").description(workbasketFieldDescriptionsMap.get("created")),
                fieldWithPath("modified").description(workbasketFieldDescriptionsMap.get("modified")),
                fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
                fieldWithPath("description").description(workbasketFieldDescriptionsMap.get("description")),
                fieldWithPath("owner").description(workbasketFieldDescriptionsMap.get("owner")),
                fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
                fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
                fieldWithPath("custom1").description(workbasketFieldDescriptionsMap.get("custom1")),
                fieldWithPath("custom2").description(workbasketFieldDescriptionsMap.get("custom2")),
                fieldWithPath("custom3").description(workbasketFieldDescriptionsMap.get("custom3")),
                fieldWithPath("custom4").description(workbasketFieldDescriptionsMap.get("custom4")),
                fieldWithPath("orgLevel1").description(workbasketFieldDescriptionsMap.get("orgLevel1")),
                fieldWithPath("orgLevel2").description(workbasketFieldDescriptionsMap.get("orgLevel2")),
                fieldWithPath("orgLevel3").description(workbasketFieldDescriptionsMap.get("orgLevel3")),
                fieldWithPath("orgLevel4").description(workbasketFieldDescriptionsMap.get("orgLevel4")),
                fieldWithPath("_links.distributionTargets.href").description(workbasketFieldDescriptionsMap.get("_links.distributionTargets.href")),
                fieldWithPath("_links.removeDistributionTargets.href").description(workbasketFieldDescriptionsMap.get("_links.removeDistributionTargets.href")),
                fieldWithPath("_links.accessItems.href").description(workbasketFieldDescriptionsMap.get("_links.accessItems.href")),
                fieldWithPath("_links.allWorkbaskets.href").description(workbasketFieldDescriptionsMap.get("_links.allWorkbaskets.href")),
                fieldWithPath("_links.self.href").ignored()
        };
        
        workbasketSubsetFieldDescriptors = new FieldDescriptor[] {

                fieldWithPath("workbasketId").description(workbasketFieldDescriptionsMap.get("workbasketId")),
                fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
                fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
                fieldWithPath("description").description(workbasketFieldDescriptionsMap.get("description")),
                fieldWithPath("owner").description(workbasketFieldDescriptionsMap.get("owner")),
                fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
                fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
                fieldWithPath("custom1").description(workbasketFieldDescriptionsMap.get("custom1")),
                fieldWithPath("custom2").description(workbasketFieldDescriptionsMap.get("custom2")),
                fieldWithPath("custom3").description(workbasketFieldDescriptionsMap.get("custom3")),
                fieldWithPath("custom4").description(workbasketFieldDescriptionsMap.get("custom4")),
                fieldWithPath("orgLevel1").description(workbasketFieldDescriptionsMap.get("orgLevel1")),
                fieldWithPath("orgLevel2").description(workbasketFieldDescriptionsMap.get("orgLevel2")),
                fieldWithPath("orgLevel3").description(workbasketFieldDescriptionsMap.get("orgLevel3")),
                fieldWithPath("orgLevel4").description(workbasketFieldDescriptionsMap.get("orgLevel4")),
                fieldWithPath("created").ignored(),
                fieldWithPath("modified").ignored(),
                fieldWithPath("_links.distributionTargets.href").ignored(),
                fieldWithPath("_links.removeDistributionTargets.href").ignored(),
                fieldWithPath("_links.accessItems.href").ignored(),
                fieldWithPath("_links.allWorkbaskets.href").ignored(),
                fieldWithPath("_links.self.href").ignored()
        };
        
        accessItemFieldDescriptors = new FieldDescriptor[] {
                
                fieldWithPath("_embedded.accessItems[].accessItemId").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessItemId")),
                fieldWithPath("_embedded.accessItems[].workbasketId").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.workbasketId")),
                fieldWithPath("_embedded.accessItems[].accessId").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessId")),
                fieldWithPath("_embedded.accessItems[].accessName").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessName")),
                fieldWithPath("_embedded.accessItems[].permRead").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permRead")),
                fieldWithPath("_embedded.accessItems[].permOpen").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permOpen")),
                fieldWithPath("_embedded.accessItems[].permAppend").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permAppend")),
                fieldWithPath("_embedded.accessItems[].permTransfer").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permTransfer")),
                fieldWithPath("_embedded.accessItems[].permDistribute").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permDistribute")),
                fieldWithPath("_embedded.accessItems[].permCustom1").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom1")),
                fieldWithPath("_embedded.accessItems[].permCustom2").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom2")),
                fieldWithPath("_embedded.accessItems[].permCustom3").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom3")),
                fieldWithPath("_embedded.accessItems[].permCustom4").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom4")),
                fieldWithPath("_embedded.accessItems[].permCustom5").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom5")),
                fieldWithPath("_embedded.accessItems[].permCustom6").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom6")),
                fieldWithPath("_embedded.accessItems[].permCustom7").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom7")),
                fieldWithPath("_embedded.accessItems[].permCustom8").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom8")),
                fieldWithPath("_embedded.accessItems[].permCustom9").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom9")),
                fieldWithPath("_embedded.accessItems[].permCustom10").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom10")),
                fieldWithPath("_embedded.accessItems[].permCustom11").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom11")),
                fieldWithPath("_embedded.accessItems[].permCustom12").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom12")),
                fieldWithPath("_embedded.accessItems[]._links.workbasket.href").description(accessItemFieldDescriptionsMap.get("_embedded.accessItems._links.workbasket.href")),
                fieldWithPath("_links.self.href").ignored(),
                fieldWithPath("_links.workbasket.href").ignored()
        };
        
        allWorkbasketAccessItemsFieldDescriptors = new FieldDescriptor[] {
                subsectionWithPath("_embedded.accessItems").description("An array of <<access-item, Access Items>>"),
                fieldWithPath("_links.workbasket.href").description("Link to the workbasket"),
                fieldWithPath("_links.self.href").ignored()
        };
        
        allDistributionTargetsFieldDescriptors = new FieldDescriptor[] {
                subsectionWithPath("_embedded.distributionTargets").description("An array of <<workbasket-subset, workbasket subsets>>"),
                fieldWithPath("_links.workbasket.href").description("Link to the workbasket"),
                fieldWithPath("_links.self.href").ignored()
        };
        
        createWorkbasketFieldDescriptors = new FieldDescriptor[] {
                fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
                fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
                fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
                fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
                fieldWithPath("description").description(workbasketFieldDescriptionsMap.get("description")).type("String").optional(),
                fieldWithPath("owner").description(workbasketFieldDescriptionsMap.get("owner")).type("String").optional(),
                fieldWithPath("orgLevel1").description(workbasketFieldDescriptionsMap.get("orgLevel1")).type("String").optional(),
                fieldWithPath("orgLevel2").description(workbasketFieldDescriptionsMap.get("orgLevel2")).type("String").optional(),
                fieldWithPath("orgLevel3").description(workbasketFieldDescriptionsMap.get("orgLevel3")).type("String").optional(),
                fieldWithPath("orgLevel4").description(workbasketFieldDescriptionsMap.get("orgLevel4")).type("String").optional(),
                fieldWithPath("created").description(workbasketFieldDescriptionsMap.get("created")).type("String").optional(),
                fieldWithPath("modified").description(workbasketFieldDescriptionsMap.get("modified")).type("String").optional(),
                fieldWithPath("custom1").description(workbasketFieldDescriptionsMap.get("custom1")).type("String").optional(),
                fieldWithPath("custom2").description(workbasketFieldDescriptionsMap.get("custom2")).type("String").optional(),
                fieldWithPath("custom3").description(workbasketFieldDescriptionsMap.get("custom3")).type("String").optional(),
                fieldWithPath("custom4").description(workbasketFieldDescriptionsMap.get("custom4")).type("String").optional(),
                fieldWithPath("_links.distributionTargets.href").description(workbasketFieldDescriptionsMap.get("_links.distributionTargets.href")).type("String").optional(),
                fieldWithPath("_links.removeDistributionTargets.href").description(workbasketFieldDescriptionsMap.get("_links.removeDistributionTargets.href")).type("String").optional(),
                fieldWithPath("_links.accessItems.href").description(workbasketFieldDescriptionsMap.get("_links.accessItems.href")).type("String").optional(),
                fieldWithPath("_links.allWorkbaskets.href").description(workbasketFieldDescriptionsMap.get("_links.allWorkbaskets.href")).type("String").optional()
        };
    }
    
    @Test
    public void getAllWorkbasketsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets?type=PERSONAL")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllWorkbasketsDocTest",
                responseFields(allWorkbasketsFieldDescriptors)));
    }
    
    @Test
    public void getSpecificWorkbasketDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000001")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetSpecificWorkbasketDocTest",
                responseFields(workbasketFieldDescriptors)));
    }
    
    @Test
    public void getAllWorkbasketAccessItemsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000001/workbasketAccessItems")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllWorkbasketAccessItemsDocTest",
                responseFields(allWorkbasketAccessItemsFieldDescriptors)));
    }
    
    @Test
    public void workbasketSubsetDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000001")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("WorkbasketSubset",
                responseFields(workbasketSubsetFieldDescriptors)));
    }
    
    @Test
    public void removeWorkbasketAsDistributionTargetDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("http://127.0.0.1:" + port + "/v1/workbaskets/distribution-targets/WBI:100000000000000000000000000000000007")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("RemoveWorkbasketAsDistributionTargetDocTest"));
    }
    
    @Test
    public void getAllWorkbasketDistributionTargets() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000002/distribution-targets")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("GetAllWorkbasketDistributionTargets",
                responseFields(allDistributionTargetsFieldDescriptors)));
    }
    
    @Test
    public void createAndDeleteWorkbasketDocTest() throws Exception {
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders.post("http://127.0.0.1:" + port + "/v1/workbaskets")
                .contentType("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .content("{\"key\" : \"asdasdasd\", \"name\" : \"Gruppenpostkorb KSC\", \"domain\" : \"DOMAIN_A\", \"type\" : \"GROUP\",   \"created\" : \"2018-02-01T11:00:00Z\",\r\n" + 
                        "  \"modified\" : \"2018-02-01T11:00:00Z\"}"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(MockMvcRestDocumentation.document("CreateWorkbasketDocTest",
                requestFields(createWorkbasketFieldDescriptors),
                responseFields(workbasketFieldDescriptors)))
        .andReturn();
        
        String newId = result.getResponse().getContentAsString().substring(17, 57);
        
        this.mockMvc.perform(RestDocumentationRequestBuilders.delete("http://127.0.0.1:" + port + "/v1/workbaskets/" + newId)
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteWorkbasketDocTest"));
    }
    
    @Test public void updateWorkbasketDocTest() throws Exception {
        URL url = new URL("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000002");
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
        String originalWorkbasket = content.toString();
        String modifiedWorkbasket = new String(originalWorkbasket.toString());
        
        this.mockMvc.perform(RestDocumentationRequestBuilders.put("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000002")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
                .contentType("application/json")
                .content(modifiedWorkbasket))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("UpdateWorkbasketDocTest",
                requestFields(workbasketFieldDescriptors),
                responseFields(workbasketFieldDescriptors)));
    }
    
    @Test
    public void accessItemDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders.get("http://127.0.0.1:" + port + "/v1/workbaskets/WBI:100000000000000000000000000000000001/workbasketAccessItems")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("AccessItemsDocTest",
                responseFields(accessItemFieldDescriptors)));
    }
}
