package pro.taskana.doc.api;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

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

import pro.taskana.rest.simplehistory.TaskHistoryRestConfiguration;

/** Generate documentation for the history event controller. */
@RunWith(SpringRunner.class)
@SpringBootTest(
    classes = TaskHistoryRestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskHistoryEventControllerRestDocumentation {

  @Rule public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();
  @LocalServerPort int port;
  @Autowired private WebApplicationContext context;

  private MockMvc mockMvc;

  private HashMap<String, String> taskHistoryEventFieldDescriptionsMap =
      new HashMap<String, String>();

  private FieldDescriptor[] allTaskHistoryEventFieldDescriptors;

  private FieldDescriptor[] taskHistoryEventFieldDescriptors;

  @Before
  public void setUp() {
    document("{methodName}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()));

    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.context)
            .apply(
                documentationConfiguration(this.restDocumentation)
                    .operationPreprocessors()
                    .withResponseDefaults(prettyPrint())
                    .withRequestDefaults(prettyPrint()))
            .build();

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
    taskHistoryEventFieldDescriptionsMap.put("comment", "");
    taskHistoryEventFieldDescriptionsMap.put("oldValue", "The old value");
    taskHistoryEventFieldDescriptionsMap.put("newValue", "The new value");
    taskHistoryEventFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
    taskHistoryEventFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
    taskHistoryEventFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
    taskHistoryEventFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
    taskHistoryEventFieldDescriptionsMap.put("oldData", "The old data");
    taskHistoryEventFieldDescriptionsMap.put("newData", "The new data");
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
          fieldWithPath("taskHistoryEvents[].taskHistoryId")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskHistoryId")),
          fieldWithPath("taskHistoryEvents[].businessProcessId")
              .description(taskHistoryEventFieldDescriptionsMap.get("businessProcessId")),
          fieldWithPath("taskHistoryEvents[].parentBusinessProcessId")
              .description(taskHistoryEventFieldDescriptionsMap.get("parentBusinessProcessId")),
          fieldWithPath("taskHistoryEvents[].taskId")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskId")),
          fieldWithPath("taskHistoryEvents[].eventType")
              .description(taskHistoryEventFieldDescriptionsMap.get("eventType")),
          fieldWithPath("taskHistoryEvents[].created")
              .description(taskHistoryEventFieldDescriptionsMap.get("created")),
          fieldWithPath("taskHistoryEvents[].userId")
              .description(taskHistoryEventFieldDescriptionsMap.get("userId")),
          fieldWithPath("taskHistoryEvents[].domain")
              .description(taskHistoryEventFieldDescriptionsMap.get("domain")),
          fieldWithPath("taskHistoryEvents[].workbasketKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("workbasketKey")),
          fieldWithPath("taskHistoryEvents[].porCompany")
              .description(taskHistoryEventFieldDescriptionsMap.get("porCompany")),
          fieldWithPath("taskHistoryEvents[].porSystem")
              .description(taskHistoryEventFieldDescriptionsMap.get("porSystem")),
          fieldWithPath("taskHistoryEvents[].porInstance")
              .description(taskHistoryEventFieldDescriptionsMap.get("porInstance")),
          fieldWithPath("taskHistoryEvents[].porValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("porValue")),
          fieldWithPath("taskHistoryEvents[].porType")
              .description(taskHistoryEventFieldDescriptionsMap.get("porType")),
          fieldWithPath("taskHistoryEvents[].taskClassificationKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskClassificationKey")),
          fieldWithPath("taskHistoryEvents[].taskClassificationCategory")
              .description(taskHistoryEventFieldDescriptionsMap.get("taskClassificationCategory")),
          fieldWithPath("taskHistoryEvents[].attachmentClassificationKey")
              .description(taskHistoryEventFieldDescriptionsMap.get("attachmentClassificationKey")),
          fieldWithPath("taskHistoryEvents[].comment")
              .description(taskHistoryEventFieldDescriptionsMap.get("comment")),
          fieldWithPath("taskHistoryEvents[].oldValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("oldValue")),
          fieldWithPath("taskHistoryEvents[].newValue")
              .description(taskHistoryEventFieldDescriptionsMap.get("newValue")),
          fieldWithPath("taskHistoryEvents[].custom1")
              .description(taskHistoryEventFieldDescriptionsMap.get("custom1")),
          fieldWithPath("taskHistoryEvents[].custom2")
              .description(taskHistoryEventFieldDescriptionsMap.get("custom2")),
          fieldWithPath("taskHistoryEvents[].custom3")
              .description(taskHistoryEventFieldDescriptionsMap.get("custom3")),
          fieldWithPath("taskHistoryEvents[].custom4")
              .description(taskHistoryEventFieldDescriptionsMap.get("custom4")),
          fieldWithPath("taskHistoryEvents[].oldData")
              .description(taskHistoryEventFieldDescriptionsMap.get("oldData")),
          fieldWithPath("taskHistoryEvents[].newData")
              .description(taskHistoryEventFieldDescriptionsMap.get("newData")),
          fieldWithPath("_links.self.href").ignored(),
            fieldWithPath("page.size").ignored(),
            fieldWithPath("page.totalElements").ignored(),
            fieldWithPath("page.totalPages").ignored(),
            fieldWithPath("page.number").ignored()
        };
  }

  @Test
  public void getAllTaskHistoryEventDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    "http://127.0.0.1:" + port + "/api/v1/task-history-event?page=1&page-size=3")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
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
                        + "/api/v1/task-history-event?business-process-id=BPI:02")
                .accept("application/hal+json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetSpecificTaskHistoryEventDocTest",
                responseFields(taskHistoryEventFieldDescriptors)));
  }
}
