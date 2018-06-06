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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pro.taskana.rest.RestConfiguration;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = RestConfiguration.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class AbstractPagingControllerRestDocumentation {
    
    @LocalServerPort
    int port;
    
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
    
    @Autowired
    private WebApplicationContext context;
    
    private MockMvc mockMvc;

    private HashMap<String, String> pagingFieldDescriptionsMap = new HashMap<String, String>();
    
    private FieldDescriptor[] pagingFieldDescriptors;
    
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
        
        pagingFieldDescriptionsMap.put("page", "Contains metainfo if there are multiple pages, else it is null");
        pagingFieldDescriptionsMap.put("page.size", "Number of items per page");
        pagingFieldDescriptionsMap.put("page.totalElements", "Total number of items");
        pagingFieldDescriptionsMap.put("page.totalPages", "Number of pages");
        pagingFieldDescriptionsMap.put("page.number", "Current page number");
        pagingFieldDescriptionsMap.put("_links.allClassifications.href", "Link to the main Summary-Resourc");
        pagingFieldDescriptionsMap.put("_links.first.href", "Link to first page");
        pagingFieldDescriptionsMap.put("_links.last.href", "Link to last page");
        pagingFieldDescriptionsMap.put("_links.prev.href", "Link to previous page");
        pagingFieldDescriptionsMap.put("_links.next.href", "Link to next page");    
        
        pagingFieldDescriptors = new FieldDescriptor[] {
                
                subsectionWithPath("_embedded.classificationSummaryResourceList").ignored(),
                fieldWithPath("_links").ignored(),
                fieldWithPath("_links.self").ignored(),
                fieldWithPath("_links.self.href").ignored(),
                fieldWithPath("page").description(pagingFieldDescriptionsMap.get("page")),
                fieldWithPath("page.size").description(pagingFieldDescriptionsMap.get("page.size")),
                fieldWithPath("page.totalElements").description(pagingFieldDescriptionsMap.get("page.totalElements")),
                fieldWithPath("page.totalPages").description(pagingFieldDescriptionsMap.get("page.totalPages")),
                fieldWithPath("page.number").description(pagingFieldDescriptionsMap.get("page.number")),
                fieldWithPath("_links.allClassifications.href").description(pagingFieldDescriptionsMap.get("_links.allClassifications.href")),
                fieldWithPath("_links.first.href").description(pagingFieldDescriptionsMap.get("_links.first.href")),
                fieldWithPath("_links.last.href").description(pagingFieldDescriptionsMap.get("_links.last.href")),
                fieldWithPath("_links.prev.href").description(pagingFieldDescriptionsMap.get("_links.prev.href")),
                fieldWithPath("_links.next.href").description(pagingFieldDescriptionsMap.get("_links.next.href"))
        };
    }
    
    @Test
    public void commonSummaryResourceFieldsDocTest() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
                .get("http://127.0.0.1:" + port + "/v1/classifications?page=2&page-size=5")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(MockMvcRestDocumentation.document("CommonSummaryResourceFields",
                responseFields(pagingFieldDescriptors)));
    }
}
