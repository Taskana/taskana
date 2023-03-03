/*-
 * #%L
 * pro.taskana.history:taskana-simplehistory-rest-spring
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
package pro.taskana.simplehistory.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.rest.test.BaseRestDocTest;

class TaskHistoryEventControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getAllTaskHistoryEventsDocTest() throws Exception {
    mockMvc
        .perform(get(HistoryRestEndpoints.URL_HISTORY_EVENTS + "?page=1&page-size=3"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getSpecificTaskHistoryEventDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                HistoryRestEndpoints.URL_HISTORY_EVENTS_ID,
                "THI:000000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
