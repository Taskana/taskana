package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.test.rest.RestHelper.TEMPLATE;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

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

  @Test
  void testQueryGroupsByDn() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID)
                + "?search-for=cn=ksc-users,cn=groups,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);
    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactly("cn=ksc-users,cn=groups,OU=Test,O=TASKANA");
  }

  @Test
  void testQueryUserByDn() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID)
                + "?search-for=uid=teamlead-1,cn=users,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);
    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactly("teamlead-1");
  }

  @Test
  void testQueryGroupsByCn() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=ksc-use",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);
    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactly("cn=ksc-users,cn=groups,OU=Test,O=TASKANA");
  }

  @Test
  void should_ReturnEmptyResults_ifInvalidCharacterIsUsedInCondition() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=ksc-teamleads,cn=groups",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);
    assertThat(response.getBody()).isNotNull().isEmpty();
  }

  @Test
  void testGetMatches() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=rig",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim", "Eifrig, Elena");
  }

  @Test
  void should_ReturnAccessIdWithUmlauten_ifBased64EncodedUserIsLookedUp() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=läf",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim");
  }

  @Test
  void should_ThrowException_When_SearchForIsTooShort() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=al",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(List.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Minimum Length is")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfUserIsGiven() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);

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
  void should_ReturnBadRequest_ifAccessIdOfUserContainsInvalidCharacter() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS)
                    + "?access-id=teamlead-2,cn=users",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("The accessId is invalid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_ReturnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfGroupIsGiven() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS)
                + "?access-id=cn=Organisationseinheit KSC 1,"
                + "cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ACCESS_ID_LIST_TYPE);

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder("cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA");
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfGroupRetrievalIsNotAdminOrBusinessAdmin() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_ACCESS_ID_GROUPS) + "?access-id=teamlead-2",
                HttpMethod.GET,
                new HttpEntity<>(restHelper.getHeadersUser_1_1()),
                ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_ThrowNotAuthorizedException_ifCallerOfValidationIsNotAdminOrBusinessAdmin() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(RestEndpoints.URL_ACCESS_ID) + "?search-for=al",
                HttpMethod.GET,
                new HttpEntity<>(restHelper.getHeadersUser_1_1()),
                ACCESS_ID_LIST_TYPE);

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }
}
