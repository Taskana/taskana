package io.kadai.user.jobs;

import static io.kadai.common.internal.util.CheckedFunction.wrap;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.api.KadaiEngine;
import io.kadai.common.rest.ldap.LdapClient;
import io.kadai.rest.test.KadaiSpringBootTest;
import io.kadai.testapi.security.JaasExtension;
import io.kadai.testapi.security.WithAccessId;
import io.kadai.user.api.UserService;
import io.kadai.user.api.models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

@KadaiSpringBootTest
@ExtendWith(JaasExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class UserInfoRefreshJobIntTest {

  KadaiEngine kadaiEngine;
  UserService userService;
  LdapClient ldapClient;

  @Autowired
  public UserInfoRefreshJobIntTest(
      KadaiEngine kadaiEngine, UserService userService, LdapClient ldapClient) {
    this.kadaiEngine = kadaiEngine;
    this.userService = userService;
    this.ldapClient = ldapClient;
  }

  @Test
  @WithAccessId(user = "businessadmin")
  @Order(1)
  void should_RefreshUserInfo_When_UserInfoRefreshJobIsExecuted() throws Exception {

    try (Connection connection = kadaiEngine.getConfiguration().getDataSource().getConnection()) {

      List<User> users = getUsers(connection);
      assertThat(users).hasSize(14);

      UserInfoRefreshJob userInfoRefreshJob = new UserInfoRefreshJob(kadaiEngine);
      userInfoRefreshJob.execute();

      users = getUsers(connection);
      List<User> ldapusers = ldapClient.searchUsersInUserRole();
      users.sort(Comparator.comparing(User::getId));
      ldapusers.sort(Comparator.comparing(User::getId));

      RecursiveComparisonConfiguration comparisonConfiguration =
          RecursiveComparisonConfiguration.builder()
              .withIgnoredCollectionOrderInFields("groups")
              .withIgnoredFields("longName", "data", "orgLevel1", "domains")
              .build();
      assertThat(users)
          .hasSize(6)
          .hasSameSizeAs(ldapusers)
          .usingRecursiveFieldByFieldElementComparator(comparisonConfiguration)
          .containsExactlyElementsOf(ldapusers);

      // validate groups
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        List<String> groupIds = getGroupInfo(connection, user.getId());
        groupIds.sort(Comparator.naturalOrder());

        User ldapUser = ldapusers.get(i);
        List<String> ldapGroups =
            ldapUser.getGroups().stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        // we know that our users from ldap have groups defined
        // so non should be empty
        assertThat(groupIds)
            .isNotEmpty()
            .hasSameSizeAs(ldapGroups)
            .containsExactlyElementsOf(ldapGroups);
      }

      // validate permissions
      for (int i = 0; i < users.size(); i++) {
        User user = users.get(i);
        List<String> permissionIds = getPermissionInfo(connection, user.getId());
        permissionIds.sort(Comparator.naturalOrder());

        User ldapUser = ldapusers.get(i);
        List<String> ldapPermissions =
            ldapUser.getPermissions().stream()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());

        assertThat(permissionIds)
            .hasSameSizeAs(ldapPermissions)
            .containsExactlyElementsOf(ldapPermissions);
      }
    }
  }

  @Test
  @WithAccessId(user = "businessadmin")
  @Order(2)
  void should_PostprocessUser_When_RefreshUserPostprocessorIsActive() throws Exception {

    try (Connection connection = kadaiEngine.getConfiguration().getDataSource().getConnection()) {

      UserInfoRefreshJob userInfoRefreshJob = new UserInfoRefreshJob(kadaiEngine);
      userInfoRefreshJob.execute();

      Statement statement = connection.createStatement();
      ResultSet rs =
          statement.executeQuery(
              "SELECT * FROM "
                  + connection.getSchema()
                  + ".USER_INFO "
                  + "WHERE USER_ID='user-2-2'");
      rs.next();
      String updatedOrgLevel = rs.getString("ORG_LEVEL_1");
      assertThat(updatedOrgLevel).isEqualTo("FirstSecond");
    }
  }

  private List<User> getUsers(Connection connection) throws Exception {

    List<String> users = new ArrayList<>();
    Statement statement = connection.createStatement();
    ResultSet rs = statement.executeQuery("SELECT * FROM " + connection.getSchema() + ".USER_INFO");

    while (rs.next()) {
      users.add(rs.getString("USER_ID"));
    }

    return users.stream().map(wrap(userService::getUser)).collect(Collectors.toList());
  }

  private List<String> getGroupInfo(Connection connection, String userId) throws Exception {
    List<String> groupIds = new ArrayList<>();
    PreparedStatement ps =
        connection.prepareStatement(
            "SELECT group_id FROM " + connection.getSchema() + ".group_info WHERE user_id = ?");
    ps.setString(1, userId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      groupIds.add(rs.getString(1));
    }
    return groupIds;
  }

  private List<String> getPermissionInfo(Connection connection, String userId) throws Exception {
    List<String> permissionIds = new ArrayList<>();
    PreparedStatement ps =
        connection.prepareStatement(
            "SELECT permission_id FROM "
                + connection.getSchema()
                + ".permission_info WHERE user_id = ?");
    ps.setString(1, userId);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      permissionIds.add(rs.getString(1));
    }
    return permissionIds;
  }
}
