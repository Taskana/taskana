package pro.taskana.ldap;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import pro.taskana.rest.RestConfiguration;
import pro.taskana.rest.resource.AccessIdResource;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT, properties = {"devMode=true"})
@ContextConfiguration(classes = {RestConfiguration.class})
public class LdapTest {

    @Autowired
    private LdapClient ldapClient;

    @Test
    public void testFindUsers() {
        if (ldapClient.useLdap()) {
            List<AccessIdResource> usersAndGroups = ldapClient.searchUsersAndGroups("ie");
            assertEquals(31, usersAndGroups.size());
        }
    }
}
