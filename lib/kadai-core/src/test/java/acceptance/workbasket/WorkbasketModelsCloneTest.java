package acceptance.workbasket;

import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_1;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_2;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_3;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_4;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_5;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_6;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_7;
import static io.kadai.workbasket.api.WorkbasketCustomField.CUSTOM_8;
import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.workbasket.api.WorkbasketPermission;
import io.kadai.workbasket.api.models.Workbasket;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import io.kadai.workbasket.internal.models.WorkbasketSummaryImpl;
import org.junit.jupiter.api.Test;

class WorkbasketModelsCloneTest {

  @Test
  void should_CopyWithoutId_When_WorkbasketSummaryClone() {
    Workbasket dummyWorkbasketForSummaryTest = new WorkbasketImpl();
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_1, "dummyCustom1");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_2, "dummyCustom2");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_3, "dummyCustom3");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_4, "dummyCustom4");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_5, "dummyCustom5");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_6, "dummyCustom6");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_7, "dummyCustom7");
    dummyWorkbasketForSummaryTest.setCustomField(CUSTOM_8, "dummyCustom8");
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
    dummyWorkbasket.setCustom5("dummyCustom5");
    dummyWorkbasket.setCustom6("dummyCustom6");
    dummyWorkbasket.setCustom7("dummyCustom7");
    dummyWorkbasket.setCustom8("dummyCustom8");
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
