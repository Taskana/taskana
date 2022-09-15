package pro.taskana.user.rest;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.BaseRestDocTest;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;
import pro.taskana.user.rest.assembler.UserRepresentationModelAssembler;
import pro.taskana.user.rest.models.UserRepresentationModel;

class UserControllerRestDocTest extends BaseRestDocTest {
  @Autowired UserRepresentationModelAssembler assembler;
  @Autowired UserService userService;

  @Test
  void getUserDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_USERS_ID, "teamlead-1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getUsersDocTest() throws Exception {
    mockMvc
        .perform(get(RestEndpoints.URL_USERS + "?user-id=teamlead-1&user-id=user-1-1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void createUserDocTest() throws Exception {
    User user = userService.newUser();
    user.setId("user-10-2");
    user.setFirstName("Hans");
    user.setLastName("Georg");
    UserRepresentationModel repModel = assembler.toModel(user);

    mockMvc
        .perform(post(RestEndpoints.URL_USERS).content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void updateUserDocTest() throws Exception {
    User user = userService.getUser("teamlead-1");
    user.setFirstName("new name");
    UserRepresentationModel repModel = assembler.toModel(user);

    mockMvc
        .perform(
            put(RestEndpoints.URL_USERS_ID, "teamlead-1")
                .content(objectMapper.writeValueAsString(repModel)))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void deleteUserDocTest() throws Exception {
    mockMvc
        .perform(delete(RestEndpoints.URL_USERS_ID, "user-1-1"))
        .andExpect(MockMvcResultMatchers.status().isNoContent());
  }
}
