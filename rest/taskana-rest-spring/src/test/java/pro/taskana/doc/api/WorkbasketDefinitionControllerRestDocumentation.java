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

import pro.taskana.common.rest.Mapping;
import pro.taskana.common.test.doc.api.BaseRestDocumentation;

/** Generate Rest Documentation for Workbasket Definitions. */
class WorkbasketDefinitionControllerRestDocumentation extends BaseRestDocumentation {

  private FieldDescriptor[] workbasketDefinitionsFieldDescriptors;

  @BeforeEach
  void setUp() {

    workbasketDefinitionsFieldDescriptors =
        new FieldDescriptor[] {
          subsectionWithPath("workbasketDefinitions")
              .description(
                  "An array of <<WorkbasketDefinitionRepresentationModels, "
                      + "workbasketsDefinitions>>")
        };
  }

  @Test
  void exportAllWorkbasketDefinitions() throws Exception {
    this.mockMvc
        .perform(
            RestDocumentationRequestBuilders.get(
                    restHelper.toUrl(Mapping.URL_WORKBASKET_DEFINITIONS))
                .accept("application/json")
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andDo(
            MockMvcRestDocumentation.document(
                "ExportWorkbasketdefinitionsDocTest",
                responseFields(workbasketDefinitionsFieldDescriptors)));
  }

  @Test
  void importWorkbasketDefinition() throws Exception {
    String definitionString =
        "{ \"workbasketDefinitions\": ["
            + "{"
            + "\"distributionTargets\":[], "
            + "\"authorizations\":[], "
            + "\"workbasket\": {\"name\":\"wbblabla\", \"key\":\"neuerKeyXy\", "
            + "\"domain\": \"DOMAIN_A\", \"type\":\"GROUP\" , "
            + "\"workbasketId\":\"gibtsNed\"}"
            + "}"
            + "]}";

    this.mockMvc
        .perform(
            multipart(restHelper.toUrl(Mapping.URL_WORKBASKET_DEFINITIONS))
                .file("file", definitionString.getBytes(UTF_8))
                .header("Authorization", TEAMLEAD_1_CREDENTIALS))
        .andExpect(MockMvcResultMatchers.status().isNoContent())
        .andDo(
            document(
                "ImportWorkbasketDefinitions",
                requestParts(partWithName("file").description("The file to upload"))));
  }
}
