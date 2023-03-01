/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.monitor.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;

class MonitorControllerRestDocTest extends BaseRestDocTest {

  @Test
  void computeWorkbasketReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_MONITOR_WORKBASKET_REPORT
                    + "?state=READY&state=CLAIMED&state=COMPLETED"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeWorkbasketPriorityReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(RestEndpoints.URL_MONITOR_WORKBASKET_PRIORITY_REPORT + "?workbasket-type=GROUP"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeClassificationCategoryReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_CLASSIFICATION_CATEGORY_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeClassificationReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_CLASSIFICATION_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeDetailedClassificationReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_DETAILED_CLASSIFICATION_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTaskCustomFieldValueReportDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_MONITOR_TASK_CUSTOM_FIELD_VALUE_REPORT
                    + "?custom-field=CUSTOM_14"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTaskStatusReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TASK_STATUS_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void computeTimestampReportDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_MONITOR_TIMESTAMP_REPORT))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
