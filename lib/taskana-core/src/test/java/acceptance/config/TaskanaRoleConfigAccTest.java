package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.h2.store.fs.FileUtils;
import org.junit.jupiter.api.Test;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;

/** Test taskana's role configuration. */
class TaskanaRoleConfigAccTest extends TaskanaEngineImpl {

  TaskanaRoleConfigAccTest() throws Exception {
    super(
        new TaskanaEngineConfiguration(
            TaskanaEngineTestConfiguration.getDataSource(),
            true,
            TaskanaEngineTestConfiguration.getSchemaName()));
  }

  @Test
  void testStandardConfig() {
    Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

    Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
    assertThat(users)
        .containsExactlyInAnyOrder(
            "teamlead-1",
            "teamlead-2",
            "user-1-1",
            "user-1-2",
            "user-2-1",
            "user-2-2",
            "user-b-1",
            "user-b-2");

    Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("uid=admin,cn=users,ou=test,o=taskana", "admin");

    Set<String> taskAdmins = getConfiguration().getRoleMap().get(TaskanaRole.TASK_ADMIN);
    assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");

    Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins)
        .containsExactlyInAnyOrder(
            "businessadmin", "cn=business-admins,cn=groups,ou=test,o=taskana");

    Set<String> monitorAccessIds = getConfiguration().getRoleMap().get(TaskanaRole.MONITOR);
    assertThat(monitorAccessIds)
        .containsExactlyInAnyOrder("monitor", "cn=monitor-users,cn=groups,ou=test,o=taskana");
  }

  @Test
  void testOtherConfigFileSameDelimiter() throws Exception {
    String propertiesFileName = createNewConfigFileWithSameDelimiter("/dummyTestConfig.properties");
    try {
      getConfiguration().initTaskanaProperties(propertiesFileName, "|");

      Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
      assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

      Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
      assertThat(users).containsExactly("nobody");

      Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
      assertThat(admins).containsExactlyInAnyOrder("user", "username");

      Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
      assertThat(businessAdmins).containsExactlyInAnyOrder("user2", "user3");

      Set<String> taskAdmins = getConfiguration().getRoleMap().get(TaskanaRole.TASK_ADMIN);
      assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");

    } finally {
      deleteFile(propertiesFileName);
    }
  }

  @Test
  void testOtherConfigFileDifferentDelimiter() throws Exception {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFileWithDifferentDelimiter("/dummyTestConfig.properties", delimiter);
    try {
      getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);

      Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
      assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

      Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
      assertThat(users).isEmpty();

      Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
      assertThat(admins).containsExactlyInAnyOrder("user", "name=username,organisation=novatec");

      Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
      assertThat(businessAdmins).containsExactlyInAnyOrder("name=user2, ou = bpm", "user3");

      Set<String> taskAdmins = getConfiguration().getRoleMap().get(TaskanaRole.TASK_ADMIN);
      assertThat(taskAdmins).contains("taskadmin");

    } finally {
      deleteFile(propertiesFileName);
    }
  }

  private String createNewConfigFileWithDifferentDelimiter(String filename, String delimiter)
      throws Exception {
    Path file = Files.createFile(Paths.get(System.getProperty("user.home") + filename));
    List<String> lines =
        List.of(
            "taskana.roles.admin =uSeR " + delimiter + "name=Username,Organisation=novatec",
            "  taskana.roles.businessadmin  = name=user2, ou = bpm " + delimiter + " user3 ",
            " taskana.roles.user = ",
            "taskana.roles.taskadmin= taskadmin");
    Files.write(file, lines, StandardCharsets.UTF_8);
    return file.toString();
  }

  private void deleteFile(String propertiesFileName) {
    File f = new File(propertiesFileName);
    if (f.exists() && !f.isDirectory()) {
      FileUtils.delete(propertiesFileName);
    }
  }

  private String createNewConfigFileWithSameDelimiter(String filename) throws Exception {
    Path file = Files.createFile(Paths.get(System.getProperty("user.home") + filename));
    List<String> lines =
        List.of(
            "taskana.roles.admin =uSeR|Username",
            "  taskana.roles.businessadmin  = user2  | user3 ",
            " taskana.roles.user = nobody",
            "taskana.roles.taskadmin= taskadmin");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }
}
