package acceptance.history;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.common.internal.util.ObjectAttributeChangeDetector;
import io.kadai.task.api.CallbackState;
import io.kadai.task.internal.models.TaskImpl;
import io.kadai.workbasket.api.WorkbasketType;
import io.kadai.workbasket.internal.models.WorkbasketAccessItemImpl;
import io.kadai.workbasket.internal.models.WorkbasketImpl;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

// TODO: this has to be moved to kadai-common but this is currently not possible due to the
// workbasket and task imports
class ObjectAttributeChangeDetectorTest {

  @Test
  void should_DetermineDifferences_When_ComparingEmptyTaskWithNonEmptyTask() {

    TaskImpl newTask = new TaskImpl();
    newTask.setOwner("new Owner");
    newTask.setCreator("new Creator");
    newTask.setId("new ID");
    newTask.setCreated(Instant.now());
    newTask.setModified(Instant.now());
    newTask.setClassificationKey("new ClassificationKey");
    newTask.setWorkbasketKey("new WorkbasketKey");
    newTask.setBusinessProcessId("new BusinessProcessId");
    newTask.setCallbackState(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    TaskImpl oldTask = new TaskImpl();

    JSONArray changedAttributes =
        new JSONObject(ObjectAttributeChangeDetector.determineChangesInAttributes(oldTask, newTask))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(9);

    changedAttributes =
        new JSONObject(ObjectAttributeChangeDetector.determineChangesInAttributes(newTask, oldTask))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(9);
  }

  @Test
  void should_DetermineDifferences_When_ComparingTasks() {

    TaskImpl oldTask = new TaskImpl();
    oldTask.setOwner("old Owner");
    oldTask.setCreator("old  Creator");
    oldTask.setId("old  ID");
    oldTask.setCreated(Instant.now().minusMillis(100));
    oldTask.setModified(Instant.now().minusMillis(100));
    oldTask.setClassificationKey("old  ClassificationKey");
    oldTask.setWorkbasketKey("old  WorkbasketKey");
    oldTask.setBusinessProcessId("old  BusinessProcessId");
    oldTask.setCallbackState(CallbackState.NONE);

    TaskImpl newTask = new TaskImpl();
    newTask.setOwner("new Owner");
    newTask.setCreator("new Creator");
    newTask.setId("old  ID");
    newTask.setCreated(Instant.now());
    newTask.setModified(Instant.now());
    newTask.setClassificationKey("new ClassificationKey");
    newTask.setWorkbasketKey("new WorkbasketKey");
    newTask.setBusinessProcessId("new BusinessProcessId");
    newTask.setCallbackState(CallbackState.CALLBACK_PROCESSING_REQUIRED);

    JSONArray changedAttributes =
        new JSONObject(ObjectAttributeChangeDetector.determineChangesInAttributes(oldTask, newTask))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(8);
  }

  @Test
  void should_IgnoreDifferencesInCustomAttributes_When_CustomAttributesHaveChanged() {

    TaskImpl oldTask = new TaskImpl();
    oldTask.setOwner("old Owner");
    oldTask.setCreator("old  Creator");

    TaskImpl newTask = new TaskImpl();
    newTask.setOwner("new Owner");
    newTask.setCreator("new Creator");

    Map<String, String> customAttriutes = new HashMap<>();
    customAttriutes.put("new key", "new value");
    newTask.setCustomAttributes(customAttriutes);

    JSONArray changedAttributes =
        new JSONObject(ObjectAttributeChangeDetector.determineChangesInAttributes(oldTask, newTask))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(2);
  }

  @Test
  void should_DetermineDifferences_When_ComparingWorkbaskets() {

    Instant created = Instant.now();

    WorkbasketImpl oldWorkbasket = new WorkbasketImpl();
    oldWorkbasket.setId("oldWbId");
    oldWorkbasket.setKey("oldWbKey");
    oldWorkbasket.setDomain("oldWbDomain");
    oldWorkbasket.setCreated(created.minus(10, ChronoUnit.SECONDS));
    oldWorkbasket.setModified(created.minus(5, ChronoUnit.SECONDS));
    oldWorkbasket.setName("oldWb");
    oldWorkbasket.setType(WorkbasketType.PERSONAL);
    oldWorkbasket.setOrgLevel1("oworgLevel1");
    oldWorkbasket.setCustom1("owcustom1");
    oldWorkbasket.setOrgLevel2("oworgLevel2");
    oldWorkbasket.setCustom2("owcustom2");
    oldWorkbasket.setOrgLevel3("oworgLevel3");
    oldWorkbasket.setCustom3("owcustom3");
    oldWorkbasket.setOrgLevel4("oworgLevel4");
    oldWorkbasket.setCustom4("owcustom4");

    WorkbasketImpl newWorkbasket = new WorkbasketImpl();
    newWorkbasket.setId("newWbId");
    newWorkbasket.setKey("newWbKey");
    newWorkbasket.setDomain("oldWbDomain");
    newWorkbasket.setName("newWb");
    newWorkbasket.setCreated(created.minus(10, ChronoUnit.SECONDS));
    newWorkbasket.setModified(created.minus(2, ChronoUnit.SECONDS));
    newWorkbasket.setType(WorkbasketType.GROUP);
    newWorkbasket.setOrgLevel1("nworgLevel1");
    newWorkbasket.setCustom1("nwcustom1");
    newWorkbasket.setOrgLevel2("nworgLevel2");
    newWorkbasket.setCustom2("nwcustom2");
    newWorkbasket.setOrgLevel3("nworgLevel3");
    newWorkbasket.setCustom3("nwcustom3");
    newWorkbasket.setOrgLevel4("nworgLevel4");
    newWorkbasket.setCustom4("nwcustom4");

    JSONArray changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    oldWorkbasket, newWorkbasket))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(13);
  }

  @Test
  void should_DetermineDifferences_When_ComparingEmptyWorkbasketWithNonEmptyWorkbasket() {

    Instant created = Instant.now();

    WorkbasketImpl oldWorkbasket = new WorkbasketImpl();

    WorkbasketImpl newWorkbasket = new WorkbasketImpl();
    newWorkbasket.setId("newWbId");
    newWorkbasket.setKey("newWbKey");
    newWorkbasket.setDomain("newWbDomain");
    newWorkbasket.setName("newWb");
    newWorkbasket.setCreated(created.minus(10, ChronoUnit.SECONDS));
    newWorkbasket.setModified(created.minus(2, ChronoUnit.SECONDS));
    newWorkbasket.setType(WorkbasketType.PERSONAL);
    newWorkbasket.setOrgLevel1("nworgLevel1");
    newWorkbasket.setCustom1("nwcustom1");
    newWorkbasket.setOrgLevel2("nworgLevel2");
    newWorkbasket.setCustom2("nwcustom2");
    newWorkbasket.setOrgLevel3("nworgLevel3");
    newWorkbasket.setCustom3("nwcustom3");
    newWorkbasket.setOrgLevel4("nworgLevel4");
    newWorkbasket.setCustom4("nwcustom4");

    JSONArray changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    oldWorkbasket, newWorkbasket))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(15);

    changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    newWorkbasket, oldWorkbasket))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(15);
  }

  @Test
  void should_DetermineDifferences_When_ComparingWorkbasketAccessItems() {

    WorkbasketAccessItemImpl oldWorkbasketAccessItem = new WorkbasketAccessItemImpl();
    oldWorkbasketAccessItem.setId("oldId");
    oldWorkbasketAccessItem.setAccessId("oldAccessId");
    oldWorkbasketAccessItem.setWorkbasketId("oldWorkbasketId");

    WorkbasketAccessItemImpl newWorkbasketAccessItem = new WorkbasketAccessItemImpl();
    newWorkbasketAccessItem.setId("newId");
    newWorkbasketAccessItem.setAccessId("oldAccessId");
    newWorkbasketAccessItem.setWorkbasketId("newWorkbasketId");

    JSONArray changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    oldWorkbasketAccessItem, newWorkbasketAccessItem))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(2);
  }

  @Test
  void should_DetermineDifferences_When_ComparingEmptyWorkbasketAccessItemWithNonEmptyWai() {

    WorkbasketAccessItemImpl oldWorkbasketAccessItem = new WorkbasketAccessItemImpl();
    oldWorkbasketAccessItem.setId("oldId");
    oldWorkbasketAccessItem.setAccessId("oldAccessId");
    oldWorkbasketAccessItem.setWorkbasketId("oldWorkbasketId");

    WorkbasketAccessItemImpl newWorkbasketAccessItem = new WorkbasketAccessItemImpl();

    JSONArray changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    oldWorkbasketAccessItem, newWorkbasketAccessItem))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(3);

    changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    newWorkbasketAccessItem, oldWorkbasketAccessItem))
            .getJSONArray("changes");

    assertThat(changedAttributes).hasSize(3);
  }

  @Test
  void should_DetermineDifferences_When_ComparingLists() {

    List<WorkbasketAccessItemImpl> accessitems1 = new ArrayList<>();

    WorkbasketAccessItemImpl workbasketAccessItem = new WorkbasketAccessItemImpl();
    accessitems1.add(workbasketAccessItem);

    WorkbasketAccessItemImpl workbasketAccessItem2 = new WorkbasketAccessItemImpl();
    accessitems1.add(workbasketAccessItem2);

    WorkbasketAccessItemImpl workbasketAccessItem3 = new WorkbasketAccessItemImpl();
    accessitems1.add(workbasketAccessItem3);

    List<WorkbasketAccessItemImpl> accessitems2 = new ArrayList<>();

    WorkbasketAccessItemImpl workbasketAccessItem4 = new WorkbasketAccessItemImpl();
    workbasketAccessItem4.setId("WAI:1000000000000000000000000000000001");
    accessitems2.add(workbasketAccessItem4);

    WorkbasketAccessItemImpl workbasketAccessItem5 = new WorkbasketAccessItemImpl();
    workbasketAccessItem5.setId("WAI:1000000000000000000000000000000002");
    accessitems2.add(workbasketAccessItem5);

    JSONObject changedAttributes =
        new JSONObject(
                ObjectAttributeChangeDetector.determineChangesInAttributes(
                    accessitems1, accessitems2))
            .getJSONObject("changes");

    assertThat(changedAttributes.getJSONArray("newValue")).hasSize(2);
    assertThat(changedAttributes.getJSONArray("oldValue")).hasSize(3);
  }
}
