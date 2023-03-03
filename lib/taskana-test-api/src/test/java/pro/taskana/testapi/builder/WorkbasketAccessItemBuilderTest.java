/*-
 * #%L
 * pro.taskana:taskana-test-api
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
package pro.taskana.testapi.builder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static pro.taskana.common.internal.util.CheckedSupplier.wrap;
import static pro.taskana.testapi.DefaultTestEntities.defaultTestWorkbasket;
import static pro.taskana.testapi.builder.WorkbasketAccessItemBuilder.newWorkbasketAccessItem;

import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.TaskanaEngine;
import pro.taskana.testapi.TaskanaInject;
import pro.taskana.testapi.TaskanaIntegrationTest;
import pro.taskana.testapi.security.WithAccessId;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.WorkbasketService;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;

@TaskanaIntegrationTest
class WorkbasketAccessItemBuilderTest {

  @TaskanaInject WorkbasketService workbasketService;
  @TaskanaInject TaskanaEngine taskanaEngine;

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PersistWorkbasketAccessItem_When_UsingWorkbasketAccessItemBuilder() throws Exception {
    Workbasket workbasket = defaultTestWorkbasket().key("key0_F").buildAndStore(workbasketService);

    WorkbasketAccessItem workbasketAccessItem =
        newWorkbasketAccessItem()
            .workbasketId(workbasket.getId())
            .accessId("user-1-1")
            .permission(WorkbasketPermission.READ)
            .buildAndStore(workbasketService);

    List<WorkbasketAccessItem> workbasketAccessItems =
        workbasketService.getWorkbasketAccessItems(workbasket.getId());

    assertThat(workbasketAccessItems).containsExactly(workbasketAccessItem);
  }

  @Test
  void should_PersistWorkbasketAccessItemAsUser_When_UsingWorkbasketAccessItemBuilder()
      throws Exception {
    Workbasket workbasket =
        defaultTestWorkbasket().key("key1_F").buildAndStore(workbasketService, "businessadmin");

    WorkbasketAccessItem workbasketAccessItem =
        newWorkbasketAccessItem()
            .workbasketId(workbasket.getId())
            .accessId("user-1-1")
            .permission(WorkbasketPermission.READ)
            .buildAndStore(workbasketService, "businessadmin");

    List<WorkbasketAccessItem> workbasketAccessItems =
        taskanaEngine.runAsAdmin(
            wrap(() -> workbasketService.getWorkbasketAccessItems(workbasket.getId())));

    assertThat(workbasketAccessItems).containsExactly(workbasketAccessItem);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_PopulateWorkbasketAccessItem_When_UsingEveryBuilderFunction() throws Exception {
    Workbasket workbasket = defaultTestWorkbasket().key("key2_F").buildAndStore(workbasketService);

    WorkbasketAccessItemImpl expectedWorkbasketAccessItem =
        (WorkbasketAccessItemImpl)
            workbasketService.newWorkbasketAccessItem(workbasket.getId(), "user-1-1");
    expectedWorkbasketAccessItem.setWorkbasketKey(workbasket.getKey());
    expectedWorkbasketAccessItem.setAccessName("Max Mustermann");
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.READ, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.OPEN, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.APPEND, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.TRANSFER, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.DISTRIBUTE, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_1, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_2, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_3, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_4, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_5, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_6, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_7, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_8, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_9, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_10, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_11, true);
    expectedWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_12, true);

    WorkbasketAccessItem accessItem =
        newWorkbasketAccessItem()
            .workbasketId(workbasket.getId())
            .accessId("user-1-1")
            .accessName("Max Mustermann")
            .permission(WorkbasketPermission.READ)
            .permission(WorkbasketPermission.OPEN)
            .permission(WorkbasketPermission.APPEND)
            .permission(WorkbasketPermission.TRANSFER)
            .permission(WorkbasketPermission.DISTRIBUTE)
            .permission(WorkbasketPermission.CUSTOM_1)
            .permission(WorkbasketPermission.CUSTOM_2)
            .permission(WorkbasketPermission.CUSTOM_3)
            .permission(WorkbasketPermission.CUSTOM_4)
            .permission(WorkbasketPermission.CUSTOM_5)
            .permission(WorkbasketPermission.CUSTOM_6)
            .permission(WorkbasketPermission.CUSTOM_7)
            .permission(WorkbasketPermission.CUSTOM_8)
            .permission(WorkbasketPermission.CUSTOM_9)
            .permission(WorkbasketPermission.CUSTOM_10)
            .permission(WorkbasketPermission.CUSTOM_11)
            .permission(WorkbasketPermission.CUSTOM_12)
            .buildAndStore(workbasketService);

    assertThat(accessItem)
        .hasNoNullFieldsOrProperties()
        .usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedWorkbasketAccessItem);
  }

  @WithAccessId(user = "businessadmin")
  @Test
  void should_ResetClassificationId_When_StoringClassificationMultipleTimes() throws Exception {
    Workbasket workbasket = defaultTestWorkbasket().key("key3_F").buildAndStore(workbasketService);

    WorkbasketAccessItemBuilder workbasketAccessItemBuilder =
        newWorkbasketAccessItem()
            .workbasketId(workbasket.getId())
            .permission(WorkbasketPermission.READ);

    assertThatCode(
            () -> {
              workbasketAccessItemBuilder.accessId("hanspeter").buildAndStore(workbasketService);
              workbasketAccessItemBuilder.accessId("hanspeter2").buildAndStore(workbasketService);
            })
        .doesNotThrowAnyException();
  }
}
