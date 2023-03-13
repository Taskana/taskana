package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.test.config.DataSourceGenerator;

/** Test the role configuration of TASKANA. */
class TaskanaRoleConfigAccTest {

  @TempDir Path tempDir;
  private TaskanaConfiguration taskanaConfiguration;

  @BeforeEach
  void setup() {
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(), true, DataSourceGenerator.getSchemaName())
            .initTaskanaProperties()
            .build();
  }

  @Test
  void should_ApplyDefaultConfiguration_For_DefaultPropertiesFile() {
    Set<TaskanaRole> rolesConfigured = taskanaConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

    Set<String> users = taskanaConfiguration.getRoleMap().get(TaskanaRole.USER);
    assertThat(users)
        .containsExactlyInAnyOrder(
            "cn=ksc-users,cn=groups,ou=test,o=taskana",
            "teamlead-1",
            "teamlead-2",
            "user-1-1",
            "user-1-2",
            "user-2-1",
            "user-2-2",
            "user-b-1",
            "user-b-2");

    Set<String> admins = taskanaConfiguration.getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("uid=admin,cn=users,ou=test,o=taskana", "admin");

    Set<String> taskAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.TASK_ADMIN);
    assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");

    Set<String> businessAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins)
        .containsExactlyInAnyOrder(
            "businessadmin", "cn=business-admins,cn=groups,ou=test,o=taskana");

    Set<String> monitorAccessIds = taskanaConfiguration.getRoleMap().get(TaskanaRole.MONITOR);
    assertThat(monitorAccessIds)
        .containsExactlyInAnyOrder("monitor", "cn=monitor-users,cn=groups,ou=test,o=taskana");

    Set<String> taskRouters = taskanaConfiguration.getRoleMap().get(TaskanaRole.TASK_ROUTER);
    assertThat(taskRouters)
        .containsExactlyInAnyOrder("cn=routers,cn=groups,ou=test,o=taskana", "user-taskrouter");
  }

  @Test
  void should_ApplyDifferentConfiguration_For_DifferentFile() throws Exception {
    String propertiesFileName = createNewConfigFileWithSameDelimiter("dummyTestConfig.properties");
    String delimiter = "|";
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();

    Set<TaskanaRole> rolesConfigured = taskanaConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

    Set<String> users = taskanaConfiguration.getRoleMap().get(TaskanaRole.USER);
    assertThat(users).containsExactly("nobody");

    Set<String> admins = taskanaConfiguration.getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("user", "username");

    Set<String> businessAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsExactlyInAnyOrder("user2", "user3");

    Set<String> taskAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.TASK_ADMIN);
    assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");
  }

  @Test
  void should_ApplyConfiguration_When_UsingDifferentDelimiter() throws Exception {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFileWithDifferentDelimiter("dummyTestConfig.properties", delimiter);

    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();

    Set<TaskanaRole> rolesConfigured = taskanaConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(TaskanaRole.values());

    Set<String> users = taskanaConfiguration.getRoleMap().get(TaskanaRole.USER);
    assertThat(users).isEmpty();

    Set<String> admins = taskanaConfiguration.getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("user", "name=username,organisation=novatec");

    Set<String> businessAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsExactlyInAnyOrder("name=user2, ou = bpm", "user3");

    Set<String> taskAdmins = taskanaConfiguration.getRoleMap().get(TaskanaRole.TASK_ADMIN);
    assertThat(taskAdmins).contains("taskadmin");
  }

  private String createNewConfigFileWithDifferentDelimiter(String filename, String delimiter)
      throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        List.of(
            "taskana.roles.admin =uSeR " + delimiter + "name=Username,Organisation=novatec",
            "  taskana.roles.businessadmin  = name=user2, ou = bpm " + delimiter + " user3 ",
            " taskana.roles.user = ",
            "taskana.roles.taskadmin= taskadmin");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }

  private String createNewConfigFileWithSameDelimiter(String filename) throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
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
