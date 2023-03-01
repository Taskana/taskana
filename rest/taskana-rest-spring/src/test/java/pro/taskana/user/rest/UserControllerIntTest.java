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
package pro.taskana.user.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.rest.test.RestHelper.TEMPLATE;

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

import pro.taskana.common.rest.RestEndpoints;
import pro.taskana.rest.test.RestHelper;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.user.rest.models.UserCollectionRepresentationModel;
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
  void should_ReturnExistingUser() throws Exception {
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
  void should_ReturnExistingUsers() throws Exception {
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
  void should_ReturnExistingUsers_When_ParameterContainsDuplicateAndInvalidIds() throws Exception {
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
  void should_CreateValidUser_When_CallingCreateEndpoint() throws Exception {
    UserRepresentationModel newUser = new UserRepresentationModel();
    newUser.setUserId("12345");
    newUser.setGroups(Set.of("group1", "group2"));
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
    assertThat(responseEntity.getBody()).isNotNull().isEqualTo(newUser);
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
