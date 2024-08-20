package acceptance.config;

import static io.kadai.common.api.SharedConstants.MASTER_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.KadaiConfiguration;
import io.kadai.common.test.config.DataSourceGenerator;
import io.kadai.workbasket.api.WorkbasketPermission;
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

/** Test kadai configuration without roles. */
class KadaiConfigAccTest {

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
  void should_ConfigureDomains_For_DefaultPropertiesFile() {
    assertThat(kadaiConfiguration.getDomains())
        .containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B", MASTER_DOMAIN);
  }

  @Test
  void should_ConfigureMinimalPermissionsToAssignDomains_For_DefaultPropertiesFile() {
    assertThat(kadaiConfiguration.getMinimalPermissionsToAssignDomains())
        .containsExactlyInAnyOrder(WorkbasketPermission.READ, WorkbasketPermission.OPEN);
  }

  @Test
  void should_ConfigureClassificationTypes_For_DefaultPropertiesFile() {
    assertThat(kadaiConfiguration.getClassificationTypes())
        .containsExactlyInAnyOrder("TASK", "DOCUMENT");
  }

  @Test
  void should_ConfigureClassificationCategories_For_DefaultPropertiesFile() {
    assertThat(kadaiConfiguration.getClassificationCategoriesByType("TASK"))
        .containsExactlyInAnyOrder("EXTERNAL", "MANUAL", "AUTOMATIC", "PROCESS");
  }

  @Test
  void should_ApplyClassificationProperties_When_PropertiesAreDefined() throws Exception {
    String delimiter = ";";
    String propertiesFileName =
        createNewConfigFile("dummyTestConfig3.properties", delimiter, true, true);
    kadaiConfiguration =
        new KadaiConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initKadaiProperties(propertiesFileName, delimiter)
            .build();
    assertThat(kadaiConfiguration.getClassificationCategoriesByType())
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
                "kadai.roles.admin =Holger|Stefan",
                "kadai.roles.business_admin  = ebe  | konstantin ",
                "kadai.roles.user = nobody")
            .collect(Collectors.toList());
    if (addingTypes) {
      lines.add(String.format("kadai.classification.types= TASK %s document", delimiter));
    }
    if (addingClassification) {
      lines.add(
          String.format(
              "kadai.classification.categories.task= EXTERNAL%s manual%s autoMAtic%s Process",
              delimiter, delimiter, delimiter));
      lines.add("kadai.classification.categories.document= EXTERNAL");
    }

    Files.write(file, lines, StandardCharsets.UTF_8);
    return file.toString();
  }
}
