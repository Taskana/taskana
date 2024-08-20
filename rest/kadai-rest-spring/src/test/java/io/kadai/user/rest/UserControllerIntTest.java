package io.kadai.user.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.rest.RestEndpoints;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import io.kadai.user.rest.models.UserCollectionRepresentationModel;
import io.kadai.user.rest.models.UserRepresentationModel;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;

/** Tests the endpoints of the UserController. */
@KadaiSpringBootTest
class UserControllerIntTest {
  private final RestHelper restHelper;

  @Autowired
  UserControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void should_ReturnExistingUser() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "TEAMLEAD-1");
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
  void should_ReturnExistingUsers() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS) + "?user-id=user-1-1&user-id=USER-1-2";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserCollectionRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    UserCollectionRepresentationModel response = responseEntity.getBody();
    assertThat(response).isNotNull();
    assertThat(response.getContent()).hasSize(2);
    assertThat(response.getContent())
        .extracting("firstName")
        .containsExactlyInAnyOrder("Max", "Elena");
  }

  @Test
  void should_ReturnCurrentUser() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS) + "?current-user";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent()).extracting("userId").containsExactly("teamlead-1");
  }

  @Test
  void should_ReturnExceptionCurrentUserWithBadValue() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS) + "?current-user=asd";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnExceptionCurrentUserWithEmptyValue() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS) + "?current-user=";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                url,
                HttpMethod.GET,
                auth,
                ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnOnlyCurrentUserWhileUsingUserIds() {
    String url = restHelper.toUrl(RestEndpoints.URL_USERS) + "?current-user&user-id=teamlead-1";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserCollectionRepresentationModel> response =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getContent()).hasSize(1);
    assertThat(response.getBody().getContent()).extracting("userId").containsExactly("teamlead-1");
  }

  @Test
  void should_ReturnExistingUsersAndCurrentUser() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_USERS)
            + "?user-id=user-1-1&user-id=USER-1-2&current-user";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserCollectionRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    UserCollectionRepresentationModel response = responseEntity.getBody();
    assertThat(response).isNotNull();
    assertThat(response.getContent()).hasSize(3);
    assertThat(response.getContent())
        .extracting("userId")
        .containsExactlyInAnyOrder("user-1-1", "user-1-2", "teamlead-1");
  }

  @Test
  void should_ReturnExistingUsers_When_ParameterContainsDuplicateAndInvalidIds() {
    // also testing different query parameter format
    String url =
        restHelper.toUrl(RestEndpoints.URL_USERS)
            + "?user-id=user-1-1"
            + "&user-id=user-1-1"
            + "&user-id=user-2-1"
            + "&user-id=user-2-1"
            + "&user-id=user-2-1"
            + "&user-id=NotExistingId"
            + "&user-id="
            + "&user-id=AnotherNonExistingId";
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserCollectionRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserCollectionRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getContent()).hasSize(2);
    assertThat(responseEntity.getBody().getContent())
        .extracting("firstName")
        .containsExactlyInAnyOrder("Max", "Simone");
  }

  @Test
  void should_CreateValidUser_When_CallingCreateEndpointWithAllAttributesExceptDomains()
      throws Exception {
    UserRepresentationModel newUser = new UserRepresentationModel();
    newUser.setUserId("12345");
    newUser.setGroups(Set.of("group1", "group2"));
    newUser.setPermissions(Set.of("perm1", "perm2"));
    newUser.setFirstName("Hans");
    newUser.setLastName("Georg");
    newUser.setFullName("Georg, Hans");
    newUser.setLongName("Georg, Hans - (12345)");
    newUser.setEmail("hans.georg@web.com");
    newUser.setMobilePhone("017325862");
    newUser.setPhone("017325862");
    newUser.setData("data");
    newUser.setOrgLevel4("orgLevel4");
    newUser.setOrgLevel3("orgLevel3");
    newUser.setOrgLevel2("orgLevel2");
    newUser.setOrgLevel1("orgLevel1");

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
    assertThat(responseEntity.getBody()).isNotNull().isEqualTo(newUser);
  }

  @Test
  void should_CreateValidUser_When_CallingCreateEndpointWithoutGroupsPermissionsDomains()
      throws Exception {
    UserRepresentationModel newUser = new UserRepresentationModel();
    newUser.setUserId("123456");
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

    url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "123456");
    auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull().isEqualTo(newUser);
  }

  @Test
  void should_UpdateExistingUser_When_CallingUpdateEndpoint() {
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
    String url = restHelper.toUrl(RestEndpoints.URL_USERS_ID, "user-1-3");
    HttpEntity<?> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<UserRepresentationModel> responseEntity =
        TEMPLATE.exchange(
            url,
            HttpMethod.GET,
            auth,
            ParameterizedTypeReference.forType(UserRepresentationModel.class));
    assertThat(responseEntity.getBody()).isNotNull();
    assertThat(responseEntity.getBody().getUserId()).isEqualTo("user-1-3");

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
