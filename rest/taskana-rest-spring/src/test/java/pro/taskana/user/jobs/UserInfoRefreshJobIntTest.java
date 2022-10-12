package pro.taskana.user.jobs;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.internal.util.CheckedFunction.wrap;

import java.sql.Connection;
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
import pro.taskana.common.test.rest.TaskanaSpringBootTest;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
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
}
