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

import static pro.taskana.rest.test.RestHelper.TEMPLATE;

import java.io.File;
import javax.sql.DataSource;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import pro.taskana.rest.test.RestHelper;
import pro.taskana.rest.test.TaskanaSpringBootTest;

/** Test DmnUploadController. */
@TaskanaSpringBootTest
class DmnUploadControllerIntTest {

  private static final String EXCEL_NAME = "testExcelRouting.xlsx";
  private static final String HTTP_BODY_FILE_NAME = "excelRoutingFile";
  private final RestHelper restHelper;
  private final DataSource dataSource;
  private final String schemaName;

  @Autowired
  DmnUploadControllerIntTest(
      RestHelper restHelper,
      DataSource dataSource,
      @Value("${taskana.schemaName:TASKANA}") String schemaName) {
    this.restHelper = restHelper;
    this.dataSource = dataSource;
    this.schemaName = schemaName;
  }

  @Test
  void should_returnCorrectAmountOfImportedRoutingRules() throws Exception {

    File excelRoutingFile = new ClassPathResource(EXCEL_NAME).getFile();

    MultiValueMap<String, FileSystemResource> body = new LinkedMultiValueMap<>();
    body.add(HTTP_BODY_FILE_NAME, new FileSystemResource(excelRoutingFile));

    HttpHeaders headers = RestHelper.generateHeadersForUser("admin");
    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

    HttpEntity<Object> auth = new HttpEntity<>(body, headers);
    String url = restHelper.toUrl(RoutingRestEndpoints.URL_ROUTING_RULES_DEFAULT);

    ResponseEntity<RoutingUploadResultRepresentationModel> responseEntity =
        TEMPLATE.exchange(url, HttpMethod.PUT, auth, RoutingUploadResultRepresentationModel.class);

    SoftAssertions softly = new SoftAssertions();

    softly
        .assertThat(responseEntity.getBody())
        .extracting(RoutingUploadResultRepresentationModel::getAmountOfImportedRows)
        .isEqualTo(3);

    softly
        .assertThat(responseEntity.getBody())
        .extracting(RoutingUploadResultRepresentationModel::getResult)
        .isEqualTo("Successfully imported 3 routing rules from the provided excel sheet");

    softly.assertAll();
  }
}
