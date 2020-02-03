package pro.taskana.ldap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pro.taskana.ldap.LdapSettings.TASKANA_LDAP_USE_LDAP;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.rest.resource.AccessIdResource;

@ExtendWith(MockitoExtension.class)
class LdapClientTest {

  @Mock Environment environment;

  @Mock LdapTemplate ldapTemplate;

  @InjectMocks LdapClient cut;

  @Test
  void testLdap_searchGroupByDn() {

    setUpEnvMock();
    cut.init();

    cut.searchGroupByDn("cn=developersgroup,ou=groups,o=taskanatest");

    verify(ldapTemplate)
        .lookup(
            eq("cn=developersgroup,ou=groups"), any(), any(LdapClient.GroupContextMapper.class));
  }

  @Test
  void testLdap_searchUsersAndGroups() throws InvalidArgumentException {

    setUpEnvMock();
    cut.init();

    AccessIdResource group = new AccessIdResource("testG", "testGId");
    AccessIdResource user = new AccessIdResource("testU", "testUId");

    when(ldapTemplate.search(
            any(String.class), any(), anyInt(), any(), any(LdapClient.GroupContextMapper.class)))
        .thenReturn(Collections.singletonList(group));
    when(ldapTemplate.search(
            any(String.class), any(), anyInt(), any(), any(LdapClient.UserContextMapper.class)))
        .thenReturn(Collections.singletonList(user));

    assertThat(cut.searchUsersAndGroups("test")).hasSize(2).containsExactlyInAnyOrder(user, group);
  }

  @Test
  void testLdap_getNameWithoutBaseDn() {

    setUpEnvMock();
    cut.init();
    assertThat(cut.getNameWithoutBaseDn("cn=developersgroup,ou=groups,o=taskanatest"))
        .isEqualTo("cn=developersgroup,ou=groups");
  }

  @Test
  void testLdap_notConfigured() {
    lenient().when(this.environment.getProperty(TASKANA_LDAP_USE_LDAP.getKey())).thenReturn("true");
    assertThatThrownBy(() -> cut.init()).isInstanceOf(SystemException.class);
  }

  @Test
  void testLdap_getFirstPageOfaResultList() {
    setUpEnvMock();
    cut.init();

    List<AccessIdResource> result =
        IntStream.range(0, 100)
            .mapToObj(i -> new AccessIdResource("" + i, "" + i))
            .collect(Collectors.toList());

    assertThat(cut.getFirstPageOfaResultList(result))
        .hasSize(cut.getMaxNumberOfReturnedAccessIds());
  }

  @Test
  void testLdap_useLdap_null() {

    when(this.environment.getProperty(TASKANA_LDAP_USE_LDAP.getKey())).thenReturn(null);
    assertThat(cut.useLdap()).isFalse();
  }

  @Test
  void testLdap_useLdap_empty() {

    when(this.environment.getProperty(TASKANA_LDAP_USE_LDAP.getKey())).thenReturn("");
    assertThat(cut.useLdap()).isFalse();
  }

  @Test
  void testLdap_useLdap_true() {

    when(this.environment.getProperty(TASKANA_LDAP_USE_LDAP.getKey())).thenReturn("true");
    assertThat(cut.useLdap()).isTrue();
  }

  @Test
  void testLdap_isInitorFail() {
    assertThatThrownBy(() -> cut.isInitOrFail()).isInstanceOf(SystemException.class);
    setUpEnvMock();
    cut.init();
    assertThatCode(() -> cut.isInitOrFail()).doesNotThrowAnyException();
  }

  @Test
  void testLdap_checkForMissingConfigurations() {
    // optional config fields
    // minSearchForLength, maxNumberOfReturnedAccessIds
    assertThat(new LdapClient().checkForMissingConfigurations())
        .hasSize(LdapSettings.values().length - 2);
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
