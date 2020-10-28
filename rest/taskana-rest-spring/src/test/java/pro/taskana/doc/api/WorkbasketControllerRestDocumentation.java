package pro.taskana.doc.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.is;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.rest.models.TaskanaPagedModelKeys;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate REST Documentatioon for the WorkbasketController. */
class WorkbasketControllerRestDocumentation extends BaseRestDocumentation {

  public static final String PROPERTY_NAME = TaskanaPagedModelKeys.ACCESS_ITEMS.getPropertyName();
  // HashMaps to store the field descriptions centrally for multiple uses
  private final HashMap<String, String> workbasketFieldDescriptionsMap = new HashMap<>();
  private final HashMap<String, String> accessItemFieldDescriptionsMap = new HashMap<>();

  private FieldDescriptor[] allWorkbasketsFieldDescriptors;
  private FieldDescriptor[] workbasketFieldDescriptors;
  private FieldDescriptor[] workbasketSubsetFieldDescriptors;
  private FieldDescriptor[] allWorkbasketAccessItemsFieldDescriptors;
  private FieldDescriptor[] accessItemFieldDescriptors;
  private FieldDescriptor[] allDistributionTargetsFieldDescriptors;
  private FieldDescriptor[] createWorkbasketFieldDescriptors;

  @BeforeEach
  void setUp() {

    workbasketFieldDescriptionsMap.put("workbasketId", "Unique ID");
    workbasketFieldDescriptionsMap.put("key", "");
    workbasketFieldDescriptionsMap.put("name", "The name of the workbasket");
    workbasketFieldDescriptionsMap.put("domain", "");
    workbasketFieldDescriptionsMap.put("type", "");
    workbasketFieldDescriptionsMap.put("description", "The description of the workbasket");
    workbasketFieldDescriptionsMap.put(
        "owner",
        "The owner of the workbasket. The owner is responsible for the on-time completion "
            + "of all tasks in the workbasket.");
    workbasketFieldDescriptionsMap.put(
        "orgLevel1",
        "The first Org Level (the top one)\nThe Org Level is an association with an org "
            + "hierarchy level in the organization. The values are used for monitoring "
            + "and statistical purposes and should reflect the responsibility of the "
            + "tasks in the workbasket.");
    workbasketFieldDescriptionsMap.put("orgLevel2", "The second Org Level");
    workbasketFieldDescriptionsMap.put("orgLevel3", "The third Org Level");
    workbasketFieldDescriptionsMap.put("orgLevel4", "The fourth Org Level (the lowest one).");
    workbasketFieldDescriptionsMap.put("markedForDeletion", "can this be deleted");
    workbasketFieldDescriptionsMap.put(
        "created", "The creation timestamp of the workbasket in the system.");
    workbasketFieldDescriptionsMap.put(
        "modified", "Timestamp of the last modification of the workbasket");
    workbasketFieldDescriptionsMap.put("custom1", "A custom property with name \"1\"");
    workbasketFieldDescriptionsMap.put("custom2", "A custom property with name \"2\"");
    workbasketFieldDescriptionsMap.put("custom3", "A custom property with name \"3\"");
    workbasketFieldDescriptionsMap.put("custom4", "A custom property with name \"4\"");
    workbasketFieldDescriptionsMap.put(
        "_links.distributionTargets.href", "The Distribution-Targets of the workbasket");
    workbasketFieldDescriptionsMap.put(
        "_links.removeDistributionTargets.href",
        "Link to remove all distribution-targets from the workbasket");
    workbasketFieldDescriptionsMap.put(
        "_links." + PROPERTY_NAME + ".href", "The Access-Items of the workbasket");
    workbasketFieldDescriptionsMap.put("_links.allWorkbaskets.href", "Link to all workbaskets");

    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".accessItemId", "Unique ID");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".workbasketId", "The workbasket");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".workbasketKey", "The workbasket key");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".accessId",
        "The access id, this ACL entry refers to. This could be either a userid or a "
            + "full qualified group id (both lower case)");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".accessName", "");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".permRead", "The permission to read the information about the workbasket");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".permOpen",
        "The permission to view the content (the tasks) of a workbasket");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".permAppend",
        "The permission to add tasks to the workbasket (required for creation "
            + "and transferring of tasks)");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".permTransfer",
        "The permission to transfer tasks (out of the current workbasket)");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + ".permDistribute",
        "The permission to distribute tasks from the workbasket");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom1", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom2", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom3", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom4", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom5", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom6", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom7", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom8", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom9", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom10", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom11", "");
    accessItemFieldDescriptionsMap.put(PROPERTY_NAME + ".permCustom12", "");
    accessItemFieldDescriptionsMap.put(
        PROPERTY_NAME + "._links.workbasket.href", "Link to the workbasket");

    allWorkbasketsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("workbaskets")
              .description("An Array of <<workbasket-subset, Workbasket-Subsets>>"),
          fieldWithPath("_links.self.href").ignored(),
        };

    workbasketFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("workbasketId")
              .description(workbasketFieldDescriptionsMap.get("workbasketId")),
          fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
          fieldWithPath("created").description(workbasketFieldDescriptionsMap.get("created")),
          fieldWithPath("modified").description(workbasketFieldDescriptionsMap.get("modified")),
          fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
          fieldWithPath("description")
              .description(workbasketFieldDescriptionsMap.get("description")),
          fieldWithPath("owner").description(workbasketFieldDescriptionsMap.get("owner")),
          fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
          fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
          fieldWithPath("custom1").description(workbasketFieldDescriptionsMap.get("custom1")),
          fieldWithPath("custom2").description(workbasketFieldDescriptionsMap.get("custom2")),
          fieldWithPath("custom3").description(workbasketFieldDescriptionsMap.get("custom3")),
          fieldWithPath("custom4").description(workbasketFieldDescriptionsMap.get("custom4")),
          fieldWithPath("orgLevel1").description(workbasketFieldDescriptionsMap.get("orgLevel1")),
          fieldWithPath("orgLevel2").description(workbasketFieldDescriptionsMap.get("orgLevel2")),
          fieldWithPath("orgLevel3").description(workbasketFieldDescriptionsMap.get("orgLevel3")),
          fieldWithPath("orgLevel4").description(workbasketFieldDescriptionsMap.get("orgLevel4")),
          fieldWithPath("markedForDeletion")
              .description(workbasketFieldDescriptionsMap.get("markedForDeletion")),
          fieldWithPath("_links.distributionTargets.href")
              .description(workbasketFieldDescriptionsMap.get("_links.distributionTargets.href")),
          fieldWithPath("_links.removeDistributionTargets.href")
              .description(
                  workbasketFieldDescriptionsMap.get("_links.removeDistributionTargets.href")),
          fieldWithPath("_links." + PROPERTY_NAME + ".href")
              .description(workbasketFieldDescriptionsMap.get("_links." + PROPERTY_NAME + ".href")),
          fieldWithPath("_links.allWorkbaskets.href")
              .description(workbasketFieldDescriptionsMap.get("_links.allWorkbaskets.href")),
          fieldWithPath("_links.self.href").ignored()
        };

    workbasketSubsetFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("workbasketId")
              .description(workbasketFieldDescriptionsMap.get("workbasketId")),
          fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
          fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
          fieldWithPath("description")
              .description(workbasketFieldDescriptionsMap.get("description")),
          fieldWithPath("owner").description(workbasketFieldDescriptionsMap.get("owner")),
          fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
          fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
          fieldWithPath("custom1").description(workbasketFieldDescriptionsMap.get("custom1")),
          fieldWithPath("custom2").description(workbasketFieldDescriptionsMap.get("custom2")),
          fieldWithPath("custom3").description(workbasketFieldDescriptionsMap.get("custom3")),
          fieldWithPath("custom4").description(workbasketFieldDescriptionsMap.get("custom4")),
          fieldWithPath("orgLevel1").description(workbasketFieldDescriptionsMap.get("orgLevel1")),
          fieldWithPath("orgLevel2").description(workbasketFieldDescriptionsMap.get("orgLevel2")),
          fieldWithPath("orgLevel3").description(workbasketFieldDescriptionsMap.get("orgLevel3")),
          fieldWithPath("orgLevel4").description(workbasketFieldDescriptionsMap.get("orgLevel4")),
          fieldWithPath("markedForDeletion")
              .description(workbasketFieldDescriptionsMap.get("markedForDeletion")),
          fieldWithPath("created").ignored(),
          fieldWithPath("modified").ignored(),
          fieldWithPath("_links.distributionTargets.href").ignored(),
          fieldWithPath("_links.removeDistributionTargets.href").ignored(),
          fieldWithPath("_links." + PROPERTY_NAME + ".href").ignored(),
          fieldWithPath("_links.allWorkbaskets.href").ignored(),
          fieldWithPath("_links.self.href").ignored()
        };

    accessItemFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath(PROPERTY_NAME + "[].accessItemId")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".accessItemId")),
          fieldWithPath(PROPERTY_NAME + "[].workbasketId")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".workbasketId")),
          fieldWithPath(PROPERTY_NAME + "[].workbasketKey")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".workbasketKey")),
          fieldWithPath(PROPERTY_NAME + "[].accessId")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".accessId")),
          fieldWithPath(PROPERTY_NAME + "[].accessName")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".accessName")),
          fieldWithPath(PROPERTY_NAME + "[].permRead")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permRead")),
          fieldWithPath(PROPERTY_NAME + "[].permOpen")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permOpen")),
          fieldWithPath(PROPERTY_NAME + "[].permAppend")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permAppend")),
          fieldWithPath(PROPERTY_NAME + "[].permTransfer")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permTransfer")),
          fieldWithPath(PROPERTY_NAME + "[].permDistribute")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permDistribute")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom1")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom1")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom2")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom2")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom3")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom3")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom4")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom4")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom5")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom5")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom6")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom6")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom7")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom7")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom8")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom8")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom9")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom9")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom10")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom10")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom11")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom11")),
          fieldWithPath(PROPERTY_NAME + "[].permCustom12")
              .description(accessItemFieldDescriptionsMap.get(PROPERTY_NAME + ".permCustom12")),
          fieldWithPath("_links.self.href").ignored(),
          fieldWithPath("_links.workbasket.href").ignored()
        };

    allWorkbasketAccessItemsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath(PROPERTY_NAME)
              .description("An array of <<access-item, Access Items>>"),
          fieldWithPath("_links.self.href").ignored(),
          fieldWithPath("_links.workbasket.href").ignored()
        };

    allDistributionTargetsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("distributionTargets")
              .description("An array of <<workbasket-subset, workbasket subsets>>"),
          fieldWithPath("_links.self.href").ignored(),
        };

    createWorkbasketFieldDescriptors =
        new FieldDescriptor[] {
          fieldWithPath("key").description(workbasketFieldDescriptionsMap.get("key")),
          fieldWithPath("name").description(workbasketFieldDescriptionsMap.get("name")),
          fieldWithPath("domain").description(workbasketFieldDescriptionsMap.get("domain")),
          fieldWithPath("type").description(workbasketFieldDescriptionsMap.get("type")),
          fieldWithPath("description")
              .description(workbasketFieldDescriptionsMap.get("description"))
              .type("String")
              .optional(),
          fieldWithPath("owner")
              .description(workbasketFieldDescriptionsMap.get("owner"))
              .type("String")
              .optional(),
          fieldWithPath("orgLevel1")
              .description(workbasketFieldDescriptionsMap.get("orgLevel1"))
              .type("String")
              .optional(),
          fieldWithPath("orgLevel2")
              .description(workbasketFieldDescriptionsMap.get("orgLevel2"))
              .type("String")
              .optional(),
          fieldWithPath("orgLevel3")
              .description(workbasketFieldDescriptionsMap.get("orgLevel3"))
              .type("String")
              .optional(),
          fieldWithPath("orgLevel4")
              .description(workbasketFieldDescriptionsMap.get("orgLevel4"))
              .type("String")
              .optional(),
          fieldWithPath("created")
              .description(workbasketFieldDescriptionsMap.get("created"))
              .type("String")
              .optional(),
          fieldWithPath("modified")
              .description(workbasketFieldDescriptionsMap.get("modified"))
              .type("String")
              .optional(),
          fieldWithPath("custom1")
              .description(workbasketFieldDescriptionsMap.get("custom1"))
              .type("String")
              .optional(),
          fieldWithPath("custom2")
              .description(workbasketFieldDescriptionsMap.get("custom2"))
              .type("String")
              .optional(),
          fieldWithPath("custom3")
              .description(workbasketFieldDescriptionsMap.get("custom3"))
              .type("String")
              .optional(),
          fieldWithPath("custom4")
              .description(workbasketFieldDescriptionsMap.get("custom4"))
              .type("String")
              .optional(),
          fieldWithPath("_links.distributionTargets.href")
              .description(workbasketFieldDescriptionsMap.get("_links.distributionTargets.href"))
              .type("String")
              .optional(),
          fieldWithPath("_links.removeDistributionTargets.href")
              .description(
                  workbasketFieldDescriptionsMap.get("_links.removeDistributionTargets.href"))
              .type("String")
              .optional(),
          fieldWithPath("_links." + PROPERTY_NAME + ".href")
              .description(workbasketFieldDescriptionsMap.get("_links." + PROPERTY_NAME + ".href"))
              .type("String")
              .optional(),
          fieldWithPath("_links.allWorkbaskets.href")
              .description(workbasketFieldDescriptionsMap.get("_links.allWorkbaskets.href"))
              .type("String")
              .optional()
        };
  }

  @Test
  void getAllWorkbasketsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(Mapping.URL_WORKBASKET) + "?type=PERSONAL")
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllWorkbasketsDocTest", responseFields(allWorkbasketsFieldDescriptors)));
  }

  @Test
  void getSpecificWorkbasketDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001"))
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "_links.self.href",
                is(
                    "http://localhost:8080/api/v1/workbaskets/WBI:100000000000000000000000000000000001")))
        .andDo(
            MockMvcRestDocumentation.document(
                "GetSpecificWorkbasketDocTest", responseFields(workbasketFieldDescriptors)));
  }

  @Test
  void getAllWorkbasketAccessItemsDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID_ACCESSITEMS,
                        "WBI:100000000000000000000000000000000001"))
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllWorkbasketAccessItemsDocTest",
                responseFields(allWorkbasketAccessItemsFieldDescriptors)));
  }

  @Test
  void workbasketSubsetDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001"))
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "_links.self.href",
                is(
                    "http://localhost:8080/api/v1/workbaskets/WBI:100000000000000000000000000000000001")))
        .andDo(
            MockMvcRestDocumentation.document(
                "WorkbasketSubset", responseFields(workbasketSubsetFieldDescriptors)));
  }

  @Test
  void removeWorkbasketAsDistributionTargetDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID_DISTRIBUTION,
                        "WBI:100000000000000000000000000000000007"))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("RemoveWorkbasketAsDistributionTargetDocTest"));
  }

  @Test
  void getAllWorkbasketDistributionTargets() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID_DISTRIBUTION,
                        "WBI:100000000000000000000000000000000002"))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "_links.self.href",
                is(
                    "http://localhost:8080/api/v1/workbaskets/WBI:100000000000000000000000000000000002/distribution-targets")))
        .andDo(
            MockMvcRestDocumentation.document(
                "GetAllWorkbasketDistributionTargets",
                responseFields(allDistributionTargetsFieldDescriptors)));
  }

  @Test
  void createWorkbasketDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.post(restHelper.toUrl(Mapping.URL_WORKBASKET))
                .contentType("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS)
                .content(
                    "{\"key\" : \"asdasdasd\", \"name\" : \"Gruppenpostkorb KSC\", "
                        + "\"domain\" : \"DOMAIN_A\", \"type\" : \"GROUP\",   "
                        + "\"created\" : \"2018-02-01T11:00:00Z\",\r\n"
                        + "  \"modified\" : \"2018-02-01T11:00:00Z\"}"))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andDo(
            MockMvcRestDocumentation.document(
                "CreateWorkbasketDocTest",
                requestFields(createWorkbasketFieldDescriptors),
                responseFields(workbasketFieldDescriptors)))
        .andReturn();
  }

  @Test
  void updateWorkbasketDocTest() throws Exception {
    URL url =
        new URL(
            restHelper.toUrl(
                Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000002"));
    HttpURLConnection con = (HttpURLConnection) url.openConnection();
    con.setRequestMethod("GET");
    con.setRequestProperty("Authorization", ADMIN_CREDENTIALS);
    assertEquals(200, con.getResponseCode());

    String modifiedWorkbasket;
    try (BufferedReader in =
        new BufferedReader(new InputStreamReader(con.getInputStream(), UTF_8))) {
      String inputLine;
      StringBuilder content = new StringBuilder();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      con.disconnect();
      modifiedWorkbasket = content.toString();
    }

    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.put(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000002"))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS)
                .contentType("application/json")
                .content(modifiedWorkbasket))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "_links.self.href",
                is(
                    "http://localhost:8080/api/v1/workbaskets/WBI:100000000000000000000000000000000002")))
        .andDo(
            MockMvcRestDocumentation.document(
                "UpdateWorkbasketDocTest",
                requestFields(workbasketFieldDescriptors),
                responseFields(workbasketFieldDescriptors)));
  }

  @Test
  void deleteWorkbasketDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.delete(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000008"))
                .header("Authorization", ADMIN_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(MockMvcRestDocumentation.document("DeleteWorkbasketDocTest"));
  }

  @Test
  void accessItemDocTest() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(
                        Mapping.URL_WORKBASKET_ID_ACCESSITEMS,
                        "WBI:100000000000000000000000000000000001"))
                .accept("application/hal+json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(
            MockMvcResultMatchers.jsonPath(
                "_links.self.href",
                is(
                    "http://localhost:8080/api/v1/workbaskets/WBI:100000000000000000000000000000000001/workbasketAccessItems")))
        .andDo(
            MockMvcRestDocumentation.document(
                "AccessItemsDocTest", responseFields(accessItemFieldDescriptors)));
  }
}
