package pro.taskana.ldap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;

@MockitoSettings
class LdapClientTest {

  @Mock Environment environment;

  @Mock LdapTemplate ldapTemplate;

  @InjectMocks LdapClient cut;

  @Test
  void testLdap() {

    setUpEnvMock();
    cut.init();

    cut.searchGroupByDn("cn=developersgroup,ou=groups,o=taskanatest");

    verify(ldapTemplate)
        .lookup(
            eq("cn=developersgroup,ou=groups"), any(), any(LdapClient.GroupContextMapper.class));
  }

  private void setUpEnvMock() {
    Stream.of(
            new String[][] {
              {"taskana.ldap.useLdap", "true"},
              {"taskana.ldap.baseDn", "o=TaskanaTest"},
              {"taskana.ldap.userSearchBase", "ou=people"},
              {"taskana.ldap.userSearchFilterName", "objectclass"},
              {"taskana.ldap.groupsOfUser", "memberUid"},
              {"taskana.ldap.groupNameAttribute", "cn"},
              {"taskana.ldap.groupSearchFilterValue", "groupOfUniqueNames"},
              {"taskana.ldap.groupSearchFilterName", "objectclass"},
              {"taskana.ldap.groupSearchBase", "ou=groups"},
              {"taskana.ldap.userIdAttribute", "uid"},
              {"taskana.ldap.userLastnameAttribute", "sn"},
              {"taskana.ldap.userFirstnameAttribute", "givenName"},
              {"taskana.ldap.userFirstnameAttribute", "givenName"},
              {"taskana.ldap.userSearchFilterValue", "person"},
              {"taskana.ldap.bindDn", "uid=admin,ou=system"},
              {"taskana.ldap.bindPassword", "secret"},
              {"taskana.ldap.serverUrl", "ldap://localhost:10389"},
            })
        .forEach(
            strings ->
                lenient().when(this.environment.getProperty(strings[0])).thenReturn(strings[1]));
  }
}
