package pro.taskana.classification.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.Classification;
import pro.taskana.classification.rest.assembler.ClassificationRepresentationModelAssembler;
import pro.taskana.classification.rest.models.ClassificationCollectionRepresentationModel;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;

class ClassificationDefinitionControllerRestDocTest extends BaseRestDocTest {

  @Autowired ClassificationRepresentationModelAssembler assembler;
  @Autowired ClassificationService classificationService;

  @Test
  void exportClassificationDefinitionsDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void importClassificationDefinitionsDocTest() throws Exception {
    Classification classification =
        classificationService.newClassification("Key0815", "DOMAIN_B", "TASK");
    classification.setServiceLevel("P1D");

    ClassificationCollectionRepresentationModel importCollection =
        new ClassificationCollectionRepresentationModel(List.of(assembler.toModel(classification)));

    this.mockMvc
        .perform(
            multipart(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS)
                .file("file", objectMapper.writeValueAsBytes(importCollection)))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
