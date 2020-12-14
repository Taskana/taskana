package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate Rest Docu for AbstractPagingController. */
class AbstractPagingControllerRestDocumentation extends BaseRestDocumentation {

  private final HashMap<String, String> pagingFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] pagingFieldDescriptors;

  @BeforeEach
  void setUp() {

    pagingFieldDescriptionsMap.put(
        "page", "Contains metainfo if there are multiple pages, else it is null");
    pagingFieldDescriptionsMap.put("page.size", "Number of items per page");
    pagingFieldDescriptionsMap.put("page.totalElements", "Total number of items");
    pagingFieldDescriptionsMap.put("page.totalPages", "Number of pages");
    pagingFieldDescriptionsMap.put("page.number", "Current page number");
    pagingFieldDescriptionsMap.put("_links.first.href", "Link to first page");
    pagingFieldDescriptionsMap.put("_links.last.href", "Link to last page");
    pagingFieldDescriptionsMap.put("_links.prev.href", "Link to previous page");
    pagingFieldDescriptionsMap.put("_links.next.href", "Link to next page");

    pagingFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("classifications").ignored(),
          fieldWithPath("_links").ignored(),
          fieldWithPath("_links.self").ignored(),
          fieldWithPath("_links.self.href").ignored(),
          fieldWithPath("page").description(pagingFieldDescriptionsMap.get("page")),
          fieldWithPath("page.size").description(pagingFieldDescriptionsMap.get("page.size")),
          fieldWithPath("page.totalElements")
              .description(pagingFieldDescriptionsMap.get("page.totalElements")),
          fieldWithPath("page.totalPages")
              .description(pagingFieldDescriptionsMap.get("page.totalPages")),
          fieldWithPath("page.number").description(pagingFieldDescriptionsMap.get("page.number")),
          fieldWithPath("_links.first.href")
              .description(pagingFieldDescriptionsMap.get("_links.first.href")),
          fieldWithPath("_links.last.href")
              .description(pagingFieldDescriptionsMap.get("_links.last.href")),
          fieldWithPath("_links.prev.href")
              .description(pagingFieldDescriptionsMap.get("_links.prev.href")),
          fieldWithPath("_links.next.href")
              .description(pagingFieldDescriptionsMap.get("_links.next.href"))
        };
  }

  @Test
  void commonSummaryResourceFieldsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_CLASSIFICATIONS) + "?page=2&page-size=5")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "CommonSummaryResourceFields", responseFields(pagingFieldDescriptors)));
  }
}
