package pro.taskana.ldap;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.AccessIdResource;

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
      List<AccessIdResource> usersAndGroups = ldapClient.searchUsersAndGroups("ser0");
      System.out.println("#### found " + LoggerUtils.listToString(usersAndGroups));
      assertEquals(50, usersAndGroups.size());
    }
  }
}
