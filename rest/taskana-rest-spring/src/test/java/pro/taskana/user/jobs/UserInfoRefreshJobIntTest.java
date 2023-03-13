package pro.taskana.user.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

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
import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.common.rest.ldap.LdapClient;
import pro.taskana.rest.test.TaskanaSpringBootTest;
import pro.taskana.testapi.security.JaasExtension;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.user.api.UserService;
import pro.taskana.user.api.models.User;

@TaskanaSpringBootTest
@ExtendWith(JaasExtension.class)
@TestMethodOrder(OrderAnnotation.class)
class UserInfoRefreshJobIntTest {

  TaskanaEngine taskanaEngine;
  UserService userService;
  LdapClient ldapClient;

  @Autowired
  public UserInfoRefreshJobIntTest(
      TaskanaEngine taskanaEngine, UserService userService, LdapClient ldapClient) {
    this.taskanaEngine = taskanaEngine;
    this.userService = userService;
    this.ldapClient = ldapClient;
  }

  @Test
  @WithAccessId(user = "businessadmin")
  @Order(1)
  void should_RefreshUserInfo_When_UserInfoRefreshJobIsExecuted() throws Exception {

    try (Connection connection = taskanaEngine.getConfiguration().getDatasource().getConnection()) {

      List<User> users = getUsers(connection);
      assertThat(users).hasSize(14);

      UserInfoRefreshJob userInfoRefreshJob = new UserInfoRefreshJob(taskanaEngine);
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
    }
  }

  @Test
  @WithAccessId(user = "businessadmin")
  @Order(2)
  void should_PostprocessUser_When_RefreshUserPostprocessorIsActive() throws Exception {

    try (Connection connection = taskanaEngine.getConfiguration().getDatasource().getConnection()) {

      UserInfoRefreshJob userInfoRefreshJob = new UserInfoRefreshJob(taskanaEngine);
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
}
