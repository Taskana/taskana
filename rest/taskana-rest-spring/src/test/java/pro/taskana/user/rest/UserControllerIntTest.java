package pro.taskana.user.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.user.rest.models.UserRepresentationModel;

/** Tests the endpoints of the UserController. */
@TaskanaSpringBootTest
class UserControllerIntTest {
  private final RestHelper restHelper;

  @Autowired
  UserControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void should_ReturnExistingUser_When_CallingGetEndpoint() throws Exception {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "teamlead-1");
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();
  }

  @Test
  void should_CreateValidUser_When_CallingCreateEndpoint() throws Exception {
    UserRepresentationModel newUser = new UserRepresentationModel();
    newUser.setUserId("12345");
    newUser.setFirstName("Hans");
    newUser.setLastName("Georg");
    newUser.setFullName("Georg, Hans");
    newUser.setLongName("Georg, Hans - (12345)");
    newUser.setEmail("hans.georg@web.com");
    newUser.setMobilePhone("017325862");

    String url = restHelper.toUrl(RestEndpoints.URL_USERS);
    HttpEntity<?> auth = new HttpEntity<>(newUser, RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.POST,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity).isNotNull();
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "12345");
    auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody()).isEqualTo(newUser);
  }

  @Test
  void should_UpdateExistingUser_When_CallingUpdateEndpoint() throws Exception {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "teamlead-1");
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();

    UserRepresentationModel model = responseEntity.getBody();
    model.setLastName("Mueller");

    auth = new HttpEntity<>(model, RestHelper.generateHeadersForUser("teamlead-1"));
    responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.PUT,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));

    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getLastName()).isEqualTo("Mueller");
  }

  @Test
  void should_DeleteExistingUser_When_CallingDeleteEndpoint() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "user-1-1");
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getUserId()).isEqualTo("user-1-1");

    responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.DELETE,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNull();

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.NOT_FOUND);
  }
}
