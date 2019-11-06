package pro.taskana.doc.api;

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

/**
 * Generate Rest Documentation for Workbasket Definitions.
 */
class WorkbasketDefinitionControllerRestDocumentation extends BaseRestDocumentation {

    private FieldDescriptor[] workbasketDefinitionsFieldDescriptors;

    @BeforeEach
    void setUp() {

        workbasketDefinitionsFieldDescriptors = new FieldDescriptor[] {
            subsectionWithPath("[]").description("An array of <<WorkbasketDefinitions, workbasketsDefinitions>>")
        };
    }

    @Test
    void exportAllWorkbasketDefinitions() throws Exception {
        this.mockMvc.perform(RestDocumentationRequestBuilders
            .get("http://127.0.0.1:" + port + "/api/v1/workbasket-definitions")
            .accept("application/json")
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcRestDocumentation.document("ExportWorkbasketdefinitionsDocTest",
                responseFields(workbasketDefinitionsFieldDescriptors)));
    }

    @Test
    void importWorkbasketDefinition() throws Exception {
        String definitionString = "["
            + "{"
            + "\"distributionTargets\":[], "
            + "\"authorizations\":[], "
            + "\"workbasket\": {\"name\":\"wbblabla\", \"key\":\"neuerKeyXy\", \"domain\": \"DOMAIN_A\", \"type\":\"GROUP\" , \"workbasketId\":\"gibtsNed\"}"
            + "}"
            + "]";

        this.mockMvc.perform(multipart("http://127.0.0.1:" + port + "/api/v1/workbasket-definitions")
            .file("file",
                definitionString.getBytes())
            .header("Authorization", "Basic dGVhbWxlYWRfMTp0ZWFtbGVhZF8x"))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(document("ImportWorkbasketDefinitions", requestParts(
                partWithName("file").description("The file to upload"))));
    }
}
