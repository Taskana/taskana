/*-
 * #%L
 * pro.taskana:taskana-rest-spring
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package pro.taskana.common.rest.ldap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.LdapTemplate;

import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;

@ExtendWith(MockitoExtension.class)
class LdapClientTest {

  @Mock Environment environment;

  @Mock LdapTemplate ldapTemplate;

  @Mock TaskanaConfiguration taskanaConfiguration;

  @Spy @InjectMocks LdapClient cut;

  @Test
  void should_SearchGroupByDn_For_LdapCall() {
    setUpEnvMock();
    cut.init();

    cut.searchAccessIdByDn("cn=developersgroup,ou=groups,o=taskanatest");

    verify(ldapTemplate)
        .lookup(eq("cn=developersgroup,ou=groups"), any(), any(LdapClient.DnContextMapper.class));
  }

  @Test
  void should_ConvertAccessIdToLowercase_When_SearchingGroupByDn() {
    setUpEnvMock();
    cut.init();

    cut.searchAccessIdByDn("cn=Developersgroup,ou=groups,o=taskanatest");

    verify(ldapTemplate)
        .lookup(eq("cn=developersgroup,ou=groups"), any(), any(LdapClient.DnContextMapper.class));
  }

  @Test
  void testLdap_searchUsersAndGroups() throws Exception {

    setUpEnvMock();
    cut.init();

    AccessIdRepresentationModel group = new AccessIdRepresentationModel("testG", "testGId");
    AccessIdRepresentationModel user = new AccessIdRepresentationModel("testU", "testUId");

    when(ldapTemplate.search(
            any(String.class), any(), anyInt(), any(), any(LdapClient.GroupContextMapper.class)))
        .thenReturn(List.of(group));
    when(ldapTemplate.search(
            any(String.class), any(), anyInt(), any(), any(LdapClient.UserContextMapper.class)))
        .thenReturn(List.of(user));

    assertThat(cut.searchUsersAndGroups("test")).hasSize(2).containsExactlyInAnyOrder(user, group);
  }

  @Test
  void should_CorrectlySortAccessIds_When_ContainingNullAccessId() {

    AccessIdRepresentationModel model1 = new AccessIdRepresentationModel("name1", "user-1");
    AccessIdRepresentationModel model2 = new AccessIdRepresentationModel("name2", "user-2");
    AccessIdRepresentationModel model3 = new AccessIdRepresentationModel("name3", null);
    AccessIdRepresentationModel model4 = new AccessIdRepresentationModel("name4", "user-4");
    // Can't use List.of because it returns an ImmutableCollection
    List<AccessIdRepresentationModel> accessIds =
        new ArrayList<>(List.of(model1, model2, model3, model4));

    LdapClient ldapClient = new LdapClient(environment, ldapTemplate, taskanaConfiguration);
    ldapClient.sortListOfAccessIdResources(accessIds);
    assertThat(accessIds)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactly("user-1", "user-2", "user-4", null);
  }

  @Test
  void should_ReturnAllUsersAndMembersOfGroupsWithTaskanaUserRole() throws Exception {

    setUpEnvMock();
    cut.init();

    AccessIdRepresentationModel user = new AccessIdRepresentationModel("testU", "testUId");

    Set<String> groupsOfUserRole = new HashSet<>();
    Map<TaskanaRole, Set<String>> roleMap = new HashMap<>();
    roleMap.put(TaskanaRole.USER, groupsOfUserRole);

    when(taskanaConfiguration.getRoleMap()).thenReturn(roleMap);

    when(ldapTemplate.search(
            any(String.class), any(), anyInt(), any(), any(LdapClient.UserContextMapper.class)))
        .thenReturn(List.of(user));

    assertThat(cut.searchUsersByNameOrAccessIdInUserRole("test")).hasSize(1).containsExactly(user);
  }

  @Test
  void testLdap_getNameWithoutBaseDn() {

    setUpEnvMock();
    cut.init();
    assertThat(cut.getNameWithoutBaseDn("cn=developersgroup,ou=groups,o=taskanatest"))
        .isEqualTo("cn=developersgroup,ou=groups");
  }

  @Test
  void shouldNot_CreateOrCriteriaWithDnAndAccessIdString_When_PropertyTypeIsSet()
      throws InvalidArgumentException {

    setUpEnvMock();
    lenient().when(this.environment.getProperty("taskana.ldap.groupsOfUser.type")).thenReturn("dn");
    lenient()
        .when(
            ldapTemplate.search(
                any(String.class),
                eq("(&(objectclass=person)(uid=user-1-1))"),
                eq(2),
                any(),
                any(LdapClient.DnStringContextMapper.class)))
        .thenReturn(Collections.singletonList("uid=user-1-1,cn=users,OU=Test,O=TASKANA"));

    cut.init();

    cut.searchGroupsAccessIdIsMemberOf("user-1-1");

    String expectedFilterValue =
        "(&(objectclass=groupOfUniqueNames)(memberUid=uid=user-1-1,cn=users,OU=Test,O=TASKANA))";
    verify(ldapTemplate)
        .search(
            any(String.class),
            eq(expectedFilterValue),
            anyInt(),
            any(),
            any(LdapClient.GroupContextMapper.class));
  }

  @Test
  void testLdap_getFirstPageOfaResultList() {
    setUpEnvMock();
    cut.init();

    List<AccessIdRepresentationModel> result =
        IntStream.range(0, 100)
            .mapToObj(i -> new AccessIdRepresentationModel("" + i, "" + i))
            .collect(Collectors.toList());

    assertThat(cut.getFirstPageOfaResultList(result))
        .hasSize(cut.getMaxNumberOfReturnedAccessIds());
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
    // optional config fields: minSearchForLength, maxNumberOfReturnedAccessIds, userPhoneAttribute,
    // userMobilePhoneAttribute, userEmailAttribute, userOrglevel1Attribute, userOrglevel2Attribute,
    // userOrglevel3Attribute, userOrglevel4Attribute, groupsOfUser, groupsOfUserName,
    // groupOfUserType
    assertThat(cut.checkForMissingConfigurations()).hasSize(LdapSettings.values().length - 12);
  }

  @Test
  void testNameIsRecognizedAsDnCorrectly() {
    setUpEnvMock();
    assertThat(cut.nameIsDn("uid=userid,cn=users,o=TaskanaTest")).isTrue();
    assertThat(cut.nameIsDn("uid=userid,cn=users,o=taskanatest")).isTrue();
    assertThat(cut.nameIsDn("uid=userid,cn=users,o=taskana")).isFalse();
  }

  private void setUpEnvMock() {

    Stream.of(
            new String[][] {
              {"taskana.ldap.minSearchForLength", "3"},
              {"taskana.ldap.maxNumberOfReturnedAccessIds", "50"},
              {"taskana.ldap.baseDn", "o=TaskanaTest"},
              {"taskana.ldap.userSearchBase", "ou=people"},
              {"taskana.ldap.userSearchFilterName", "objectclass"},
              {"taskana.ldap.groupsOfUser", "memberUid"},
              {"taskana.ldap.groupNameAttribute", "cn"},
              {"taskana.ldap.groupSearchFilterValue", "groupOfUniqueNames"},
              {"taskana.ldap.groupSearchFilterName", "objectclass"},
              {"taskana.ldap.groupSearchBase", "ou=groups"},
              {"taskana.ldap.userIdAttribute", "uid"},
              {"taskana.ldap.userMemberOfGroupAttribute", "memberOf"},
              {"taskana.ldap.userLastnameAttribute", "sn"},
              {"taskana.ldap.userFirstnameAttribute", "givenName"},
              {"taskana.ldap.userFullnameAttribute", "cn"},
              {"taskana.ldap.userSearchFilterValue", "person"},
              {"taskana.ldap.userPhoneAttribute", "phoneNumber"},
              {"taskana.ldap.userMobilePhoneAttribute", "mobileNumber"},
              {"taskana.ldap.userEmailAttribute", "email"},
              {"taskana.ldap.userOrglevel1Attribute", "orgLevel1"},
              {"taskana.ldap.userOrglevel2Attribute", "orgLevel2"},
              {"taskana.ldap.userOrglevel3Attribute", "orgLevel3"},
              {"taskana.ldap.userOrglevel4Attribute", "orgLevel4"},
            })
        .forEach(
            strings ->
                lenient().when(this.environment.getProperty(strings[0])).thenReturn(strings[1]));
  }
}
