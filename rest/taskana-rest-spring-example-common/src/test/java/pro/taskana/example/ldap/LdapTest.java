package pro.taskana.example.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.common.test.rest.TaskanaSpringBootTest;

/** Test Ldap attachment. */
@TaskanaSpringBootTest
class LdapTest {

  @Autowired LdapClient ldapClient;

  @Test
  void should_FindAllUsersAndGroup_When_SearchWithSubstringOfName() throws Exception {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("lead");
    assertThat(usersAndGroups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "teamlead-1", "teamlead-2", "cn=ksc-teamleads,cn=groups,ou=test,o=taskana");
  }

  @Test
  void should_FindUser_When_SearchingWithFirstAndLastname() throws Exception {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("Elena");
    assertThat(usersAndGroups).hasSize(2);

    usersAndGroups = ldapClient.searchUsersAndGroups("Elena Faul");
    assertThat(usersAndGroups).hasSize(1);
  }

  @Test
  void should_FindGroupsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> groups =
        ldapClient.searchGroupsAccessIdIsMemberOf("user-2-2");
    assertThat(groups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder("cn=ksc-users,cn=groups,ou=test,o=taskana");
  }

  @Test
  void should_FeturnFullDnForUser_When_AccessIdOfUserIsGiven() {
    String dn = ldapClient.searchDnForAccessId("user-2-2");
    assertThat(dn).isEqualTo("uid=user-2-2,cn=users,ou=test,o=taskana");
  }
}
