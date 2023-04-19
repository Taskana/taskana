package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.api.SharedConstants.MASTER_DOMAIN;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pro.taskana.TaskanaConfiguration;
import pro.taskana.common.test.config.DataSourceGenerator;
import pro.taskana.workbasket.api.WorkbasketPermission;

/** Test taskana configuration without roles. */
class TaskanaConfigAccTest {

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
  void should_ConfigureDomains_For_DefaultPropertiesFile() {
    assertThat(taskanaConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
  }

  @Test
  void should_ConfigureMinimalPermissionsToAssignDomains_For_DefaultPropertiesFile() {
    assertThat(taskanaConfiguration.getMinimalPermissionsToAssignDomains())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.OPEN);
  }

  @Test
  void should_ConfigureClassificationTypes_For_DefaultPropertiesFile() {
    assertThat(taskanaConfiguration.getClassificationTypes())
        .containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ConfigureClassificationCategories_For_DefaultPropertiesFile() {
    assertThat(taskanaConfiguration.getClassificationCategoriesByType("TASK"))
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_ApplyClassificationProperties_When_PropertiesAreDefined() throws Exception {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFile("dummyTestConfig3.properties", delimiter, true, true);
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaConfiguration.getClassificationCategoriesByType())
        .containsExactlyInAnyOrderEntriesOf(
            Map.ofEntries(
                Map.entry("TASK", List.of("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS")),
                Map.entry("DOCUMENT", List.of("EXTERNAL"))));
  }

  private String createNewConfigFile(
      String filename, String delimiter, boolean addingTypes, boolean addingClassification)
      throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        Stream.of(
                "taskana.roles.admin =Holger|Stefan",
                "taskana.roles.business_admin  = ebe  | konstantin ",
                "taskana.roles.user = nobody")
            .collect(Collectors.toList());
    if (addingTypes) {
      lines.add(String.format("taskana.classification.types= TASK %s document", delimiter));
    }
    if (addingClassification) {
      lines.add(
          String.format(
              "taskana.classification.categories.task= EXTERNAL%s manual%s autoMAtic%s Process",
              delimiter, delimiter, delimiter));
      lines.add("taskana.classification.categories.document= EXTERNAL");
    }

    Files.write(file, lines, StandardCharsets.UTF_8);
    return file.toString();
  }
}
