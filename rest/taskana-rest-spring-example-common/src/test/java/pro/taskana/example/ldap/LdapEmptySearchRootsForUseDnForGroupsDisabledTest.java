package pro.taskana.example.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.rest.test.TaskanaSpringBootTest;

/** Test Ldap attachment. */
@TaskanaSpringBootTest
@TestPropertySource(properties = "taskana.ldap.useDnForGroups=false")
@ActiveProfiles({"emptySearchRoots"})
class LdapEmptySearchRootsForUseDnForGroupsDisabledTest
    extends LdapForUseDnForGroupsDisabledTest {

  @Test
  void should_FindGroupsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> groups =
        ldapClient.searchGroupsAccessIdIsMemberOf("user-2-2");
    assertThat(groups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "ksc-users", "organisationseinheit ksc 2");
  }

  @Test
  void should_FindPermissionsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> permissions =
        ldapClient.searchPermissionsAccessIdHas("user-1-2");
    assertThat(permissions)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder("taskana:callcenter:ab:ab/a:callcenter",
            "taskana:callcenter:ab:ab/a:callcenter-vip");
  }

  @Test
  void should_ReturnFullDnForUser_When_AccessIdOfUserIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("otheruser");
    assertThat(dn).isEqualTo("uid=otheruser,cn=other-users,ou=test,o=taskana");
  }

  @Test
  void should_ReturnFullDnForPermission_When_AccessIdOfPermissionIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("taskana:callcenter:ab:ab/a:callcenter-vip");
    assertThat(dn).isEqualTo("cn=g02,cn=groups,ou=test,o=taskana");
  }
}

