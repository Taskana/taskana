package pro.taskana.doc.api;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Test ClassificationDefinitionControlller. */
class ClassificationDefinitionControllerRestDocumentation extends BaseRestDocumentation {

  private FieldDescriptor[] classificationDefinitionsFieldDescriptors;

  @BeforeEach
  void setUp() {

    classificationDefinitionsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("classifications")
              .description("An array of <<ClassificationRepresentationModels, classifications>>")
        };
  }

  @Test
  void exportAllClassificationDefinitions() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "ExportClassificationDefinitionsDocTest",
                responseFields(classificationDefinitionsFieldDescriptors)));
  }

  @Test
  void importClassificationDefinitions() throws Exception {
    String definitionString =
        "{\"classifications\":[{\"key\":\"Key0815\", \"domain\":\"DOMAIN_B\"}]}";

    this.mockMvc
        .perform(
            multipart(restHelper.toUrl(RestEndpoints.URL_CLASSIFICATION_DEFINITIONS))
                .file("file", definitionString.getBytes(UTF_8))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(
            document(
                "ImportClassificationDefinitions",
                requestParts(partWithName("file").description("The file to upload"))));
  }
}
