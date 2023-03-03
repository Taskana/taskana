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
package acceptance.workbasket;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static pro.taskana.workbasket.api.WorkbasketCustomField.CUSTOM_4;

import org.junit.jupiter.api.Test;

import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.models.Workbasket;
import pro.taskana.workbasket.internal.models.WorkbasketAccessItemImpl;
import pro.taskana.workbasket.internal.models.WorkbasketImpl;
import pro.taskana.workbasket.internal.models.WorkbasketSummaryImpl;

class WorkbasketModelsCloneTest {

  @Test
  void should_CopyWithoutId_When_WorkbasketSummaryClone() {
    Workbasket dummyWorkbasketForSummaryTest = new WorkbasketImpl();
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_1, "dummyCustom1");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_2, "dummyCustom2");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_3, "dummyCustom3");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_4, "dummyCustom4");
    dummyWorkbasketForSummaryTest.setDescription("dummyDescription");
    dummyWorkbasketForSummaryTest.setMarkedForDeletion(false);
    dummyWorkbasketForSummaryTest.setName("dummyName");
    dummyWorkbasketForSummaryTest.setOrgLevel1("dummyOrgLevel1");
    dummyWorkbasketForSummaryTest.setOrgLevel2("dummyOrgLevel2");
    dummyWorkbasketForSummaryTest.setOrgLevel3("dummyOrgLevel3");
    dummyWorkbasketForSummaryTest.setOrgLevel4("dummyOrgLevel4");
    dummyWorkbasketForSummaryTest.setOwner("dummyOwner");
    WorkbasketSummaryImpl dummyWorkbasketSummary =
        (WorkbasketSummaryImpl) dummyWorkbasketForSummaryTest.asSummary();
    dummyWorkbasketSummary.setId("dummyId");

    WorkbasketSummaryImpl dummyWorkbasketSummaryCloned = dummyWorkbasketSummary.copy();

    assertThat(dummyWorkbasketSummaryCloned).isNotEqualTo(dummyWorkbasketSummary);
    dummyWorkbasketSummaryCloned.setId(dummyWorkbasketSummary.getId());
    assertThat(dummyWorkbasketSummaryCloned)
        .isEqualTo(dummyWorkbasketSummary)
        .isNotSameAs(dummyWorkbasketSummary);
  }

  @Test
  void should_CopyWithoutId_When_WorkbasketClone() {
    WorkbasketImpl dummyWorkbasket = new WorkbasketImpl();
    dummyWorkbasket.setId("dummyId");
    dummyWorkbasket.setCustom1("dummyCustom1");
    dummyWorkbasket.setCustom2("dummyCustom2");
    dummyWorkbasket.setCustom3("dummyCustom3");
    dummyWorkbasket.setCustom4("dummyCustom4");
    dummyWorkbasket.setDescription("dummyDescription");
    dummyWorkbasket.setMarkedForDeletion(false);
    dummyWorkbasket.setName("dummyName");
    dummyWorkbasket.setOrgLevel1("dummyOrgLevel1");
    dummyWorkbasket.setOrgLevel2("dummyOrgLevel2");
    dummyWorkbasket.setOrgLevel3("dummyOrgLevel3");
    dummyWorkbasket.setOrgLevel4("dummyOrgLevel4");
    dummyWorkbasket.setOwner("dummyOwner");

    WorkbasketImpl dummyWorkbasketCloned = dummyWorkbasket.copy(dummyWorkbasket.getKey());

    assertThat(dummyWorkbasketCloned).isNotEqualTo(dummyWorkbasket);
    dummyWorkbasketCloned.setId(dummyWorkbasket.getId());
    assertThat(dummyWorkbasketCloned).isEqualTo(dummyWorkbasket).isNotSameAs(dummyWorkbasket);
  }

  @Test
  void should_CopyWithoutId_When_WorkbasketAccessItemClone() {
    WorkbasketAccessItemImpl dummyWorkbasketAccessItem = new WorkbasketAccessItemImpl();
    dummyWorkbasketAccessItem.setId("dummyId");
    dummyWorkbasketAccessItem.setAccessName("dummyAccessName");
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.OPEN, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.READ, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.APPEND, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.TRANSFER, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.DISTRIBUTE, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_1, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_2, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_3, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_4, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_5, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_6, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_7, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_8, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_9, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_10, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_11, false);
    dummyWorkbasketAccessItem.setPermission(WorkbasketPermission.CUSTOM_12, false);

    WorkbasketAccessItemImpl dummyWorkbasketAccessItemCloned = dummyWorkbasketAccessItem.copy();

    assertThat(dummyWorkbasketAccessItemCloned).isNotEqualTo(dummyWorkbasketAccessItem);
    dummyWorkbasketAccessItemCloned.setId(dummyWorkbasketAccessItem.getId());
    assertThat(dummyWorkbasketAccessItemCloned)
        .isEqualTo(dummyWorkbasketAccessItem)
        .isNotSameAs(dummyWorkbasketAccessItem);
  }
}
