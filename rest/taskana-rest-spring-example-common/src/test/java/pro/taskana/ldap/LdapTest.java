package pro.taskana.ldap;

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

  @Autowired private LdapClient ldapClient;

  @Test
  void testFindUsers() throws Exception {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("lead");
    assertThat(usersAndGroups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "teamlead-1", "teamlead-2", "cn=ksc-teamleads,cn=groups,ou=Test,O=TASKANA");
  }

  @Test
  void should_findUserByWholeName_WhenSearchingWithLdapClient() throws Exception {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("Elena");
    assertThat(usersAndGroups).hasSize(2);

    usersAndGroups = ldapClient.searchUsersAndGroups("Elena Faul");
    assertThat(usersAndGroups).hasSize(1);
  }
}
