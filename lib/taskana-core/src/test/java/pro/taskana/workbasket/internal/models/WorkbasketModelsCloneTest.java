package pro.taskana.workbasket.internal.models;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import pro.taskana.workbasket.api.models.Workbasket;

class WorkbasketModelsCloneTest {

  @Test
  void should_CopyWithoutId_When_WorkbasketSummaryClone() {
    Workbasket dummyWorkbasketForSummaryTest = new WorkbasketImpl();
    dummyWorkbasketForSummaryTest.setCustom1("dummyCustom1");
    dummyWorkbasketForSummaryTest.setCustom2("dummyCustom2");
    dummyWorkbasketForSummaryTest.setCustom3("dummyCustom3");
    dummyWorkbasketForSummaryTest.setCustom4("dummyCustom4");
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
    assertThat(dummyWorkbasketSummaryCloned).isEqualTo(dummyWorkbasketSummary);
    assertThat(dummyWorkbasketSummaryCloned).isNotSameAs(dummyWorkbasketSummary);
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
    assertThat(dummyWorkbasketCloned).isEqualTo(dummyWorkbasket);
    assertThat(dummyWorkbasketCloned).isNotSameAs(dummyWorkbasket);
  }

  @Test
  void should_CopyWithoutId_When_WorkbasketAccessItemClone() {
    WorkbasketAccessItemImpl dummyWorkbasketAccessItem = new WorkbasketAccessItemImpl();
    dummyWorkbasketAccessItem.setId("dummyId");
    dummyWorkbasketAccessItem.setAccessName("dummyAccessName");
    dummyWorkbasketAccessItem.setPermAppend(false);
    dummyWorkbasketAccessItem.setPermCustom1(false);
    dummyWorkbasketAccessItem.setPermCustom2(false);
    dummyWorkbasketAccessItem.setPermCustom3(false);
    dummyWorkbasketAccessItem.setPermCustom4(false);
    dummyWorkbasketAccessItem.setPermCustom5(false);
    dummyWorkbasketAccessItem.setPermCustom6(false);
    dummyWorkbasketAccessItem.setPermCustom7(false);
    dummyWorkbasketAccessItem.setPermCustom8(false);
    dummyWorkbasketAccessItem.setPermCustom9(false);
    dummyWorkbasketAccessItem.setPermCustom10(false);
    dummyWorkbasketAccessItem.setPermCustom11(false);
    dummyWorkbasketAccessItem.setPermCustom12(false);
    dummyWorkbasketAccessItem.setPermDistribute(false);
    dummyWorkbasketAccessItem.setPermOpen(false);
    dummyWorkbasketAccessItem.setPermRead(false);
    dummyWorkbasketAccessItem.setPermTransfer(false);

    WorkbasketAccessItemImpl dummyWorkbasketAccessItemCloned = dummyWorkbasketAccessItem.copy();

    assertThat(dummyWorkbasketAccessItemCloned).isNotEqualTo(dummyWorkbasketAccessItem);
    dummyWorkbasketAccessItemCloned.setId(dummyWorkbasketAccessItem.getId());
    assertThat(dummyWorkbasketAccessItemCloned).isEqualTo(dummyWorkbasketAccessItem);
    assertThat(dummyWorkbasketAccessItemCloned).isNotSameAs(dummyWorkbasketAccessItem);
  }
}
