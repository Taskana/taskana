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
import pro.taskana.common.test.BaseRestDocTest;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.ObjectReference;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.models.TaskRepresentationModel;

@ExtendWith(JaasExtension.class)
class TaskControllerRestDocTest extends BaseRestDocTest {

  @Autowired TaskService taskService;
  @Autowired TaskRepresentationModelAssembler assembler;

  @Test
  void getAllTasksDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_TASKS + "?por.type=VNR&por.value=22334455&sortBy=NAME"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void deleteTasksDocTest() throws Exception {
    mockMvc
        .perform(
            delete(
                RestEndpoints.URL_TASKS
                    + "?task-id=TKI:000000000000000000000000000000000036"
                    + "&task-id=TKI:000000000000000000000000000000000037"
                    + "&task-id=TKI:000000000000000000000000000000000038"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getSpecificTaskDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_TASKS_ID, "TKI:100000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void claimTaskDocTest() throws Exception {
    mockMvc
        .perform(post(RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000003"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void cancelClaimTaskDocTest() throws Exception {
    mockMvc
        .perform(
            delete(RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000002")
                .headers(restHelper.getHeadersUser_1_1()))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void selectAndClaimTaskDocTest() throws Exception {
    mockMvc
        .perform(post(RestEndpoints.URL_TASKS_ID_SELECT_AND_CLAIM + "?custom14=abc"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void completeTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(RestEndpoints.URL_TASKS_ID_COMPLETE, "TKI:000000000000000000000000000000000003"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void cancelTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(RestEndpoints.URL_TASKS_ID_CANCEL, "TKI:000000000000000000000000000000000026"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createTaskDocTest() throws Exception {
    final Task task = taskService.newTask("WBI:100000000000000000000000000000000004");
    ObjectReference objectReference = new ObjectReference();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");
    task.setPrimaryObjRef(objectReference);
    task.setClassificationKey("L11010");
    TaskRepresentationModel repModel = assembler.toModel(task);
    mockMvc
        .perform(post(RestEndpoints.URL_TASKS).content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void deleteTaskDocTest() throws Exception {
    mockMvc
        .perform(delete(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000001"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void transferTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                RestEndpoints.URL_TASKS_ID_TRANSFER_WORKBASKET_ID,
                "TKI:000000000000000000000000000000000004",
                "WBI:100000000000000000000000000000000001"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithAccessId(user = "admin")
  @Test
  void updateTaskDocTest() throws Exception {
    Task task = taskService.getTask("TKI:000000000000000000000000000000000003");
    task.setDescription("new description");

    TaskRepresentationModel repModel = assembler.toModel(task);

    mockMvc
        .perform(
            put(RestEndpoints.URL_TASKS_ID, task.getId())
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
