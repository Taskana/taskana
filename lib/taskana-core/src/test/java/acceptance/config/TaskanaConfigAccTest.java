/*-
 * #%L
 * pro.taskana:taskana-core
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
    assertThat(taskanaConfiguration.getDomains()).containsExactlyInAnyOrder("DOMAIN_A", "DOMAIN_B");
  }

  @Test
  void should_ConfigureMinimalPermissionsToAssignDomains_For_DefaultPropertiesFile() {
    assertThat(taskanaConfiguration.getMinimalPermissionsToAssignDomains())
        .containsExactly(WorkbasketPermission.READ, WorkbasketPermission.OPEN);
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
  void should_NotConfigureClassificationTypes_When_PropertiesAreNotDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig1.properties", false, true);
    String delimiter = ";";
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaConfiguration.getClassificationTypes()).isEmpty();
  }

  @Test
  void should_NotConfigureClassificationCategories_When_PropertiesAreNotDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig2.properties", true, false);
    String delimiter = ";";
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaConfiguration.getClassificationCategoriesByType())
        .containsExactly(
            Map.entry("TASK", Collections.emptyList()),
            Map.entry("DOCUMENT", Collections.emptyList()));
  }

  @Test
  void should_ApplyClassificationProperties_When_PropertiesAreDefined() throws Exception {
    String propertiesFileName = createNewConfigFile("dummyTestConfig3.properties", true, true);
    String delimiter = ";";
    taskanaConfiguration =
        new TaskanaConfiguration.Builder(
                DataSourceGenerator.getDataSource(),
                true,
                DataSourceGenerator.getSchemaName(),
                true)
            .initTaskanaProperties(propertiesFileName, delimiter)
            .build();
    assertThat(taskanaConfiguration.getClassificationCategoriesByType())
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
