package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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

import pro.taskana.rest.RestConfiguration;

/**
 * Generate REST Docu for the WorkbasketAccessItemController.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WorkbasketAccessItemControllerRestDocumentation {

    @LocalServerPort
    int port;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private HashMap<String, String> accessItemFieldDescriptionsMap = new HashMap<String, String>();
    private FieldDescriptor[] accessItemFieldDescriptors;

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

        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessItemId", "Unique ID");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.workbasketId", "The workbasket id");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessId",
            "The access id. This could be either a userid or a full qualified group id");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.accessName", "The name");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.workbasketKey", "The workbasket key");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permRead",
            "The permission to read the information about the workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permOpen",
            "The permission to view the content (the tasks) of a workbasket");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permAppend",
            "The permission to add tasks to the workbasket (required for creation and tranferring of tasks)");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permTransfer",
            "The permission to transfer tasks (out of the current workbasket)");
        accessItemFieldDescriptionsMap.put("_embedded.accessItems.permDistribute",
            "The permission to distribute tasks from the workbasket");
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
        accessItemFieldDescriptionsMap.put("_links.self.href", "Link to self");
        accessItemFieldDescriptionsMap.put("page", "Number of page");

        accessItemFieldDescriptors = new FieldDescriptor[] {
            fieldWithPath("_embedded.accessItems[].accessItemId")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessItemId")),
            fieldWithPath("_embedded.accessItems[].workbasketId")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.workbasketId")),
            fieldWithPath("_embedded.accessItems[].accessId")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessId")),
            fieldWithPath("_embedded.accessItems[].accessName")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.accessName")),
            fieldWithPath("_embedded.accessItems[].workbasketKey")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.workbasketKey")),
            fieldWithPath("_embedded.accessItems[].permRead")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permRead")),
            fieldWithPath("_embedded.accessItems[].permOpen")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permOpen")),
            fieldWithPath("_embedded.accessItems[].permAppend")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permAppend")),
            fieldWithPath("_embedded.accessItems[].permTransfer")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permTransfer")),
            fieldWithPath("_embedded.accessItems[].permDistribute")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permDistribute")),
            fieldWithPath("_embedded.accessItems[].permCustom1")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom1")),
            fieldWithPath("_embedded.accessItems[].permCustom2")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom2")),
            fieldWithPath("_embedded.accessItems[].permCustom3")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom3")),
            fieldWithPath("_embedded.accessItems[].permCustom4")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom4")),
            fieldWithPath("_embedded.accessItems[].permCustom5")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom5")),
            fieldWithPath("_embedded.accessItems[].permCustom6")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom6")),
            fieldWithPath("_embedded.accessItems[].permCustom7")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom7")),
            fieldWithPath("_embedded.accessItems[].permCustom8")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom8")),
            fieldWithPath("_embedded.accessItems[].permCustom9")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom9")),
            fieldWithPath("_embedded.accessItems[].permCustom10")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom10")),
            fieldWithPath("_embedded.accessItems[].permCustom11")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom11")),
            fieldWithPath("_embedded.accessItems[].permCustom12")
                .description(accessItemFieldDescriptionsMap.get("_embedded.accessItems.permCustom12")),
            fieldWithPath("_links.self.href").description(accessItemFieldDescriptionsMap.get("_links.self.href")),
            fieldWithPath("page").description(accessItemFieldDescriptionsMap.get("page"))
        };
    }

    @Test
    public void getWorkbasketAccessItemsDocTest() throws Exception {
        this.mockMvc
            .perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port
                    + "/v1/workbasket-access-items/?sort-by=workbasket-key&order=asc&access-ids=user_1_1")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetWorkbasketAccessItemsDocTest",
                responseFields(accessItemFieldDescriptors)));
    }

    @Test
    public void removeWorkbasketAccessItemsDocTest() throws Exception {
        this.mockMvc
            .perform(RestDocumentationRequestBuilders
                .delete("http://127.0.0.1:" + port + "/v1/workbasket-access-items/?access-id=user_1_1")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcRestDocumentation.document("RemoveWorkbasketAccessItemsDocTest"));
    }
}
