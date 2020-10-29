package pro.taskana.doc.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
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
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate REST Documentation for the TaskController. */
class TaskControllerRestDocumentation extends BaseRestDocumentation {

  private final HashMap<String, String> taskFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] allTasksFieldDescriptors;
  private FieldDescriptor[] taskFieldDescriptors;
  private FieldDescriptor[] taskSubsetFieldDescriptors;
  private FieldDescriptor[] createTaskFieldDescriptors;

  @BeforeEach
  void setUp() {

    taskFieldDescriptionsMap.put("taskId", "Unique ID");
    taskFieldDescriptionsMap.put(
        "externalId",
        "External ID. Can be used to enforce idempotence at task creation. "
            + "Can identify an external task.");
    taskFieldDescriptionsMap.put("created", "The creation timestamp of the task in the system.");
    taskFieldDescriptionsMap.put(
        "claimed", "The timestamp of the last claim-operation on the task");
    taskFieldDescriptionsMap.put("completed", "The timestamp of the completion of the task");
    taskFieldDescriptionsMap.put("modified", "Timestamp of the last modification of the task");
    taskFieldDescriptionsMap.put(
        "planned",
        "Planned start of the task. The actual completion of the task "
            + "should be between PLANNED and DUE.");
    taskFieldDescriptionsMap.put(
        "due",
        "Timestamp when the task is due. The actual completion of the task "
            + "should be between PLANNED and DUE.");
    taskFieldDescriptionsMap.put("name", "The name of the task");
    taskFieldDescriptionsMap.put("creator", "");
    taskFieldDescriptionsMap.put("description", "The description of the task");
    taskFieldDescriptionsMap.put("note", "note");
    taskFieldDescriptionsMap.put("priority", "The priority of the task");
    taskFieldDescriptionsMap.put("state", "he state of the task. See (...)");
    taskFieldDescriptionsMap.put(
        "classificationSummary", "The <<classification-subset, Classification>> of the task");
    taskFieldDescriptionsMap.put(
        "workbasketSummary", "The <<workbasket-subset, Workbasket>> of the task");
    taskFieldDescriptionsMap.put("businessProcessId", "");
    taskFieldDescriptionsMap.put("parentBusinessProcessId", "");
    taskFieldDescriptionsMap.put(
        "owner", "The owner of the tasks. The owner is set upon claiming of the task.");
    taskFieldDescriptionsMap.put("primaryObjRef.id", "");
    taskFieldDescriptionsMap.put(
        "primaryObjRef.company", "The company referenced primary object belongs to.");
    taskFieldDescriptionsMap.put(
        "primaryObjRef.system",
        "The (kind of) system, the object resides in (e.g. SAP, MySystem A, ...) ");
    taskFieldDescriptionsMap.put(
        "primaryObjRef.systemInstance", "The instance of the system, the object resides in.");
    taskFieldDescriptionsMap.put(
        "primaryObjRef.type", "The type of the reference (contract, claim, policy, customer, ...)");
    taskFieldDescriptionsMap.put(
        "primaryObjRef.value", "The value of the primary object reference");
    taskFieldDescriptionsMap.put(
        "customAttributes",
        "A container for all additional information on the task in JSON representation");
    taskFieldDescriptionsMap.put(
        "callbackInfo",
        "A container for all callback information of the task in JSON representation");
    taskFieldDescriptionsMap.put("attachments", "");
    taskFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
    taskFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
    taskFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
    taskFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
    taskFieldDescriptionsMap.put("custom5", "A custom property with name \"5\"");
    taskFieldDescriptionsMap.put("custom6", "A custom property with name \"6\"");
    taskFieldDescriptionsMap.put("custom7", "A custom property with name \"7\"");
    taskFieldDescriptionsMap.put("custom8", "A custom property with name \"8\"");
    taskFieldDescriptionsMap.put("custom9", "A custom property with name \"9\"");
    taskFieldDescriptionsMap.put("custom10", "A custom property with name \"10\"");
    taskFieldDescriptionsMap.put("custom11", "A custom property with name \"11\"");
    taskFieldDescriptionsMap.put("custom12", "A custom property with name \"12\"");
    taskFieldDescriptionsMap.put("custom13", "A custom property with name \"13\"");
    taskFieldDescriptionsMap.put("custom14", "A custom property with name \"14\"");
    taskFieldDescriptionsMap.put("custom15", "A custom property with name \"15\"");
    taskFieldDescriptionsMap.put("custom16", "A custom property with name \"16\"");
    taskFieldDescriptionsMap.put("read", "Indicator if the task has been read");
    taskFieldDescriptionsMap.put("transferred", "Indicator if the task has been transferred");

    allTasksFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("tasks").description("An Array of <<task-subset, Task-Subsets>>"),
          fieldWithPath("_links").ignored(),
          fieldWithPath("_links.self").ignored(),
          fieldWithPath("_links.self.href").ignored(),
        };

    taskFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("taskId").description(taskFieldDescriptionsMap.get("taskId")),
          fieldWithPath("externalId").description(taskFieldDescriptionsMap.get("externalId")),
          fieldWithPath("created").description(taskFieldDescriptionsMap.get("created")),
          fieldWithPath("claimed")
              .description(taskFieldDescriptionsMap.get("claimed"))
              .type("String"),
          fieldWithPath("completed")
              .description(taskFieldDescriptionsMap.get("completed"))
              .type("String"),
          fieldWithPath("modified")
              .description(taskFieldDescriptionsMap.get("modified"))
              .type("String"),
          fieldWithPath("planned")
              .description(taskFieldDescriptionsMap.get("planned"))
              .type("String"),
          fieldWithPath("due").description(taskFieldDescriptionsMap.get("due")).type("String"),
          fieldWithPath("name").description(taskFieldDescriptionsMap.get("name")),
          fieldWithPath("creator").description(taskFieldDescriptionsMap.get("creator")),
          fieldWithPath("description").description(taskFieldDescriptionsMap.get("description")),
          fieldWithPath("note")
              .description(taskFieldDescriptionsMap.get("note"))
              .description("Some custom Note"),
          fieldWithPath("priority").description(taskFieldDescriptionsMap.get("priority")),
          fieldWithPath("state").description(taskFieldDescriptionsMap.get("state")),
          subsectionWithPath("classificationSummary")
              .description(taskFieldDescriptionsMap.get("classificationSummary")),
          subsectionWithPath("workbasketSummary")
              .description(taskFieldDescriptionsMap.get("workbasketSummary")),
          fieldWithPath("businessProcessId")
              .description(taskFieldDescriptionsMap.get("businessProcessId")),
          fieldWithPath("parentBusinessProcessId")
              .description(taskFieldDescriptionsMap.get("parentBusinessProcessId")),
          fieldWithPath("owner").description(taskFieldDescriptionsMap.get("owner")).type("String"),
          fieldWithPath("primaryObjRef.id")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.id"))
              .type("String"),
          fieldWithPath("primaryObjRef.company")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
          fieldWithPath("primaryObjRef.system")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
          fieldWithPath("primaryObjRef.systemInstance")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
          fieldWithPath("primaryObjRef.type")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
          fieldWithPath("primaryObjRef.value")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
          fieldWithPath("read").description(taskFieldDescriptionsMap.get("read")),
          fieldWithPath("transferred").description(taskFieldDescriptionsMap.get("transferred")),
          fieldWithPath("customAttributes")
              .description(taskFieldDescriptionsMap.get("customAttributes")),
          fieldWithPath("callbackInfo").description(taskFieldDescriptionsMap.get("callbackInfo")),
          fieldWithPath("attachments").description(taskFieldDescriptionsMap.get("attachments")),
          fieldWithPath("custom1")
              .description(taskFieldDescriptionsMap.get("custom1"))
              .type("String"),
          fieldWithPath("custom2")
              .description(taskFieldDescriptionsMap.get("custom2"))
              .type("String"),
          fieldWithPath("custom3")
              .description(taskFieldDescriptionsMap.get("custom3"))
              .type("String"),
          fieldWithPath("custom4")
              .description(taskFieldDescriptionsMap.get("custom4"))
              .type("String"),
          fieldWithPath("custom5")
              .description(taskFieldDescriptionsMap.get("custom5"))
              .type("String"),
          fieldWithPath("custom6")
              .description(taskFieldDescriptionsMap.get("custom6"))
              .type("String"),
          fieldWithPath("custom7")
              .description(taskFieldDescriptionsMap.get("custom7"))
              .type("String"),
          fieldWithPath("custom8")
              .description(taskFieldDescriptionsMap.get("custom8"))
              .type("String"),
          fieldWithPath("custom9")
              .description(taskFieldDescriptionsMap.get("custom9"))
              .type("String"),
          fieldWithPath("custom10")
              .description(taskFieldDescriptionsMap.get("custom10"))
              .type("String"),
          fieldWithPath("custom11")
              .description(taskFieldDescriptionsMap.get("custom11"))
              .type("String"),
          fieldWithPath("custom12")
              .description(taskFieldDescriptionsMap.get("custom12"))
              .type("String"),
          fieldWithPath("custom13")
              .description(taskFieldDescriptionsMap.get("custom13"))
              .type("String"),
          fieldWithPath("custom14")
              .description(taskFieldDescriptionsMap.get("custom14"))
              .type("String"),
          fieldWithPath("custom15")
              .description(taskFieldDescriptionsMap.get("custom15"))
              .type("String"),
          fieldWithPath("custom16")
              .description(taskFieldDescriptionsMap.get("custom16"))
              .type("String"),
          fieldWithPath("_links.self.href").ignored()
        };

    taskSubsetFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("taskId").description(taskFieldDescriptionsMap.get("taskId")),
          fieldWithPath("externalId").description(taskFieldDescriptionsMap.get("externalId")),
          fieldWithPath("created").description(taskFieldDescriptionsMap.get("created")),
          fieldWithPath("claimed").description(taskFieldDescriptionsMap.get("claimed")),
          fieldWithPath("completed")
              .description(taskFieldDescriptionsMap.get("completed"))
              .type("String"),
          fieldWithPath("modified")
              .description(taskFieldDescriptionsMap.get("modified"))
              .type("String"),
          fieldWithPath("planned")
              .description(taskFieldDescriptionsMap.get("planned"))
              .type("String"),
          fieldWithPath("due").description(taskFieldDescriptionsMap.get("due")).type("String"),
          fieldWithPath("name").description(taskFieldDescriptionsMap.get("name")),
          fieldWithPath("creator").description(taskFieldDescriptionsMap.get("creator")),
          fieldWithPath("description").ignored(),
          fieldWithPath("note")
              .description(taskFieldDescriptionsMap.get("note"))
              .description("Some custom Note"),
          fieldWithPath("priority").description(taskFieldDescriptionsMap.get("priority")),
          fieldWithPath("state").description(taskFieldDescriptionsMap.get("state")),
          subsectionWithPath("classificationSummary")
              .description(taskFieldDescriptionsMap.get("classificationSummary")),
          subsectionWithPath("workbasketSummary")
              .description(taskFieldDescriptionsMap.get("workbasketSummary")),
          fieldWithPath("businessProcessId")
              .description(taskFieldDescriptionsMap.get("businessProcessId")),
          fieldWithPath("parentBusinessProcessId")
              .description(taskFieldDescriptionsMap.get("parentBusinessProcessId")),
          fieldWithPath("owner").description(taskFieldDescriptionsMap.get("owner")),
          fieldWithPath("primaryObjRef.id")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.id"))
              .type("String"),
          fieldWithPath("primaryObjRef.company")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
          fieldWithPath("primaryObjRef.system")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
          fieldWithPath("primaryObjRef.systemInstance")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
          fieldWithPath("primaryObjRef.type")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
          fieldWithPath("primaryObjRef.value")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
          fieldWithPath("read").description(taskFieldDescriptionsMap.get("read")),
          fieldWithPath("transferred").description(taskFieldDescriptionsMap.get("transferred")),
          fieldWithPath("customAttributes").ignored(),
          fieldWithPath("callbackInfo").ignored(),
          fieldWithPath("attachments").description(taskFieldDescriptionsMap.get("attachments")),
          fieldWithPath("custom1").description(taskFieldDescriptionsMap.get("custom1")),
          fieldWithPath("custom2").description(taskFieldDescriptionsMap.get("custom2")),
          fieldWithPath("custom3").description(taskFieldDescriptionsMap.get("custom3")),
          fieldWithPath("custom4").description(taskFieldDescriptionsMap.get("custom4")),
          fieldWithPath("custom5").description(taskFieldDescriptionsMap.get("custom5")),
          fieldWithPath("custom6").description(taskFieldDescriptionsMap.get("custom6")),
          fieldWithPath("custom7").description(taskFieldDescriptionsMap.get("custom7")),
          fieldWithPath("custom8").description(taskFieldDescriptionsMap.get("custom8")),
          fieldWithPath("custom9").description(taskFieldDescriptionsMap.get("custom9")),
          fieldWithPath("custom10").description(taskFieldDescriptionsMap.get("custom10")),
          fieldWithPath("custom11").description(taskFieldDescriptionsMap.get("custom11")),
          fieldWithPath("custom12").description(taskFieldDescriptionsMap.get("custom12")),
          fieldWithPath("custom13").description(taskFieldDescriptionsMap.get("custom13")),
          fieldWithPath("custom14").description(taskFieldDescriptionsMap.get("custom14")),
          fieldWithPath("custom15").description(taskFieldDescriptionsMap.get("custom15")),
          fieldWithPath("custom16").description(taskFieldDescriptionsMap.get("custom16")),
          fieldWithPath("_links.self.href").ignored()
        };

    createTaskFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("classificationSummary")
              .description("The new classificationSummary for the task"),
          subsectionWithPath("workbasketSummary")
              .description("The new workbasketSummary for the task"),
          fieldWithPath("externalId")
              .description(taskFieldDescriptionsMap.get("externalId"))
              .type("String")
              .optional(),
          fieldWithPath("primaryObjRef.company")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.company")),
          fieldWithPath("primaryObjRef.system")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.system")),
          fieldWithPath("primaryObjRef.systemInstance")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.systemInstance")),
          fieldWithPath("primaryObjRef.type")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.type")),
          fieldWithPath("primaryObjRef.value")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.value")),
          fieldWithPath("created")
              .description(taskFieldDescriptionsMap.get("created"))
              .type("String")
              .optional(),
          fieldWithPath("claimed")
              .description(taskFieldDescriptionsMap.get("claimed"))
              .type("String")
              .optional(),
          fieldWithPath("completed")
              .description(taskFieldDescriptionsMap.get("completed"))
              .type("String")
              .optional(),
          fieldWithPath("modified")
              .description(taskFieldDescriptionsMap.get("modified"))
              .type("String")
              .optional(),
          fieldWithPath("planned")
              .description(taskFieldDescriptionsMap.get("planned"))
              .type("String")
              .optional(),
          fieldWithPath("due")
              .description(taskFieldDescriptionsMap.get("due"))
              .type("String")
              .optional(),
          fieldWithPath("name")
              .description(taskFieldDescriptionsMap.get("name"))
              .type("String")
              .optional(),
          fieldWithPath("creator")
              .description(taskFieldDescriptionsMap.get("creator"))
              .type("String")
              .optional(),
          fieldWithPath("description")
              .description(taskFieldDescriptionsMap.get("description"))
              .type("String")
              .optional(),
          fieldWithPath("note")
              .description(taskFieldDescriptionsMap.get("note"))
              .description("Some custom Note")
              .type("String")
              .optional(),
          fieldWithPath("priority")
              .description(taskFieldDescriptionsMap.get("priority"))
              .type("String")
              .optional(),
          fieldWithPath("state")
              .description(taskFieldDescriptionsMap.get("state"))
              .type("String")
              .optional(),
          fieldWithPath("businessProcessId")
              .description(taskFieldDescriptionsMap.get("businessProcessId"))
              .type("String")
              .optional(),
          fieldWithPath("parentBusinessProcessId")
              .description(taskFieldDescriptionsMap.get("parentBusinessProcessId"))
              .type("String")
              .optional(),
          fieldWithPath("owner")
              .description(taskFieldDescriptionsMap.get("owner"))
              .type("String")
              .optional(),
          fieldWithPath("primaryObjRef.id")
              .description(taskFieldDescriptionsMap.get("primaryObjRef.id"))
              .type("String")
              .optional(),
          fieldWithPath("customAttributes")
              .description(taskFieldDescriptionsMap.get("customAttributes"))
              .type("Object")
              .optional(),
          fieldWithPath("callbackInfo")
              .description(taskFieldDescriptionsMap.get("callbackInfo"))
              .type("Object")
              .optional(),
          fieldWithPath("attachments")
              .description(taskFieldDescriptionsMap.get("attachments"))
              .type("Array")
              .optional(),
          fieldWithPath("custom1")
              .description(taskFieldDescriptionsMap.get("custom1"))
              .type("String")
              .optional(),
          fieldWithPath("custom2")
              .description(taskFieldDescriptionsMap.get("custom2"))
              .type("String")
              .optional(),
          fieldWithPath("custom3")
              .description(taskFieldDescriptionsMap.get("custom3"))
              .type("String")
              .optional(),
          fieldWithPath("custom4")
              .description(taskFieldDescriptionsMap.get("custom4"))
              .type("String")
              .optional(),
          fieldWithPath("custom5")
              .description(taskFieldDescriptionsMap.get("custom5"))
              .type("String")
              .optional(),
          fieldWithPath("custom6")
              .description(taskFieldDescriptionsMap.get("custom6"))
              .type("String")
              .optional(),
          fieldWithPath("custom7")
              .description(taskFieldDescriptionsMap.get("custom7"))
              .type("String")
              .optional(),
          fieldWithPath("custom8")
              .description(taskFieldDescriptionsMap.get("custom8"))
              .type("String")
              .optional(),
          fieldWithPath("custom9")
              .description(taskFieldDescriptionsMap.get("custom9"))
              .type("String")
              .optional(),
          fieldWithPath("custom10")
              .description(taskFieldDescriptionsMap.get("custom10"))
              .type("String")
              .optional(),
          fieldWithPath("custom11")
              .description(taskFieldDescriptionsMap.get("custom11"))
              .type("String")
              .optional(),
          fieldWithPath("custom12")
              .description(taskFieldDescriptionsMap.get("custom12"))
              .type("String")
              .optional(),
          fieldWithPath("custom13")
              .description(taskFieldDescriptionsMap.get("custom13"))
              .type("String")
              .optional(),
          fieldWithPath("custom14")
              .description(taskFieldDescriptionsMap.get("custom14"))
              .type("String")
              .optional(),
          fieldWithPath("custom15")
              .description(taskFieldDescriptionsMap.get("custom15"))
              .type("String")
              .optional(),
          fieldWithPath("custom16")
              .description(taskFieldDescriptionsMap.get("custom16"))
              .type("String")
              .optional(),
          fieldWithPath("read")
              .description(taskFieldDescriptionsMap.get("read"))
              .type("Boolean")
              .optional(),
          fieldWithPath("transferred")
              .description(taskFieldDescriptionsMap.get("transferred"))
              .type("Boolean")
              .optional()
        };
  }

  @Test
  void getAllTasksDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_TASKS) + "?por.type=VNR&por.value=22334455")
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllTasksDocTest", responseFields(allTasksFieldDescriptors)));
  }

  @Test
  void getSpecificTaskDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"))
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetSpecificTaskDocTest", responseFields(taskFieldDescriptors)));
  }

  @Test
  void taskSubSetDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"))
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "TaskSubset", responseFields(taskSubsetFieldDescriptors)));
  }

  @Test
  void updateTaskDocTest() throws Exception {
    URL url =
        new URL(
            restHelper.toUrl(
                RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", ADMIN_CREDENTIALS);
    assertEquals(200, con.getResponseCode());

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8));
    String inputLine;
    StringBuilder content = new StringBuilder();
    while ((inputLine = in.readLine()) != null) {
      content.append(inputLine);
    }
    in.close();
    con.disconnect();
    String originalTask = content.toString();

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.put(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"))
                .header("Authorization", ADMIN_CREDENTIALS)
                .contentType("application/json")
                .content(originalTask))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "UpdateTaskDocTest",
                requestFields(taskFieldDescriptors),
                responseFields(taskFieldDescriptors)));
  }

  @Test
  void selectAndClaimTaskDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post(
                    restHelper.toUrl(RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM) + "?custom14=abc")
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "SelectAndClaimTaskDocTest", responseFields(taskFieldDescriptors)));
  }

  @Test
  void createAndDeleteTaskDocTest() throws Exception {

    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(RestEndpoints.URL_TASKS))
                    .contentType("application/hal+json")
                    .content(
                        "{\"classificationSummary\":{\"key\":\"L11010\"},"
                            + "\"workbasketSummary\":"
                            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
                            + "\"primaryObjRef\":{\"company\":\"MyCompany1\","
                            + "\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\","
                            + "\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "CreateTaskDocTest",
                    requestFields(createTaskFieldDescriptors),
                    responseFields(taskFieldDescriptors)))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("TKI:"), content.indexOf("TKI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(RestEndpoints.URL_TASKS_ID, newId))
                .header("Authorization", "Basic YWRtaW46YWRtaW4=")) // admin
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteTaskDocTest"));
  }

  @Test
  void deleteTasksDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_TASKS)
                        + "?task-id=TKI:000000000000000000000000000000000036,"
                        + "TKI:000000000000000000000000000000000037,"
                        + "TKI:000000000000000000000000000000000038"
                        + "&custom14=abc")
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "DeleteTasksDocTest", responseFields(allTasksFieldDescriptors)));
  }

  @Test
  void claimTaskDocTest() throws Exception {

    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(RestEndpoints.URL_TASKS))
                    .contentType("application/hal+json")
                    .content(
                        "{\"classificationSummary\":{\"key\":\"L11010\"},"
                            + "\"workbasketSummary\":"
                            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
                            + "\"primaryObjRef\":{\"company\":\"MyCompany1\","
                            + "\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\","
                            + "\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcRestDocumentation.document("temp"))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("TKI:"), content.indexOf("TKI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post(
                    restHelper.toUrl(RestEndpoints.URL_TASKS_ID_CLAIM, newId))
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS)
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "ClaimTaskDocTest", responseFields(taskFieldDescriptors)));
  }

  @Test
  void cancelClaimTaskDocTest() throws Exception {

    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(RestEndpoints.URL_TASKS))
                    .contentType("application/hal+json")
                    .content(
                        "{\"classificationSummary\":{\"key\":\"L11010\"},"
                            + "\"workbasketSummary\":"
                            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
                            + "\"primaryObjRef\":{\"company\":\"MyCompany1\","
                            + "\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\","
                            + "\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcRestDocumentation.document("temp"))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("TKI:"), content.indexOf("TKI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(RestEndpoints.URL_TASKS_ID_CLAIM, newId))
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS)
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "CancelClaimTaskDocTest", responseFields(taskFieldDescriptors)));
  }

  @Test
  void completeTaskDocTest() throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(RestEndpoints.URL_TASKS))
                    .contentType("application/hal+json")
                    .content(
                        "{\"classificationSummary\":{\"key\":\"L11010\"},"
                            + "\"workbasketSummary\":"
                            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
                            + "\"primaryObjRef\":{\"company\":\"MyCompany1\","
                            + "\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\","
                            + "\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(MockMvcRestDocumentation.document("temp"))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("TKI:"), content.indexOf("TKI:") + 40);
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post(
                    restHelper.toUrl(RestEndpoints.URL_TASKS_ID_COMPLETE, newId))
                .accept("application/hal+json")
                .header("Authorization", ADMIN_CREDENTIALS)
                .content("{}"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "CompleteTaskDocTest", responseFields(taskFieldDescriptors)));
  }

  @Test
  void transferTaskDocTest() throws Exception {
    MvcResult result =
        this.mockMvc
            .perform(
                RestDocumentationRequestBuilders.post(restHelper.toUrl(RestEndpoints.URL_TASKS))
                    .contentType("application/hal+json")
                    .content(
                        "{\"classificationSummary\":{\"key\":\"L11010\"},"
                            + "\"workbasketSummary\":"
                            + "{\"workbasketId\":\"WBI:100000000000000000000000000000000004\"},"
                            + "\"primaryObjRef\":{\"company\":\"MyCompany1\","
                            + "\"system\":\"MySystem1\",\"systemInstance\":\"MyInstance1\","
                            + "\"type\":\"MyType1\",\"value\":\"00000001\"}}")
                    .header("Authorization", ADMIN_CREDENTIALS))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andDo(
                MockMvcRestDocumentation.document(
                    "TransferTaskDocTest", responseFields(taskFieldDescriptors)))
            .andReturn();

    String content = result.getResponse().getContentAsString();
    String newId = content.substring(content.indexOf("TKI:"), content.indexOf("TKI:") + 40);

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post(
                    restHelper.toUrl(
                        RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
                        newId,
                        "WBI:100000000000000000000000000000000001"))
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "TransferTaskDocTest", responseFields(taskFieldDescriptors)));
  }
}
