package io.kadai.common.rest;

import static io.kadai.rest.test.RestHelper.TEMPLATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kadai.common.internal.util.Pair;
import io.kadai.common.rest.models.AccessIdRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.rest.test.RestHelper;
import java.util.List;
import java.util.stream.Stream;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.ThrowingConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpStatusCodeException;

@TestPropertySource(properties = "kadai.ldap.useDnForGroups=false")
@KadaiSpringBootTest
class AccessIdControllerForUseDnForGroupsDisabledIntTest {

  private static final ParameterizedTypeReference<List<AccessIdRepresentationModel>>
      ACCESS_ID_LIST_TYPE = new ParameterizedTypeReference<List<AccessIdRepresentationModel>>() {};

  private final RestHelper restHelper;

  @Autowired
  AccessIdControllerForUseDnForGroupsDisabledIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @TestFactory
  Stream<DynamicTest> should_ResolveAccessId_When_SearchingForDnOrCn() {
    List<Pair<String, String>> list =
        List.of(
            Pair.of(
                "cn=ksc-users,cn=groups,OU=Test,O=KADAI",
                "ksc-users"),
            Pair.of("uid=teamlead-1,cn=users,OU=Test,O=KADAI", "teamlead-1"),
            Pair.of("ksc-use", "ksc-users"),
            Pair.of("user-b-2", "user-b-2"),
            Pair.of("User-b-2", "user-b-2"),
            Pair.of("cn=g01,cn=groups,OU=Test,O=KADAI",
                "kadai:callcenter:ab:ab/a:callcenter"),
            Pair.of("cn=g02,cn=groups,OU=Test,O=KADAI",
                "kadai:callcenter:ab:ab/a:callcenter-vip"));

    ThrowingConsumer<Pair<String, String>> test =
        pair -> {
          String url =
              restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=" + pair.getLeft();
          HttpEntity<Object> auth =
              new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

          ResponseEntity<List<AccessIdRepresentationModel>> response =
              TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

          assertThat(response.getBody())
              .isNotNull()
              .extracting(AccessIdRepresentationModel::getAccessId)
              .containsExactly(pair.getRight());
        };

    return DynamicTest.stream(list.iterator(), pair -> "search for: " + pair.getLeft(), test);
  }

  @Test
  void should_ReturnEmptyResults_ifInvalidCharacterIsUsedInCondition() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=ksc-teamleads,cn=groups";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody()).isNotNull().isEmpty();
  }

  @Test
  void testGetMatches() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=rig";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim", "Eifrig, Elena");
  }

  @Test
  void should_ReturnAccessIdWithUmlauten_ifBased64EncodedUserIsLookedUp() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=läf";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim");
  }

  @Test
  void should_ThrowException_When_SearchForIsTooShort() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=al";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(
              url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("Minimum Length is")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfUserIsGiven() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder(
            "ksc-teamleads",
            "business-admins",
            "monitor-users",
            "Organisationseinheit KSC 2");
  }

  @Test
  void should_ReturnAccessIdsOfPermissionsTheAccessIdIsMemberOf_ifAccessIdOfUserIsGiven() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_PERMISSIONS)
        + "?access-id=user-1-2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder("kadai:callcenter:ab:ab/a:callcenter",
            "kadai:callcenter:ab:ab/a:callcenter-vip");
  }

  @Test
  void should_ValidateAccessIdWithEqualsFilterAndReturnAccessIdsOfGroupsTheAccessIdIsMemberOf() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=user-2-1";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder(
            "ksc-users",
            "Organisationseinheit KSC 2");
  }

  @Test
  void should_ValidateAccessIdWithEqualsFilterAndReturnAccessIdsOfPermissionsAccessIdIsMemberOf() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_PERMISSIONS) + "?access-id=user-2-1";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder(
            "kadai:callcenter:ab:ab/a:callcenter");
  }

  @Test
  void should_ReturnBadRequest_ifAccessIdOfUserContainsInvalidCharacter() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2,cn=users";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .hasMessageContaining("The AccessId is invalid")
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfGroupIsGiven() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS)
            + "?access-id=cn=Organisationseinheit KSC 1,"
            + "cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=KADAI";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("teamlead-1"));

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder("Organisationseinheit KSC");
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfGroupRetrievalIsNotAdminOrBusinessAdmin() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfPermissionRetrievalIsNotAdminOrBusinessAdmin() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_PERMISSIONS)
        + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfValidationIsNotAdminOrBusinessAdmin() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=al";
    HttpEntity<Object> auth = new HttpEntity<>(RestHelper.generateHeadersForUser("user-1-1"));

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpStatusCodeException.class)
        .extracting(HttpStatusCodeException.class::cast)
        .extracting(HttpStatusCodeException::getStatusCode)
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
