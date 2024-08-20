package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.common.api.KadaiRole;
import io.kadai.common.test.config.DataSourceGenerator;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Test the role configuration of KADAI. */
class KadaiRoleConfigAccTest {

  @TempDir Path tempDir;
  private KadaiConfiguration kadaiConfiguration;

  @BeforeEach
  void setup() {
    kadaiConfiguration =
        new KadaiConfiguration.Builder(
                DataSourceGenerator.getDataSource(), true, DataSourceGenerator.getSchemaName())
            .initKadaiProperties()
            .build();
  }

  @Test
  void should_ApplyDefaultConfiguration_For_DefaultPropertiesFile() {
    Set<KadaiRole> rolesConfigured = kadaiConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(KadaiRole.values());

    Set<String> users = kadaiConfiguration.getRoleMap().get(KadaiRole.USER);
    assertThat(users)
        .containsExactlyInAnyOrder(
            "cn=ksc-users,cn=groups,ou=test,o=kadai",
            "teamlead-1",
            "teamlead-2",
            "user-1-1",
            "user-1-2",
            "user-2-1",
            "user-2-2",
            "user-b-1",
            "user-b-2");

    Set<String> admins = kadaiConfiguration.getRoleMap().get(KadaiRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("uid=admin,cn=users,ou=test,o=kadai", "admin");

    Set<String> taskAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.TASK_ADMIN);
    assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");

    Set<String> businessAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.BUSINESS_ADMIN);
    assertThat(businessAdmins)
        .containsExactlyInAnyOrder("businessadmin", "cn=business-admins,cn=groups,ou=test,o=kadai");

    Set<String> monitorAccessIds = kadaiConfiguration.getRoleMap().get(KadaiRole.MONITOR);
    assertThat(monitorAccessIds)
        .containsExactlyInAnyOrder("monitor", "cn=monitor-users,cn=groups,ou=test,o=kadai");

    Set<String> taskRouters = kadaiConfiguration.getRoleMap().get(KadaiRole.TASK_ROUTER);
    assertThat(taskRouters)
        .containsExactlyInAnyOrder("cn=routers,cn=groups,ou=test,o=kadai", "user-taskrouter");
  }

  @Test
  void should_ApplyDifferentConfiguration_For_DifferentFile() throws Exception {
    String propertiesFileName = createNewConfigFileWithSameDelimiter("dummyTestConfig.properties");
    String delimiter = "|";
    kadaiConfiguration =
        new KadaiConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initKadaiProperties(propertiesFileName, delimiter)
            .build();

    Set<KadaiRole> rolesConfigured = kadaiConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(KadaiRole.values());

    Set<String> users = kadaiConfiguration.getRoleMap().get(KadaiRole.USER);
    assertThat(users).containsExactly("nobody");

    Set<String> admins = kadaiConfiguration.getRoleMap().get(KadaiRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("user", "username");

    Set<String> businessAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsExactlyInAnyOrder("user2", "user3");

    Set<String> taskAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.TASK_ADMIN);
    assertThat(taskAdmins).containsExactlyInAnyOrder("taskadmin");
  }

  @Test
  void should_ApplyConfiguration_When_UsingDifferentDelimiter() throws Exception {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFileWithDifferentDelimiter("dummyTestConfig.properties", delimiter);

    kadaiConfiguration =
        new KadaiConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initKadaiProperties(propertiesFileName, delimiter)
            .build();

    Set<KadaiRole> rolesConfigured = kadaiConfiguration.getRoleMap().keySet();
    assertThat(rolesConfigured).containsExactlyInAnyOrder(KadaiRole.values());

    Set<String> users = kadaiConfiguration.getRoleMap().get(KadaiRole.USER);
    assertThat(users).isEmpty();

    Set<String> admins = kadaiConfiguration.getRoleMap().get(KadaiRole.ADMIN);
    assertThat(admins).containsExactlyInAnyOrder("user", "name=username,organisation=envite");

    Set<String> businessAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.BUSINESS_ADMIN);
    assertThat(businessAdmins).containsExactlyInAnyOrder("name=user2, ou = bpm", "user3");

    Set<String> taskAdmins = kadaiConfiguration.getRoleMap().get(KadaiRole.TASK_ADMIN);
    assertThat(taskAdmins).contains("taskadmin");
  }

  private String createNewConfigFileWithDifferentDelimiter(String filename, String delimiter)
      throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        List.of(
            "kadai.roles.admin =uSeR " + delimiter + "name=Username,Organisation=envite",
            "  kadai.roles.business_admin  = name=user2, ou = bpm " + delimiter + " user3 ",
            " kadai.roles.user = ",
            "kadai.roles.task_admin= taskadmin");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }

  private String createNewConfigFileWithSameDelimiter(String filename) throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        List.of(
            "kadai.roles.admin =uSeR|Username",
            "  kadai.roles.business_admin  = user2  | user3 ",
            " kadai.roles.user = nobody",
            "kadai.roles.task_admin= taskadmin");

    Files.write(file, lines, StandardCharsets.UTF_8);

    return file.toString();
  }
}
