package io.kadai.example.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.rest.models.AccessIdRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

/** Test Ldap attachment. */
@KadaiSpringBootTest
@ActiveProfiles({"emptySearchRoots"})
class LdapEmptySearchRootsTest extends LdapTest {

  @Test
  void should_FindGroupsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> groups =
        ldapClient.searchGroupsAccessIdIsMemberOf("user-2-2");
    assertThat(groups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "cn=ksc-users,cn=groups,ou=test,o=kadai",
            "cn=organisationseinheit ksc 2,cn=organisationseinheit ksc,"
                + "cn=organisation,ou=test,o=kadai");
  }

  @Test
  void should_FindPermissionsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> permissions =
        ldapClient.searchPermissionsAccessIdHas("user-1-2");
    assertThat(permissions)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "kadai:callcenter:ab:ab/a:callcenter", "kadai:callcenter:ab:ab/a:callcenter-vip");
  }

  @Test
  void should_ReturnFullDnForUser_When_AccessIdOfUserIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("otheruser");
    assertThat(dn).isEqualTo("uid=otheruser,cn=other-users,ou=test,o=kadai");
  }

  @Test
  void should_ReturnFullDnForPermission_When_AccessIdOfPermissionIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("kadai:callcenter:ab:ab/a:callcenter-vip");
    assertThat(dn).isEqualTo("cn=g02,cn=groups,ou=test,o=kadai");
  }
}
