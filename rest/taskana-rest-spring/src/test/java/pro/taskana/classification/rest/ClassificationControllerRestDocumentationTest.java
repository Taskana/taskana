package pro.taskana.classification.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.BaseRestDocumentationTest;

@TestMethodOrder(OrderAnnotation.class)
class ClassificationControllerRestDocumentationTest extends BaseRestDocumentationTest {

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
  @Order(1)
  void createClassificationDocTest() throws Exception {
    Classification classification =
        classificationService.newClassification("Key0815casdgdgh", "DOMAIN_B", "TASK");
    ClassificationRepresentationModel repModel = assembler.toModel(classification);
    mockMvc
        .perform(
            post(RestEndpoints.URL_CLASSIFICATIONS)
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  /**
   * this test only works with the {@link
   * ClassificationControllerRestDocumentationTest#createClassificationDocTest()} test.
   *
   * @throws Exception if any exception is thrown.
   */
  @Test
  @Order(2)
  void deleteClassificationDocTest() throws Exception {
    List<ClassificationSummary> list =
        classificationService
            .createClassificationQuery()
            .keyIn("Key0815casdgdgh")
            .domainIn("DOMAIN_B")
            .typeIn("TASK")
            .list();
    assertThat(list).hasSize(1);
    mockMvc
        .perform(delete(RestEndpoints.URL_CLASSIFICATIONS_ID, list.get(0).getId()))
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
