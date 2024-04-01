package pro.taskana.workbasket.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;
import pro.taskana.testapi.security.JaasExtension;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.WorkbasketType;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.rest.assembler.WorkbasketRepresentationModelAssembler;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionCollectionRepresentationModel;
import pro.taskana.workbasket.rest.models.WorkbasketDefinitionRepresentationModel;

@Disabled
@ExtendWith(JaasExtension.class)
class WorkbasketDefinitionControllerRestDocTest extends BaseRestDocTest {

  @Autowired WorkbasketService workbasketService;
  @Autowired WorkbasketRepresentationModelAssembler assembler;

  @Test
  void exportWorkbasketDefinitionsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_WORKBASKET_DEFINITIONS))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  @WithAccessId(user = "admin")
  void importWorkbasketDefinitionDocTest() throws Exception {
    WorkbasketImpl workbasket =
        (WorkbasketImpl) workbasketService.newWorkbasket("neuerKey", "DOMAIN_A");
    workbasket.setName("neuer Name");
    workbasket.setType(WorkbasketType.GROUP);
    workbasket.setId("gibtsNochNicht");

    WorkbasketDefinitionRepresentationModel repModel =
        new WorkbasketDefinitionRepresentationModel();
    repModel.setWorkbasket(assembler.toModel(workbasket));
    WorkbasketDefinitionCollectionRepresentationModel repModelList =
        new WorkbasketDefinitionCollectionRepresentationModel(List.of(repModel));

    mockMvc
        .perform(
            multipart(RestEndpoints.URL_WORKBASKET_DEFINITIONS)
                .file("file", objectMapper.writeValueAsBytes(repModelList)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
