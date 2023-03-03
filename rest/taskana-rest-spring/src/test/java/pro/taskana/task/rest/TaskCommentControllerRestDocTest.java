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
package pro.taskana.task.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.TaskComment;
import pro.taskana.task.rest.assembler.TaskCommentRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskCommentRepresentationModel;
import pro.taskana.testapi.security.JaasExtension;
import pro.taskana.testapi.security.WithAccessId;

@ExtendWith(JaasExtension.class)
class TaskCommentControllerRestDocTest extends BaseRestDocTest {

  @Autowired TaskCommentRepresentationModelAssembler assembler;
  @Autowired TaskService taskService;

  @Test
  void getAllTaskCommentsForSpecificTaskDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_TASK_COMMENTS, "TKI:000000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getSpecificTaskCommentDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithAccessId(user = "admin")
  @Test
  void updateTaskCommentDocTest() throws Exception {
    TaskComment comment = taskService.getTaskComment("TCI:000000000000000000000000000000000000");
    comment.setTextField("updated text in textfield");
    TaskCommentRepresentationModel repModel = assembler.toModel(comment);

    mockMvc
        .perform(
            put(RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000000")
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createTaskCommentDocTest() throws Exception {
    TaskComment comment = taskService.newTaskComment("TKI:000000000000000000000000000000000000");
    comment.setTextField("some text in textfield");
    TaskCommentRepresentationModel repModel = assembler.toModel(comment);
    mockMvc
        .perform(
            post(RestEndpoints.URL_TASK_COMMENTS, comment.getTaskId())
                .content(objectMapper.writeValueAsBytes(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithAccessId(user = "admin")
  @Test
  void deleteTaskCommentDocTest() throws Exception {
    this.mockMvc
        .perform(delete(RestEndpoints.URL_TASK_COMMENT, "TCI:000000000000000000000000000000000001"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
