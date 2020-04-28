package pro.taskana.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.AccessIdRepresentationModel;

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
    if (ldapClient.useLdap()) {
      List<AccessIdRepresentationModel> usersAndGroups = ldapClient.searchUsersAndGroups("ser0");
      System.out.println("#### found " + LoggerUtils.listToString(usersAndGroups));
      assertThat(usersAndGroups).hasSize(50);
    }
  }
}
