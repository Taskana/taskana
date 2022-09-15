package pro.taskana.workbasket.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;
import pro.taskana.testapi.security.JaasExtension;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.rest.assembler.WorkbasketAccessItemRepresentationModelAssembler;
import pro.taskana.workbasket.rest.assembler.WorkbasketRepresentationModelAssembler;
import pro.taskana.workbasket.rest.models.WorkbasketAccessItemCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketRepresentationModel;

@ExtendWith(JaasExtension.class)
class WorkbasketControllerRestDocTest extends BaseRestDocTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketRepresentationModelAssembler assembler;
  @Autowired WorkbasketAccessItemRepresentationModelAssembler accessItemAssembler;

  @Test
  void getAllWorkbasketsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_WORKBASKET + "?type=PERSONAL"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getSpecificWorkbasketDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void deleteWorkbasketDocTest() throws Exception {
    mockMvc
        .perform(
            delete(RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000002"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void createWorkbasketDocTest() throws Exception {
    Workbasket workbasket = workbasketService.newWorkbasket("asdasdasd", "DOMAIN_A");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setName("this is a wonderful workbasket name");

    WorkbasketRepresentationModel repModel = assembler.toModel(workbasket);

    mockMvc
        .perform(
            post(RestEndpoints.URL_WORKBASKET).content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  @WithAccessId(user = "admin")
  void updateWorkbasketDocTest() throws Exception {

    Workbasket workbasket =
        workbasketService.getWorkbasket("WBI:100000000000000000000000000000000003");
    workbasket.setName("new name");

    WorkbasketRepresentationModel repModel = assembler.toModel(workbasket);

    mockMvc
        .perform(
            put(RestEndpoints.URL_WORKBASKET_ID, "WBI:100000000000000000000000000000000003")
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getAllWorkbasketAccessItemsDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS,
                "WBI:100000000000000000000000000000000001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void setAllWorkbasketAccessItemsDocTest() throws Exception {
    String workbasketId = "WBI:100000000000000000000000000000000001";
    WorkbasketAccessItem accessItem =
        workbasketService.newWorkbasketAccessItem(workbasketId, "new-access-id");
    accessItem.setAccessName("new-access-name");
    accessItem.setPermission(WorkbasketPermission.OPEN, true);

    WorkbasketAccessItemCollectionRepresentationModel repModel =
        new WorkbasketAccessItemCollectionRepresentationModel(
            List.of(accessItemAssembler.toModel(accessItem)));

    mockMvc
        .perform(
            put(RestEndpoints.URL_WORKBASKET_ID_ACCESS_ITEMS, workbasketId)
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getAllWorkbasketDistributionTargetsDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                "WBI:100000000000000000000000000000000002"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void setAllDistributionTargetsDocTest() throws Exception {

    List<String> distributionTargets =
        List.of(
            "WBI:100000000000000000000000000000000002", "WBI:100000000000000000000000000000000003");

    mockMvc
        .perform(
            put(
                    RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                    "WBI:100000000000000000000000000000000001")
                .content(objectMapper.writeValueAsString(distributionTargets)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void removeWorkbasketAsDistributionTargetDocTest() throws Exception {
    mockMvc
        .perform(
            delete(
                RestEndpoints.URL_WORKBASKET_ID_DISTRIBUTION,
                "WBI:100000000000000000000000000000000007"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
