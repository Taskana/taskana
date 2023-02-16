package acceptance.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
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
  private TaskanaConfiguration taskanaEngineConfiguration;

  @BeforeEach
  void setup() {
    taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(), true, DataSourceGenerator.getSchemaName())
            .initTaskanaProperties()
            .build();
  }

  @Test
  void should_ConfigureDomains_For_DefaultPropertiesFile() {
    assertThat(taskanaEngineConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
  }

  @Test
  void should_ConfigureMinimalPermissionsToAssignDomains_For_DefaultPropertiesFile() {
    assertThat(taskanaEngineConfiguration.getMinimalPermissionsToAssignDomains())
        .containsExactly(WorkbasketPermission.READ, WorkbasketPermission.OPEN);
  }

  @Test
  void should_ConfigureClassificationTypes_For_DefaultPropertiesFile() {
    assertThat(taskanaEngineConfiguration.getClassificationTypes())
        .containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ConfigureClassificationCategories_For_DefaultPropertiesFile() {
    assertThat(taskanaEngineConfiguration.getClassificationCategoriesByType("TASK"))
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_NotConfigureClassificationTypes_When_PropertiesAreNotDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig1.properties", false, true);
    String delimiter = ";";
    taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaEngineConfiguration.getClassificationTypes()).isEmpty();
  }

  @Test
  void should_NotConfigureClassificationCategories_When_PropertiesAreNotDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig2.properties", true, false);
    String delimiter = ";";
    taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaEngineConfiguration.getClassificationCategoriesByType())
        .containsExactly(
            Map.entry("TASK", Collections.emptyList()),
            Map.entry("DOCUMENT", Collections.emptyList()));
  }

  @Test
  void should_ApplyClassificationProperties_When_PropertiesAreDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig3.properties", true, true);
    String delimiter = ";";
    taskanaEngineConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaEngineConfiguration.getClassificationCategoriesByType())
        .containsExactly(
            Map.entry("TASK", List.of("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS")),
            Map.entry("DOCUMENT", List.of("EXTERNAL")));
  }

  private String createNewConfigFile(
      String filename, boolean addingTypes, boolean addingClassification) throws Exception {
    Path file = Files.createFile(tempDir.resolve(filename));
    List<String> lines =
        Stream.of(
                "taskana.roles.admin =Holger|Stefan",
                "taskana.roles.businessadmin  = ebe  | konstantin ",
                "taskana.roles.user = nobody")
            .collect(Collectors.toList());
    if (addingTypes) {
      lines.add("taskana.classification.types= TASK , document");
    }
    if (addingClassification) {
      lines.add("taskana.classification.categories.task= EXTERNAL, manual, autoMAtic, Process");
      lines.add("taskana.classification.categories.document= EXTERNAL");
    }

    Files.write(file, lines, StandardCharsets.UTF_8);
    return file.toString();
  }
}
