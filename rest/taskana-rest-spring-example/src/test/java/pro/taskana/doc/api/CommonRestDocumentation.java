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
public class CommonRestDocumentation {

    @LocalServerPort
    int port;

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private HashMap<String, String> selfLinkFieldDescriptionsMap = new HashMap<String, String>();

    private FieldDescriptor[] selfLinkFieldDescriptors;

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

        selfLinkFieldDescriptionsMap.put("_links", "Links section");
        selfLinkFieldDescriptionsMap.put("_links.self", "Link to self");
        selfLinkFieldDescriptionsMap.put("_links.self.href", "Link to instance");

        selfLinkFieldDescriptors = new FieldDescriptor[] {

            fieldWithPath("classificationId").ignored(),
            fieldWithPath("key").ignored(),
            fieldWithPath("parentId").ignored(),
            fieldWithPath("parentKey").ignored(),
            fieldWithPath("category").ignored(),
            fieldWithPath("type").ignored(),
            fieldWithPath("domain").ignored(),
            fieldWithPath("isValidInDomain").ignored(),
            fieldWithPath("created").ignored(),
            fieldWithPath("modified").ignored(),
            fieldWithPath("name").ignored(),
            fieldWithPath("description").ignored(),
            fieldWithPath("priority").ignored(),
            fieldWithPath("serviceLevel").ignored(),
            fieldWithPath("applicationEntryPoint").ignored(),
            fieldWithPath("custom1").ignored(),
            fieldWithPath("custom2").ignored(),
            fieldWithPath("custom3").ignored(),
            fieldWithPath("custom4").ignored(),
            fieldWithPath("custom5").ignored(),
            fieldWithPath("custom6").ignored(),
            fieldWithPath("custom7").ignored(),
            fieldWithPath("custom8").ignored(),
            fieldWithPath("_links").description(selfLinkFieldDescriptionsMap.get("_links")),
            fieldWithPath("_links.self").description(selfLinkFieldDescriptionsMap.get("_links.self")),
            fieldWithPath("_links.self.href").description(selfLinkFieldDescriptionsMap.get("_links.self.href"))
        };
    }

    @Test
    public void commonFieldsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/v1/classifications/CLI:100000000000000000000000000000000009")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("CommonFields",
                responseFields(selfLinkFieldDescriptors)));
    }
}
