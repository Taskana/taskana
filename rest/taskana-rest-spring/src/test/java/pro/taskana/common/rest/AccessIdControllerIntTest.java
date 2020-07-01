package pro.taskana.common.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static pro.taskana.common.rest.RestHelper.TEMPLATE;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;

import pro.taskana.common.rest.models.AccessIdRepresentationModel;

@TaskanaSpringBootTest
@ActiveProfiles({"test"})
class AccessIdControllerIntTest {

  private final RestHelper restHelper;

  @Autowired
  AccessIdControllerIntTest(RestHelper restHelper) {
    this.restHelper = restHelper;
  }

  @Test
  void testQueryGroupsByDn() {
    ResponseEntity<AccessIdListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID)
                + "?search-for=cn=ksc-users,cn=groups,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));
    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactly("cn=ksc-users,cn=groups,OU=Test,O=TASKANA");
  }

  @Test
  void testQueryGroupsByCn() {
    ResponseEntity<AccessIdListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=ksc-use",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));
    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactly("cn=ksc-users,cn=groups,OU=Test,O=TASKANA");
  }

  @Test
  void should_returnEmptyResults_ifInvalidCharacterIsUsedInCondition() {
    ResponseEntity<AccessIdListResource> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=ksc-teamleads,cn=groups",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));
    assertThat(response.getBody()).isNotNull().isEmpty();
  }

  @Test
  void testGetMatches() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=rig",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim", "Eifrig, Elena");
  }

  @Test
  void should_returnAccessIdWithUmlauten_ifBased64EncodedUserIsLookedUp() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=läf",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Schläfrig, Tim");
  }

  @Test
  void testBadRequestWhenSearchForIsTooShort() {
    ThrowingCallable httpCall =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=al",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(List.class));
    assertThatThrownBy(httpCall)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("Minimum searchFor length =")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_returnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfUserIsGiven() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID_GROUPS) + "?access-id=teamlead-2",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));

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
  void should_returnBadRequest_ifAccessIdOfUserContainsInvalidCharacter() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(Mapping.URL_ACCESSID_GROUPS) + "?access-id=teamlead-2,cn=users",
                HttpMethod.GET,
                restHelper.defaultRequest(),
                ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .hasMessageContaining("The accessId is invalid")
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  void should_returnAccessIdsOfGroupsTheAccessIdIsMemberOf_ifAccessIdOfGroupIsGiven() {
    ResponseEntity<List<AccessIdRepresentationModel>> response =
        TEMPLATE.exchange(
            restHelper.toUrl(Mapping.URL_ACCESSID_GROUPS)
                + "?access-id=cn=Organisationseinheit KSC 1,"
                + "cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA",
            HttpMethod.GET,
            restHelper.defaultRequest(),
            ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThat(response.getBody())
        .isNotNull()
        .extracting(AccessIdRepresentationModel::getAccessId)
        .usingElementComparator(String.CASE_INSENSITIVE_ORDER)
        .containsExactlyInAnyOrder("cn=Organisationseinheit KSC,cn=organisation,OU=Test,O=TASKANA");
  }

  @Test
  void should_throwNotAuthorizedException_ifCallerOfGroupRetrievalIsNotAdminOrBusinessAdmin() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(Mapping.URL_ACCESSID_GROUPS) + "?access-id=teamlead-2",
                HttpMethod.GET,
                new HttpEntity<>(restHelper.getHeadersUser_1_1()),
                ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  @Test
  void should_throwNotAuthorizedException_ifCallerOfValidationIsNotAdminOrBusinessAdmin() {
    ThrowingCallable call =
        () ->
            TEMPLATE.exchange(
                restHelper.toUrl(Mapping.URL_ACCESSID) + "?search-for=al",
                HttpMethod.GET,
                new HttpEntity<>(restHelper.getHeadersUser_1_1()),
                ParameterizedTypeReference.forType(AccessIdListResource.class));

    assertThatThrownBy(call)
        .isInstanceOf(HttpClientErrorException.class)
        .extracting(ex -> ((HttpClientErrorException) ex).getStatusCode())
        .isEqualTo(HttpStatus.FORBIDDEN);
  }

  static class AccessIdListResource extends ArrayList<AccessIdRepresentationModel> {
    private static final long serialVersionUID = 1L;
  }
}
