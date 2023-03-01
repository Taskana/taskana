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
package pro.taskana.common.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.rest.test.BaseRestDocTest;

class AccessIdControllerRestDocTest extends BaseRestDocTest {

  @Test
  void searchForAccessIdDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID + "?search-for=max"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getGroupsForAccessIdDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID_GROUPS + "?access-id=teamlead-1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void searchUsersByNameOrAccessIdForRoleTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_ACCESS_ID_WITH_NAME + "?search-for=user-1&role=user"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
