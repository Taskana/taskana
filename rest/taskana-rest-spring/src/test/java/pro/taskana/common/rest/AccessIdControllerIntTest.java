package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

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
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.common.internal.util.Pair;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.common.test.rest.RestHelper;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

@TaskanaSpringBootTest
class AccessIdControllerIntTest {

  private static final ParameterizedTypeReference<List<AccessIdRepresentationModel>>
      ACCESS_ID_LIST_TYPE = new ParameterizedTypeReference<List<AccessIdRepresentationModel>>() {};

  private final RestHelper restHelper;

  @Autowired
  AccessIdControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @TestFactory
  Stream<DynamicTest> should_ResolveAccessId_When_SearchingForDnOrCn() {
    List<Pair<String, String>> list =
        List.of(
            Pair.of(
                "cn=ksc-users,cn=groups,OU=Test,O=TASKANA",
                "cn=ksc-users,cn=groups,OU=Test,O=TASKANA"),
            Pair.of("uid=teamlead-1,cn=users,OU=Test,O=TASKANA", "teamlead-1"),
            Pair.of("ksc-use", "cn=ksc-users,cn=groups,OU=Test,O=TASKANA"));

    ThrowingConsumer<Pair<String, String>> test =
        pair -> {
          String url =
              restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=" + pair.getLeft();
          HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

          ResponseEntity<List<AccessIdRepresentationModel>> response =
              TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

          assertThat(response.getBody())
              .isNotNull()
              .extracting(AccessIdRepresentationModel::getAccessId)
              .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
              .containsExactly(pair.getRight());
        };

    return DynamicTest.stream(list.iterator(), pair -> "search for: " + pair.getLeft(), test);
  }

  @Test
  void should_ReturnEmptyResults_ifInvalidCharacterIsUsedInCondition() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=ksc-teamleads,cn=groups";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody()).isNotNull().isEmpty();
  }

  @Test
  void testGetMatches() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=rig";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

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
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

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
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ThrowingCallable httpCall =
        () -> {
          TEMPLATE.exchange(
              url, HttpMethod.GET, auth, ParameterizedTypeReference.forType(List.class));
        };

    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Minimum Length is")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfUserIsGiven() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder(
            "cn=ksc-teamleads,cn=groups,OU=Test,O=TASKANA",
            "cn=business-admins,cn=groups,OU=Test,O=TASKANA",
            "cn=monitor-users,cn=groups,OU=Test,O=TASKANA",
            "cn=Organisationseinheit KSC 2,"
                + "cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA");
  }

  @Test
  void should_ValidateAccessIdWithEqualsFilterAndReturnAccessIdsOfGroupsTheAccessIdIsMemberOf() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=user-2-1";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder(
            "cn=ksc-users,cn=groups,ou=Test,O=TASKANA",
            "cn=Organisationseinheit KSC 2,cn=Organisationseinheit KSC,"
                + "cn=organisation,ou=Test,O=TASKANA");
  }

  @Test
  void should_ReturnBadRequest_ifAccessIdOfUserContainsInvalidCharacter() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2,cn=users";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("The AccessId is invalid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfGroupIsGiven() {
    String url =
        restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS)
            + "?access-id=cn=Organisationseinheit KSC 1,"
            + "cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersTeamlead_1());

    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder("cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA");
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfGroupRetrievalIsNotAdminOrBusinessAdmin() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersUser_1_1());

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfValidationIsNotAdminOrBusinessAdmin() {
    String url = restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=al";
    HttpEntity<Object> auth = new HttpEntity<>(restHelper.getHeadersUser_1_1());

    ThrowingCallable call = () -> TEMPLATE.exchange(url, HttpMethod.GET, auth, ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
