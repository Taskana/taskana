package pro.taskana.task.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;
import pro.taskana.rest.test.RestHelper;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.task.internal.models.ObjectReferenceImpl;
import pro.taskana.task.rest.assembler.TaskRepresentationModelAssembler;
import pro.taskana.task.rest.models.IsReadRepresentationModel;
import pro.taskana.task.rest.models.TaskRepresentationModel;
import pro.taskana.task.rest.models.TransferTaskRepresentationModel;
import pro.taskana.testapi.security.JaasExtension;
import pro.taskana.testapi.security.WithAccessId;

@ExtendWith(JaasExtension.class)
class TaskControllerRestDocTest extends BaseRestDocTest {

  @Autowired TaskService taskService;
  @Autowired TaskRepresentationModelAssembler assembler;

  @Test
  void getAllTasksDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_TASKS + "?por-type=VNR&por-value=22334455&sort-by=NAME"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void deleteTaskDocTest() throws Exception {
    mockMvc
        .perform(delete(RestEndpoints.URL_TASKS_ID, "TKI:000000000000000000000000000000000039"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }

  @Test
  void forceDeleteTaskDocTest() throws Exception {
    mockMvc
        .perform(
            delete(RestEndpoints.URL_TASKS_ID_FORCE, "TKI:000000000000000000000000000000000005"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
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
  void forceClaimTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                RestEndpoints.URL_TASKS_ID_CLAIM_FORCE, "TKI:000000000000000000000000000000000003"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void cancelClaimTaskDocTest() throws Exception {
    mockMvc
        .perform(
            delete(RestEndpoints.URL_TASKS_ID_CLAIM, "TKI:000000000000000000000000000000000002")
                .headers(RestHelper.generateHeadersForUser("user-1-1")))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void forceCancelClaimTaskDocTest() throws Exception {
    mockMvc
        .perform(
            delete(
                    RestEndpoints.URL_TASKS_ID_CLAIM_FORCE,
                    "TKI:000000000000000000000000000000000035")
                .headers(RestHelper.generateHeadersForUser("user-1-2")))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void requestReviewTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                    RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW,
                    "TKI:000000000000000000000000000000000032")
                .headers(RestHelper.generateHeadersForUser("user-1-2")))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void forceRequestReviewTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                    RestEndpoints.URL_TASKS_ID_REQUEST_REVIEW_FORCE,
                    "TKI:000000000000000000000000000000000101")
                .headers(RestHelper.generateHeadersForUser("user-1-2")))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void requestChangesTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                    RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES,
                    "TKI:000000000000000000000000000000000136")
                .headers(RestHelper.generateHeadersForUser("user-1-1")))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void forceRequestChangesTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                    RestEndpoints.URL_TASKS_ID_REQUEST_CHANGES_FORCE,
                    "TKI:000000000000000000000000000000000100")
                .headers(RestHelper.generateHeadersForUser("user-1-1")))
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
  void forceCompleteTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(
                RestEndpoints.URL_TASKS_ID_COMPLETE_FORCE,
                "TKI:000000000000000000000000000000000003"))
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
  void terminateTaskDocTest() throws Exception {
    mockMvc
        .perform(
            post(RestEndpoints.URL_TASKS_ID_TERMINATE, "TKI:000000000000000000000000000000000000"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void setTaskReadDocTest() throws Exception {

    IsReadRepresentationModel isRead = new IsReadRepresentationModel(true);

    mockMvc
        .perform(
            post(RestEndpoints.URL_TASKS_ID_SET_READ, "TKI:000000000000000000000000000000000025")
                .content(objectMapper.writeValueAsString(isRead)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createTaskDocTest() throws Exception {
    final Task task = taskService.newTask("WBI:100000000000000000000000000000000004");
    ObjectReferenceImpl objectReference = new ObjectReferenceImpl();
    objectReference.setCompany("MyCompany1");
    objectReference.setSystem("MySystem1");
    objectReference.setSystemInstance("MyInstance1");
    objectReference.setType("MyType1");
    objectReference.setValue("00000001");
    task.setPrimaryObjRef(objectReference);
    task.setClassificationKey("L11010");
    task.addSecondaryObjectReference("company", "system", "systemInstance", "type", "value");
    TaskRepresentationModel repModel = assembler.toModel(task);
    mockMvc
        .perform(post(RestEndpoints.URL_TASKS).content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
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

  @Test
  void transferTasksDocTest() throws Exception {
    List<String> taskIds =
        List.of(
            "TKI:000000000000000000000000000000000000", "TKI:000000000000000000000000000000000001");
    TransferTaskRepresentationModel transferTaskRepresentationModel =
        new TransferTaskRepresentationModel(true, "user-1-1", taskIds);
    mockMvc
        .perform(
            post(
                    RestEndpoints.URL_TRANSFER_WORKBASKET_ID,
                    "WBI:100000000000000000000000000000000001")
                .content(objectMapper.writeValueAsString(transferTaskRepresentationModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
