package pro.taskana.doc.api;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate REST Docu for the WorkbasketAccessItemController. */
class WorkbasketAccessItemControllerRestDocumentation extends BaseRestDocumentation {

  private HashMap<String, String> accessItemFieldDescriptionsMap = new HashMap<String, String>();
  private FieldDescriptor[] accessItemFieldDescriptors;

  @BeforeEach
  void setUp() {

    accessItemFieldDescriptionsMap.put("accessItems.accessItemId", "Unique ID");
    accessItemFieldDescriptionsMap.put("accessItems.workbasketId", "The workbasket id");
    accessItemFieldDescriptionsMap.put(
        "accessItems.accessId",
        "The access id. This could be either a userid or a full qualified group id");
    accessItemFieldDescriptionsMap.put("accessItems.accessName", "The name");
    accessItemFieldDescriptionsMap.put("accessItems.workbasketKey", "The workbasket key");
    accessItemFieldDescriptionsMap.put(
        "accessItems.permRead", "The permission to read the information about the workbasket");
    accessItemFieldDescriptionsMap.put(
        "accessItems.permOpen", "The permission to view the content (the tasks) of a workbasket");
    accessItemFieldDescriptionsMap.put(
        "accessItems.permAppend",
        "The permission to add tasks to the workbasket "
            + "(required for creation and tranferring of tasks)");
    accessItemFieldDescriptionsMap.put(
        "accessItems.permTransfer",
        "The permission to transfer tasks (out of the current workbasket)");
    accessItemFieldDescriptionsMap.put(
        "accessItems.permDistribute", "The permission to distribute tasks from the workbasket");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom1", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom2", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom3", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom4", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom5", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom6", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom7", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom8", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom9", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom10", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom11", "");
    accessItemFieldDescriptionsMap.put("accessItems.permCustom12", "");
    accessItemFieldDescriptionsMap.put("_links.self.href", "Link to self");

    accessItemFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("accessItems[].accessItemId")
              .description(accessItemFieldDescriptionsMap.get("accessItems.accessItemId")),
          fieldWithPath("accessItems[].workbasketId")
              .description(accessItemFieldDescriptionsMap.get("accessItems.workbasketId")),
          fieldWithPath("accessItems[].accessId")
              .description(accessItemFieldDescriptionsMap.get("accessItems.accessId")),
          fieldWithPath("accessItems[].accessName")
              .description(accessItemFieldDescriptionsMap.get("accessItems.accessName")),
          fieldWithPath("accessItems[].workbasketKey")
              .description(accessItemFieldDescriptionsMap.get("accessItems.workbasketKey")),
          fieldWithPath("accessItems[].permRead")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permRead")),
          fieldWithPath("accessItems[].permOpen")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permOpen")),
          fieldWithPath("accessItems[].permAppend")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permAppend")),
          fieldWithPath("accessItems[].permTransfer")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permTransfer")),
          fieldWithPath("accessItems[].permDistribute")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permDistribute")),
          fieldWithPath("accessItems[].permCustom1")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom1")),
          fieldWithPath("accessItems[].permCustom2")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom2")),
          fieldWithPath("accessItems[].permCustom3")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom3")),
          fieldWithPath("accessItems[].permCustom4")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom4")),
          fieldWithPath("accessItems[].permCustom5")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom5")),
          fieldWithPath("accessItems[].permCustom6")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom6")),
          fieldWithPath("accessItems[].permCustom7")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom7")),
          fieldWithPath("accessItems[].permCustom8")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom8")),
          fieldWithPath("accessItems[].permCustom9")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom9")),
          fieldWithPath("accessItems[].permCustom10")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom10")),
          fieldWithPath("accessItems[].permCustom11")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom11")),
          fieldWithPath("accessItems[].permCustom12")
              .description(accessItemFieldDescriptionsMap.get("accessItems.permCustom12")),
          fieldWithPath("_links.self.href")
              .description(accessItemFieldDescriptionsMap.get("_links.self.href")),
        };
  }

  @Test
  void getWorkbasketAccessItemsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                        + "?sort-by=workbasket-key&order=asc&access-ids=user-2-2")
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetWorkbasketAccessItemsDocTest", responseFields(accessItemFieldDescriptors)));
  }

  @Test
  @DirtiesContext
  void removeWorkbasketAccessItemsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS)
                        + "?access-id=user-2-2")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("RemoveWorkbasketAccessItemsDocTest"));
  }
}
