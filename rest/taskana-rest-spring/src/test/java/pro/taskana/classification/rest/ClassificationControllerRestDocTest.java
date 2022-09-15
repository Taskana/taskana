package pro.taskana.classification.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;

class ClassificationControllerRestDocTest extends BaseRestDocTest {

  @Autowired ClassificationRepresentationModelAssembler assembler;
  @Autowired ClassificationService classificationService;

  @Test
  void getAllClassificationsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATIONS))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getClassificationDocTest() throws Exception {
    mockMvc
        .perform(
            get(RestEndpoints.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000009"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createClassificationDocTest() throws Exception {
    Classification classification =
        classificationService.newClassification("Key0815casdgdgh", "DOMAIN_B", "TASK");
    classification.setServiceLevel("P1D");
    ClassificationRepresentationModel repModel = assembler.toModel(classification);
    mockMvc
        .perform(
            post(RestEndpoints.URL_CLASSIFICATIONS)
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void deleteClassificationDocTest() throws Exception {
    mockMvc
        .perform(
            delete(
                RestEndpoints.URL_CLASSIFICATIONS_ID, "CLI:100000000000000000000000000000000010"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void updateClassificationDocTest() throws Exception {
    Classification classification =
        classificationService.getClassification("CLI:100000000000000000000000000000000009");
    classification.setName("new name");

    ClassificationRepresentationModel repModel = assembler.toModel(classification);

    mockMvc
        .perform(
            put(RestEndpoints.URL_CLASSIFICATIONS_ID, classification.getId())
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
