/*-
 * #%L
 * pro.taskana:taskana-core-test
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
package acceptance.taskpreprocessing;

import static acceptance.taskpreprocessing.CreateTaskPreprocessingAccTest.TestCreateTaskPreprocessorProvider.SPI_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.taskpreprocessing.CreateTaskPreprocessingAccTest.TestCreateTaskPreprocessorProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import pro.taskana.classification.api.ClassificationService;
import pro.taskana.classification.api.models.ClassificationSummary;
import pro.taskana.spi.task.api.CreateTaskPreprocessor;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskService;
import pro.taskana.task.api.models.Task;
import pro.taskana.testapi.DefaultTestEntities;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.WithServiceProvider;
import pro.taskana.testapi.builder.WorkbasketAccessItemBuilder;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.WorkbasketSummary;

/** Acceptance test for "task preprocessing" scenario. */
@TaskanaIntegrationTest
@WithServiceProvider(
    serviceProviderInterface = CreateTaskPreprocessor.class,
    serviceProviders = TestCreateTaskPreprocessorProvider.class)
class CreateTaskPreprocessingAccTest {

  @TaskanaInject TaskService taskService;

  WorkbasketSummary workbasketSummary;
  ClassificationSummary classificationSummary;

  @WithAccessId(user = "businessadmin")
  @BeforeAll
  void setup(ClassificationService classificationService, WorkbasketService workbasketService)
      throws Exception {
    classificationSummary =
        DefaultTestEntities.defaultTestClassification()
            .buildAndStoreAsSummary(classificationService);

    workbasketSummary =
        DefaultTestEntities.defaultTestWorkbasket().buildAndStoreAsSummary(workbasketService);

    WorkbasketAccessItemBuilder.newWorkbasketAccessItem()
        .accessId("user-1-1")
        .workbasketId(workbasketSummary.getId())
        .permission(WorkbasketPermission.READ)
        .permission(WorkbasketPermission.OPEN)
        .permission(WorkbasketPermission.APPEND)
        .buildAndStore(workbasketService);
  }

  @WithAccessId(user = "user-1-1")
  @Test
  void should_ProcessTaskBeforeCreation_When_CreateTaskPreprocessorEnabled() throws Exception {
    Task newTaskToCreate = taskService.newTask(workbasketSummary.getId());
    newTaskToCreate.setClassificationKey(classificationSummary.getKey());
    newTaskToCreate.setPrimaryObjRef(DefaultTestEntities.defaultTestObjectReference().build());

    Task createdTask = taskService.createTask(newTaskToCreate);

    assertThat(createdTask.getCustomField(TaskCustomField.CUSTOM_1)).isEqualTo(SPI_VALUE);
  }

  static class TestCreateTaskPreprocessorProvider implements CreateTaskPreprocessor {

    static final String SPI_VALUE = "preprocessedCustomField";

    @Override
    public void processTaskBeforeCreation(Task taskToProcess) {
      taskToProcess.setCustomField(TaskCustomField.CUSTOM_1, SPI_VALUE);
    }
  }
}
