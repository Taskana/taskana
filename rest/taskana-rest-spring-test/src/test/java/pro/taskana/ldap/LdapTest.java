package pro.taskana.ldap;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import pro.taskana.TaskanaSpringBootTest;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.util.LoggerUtils;
import pro.taskana.rest.resource.AccessIdResource;

/**
 * Test Ldap attachment.
 *
 */
@TaskanaSpringBootTest
class LdapTest {

    @Autowired
    private LdapClient ldapClient;

    @Test
    void testFindUsers() throws InvalidArgumentException {
        if (ldapClient.useLdap()) {
            List<AccessIdResource> usersAndGroups = ldapClient.searchUsersAndGroups("ser0");
            System.out.println("#### found " + LoggerUtils.listToString(usersAndGroups));
            assertEquals(50, usersAndGroups.size());
        }
    }
}
