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

import pro.taskana.rest.Mapping;

/** Test ClassificationDefinitionControlller. */
class ClassificationDefinitionControllerRestDocumentation extends BaseRestDocumentation {

  private FieldDescriptor[] classificationDefinitionsFieldDescriptors;

  @BeforeEach
  void setUp() {

    classificationDefinitionsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("[]")
              .description("An array of <<ClassificationResource, classifications>>")
        };
  }

  @Test
  void exportAllClassificationDefinitions() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(Mapping.URL_CLASSIFICATIONDEFINITION))
                .accept("application/json")
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "ExportClassificationDefinitionsDocTest",
                responseFields(classificationDefinitionsFieldDescriptors)));
  }

  @Test
  void importClassificationDefinitions() throws Exception {
    String definitionString = "[{\"key\":\"Key0815\", \"domain\":\"DOMAIN_B\"}]";

    this.mockMvc
        .perform(
            multipart(restHelper.toUrl(Mapping.URL_CLASSIFICATIONDEFINITION))
                .file("file", definitionString.getBytes(UTF_8))
                .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(
            document(
                "ImportClassificationDefinitions",
                requestParts(partWithName("file").description("The file to upload"))));
  }
}
