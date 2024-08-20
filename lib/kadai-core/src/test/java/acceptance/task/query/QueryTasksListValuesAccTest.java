package acceptance.task.query;

import static io.kadai.common.api.BaseQuery.SortDirection.ASCENDING;
import static io.kadai.common.api.BaseQuery.SortDirection.DESCENDING;
import static io.kadai.task.api.TaskQueryColumnName.A_CHANNEL;
import static io.kadai.task.api.TaskQueryColumnName.A_CLASSIFICATION_ID;
import static io.kadai.task.api.TaskQueryColumnName.A_REF_VALUE;
import static io.kadai.task.api.TaskQueryColumnName.CLASSIFICATION_KEY;
import static io.kadai.task.api.TaskQueryColumnName.OWNER;
import static io.kadai.task.api.TaskQueryColumnName.STATE;
import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import io.kadai.common.api.BaseQuery.SortDirection;
import io.kadai.common.api.KeyDomain;
import io.kadai.common.test.security.JaasExtension;
import io.kadai.common.test.security.WithAccessId;
import io.kadai.task.api.TaskQueryColumnName;
import io.kadai.task.api.TaskService;
import io.kadai.task.internal.TaskServiceImpl;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

/** Acceptance test for listing the column values within a workbasket. */
@ExtendWith(JaasExtension.class)
class QueryTasksListValuesAccTest extends AbstractAccTest {

  private static final TaskServiceImpl TASK_SERVICE =
      (TaskServiceImpl) kadaiEngine.getTaskService();

  @Nested
  @TestInstance(Lifecycle.PER_CLASS)
  class ListValuesTest {

    @WithAccessId(user = "admin")
    @Test
    void testQueryTaskValuesForAttachmentClassificationName() {
      TaskService taskService = kadaiEngine.getTaskService();
      List<String> columnValueList =
          taskService
              .createTaskQuery()
              .ownerLike("%user%")
              .orderByOwner(DESCENDING)
              .listValues(TaskQueryColumnName.A_CLASSIFICATION_NAME, null);
      assertThat(columnValueList).hasSize(8);
    }

    @WithAccessId(user = "admin")
    @Test
    void testQueryTaskValuesForClassificationName() {
      TaskService taskService = kadaiEngine.getTaskService();
      List<String> columnValueList =
          taskService
              .createTaskQuery()
              .ownerLike("%user%")
              .orderByClassificationName(ASCENDING)
              .listValues(TaskQueryColumnName.CLASSIFICATION_NAME, null);
      assertThat(columnValueList).hasSize(6);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnColumnValues_When_ListValuesForColumnIsInvoked() {
      SoftAssertions softly = new SoftAssertions();
      Arrays.stream(TaskQueryColumnName.values())
          .forEach(
              columnName ->
                  softly
                      .assertThatCode(
                          () -> TASK_SERVICE.createTaskQuery().listValues(columnName, ASCENDING))
                      .describedAs("Column is not working " + columnName)
                      .doesNotThrowAnyException());
      softly.assertAll();
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnOwnerValues_When_ListValuesForOwnerIsInvoked() {
      List<String> columnValueList =
          TASK_SERVICE
              .createTaskQuery()
              .ownerLike("%user%")
              .orderByOwner(DESCENDING)
              .listValues(OWNER, null);
      assertThat(columnValueList).hasSize(3);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnStateValues_When_ListValuesForStateIsInvoked() {
      List<String> columnValueList = TASK_SERVICE.createTaskQuery().listValues(STATE, null);
      assertThat(columnValueList).hasSize(7);
    }

    @WithAccessId(user = "admin")
    @Test
    void should_ReturnAttachmentColumnValues_When_ListValuesForAttachmentColumnsIsInvoked() {
      List<String> columnValueList =
          TASK_SERVICE
              .createTaskQuery()
              .attachmentReferenceValueIn("val4")
              .listValues(A_CHANNEL, null);
      assertThat(columnValueList).hasSize(2);

      columnValueList =
          TASK_SERVICE
              .createTaskQuery()
              .attachmentReferenceValueLike("%")
              .listValues(A_REF_VALUE, null);
      assertThat(columnValueList).hasSize(6);

      columnValueList =
          TASK_SERVICE
              .createTaskQuery()
              .orderByAttachmentClassificationId(DESCENDING)
              .listValues(A_CLASSIFICATION_ID, null);
      assertThat(columnValueList).hasSize(11);

      columnValueList =
          TASK_SERVICE
              .createTaskQuery()
              .orderByClassificationKey(DESCENDING)
              .listValues(CLASSIFICATION_KEY, null);
      assertThat(columnValueList).hasSize(7);
    }

    @Disabled()
    @WithAccessId(user = "admin")
    @Test
    void should_ReturnAllExternalIds_When_ListValuesForExternalIdsIsInvoked() {

      List<String> resultValues =
          TASK_SERVICE.createTaskQuery().listValues(TaskQueryColumnName.EXTERNAL_ID, DESCENDING);
      assertThat(resultValues).hasSize(74);
    }

    @WithAccessId(user = "teamlead-1")
    @Test
    void should_ReturnPorTypes_When_QueryingForListOfPorTypesForWorkbasket() {
      KeyDomain keyDomain = new KeyDomain("GPK_KSC", "DOMAIN_A");
      List<String> porTypes =
          TASK_SERVICE
              .createTaskQuery()
              .workbasketKeyDomainIn(keyDomain)
              .primaryObjectReferenceCompanyIn("00", "11")
              .listValues(TaskQueryColumnName.POR_TYPE, SortDirection.ASCENDING);
      assertThat(porTypes).containsExactly("SDNR", "VNR");
    }

    @WithAccessId(user = "teamlead-1")
    @Test
    void should_ReturnAttachmentClassificationNames_When_QueryingForListNames() {
      KeyDomain keyDomain = new KeyDomain("GPK_KSC", "DOMAIN_A");
      List<String> attachmentClassificationNames =
          TASK_SERVICE
              .createTaskQuery()
              .workbasketKeyDomainIn(keyDomain)
              .listValues(TaskQueryColumnName.A_CLASSIFICATION_NAME, SortDirection.ASCENDING);
      assertThat(attachmentClassificationNames)
          // PostgreSQL treats null differently while sorting
          .containsExactlyInAnyOrder(
              null, "Beratungsprotokoll", "Dynamikänderung", "Widerruf", "Zustimmungserklärung");
    }
  }
}
