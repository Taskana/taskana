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

import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate documentation for the history event controller. */
public class TaskHistoryEventControllerRestDocumentation extends BaseRestDocumentation {


  private final HashMap<String, String> taskHistoryEventFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] allTaskHistoryEventFieldDescriptors;
  private FieldDescriptor[] taskHistoryEventFieldDescriptors;

  @BeforeEach
  public void setUp() {
    taskHistoryEventFieldDescriptionsMap.put("taskHistoryId", "Unique ID");
    taskHistoryEventFieldDescriptionsMap.put("businessProcessId", "The id of the business process");
    taskHistoryEventFieldDescriptionsMap.put(
        "parentBusinessProcessId", "The id of the parent business process");
    taskHistoryEventFieldDescriptionsMap.put("taskId", "The id of the task");
    taskHistoryEventFieldDescriptionsMap.put("eventType", "The type of the event");
    taskHistoryEventFieldDescriptionsMap.put("created", "The time was created");
    taskHistoryEventFieldDescriptionsMap.put("userId", "The id of the user");
    taskHistoryEventFieldDescriptionsMap.put("domain", "Domain");
    taskHistoryEventFieldDescriptionsMap.put("workbasketKey", "The key of workbasket");
    taskHistoryEventFieldDescriptionsMap.put("porCompany", "");
    taskHistoryEventFieldDescriptionsMap.put("porSystem", "");
    taskHistoryEventFieldDescriptionsMap.put("porInstance", "");
    taskHistoryEventFieldDescriptionsMap.put("porValue", "");
    taskHistoryEventFieldDescriptionsMap.put("porType", "");
    taskHistoryEventFieldDescriptionsMap.put(
        "taskClassificationKey", "The key of classification task");
    taskHistoryEventFieldDescriptionsMap.put(
        "taskClassificationCategory", "The category of classification");
    taskHistoryEventFieldDescriptionsMap.put("attachmentClassificationKey", "");
    taskHistoryEventFieldDescriptionsMap.put("oldValue", "The old value");
    taskHistoryEventFieldDescriptionsMap.put("newValue", "The new value");
    taskHistoryEventFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
    taskHistoryEventFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
    taskHistoryEventFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
    taskHistoryEventFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
    taskHistoryEventFieldDescriptionsMap.put("details", "details of changes within the task");

    taskHistoryEventFieldDescriptionsMap.put(
        "_links.self.href", "The links of this task history event");
    taskHistoryEventFieldDescriptionsMap.put(
        "_links.allTaskHistoryEvent.href", "Link to all task history event");
    taskHistoryEventFieldDescriptionsMap.put("_links.first.href", "Link to the first result");
    taskHistoryEventFieldDescriptionsMap.put("_links.last.href", "Link to the last result");

    allTaskHistoryEventFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("taskHistoryEvents").description("An array of Task history event"),
          fieldWithPath("_links.allTaskHistoryEvent.href").ignored(),
          fieldWithPath("_links.self.href").ignored(),
          fieldWithPath("_links.first.href").ignored(),
          fieldWithPath("_links.last.href").ignored(),
          fieldWithPath("_links.next.href").ignored(),
          fieldWithPath("page.size").ignored(),
          fieldWithPath("page.totalElements").ignored(),
          fieldWithPath("page.totalPages").ignored(),
          fieldWithPath("page.number").ignored()
        };

    taskHistoryEventFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("taskHistoryId")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskHistoryId")),
          fieldWithPath("businessProcessId")
              .description(taskHistoryEventFieldDescriptionsMap.get("businessProcessId")),
          fieldWithPath("parentBusinessProcessId")
              .description(taskHistoryEventFieldDescriptionsMap.get("parentBusinessProcessId")),
          fieldWithPath("taskId").description(taskHistoryEventFieldDescriptionsMap.get("taskId")),
          fieldWithPath("eventType")
              .description(taskHistoryEventFieldDescriptionsMap.get("eventType")),
          fieldWithPath("created").description(taskHistoryEventFieldDescriptionsMap.get("created")),
          fieldWithPath("userId").description(taskHistoryEventFieldDescriptionsMap.get("userId")),
          fieldWithPath("domain").description(taskHistoryEventFieldDescriptionsMap.get("domain")),
          fieldWithPath("workbasketKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("workbasketKey")),
          fieldWithPath("porCompany")
              .description(taskHistoryEventFieldDescriptionsMap.get("porCompany")),
          fieldWithPath("porSystem")
              .description(taskHistoryEventFieldDescriptionsMap.get("porSystem")),
          fieldWithPath("porInstance")
              .description(taskHistoryEventFieldDescriptionsMap.get("porInstance")),
          fieldWithPath("porValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("porValue")),
          fieldWithPath("porType").description(taskHistoryEventFieldDescriptionsMap.get("porType")),
          fieldWithPath("taskClassificationKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskClassificationKey")),
          fieldWithPath("taskClassificationCategory")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskClassificationCategory")),
          fieldWithPath("attachmentClassificationKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("attachmentClassificationKey")),
          fieldWithPath("oldValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("oldValue")),
          fieldWithPath("newValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("newValue")),
          fieldWithPath("custom1").description(taskHistoryEventFieldDescriptionsMap.get("custom1")),
          fieldWithPath("custom2").description(taskHistoryEventFieldDescriptionsMap.get("custom2")),
          fieldWithPath("custom3").description(taskHistoryEventFieldDescriptionsMap.get("custom3")),
          fieldWithPath("custom4").description(taskHistoryEventFieldDescriptionsMap.get("custom4")),
          fieldWithPath("details").description(taskHistoryEventFieldDescriptionsMap.get("details")),
          fieldWithPath("_links.self.href").ignored()
        };
  }

  @Test
  public void getAllTaskHistoryEventDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    "http://127.0.0.1:" + port + "/api/v1/task-history-event?page=1&page-size=3")
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllTaskHistoryEventDocTest",
                responseFields(allTaskHistoryEventFieldDescriptors)));
  }

  @Test
  public void getSpecificTaskHistoryEventDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    "http://127.0.0.1:"
                        + port
                        + "/api/v1/task-history-event/THI:000000000000000000000000000000000000")
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetSpecificTaskHistoryEventDocTest",
                responseFields(taskHistoryEventFieldDescriptors)));
  }
}
