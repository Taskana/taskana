package pro.taskana.example.ldap;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.common.rest.models.AccessIdRepresentationModel;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.user.api.models.User;

/** Test Ldap attachment. */
@TestPropertySource(properties = "taskana.ldap.useDnForGroups=false")
@TaskanaSpringBootTest
class LdapForUseDnForGroupsDisabledTest {

  @Autowired
  LdapClient ldapClient;

  @Test
  void should_FindAllUsersAndGroupAndPermissions_When_SearchWithSubstringOfName() throws Exception {
    List<AccessIdRepresentationModel> usersGroupsPermissions =
        ldapClient.searchUsersAndGroupsAndPermissions("lead");
    assertThat(usersGroupsPermissions)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder(
            "teamlead-1", "teamlead-2", "ksc-teamleads");
  }

  @Test
  void should_FindUser_When_SearchingWithFirstAndLastname() throws Exception {
    List<AccessIdRepresentationModel> usersGroupsPermissions =
        ldapClient.searchUsersAndGroupsAndPermissions("Elena");
    assertThat(usersGroupsPermissions).hasSize(2);

    usersGroupsPermissions = ldapClient.searchUsersAndGroupsAndPermissions("Elena Faul");
    assertThat(usersGroupsPermissions).hasSize(1);
  }

  @Test
  void should_FindGroupsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> groups =
        ldapClient.searchGroupsAccessIdIsMemberOf("user-2-2");
    assertThat(groups)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder("ksc-users");
  }

  @Test
  void should_FindPermissionsForUser_When_UserIdIsProvided() throws Exception {
    List<AccessIdRepresentationModel> permissions =
        ldapClient.searchPermissionsAccessIdHas("user-1-2");
    assertThat(permissions)
        .extracting(AccessIdRepresentationModel::getAccessId)
        .containsExactlyInAnyOrder("taskana:callcenter:ab:ab/a:callcenter-vip",
            "taskana:callcenter:ab:ab/a:callcenter");
    assertThat(permissions)
        .extracting(AccessIdRepresentationModel::getName)
        .containsExactlyInAnyOrder("Taskana:CallCenter:AB:AB/A:CallCenter-vip",
            "Taskana:CallCenter:AB:AB/A:CallCenter");
  }

  @Test
  void should_ReturnFullDnForUser_When_AccessIdOfUserIsGiven() throws Exception {
    String dn = ldapClient.searchDnForAccessId("user-2-2");
    assertThat(dn).isEqualTo("uid=user-2-2,cn=users,ou=test,o=taskana");
  }

  @Test
  void should_ReturnAllUsersInUserRoleWithCorrectAttributes() {

    Map<String, User> users =
        ldapClient.searchUsersInUserRole().stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    assertThat(users).hasSize(8);

    User teamlead1 = users.get("teamlead-1");
    assertThat(teamlead1.getId()).isEqualTo("teamlead-1");
    assertThat(teamlead1.getFirstName()).isEqualTo("Titus");
    assertThat(teamlead1.getLastName()).isEqualTo("Toll");
    assertThat(teamlead1.getFullName()).isEqualTo("Titus Toll");
    assertThat(teamlead1.getEmail()).isEqualTo("Titus.Toll@taskana.de");
    assertThat(teamlead1.getPhone()).isEqualTo("012345678");
    assertThat(teamlead1.getMobilePhone()).isEqualTo("09876554321");
    assertThat(teamlead1.getOrgLevel1()).isEqualTo("ABC");
    assertThat(teamlead1.getOrgLevel2()).isEqualTo("DEF/GHI");
    assertThat(teamlead1.getOrgLevel3()).isEqualTo("JKL");
    assertThat(teamlead1.getOrgLevel4()).isEqualTo("MNO/PQR");

    User user11 = users.get("user-1-1");
    assertThat(user11.getId()).isEqualTo("user-1-1");
    assertThat(user11.getFirstName()).isEqualTo("Max");
    assertThat(user11.getLastName()).isEqualTo("Mustermann");
    assertThat(user11.getFullName()).isEqualTo("Max Mustermann");
    assertThat(user11.getEmail()).isNull();
    assertThat(user11.getPhone()).isNull();
    assertThat(user11.getMobilePhone()).isNull();
    assertThat(user11.getOrgLevel1()).isNull();
    assertThat(user11.getOrgLevel2()).isNull();
    assertThat(user11.getOrgLevel3()).isNull();
    assertThat(user11.getOrgLevel4()).isNull();
  }
}

