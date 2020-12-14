package pro.taskana.doc.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

class TaskCommentControllerRestDocumentation extends BaseRestDocumentation {

  private final HashMap<String, String> taskCommentFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] allTaskCommentsFieldDescriptors;
  private FieldDescriptor[] taskCommentFieldDescriptors;
  private FieldDescriptor[] createTaskCommentFieldDescriptors;

  @BeforeEach
  void setUp() {

    taskCommentFieldDescriptionsMap.put("taskCommentId", "Unique ID");
    taskCommentFieldDescriptionsMap.put(
        "taskId", "Task ID. Can identify the task to which the comment belongs");
    taskCommentFieldDescriptionsMap.put("textField", "The content of the actual comment");
    taskCommentFieldDescriptionsMap.put("creator", "The creator of the task comment");
    taskCommentFieldDescriptionsMap.put(
        "created", "The creation timestamp of the task comment in the system.");
    taskCommentFieldDescriptionsMap.put(
        "modified", "Timestamp of the last modification of the task comment");

    taskCommentFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("taskCommentId")
              .description(taskCommentFieldDescriptionsMap.get("taskCommentId")),
          fieldWithPath("taskId").description(taskCommentFieldDescriptionsMap.get("taskId")),
          fieldWithPath("textField").description(taskCommentFieldDescriptionsMap.get("textField")),
          fieldWithPath("creator").description(taskCommentFieldDescriptionsMap.get("creator")),
          fieldWithPath("created")
              .description(taskCommentFieldDescriptionsMap.get("created"))
              .type("String"),
          fieldWithPath("modified")
              .description(taskCommentFieldDescriptionsMap.get("modified"))
              .type("String"),
          fieldWithPath("_links").ignored(),
          fieldWithPath("_links.self").ignored(),
          fieldWithPath("_links.self.href").ignored()
        };

    createTaskCommentFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("taskId").description(taskCommentFieldDescriptionsMap.get("taskId")),
          fieldWithPath("textField").description(taskCommentFieldDescriptionsMap.get("textField")),
          fieldWithPath("creator")
              .description(taskCommentFieldDescriptionsMap.get("creator"))
              .type("String")
              .optional(),
          fieldWithPath("created")
              .description(taskCommentFieldDescriptionsMap.get("created"))
              .type("String")
              .optional(),
          fieldWithPath("modified")
              .description(taskCommentFieldDescriptionsMap.get("modified"))
              .type("String")
              .optional(),
        };

    allTaskCommentsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath(TaskanaPagedModelKeys.TASK_COMMENTS.getPropertyName())
              .description("An Array of task comments"),
          fieldWithPath("_links.self.href").ignored()
        };
  }

  @Test
  void getAllTaskCommentsForSpecificTaskDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASK_COMMENTS,
                        "TKI:000000000000000000000000000000000000"))
                .accept(MediaTypes.HAL_JSON)
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllTaskCommentsForSpecificTaskDocTest",
                responseFields(allTaskCommentsFieldDescriptors)));
  }

  @Test
  void getSpecificTaskCommentDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000"))
                .accept(MediaTypes.HAL_JSON)
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetSpecificTaskCommentDocTest", responseFields(taskCommentFieldDescriptors)));
  }

  @Test
  void updateTaskCommentDocTest() throws Exception {
    URL url =
        new URL(
            restHelper.toUrl(
                RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000"));

    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", ADMIN_CREDENTIALS);
    assertThat(con.getResponseCode()).isEqualTo(200);

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    String originalTaskComment = content.toString();
    String modifiedTaskComment =
        originalTaskComment.replace("some text in textfield", "updated text in textfield");

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.put(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000"))
                .header("Authorization", ADMIN_CREDENTIALS)
                .contentType(MediaTypes.HAL_JSON)
                .content(modifiedTaskComment))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "UpdateTaskCommentDocTest",
                requestFields(taskCommentFieldDescriptors),
                responseFields(taskCommentFieldDescriptors)));
  }

  @Test
  void createAndDeleteTaskCommentDocTest() throws Exception {

    String createTaskCommentContent =
        "{ \"taskId\" : \"TKI:000000000000000000000000000000000000\",\n"
            + "  \"textField\" : \"some text in textfield\"} ";

    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(
                        restHelper.toUrl(
                            RestEndpoints.URL_TASK_COMMENTS,
                            "TKI:000000000000000000000000000000000000"))
                    .contentType(MediaTypes.HAL_JSON)
                    .content(createTaskCommentContent)
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "CreateTaskCommentDocTest",
                    requestFields(createTaskCommentFieldDescriptors),
                    responseFields(taskCommentFieldDescriptors)))
            .andReturn();

    String resultContent = result.getResponse().getContentAsString();
    String newId =
        resultContent.substring(resultContent.indexOf("TCI:"), resultContent.indexOf("TCI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(RestEndpoints.URL_TASK_COMMENT, newId))
                .header("Authorization", ADMIN_CREDENTIALS)) // admin
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteTaskCommentDocTest"));
  }
}
