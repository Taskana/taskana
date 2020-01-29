package pro.taskana.ldap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;

@ExtendWith(MockitoExtension.class)
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
              {"taskana.ldap.minSearchForLength", "3"},
              {"taskana.ldap.maxNumberOfReturnedAccessIds", "50"},
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
              {"taskana.ldap.userSearchFilterValue", "person"}
            })
        .forEach(strings -> when(this.environment.getProperty(strings[0])).thenReturn(strings[1]));
  }
}
