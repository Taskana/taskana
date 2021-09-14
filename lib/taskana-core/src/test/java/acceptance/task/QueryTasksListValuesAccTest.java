package acceptance.task;

import static org.assertj.core.api.Assertions.assertThat;
import static pro.taskana.common.api.BaseQuery.SortDirection.ASCENDING;
import static pro.taskana.common.api.BaseQuery.SortDirection.DESCENDING;
import static pro.taskana.task.api.TaskQueryColumnName.A_CHANNEL;
import static pro.taskana.task.api.TaskQueryColumnName.A_CLASSIFICATION_ID;
import static pro.taskana.task.api.TaskQueryColumnName.A_REF_VALUE;
import static pro.taskana.task.api.TaskQueryColumnName.CLASSIFICATION_KEY;
import static pro.taskana.task.api.TaskQueryColumnName.OWNER;
import static pro.taskana.task.api.TaskQueryColumnName.STATE;

import acceptance.AbstractAccTest;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.KeyDomain;
import pro.taskana.common.test.security.JaasExtension;
import pro.taskana.common.test.security.WithAccessId;
import pro.taskana.task.api.TaskQueryColumnName;

/** Acceptance test for listing the column values within a workbasket. */
@ExtendWith(JaasExtension.class)
class QueryTasksListValuesAccTest extends AbstractAccTest {

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnColumnValues_When_ListValuesForColumnIsInvoked() {
    SoftAssertions softly = new SoftAssertions();
    Arrays.stream(TaskQueryColumnName.values())
        .forEach(
            columnName ->
                softly
                    .assertThatCode(
                        () -> taskService.createTaskQuery().listValues(columnName, ASCENDING))
                    .describedAs("Column is not working " + columnName)
                    .doesNotThrowAnyException());
    softly.assertAll();
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnOwnerValues_When_ListValuesForOwnerIsInvoked() {
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .ownerLike("%user%")
            .orderByOwner(DESCENDING)
            .listValues(OWNER, null);
    assertThat(columnValueList).hasSize(3);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnStateValues_When_ListValuesForStateIsInvoked() {
    List<String> columnValueList = taskService.createTaskQuery().listValues(STATE, null);
    assertThat(columnValueList).hasSize(5);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAttachmentColumnValues_When_ListValuesForAttachmentColumnsIsInvoked() {
    List<String> columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueIn("val4")
            .listValues(A_CHANNEL, null);
    assertThat(columnValueList).hasSize(2);

    columnValueList =
        taskService
            .createTaskQuery()
            .attachmentReferenceValueLike("%")
            .listValues(A_REF_VALUE, null);
    assertThat(columnValueList).hasSize(6);

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByAttachmentClassificationId(DESCENDING)
            .listValues(A_CLASSIFICATION_ID, null);
    assertThat(columnValueList).hasSize(11);

    columnValueList =
        taskService
            .createTaskQuery()
            .orderByClassificationKey(DESCENDING)
            .listValues(CLASSIFICATION_KEY, null);
    assertThat(columnValueList).hasSize(7);
  }

  @WithAccessId(user = "admin")
  @Test
  void should_ReturnAllExternalIds_When_ListValuesForExternalIdsIsInvoked() {

    List<String> resultValues =
        taskService
            .createTaskQuery()
            .externalIdLike("ETI:000000000000000000000000000000%")
            .listValues(TaskQueryColumnName.EXTERNAL_ID, DESCENDING);
    assertThat(resultValues).hasSize(74);
  }

  @WithAccessId(user = "teamlead-1")
  @Test
  void should_ReturnPorTypes_When_QueryingForListOfPorTypesForWorkbasket() {
    KeyDomain keyDomain = new KeyDomain("GPK_KSC", "DOMAIN_A");
    List<String> porTypes =
        taskService
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
        taskService
            .createTaskQuery()
            .workbasketKeyDomainIn(keyDomain)
            .listValues(TaskQueryColumnName.A_CLASSIFICATION_NAME, SortDirection.ASCENDING);
    assertThat(attachmentClassificationNames)
        // PostgreSQL treats null differently while sorting
        .containsExactlyInAnyOrder(
            null, "Beratungsprotokoll", "Dynamikänderung", "Widerruf", "Zustimmungserklärung");
  }
}
