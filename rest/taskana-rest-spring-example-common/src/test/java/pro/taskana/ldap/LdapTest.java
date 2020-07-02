package pro.taskana.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/** Test Ldap attachment. */
@TaskanaSpringBootTest
class LdapTest {

  @Autowired private LdapClient ldapClient;

  @Test
  void testFindUsers() throws Exception {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("lead");
    assertThat(usersAndGroups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsOnly("teamlead-1", "teamlead-2", "cn=ksc-teamleads,cn=groups,ou=Test,O=TASKANA");
  }
}
