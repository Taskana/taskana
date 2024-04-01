package pro.taskana.routing.dmn.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.rest.test.BaseRestDocTest;

@Disabled
class DmnUploadControllerRestDocTest extends BaseRestDocTest {

  private static final String EXCEL_NAME = "testExcelRouting.xlsx";
  private static final String REST_REQUEST_PARAM_NAME = "excelRoutingFile";

  @Test
  void convertAndUploadDocTest() throws Exception {

    File excelRoutingFile = new ClassPathResource(EXCEL_NAME).getFile();
    InputStream targetStream = new FileInputStream(excelRoutingFile);

    MockMultipartFile routingMultiPartFile =
        new MockMultipartFile(REST_REQUEST_PARAM_NAME, targetStream);

    mockMvc
        .perform(
            MockMvcRequestBuilders.multipart(RoutingRestEndpoints.URL_ROUTING_RULES_DEFAULT)
                .file(routingMultiPartFile)
                .with(
                    request -> {
                      request.setMethod("PUT");
                      return request;
                    }))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
