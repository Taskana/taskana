package pro.taskana.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.RestConfiguration;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

/** Test Ldap attachment. */
@ActiveProfiles({"test"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(
    classes = RestConfiguration.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LdapTest {

  @Autowired private LdapClient ldapClient;

  @Test
  void testFindUsers() throws InvalidArgumentException {
    List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("lead");
    assertThat(usersAndGroups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsOnly("teamlead-1", "teamlead-2", "cn=ksc-teamleads,cn=groups,ou=Test,O=TASKANA");
  }
}
