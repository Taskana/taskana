package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import pro.taskana.TaskanaEngineConfiguration;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.internal.TaskanaEngineImpl;
import pro.taskana.common.internal.TaskanaEngineTestConfiguration;

/**
 * Test taskana's role configuration.
 *
 * @author bbr
 */
class TaskanaRoleConfigAccTest extends TaskanaEngineImpl {

  @TempDir Path tempDir;

  TaskanaRoleConfigAccTest() throws SQLException {
    super(
        new TaskanaEngineConfiguration(
            TaskanaEngineTestConfiguration.getDataSource(),
            true,
            TaskanaEngineTestConfiguration.getSchemaName()));
  }

  @Test
  void testStandardConfig() {
    Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
    assertThat(rolesConfigured).containsOnly(TaskanaRole.values());

    Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
    assertThat(users).contains("user_1_1", "user_1_2");

    Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).contains("name=konrad,organisation=novatec", "admin");

    Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).contains("max", "moritz");

    Set<String> monitorAccessIds = getConfiguration().getRoleMap().get(TaskanaRole.MONITOR);
    assertThat(monitorAccessIds).contains("teamlead_2", "monitor");
  }

  @Test
  void testOtherConfigFileSameDelimiter() throws IOException {
    String propertiesFileName = createNewConfigFileWithSameDelimiter("dummyTestConfig.properties");
    getConfiguration().initTaskanaProperties(propertiesFileName, "|");

    Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
    assertThat(rolesConfigured).containsOnly(TaskanaRole.values());

    Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
    assertThat(users).containsOnly("nobody");

    Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsOnly("holger", "stefan");

    Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsOnly("ebe", "konstantin");
  }

  @Test
  void testOtherConfigFileDifferentDelimiter() throws IOException {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFileWithDifferentDelimiter("dummyTestConfig.properties", delimiter);
    getConfiguration().initTaskanaProperties(propertiesFileName, delimiter);

    Set<TaskanaRole> rolesConfigured = getConfiguration().getRoleMap().keySet();
    assertThat(rolesConfigured).containsOnly(TaskanaRole.values());

    Set<String> users = getConfiguration().getRoleMap().get(TaskanaRole.USER);
    assertThat(users).isEmpty();

    Set<String> admins = getConfiguration().getRoleMap().get(TaskanaRole.ADMIN);
    assertThat(admins).containsOnly("holger", "name=stefan,organisation=novatec");

    Set<String> businessAdmins = getConfiguration().getRoleMap().get(TaskanaRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsOnly("name=ebe, ou = bpm", "konstantin");
  }

  private String createNewConfigFileWithDifferentDelimiter(String filename, String delimiter)
      throws IOException {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        Arrays.asList(
            "taskana.roles.Admin =hOlGeR " + delimiter + "name=Stefan,Organisation=novatec",
            "  taskana.roles.businessadmin  = name=ebe, ou = bpm " + delimiter + " konstantin ",
            " taskana.roles.user = ");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }

  private String createNewConfigFileWithSameDelimiter(String filename) throws IOException {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        Arrays.asList(
            "taskana.roles.Admin =hOlGeR|Stefan",
            "  taskana.roles.businessadmin  = ebe  | konstantin ",
            " taskana.roles.user = nobody");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }
}
