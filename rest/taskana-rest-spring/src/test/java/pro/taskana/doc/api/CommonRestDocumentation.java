package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate common REST Documentation. */
class CommonRestDocumentation extends BaseRestDocumentation {

  private HashMap<String, String> selfLinkFieldDescriptionsMap = new HashMap<String, String>();

  private FieldDescriptor[] selfLinkFieldDescriptors;

  @BeforeEach
  void setUp() {

    selfLinkFieldDescriptionsMap.put("_links", "Links section");
    selfLinkFieldDescriptionsMap.put("_links.self", "Link to self");
    selfLinkFieldDescriptionsMap.put("_links.self.href", "Link to instance");

    selfLinkFieldDescriptors =
        new FieldDescriptor[] {
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
          fieldWithPath("_links.self.href")
              .description(selfLinkFieldDescriptionsMap.get("_links.self.href"))
        };
  }

  @Test
  void commonFieldsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        RestEndpoints.URL_CLASSIFICATIONS_ID,
                        "CLI:100000000000000000000000000000000009"))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "CommonFields", responseFields(selfLinkFieldDescriptors)));
  }
}
