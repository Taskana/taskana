/*-
 * #%L
 * pro.taskana:taskana-routing-rest
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.routing.dmn.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.rest.test.BaseRestDocTest;

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
