package pro.taskana.doc.api;

import static org.junit.Assert.assertEquals;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClassificationControllerRestDocumentation {

    @LocalServerPort
    int port;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private HashMap<String, String> classificationFieldDescriptionsMap = new HashMap<String, String>();

    private FieldDescriptor[] allClassificationsFieldDescriptors;
    private FieldDescriptor[] classificationFieldDescriptors;
    private FieldDescriptor[] classificationSubsetFieldDescriptors;
    private FieldDescriptor[] createClassificationFieldDescriptors;

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

        classificationFieldDescriptionsMap.put("classificationId", "Unique Id");
        classificationFieldDescriptionsMap.put("key",
            "The key of the classification. This is typically an externally known code or abbreviation of the classification");
        classificationFieldDescriptionsMap.put("parentId",
            "The id of the parent classification. Empty string (\"\") if this is a root classification.");
        classificationFieldDescriptionsMap.put("parentKey",
            "The key of the parent classification. Empty string (\"\") if this is a root classification.");
        classificationFieldDescriptionsMap.put("category",
            "The category of the classification (MANUAL, EXTERNAL, AUTOMATIC, PROCESS)");
        classificationFieldDescriptionsMap.put("type", "The type of classification (TASK, DOCUMENT)");
        classificationFieldDescriptionsMap.put("domain", "The domain for which this classification is specified");
        classificationFieldDescriptionsMap.put("isValidInDomain",
            "True, if this classification to objects in this domain");
        classificationFieldDescriptionsMap.put("created", "The creation timestamp of the classification in the system");
        classificationFieldDescriptionsMap.put("modified", "The timestamp of the last modification date");
        classificationFieldDescriptionsMap.put("name", "The name of the classification");
        classificationFieldDescriptionsMap.put("description", "The description of the classification");
        classificationFieldDescriptionsMap.put("priority", "The priority of the classification");
        classificationFieldDescriptionsMap.put("serviceLevel",
            "The service level of the classification. This is stated according to ISO 8601");
        classificationFieldDescriptionsMap.put("applicationEntryPoint",
            "The logical name of the entry point, the task list application should redirect to work on a task of this classification");
        classificationFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
        classificationFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
        classificationFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
        classificationFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
        classificationFieldDescriptionsMap.put("custom5", "A custom property with name \"5\"");
        classificationFieldDescriptionsMap.put("custom6", "A custom property with name \"6\"");
        classificationFieldDescriptionsMap.put("custom7", "A custom property with name \"7\"");
        classificationFieldDescriptionsMap.put("custom8", "A custom property with name \"8\"");
        classificationFieldDescriptionsMap.put("_links.getAllClassifications.href", "Link to all classifications");
        classificationFieldDescriptionsMap.put("_links.getAllClassifications.templated", "");

        allClassificationsFieldDescriptors = new FieldDescriptor[] {

            subsectionWithPath("_embedded.classificationSummaryResourceList")
                .description("An Array of <<classification-subset, Classification-Subsets>>"),
            fieldWithPath("_links.self.href").ignored(),
            fieldWithPath("page").ignored()
        };

        classificationFieldDescriptors = new FieldDescriptor[] {

            fieldWithPath("classificationId").description(classificationFieldDescriptionsMap.get("classificationId")),
            fieldWithPath("key").description(classificationFieldDescriptionsMap.get("key")),
            fieldWithPath("parentId").description(classificationFieldDescriptionsMap.get("parentId")),
            fieldWithPath("parentKey").description(classificationFieldDescriptionsMap.get("parentKey")),
            fieldWithPath("category").description(classificationFieldDescriptionsMap.get("category")),
            fieldWithPath("type").description(classificationFieldDescriptionsMap.get("type")),
            fieldWithPath("domain").description(classificationFieldDescriptionsMap.get("domain")),
            fieldWithPath("isValidInDomain").description(classificationFieldDescriptionsMap.get("isValidInDomain")),
            fieldWithPath("created").description(classificationFieldDescriptionsMap.get("created")),
            fieldWithPath("modified").description(classificationFieldDescriptionsMap.get("modified")),
            fieldWithPath("name").description(classificationFieldDescriptionsMap.get("name")),
            fieldWithPath("description").description(classificationFieldDescriptionsMap.get("description")),
            fieldWithPath("priority").description(classificationFieldDescriptionsMap.get("priority")),
            fieldWithPath("serviceLevel").description(classificationFieldDescriptionsMap.get("serviceLevel")),
            fieldWithPath("applicationEntryPoint")
                .description(classificationFieldDescriptionsMap.get("applicationEntryPoint")),
            fieldWithPath("custom1").description(classificationFieldDescriptionsMap.get("custom1")),
            fieldWithPath("custom2").description(classificationFieldDescriptionsMap.get("custom2")),
            fieldWithPath("custom3").description(classificationFieldDescriptionsMap.get("custom3")),
            fieldWithPath("custom4").description(classificationFieldDescriptionsMap.get("custom4")),
            fieldWithPath("custom5").description(classificationFieldDescriptionsMap.get("custom5")),
            fieldWithPath("custom6").description(classificationFieldDescriptionsMap.get("custom6")),
            fieldWithPath("custom7").description(classificationFieldDescriptionsMap.get("custom7")),
            fieldWithPath("custom8").description(classificationFieldDescriptionsMap.get("custom8")),
            fieldWithPath("_links.self.href").ignored()
        };

        classificationSubsetFieldDescriptors = new FieldDescriptor[] {

            fieldWithPath("classificationId").description(classificationFieldDescriptionsMap.get("classificationId")),
            fieldWithPath("key").description(classificationFieldDescriptionsMap.get("key")),
            fieldWithPath("category").description(classificationFieldDescriptionsMap.get("category")),
            fieldWithPath("type").description(classificationFieldDescriptionsMap.get("type")),
            fieldWithPath("domain").description(classificationFieldDescriptionsMap.get("domain")),
            fieldWithPath("isValidInDomain").ignored(),
            fieldWithPath("created").ignored(),
            fieldWithPath("modified").ignored(),
            fieldWithPath("name").description(classificationFieldDescriptionsMap.get("name")),
            fieldWithPath("parentId").description(classificationFieldDescriptionsMap.get("parentId")),
            fieldWithPath("parentKey").description(classificationFieldDescriptionsMap.get("parentKey")),
            fieldWithPath("description").ignored(),
            fieldWithPath("priority").description(classificationFieldDescriptionsMap.get("priority")),
            fieldWithPath("serviceLevel").description(classificationFieldDescriptionsMap.get("serviceLevel")),
            fieldWithPath("applicationEntryPoint").ignored(),
            fieldWithPath("custom1").description(classificationFieldDescriptionsMap.get("custom1")),
            fieldWithPath("custom2").description(classificationFieldDescriptionsMap.get("custom2")),
            fieldWithPath("custom3").description(classificationFieldDescriptionsMap.get("custom3")),
            fieldWithPath("custom4").description(classificationFieldDescriptionsMap.get("custom4")),
            fieldWithPath("custom5").description(classificationFieldDescriptionsMap.get("custom5")),
            fieldWithPath("custom6").description(classificationFieldDescriptionsMap.get("custom6")),
            fieldWithPath("custom7").description(classificationFieldDescriptionsMap.get("custom7")),
            fieldWithPath("custom8").description(classificationFieldDescriptionsMap.get("custom8")),
            fieldWithPath("_links.self.href").ignored()
        };

        createClassificationFieldDescriptors = new FieldDescriptor[] {

            fieldWithPath("category").type("String")
                .description("The category of the classification (MANUAL, EXTERNAL, AUTOMATIC, PROCESS)")
                .optional(),
            fieldWithPath("domain").description("The domain for which this classification is specified"),
            fieldWithPath("key").description(
                "The key of the classification. This is typically an externally known code or abbreviation of the classification"),
            fieldWithPath("name").type("String").description("The name of the classification").optional(),
            fieldWithPath("type").type("String").description("The type of classification (TASK, DOCUMENT)").optional(),
            fieldWithPath("parentId").type("String")
                .description(classificationFieldDescriptionsMap.get("parentId"))
                .optional(),
            fieldWithPath("parentKey").type("String")
                .description(classificationFieldDescriptionsMap.get("parentKey"))
                .optional(),
            fieldWithPath("isValidInDomain").type("Boolean")
                .description(classificationFieldDescriptionsMap.get("isValidInDomain"))
                .optional(),
            fieldWithPath("created").type("String")
                .description(classificationFieldDescriptionsMap.get("created"))
                .optional(),
            fieldWithPath("modified").type("String")
                .description(classificationFieldDescriptionsMap.get("modified"))
                .optional(),
            fieldWithPath("description").type("String")
                .description(classificationFieldDescriptionsMap.get("description"))
                .optional(),
            fieldWithPath("priority").type("Number")
                .description(classificationFieldDescriptionsMap.get("priority"))
                .optional(),
            fieldWithPath("serviceLevel").type("String")
                .description(classificationFieldDescriptionsMap.get("serviceLevel"))
                .optional(),
            fieldWithPath("applicationEntryPoint").type("String")
                .description(classificationFieldDescriptionsMap.get("applicationEntryPoint"))
                .optional(),
            fieldWithPath("custom1").type("String")
                .description(classificationFieldDescriptionsMap.get("custom1"))
                .optional(),
            fieldWithPath("custom2").type("String")
                .description(classificationFieldDescriptionsMap.get("custom2"))
                .optional(),
            fieldWithPath("custom3").type("String")
                .description(classificationFieldDescriptionsMap.get("custom3"))
                .optional(),
            fieldWithPath("custom4").type("String")
                .description(classificationFieldDescriptionsMap.get("custom4"))
                .optional(),
            fieldWithPath("custom5").type("String")
                .description(classificationFieldDescriptionsMap.get("custom5"))
                .optional(),
            fieldWithPath("custom6").type("String")
                .description(classificationFieldDescriptionsMap.get("custom6"))
                .optional(),
            fieldWithPath("custom7").type("String")
                .description(classificationFieldDescriptionsMap.get("custom7"))
                .optional(),
            fieldWithPath("custom8").type("String")
                .description(classificationFieldDescriptionsMap.get("custom8"))
                .optional()
        };
    }

    @Test
    public void getAllClassificationsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/v1/classifications?domain=DOMAIN_B")
            .accept("application/hal+json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetAllClassificationsDocTest",
                responseFields(allClassificationsFieldDescriptors)));
    }

    @Test
    public void getSpecificClassificationDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("GetSpecificClassificationDocTest",
                responseFields(classificationFieldDescriptors)));
    }

    @Test
    public void classificationSubsetDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("ClassificationSubset",
                responseFields(classificationSubsetFieldDescriptors)));
    }

    @Test
    public void createAndDeleteClassificationDocTest() throws Exception {
        MvcResult result = this.mockMvc.perform(RestDocumentationRequestBuilders
            .post("http://127.0.0.1:" + port + "/v1/classifications")
            .contentType("application/hal+json")
            .content("{\"key\":\"Key0815casdgdgh\", \"domain\":\"DOMAIN_B\"}")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcRestDocumentation.document("CreateClassificationDocTest",
                requestFields(createClassificationFieldDescriptors),
                responseFields(classificationFieldDescriptors)))
            .andReturn();

        String newId = result.getResponse().getContentAsString().substring(21, 61);

        this.mockMvc.perform(RestDocumentationRequestBuilders
            .delete("http://127.0.0.1:" + port + "/v1/classifications/" + newId)
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(MockMvcRestDocumentation.document("DeleteClassificationDocTest"));
    }

    @Test
    public void updateClassificationDocTest() throws Exception {
        URL url = new URL("http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009");
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
            .put("http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x")
            .contentType("application/json")
            .content(modifiedTask))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("UpdateClassificationDocTest",
                requestFields(classificationFieldDescriptors),
                responseFields(classificationFieldDescriptors)));
    }
}
