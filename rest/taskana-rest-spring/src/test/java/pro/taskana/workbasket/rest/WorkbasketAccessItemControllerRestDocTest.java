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
package pro.taskana.workbasket.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;

class WorkbasketAccessItemControllerRestDocTest extends BaseRestDocTest {

  @Test
  void getWorkbasketAccessItemsDocTest() throws Exception {
    mockMvc
        .perform(
            get(
                RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS
                    + "?sort-by=WORKBASKET_KEY&order=ASCENDING&access-id=user-2-2"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void removeWorkbasketAccessItemsDocTest() throws Exception {
    mockMvc
        .perform(delete(RestEndpoints.URL_WORKBASKET_ACCESS_ITEMS + "?access-id=user-2-1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
