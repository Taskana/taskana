package acceptance.query;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.simplehistory.impl.task.TaskHistoryQuery;
import pro.taskana.simplehistory.impl.task.TaskHistoryQueryColumnName;
import pro.taskana.spi.history.api.events.task.TaskHistoryCustomField;
import pro.taskana.spi.history.api.events.task.TaskHistoryEvent;
import pro.taskana.spi.history.api.events.task.TaskHistoryEventType;

/** Test for Task History queries. */
class QueryTaskHistoryAccTest extends AbstractAccTest {

  public QueryTaskHistoryAccTest() {
    super();
  }

  @Test
  void should_ConfirmEquality_When_UsingListValuesAscendingAndDescending() {
    List<String> defaultList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CREATED, null);
    List<String> ascendingList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CREATED, SortDirection.ASCENDING);

    assertThat(ascendingList).hasSize(13).isEqualTo(defaultList);

    List<String> descendingList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CREATED, SortDirection.DESCENDING);
    Collections.reverse(ascendingList);

    assertThat(ascendingList).isEqualTo(descendingList);
  }

  @Test
  void should_ReturnHistoryEvents_For_ComplexQuery() {
    TaskHistoryQuery query =
        getHistoryService()
            .createTaskHistoryQuery()
            .businessProcessIdLike("just some string", "BPI:%")
            .domainLike("%A")
            .orderByCreated(SortDirection.DESCENDING);

    List<TaskHistoryEvent> results = query.list();
    assertThat(results).extracting(TaskHistoryEvent::getUserId).containsOnly("admin", "peter");
    results = query.orderByUserId(SortDirection.DESCENDING).list();
    assertThat(results).extracting(TaskHistoryEvent::getUserId).containsOnly("admin", "peter");
    assertThat(query.domainLike().count()).isEqualTo(13);
  }

  @Test
  void should_ConfirmQueryListOffset_When_ProvidingOffsetAndLimit() {
    List<TaskHistoryEvent> offsetAndLimitResult =
        getHistoryService().createTaskHistoryQuery().list(1, 2);
    List<TaskHistoryEvent> regularResult = getHistoryService().createTaskHistoryQuery().list();

    assertThat(offsetAndLimitResult).hasSize(2);
    assertThat(offsetAndLimitResult.get(0).getUserId())
        .isNotEqualTo(regularResult.get(0).getUserId());
    assertThat(offsetAndLimitResult.get(0).getUserId()).isEqualTo(regularResult.get(1).getUserId());
  }

  @Test
  void should_ReturnEmptyList_When_ProvidingWrongContraints() {
    List<TaskHistoryEvent> result = getHistoryService().createTaskHistoryQuery().list(1, 1000);
    assertThat(result).hasSize(12);

    result = getHistoryService().createTaskHistoryQuery().list(100, 1000);
    assertThat(result).isEmpty();
  }

  @Test
  void should_ReturnSingleHistoryEvent_When_UsingSingleMethod() {
    TaskHistoryEvent single =
        getHistoryService()
            .createTaskHistoryQuery()
            .userIdIn("peter")
            .taskIdIn("TKI:000000000000000000000000000000000036")
            .single();

    assertThat(single.getEventType()).isEqualTo(TaskHistoryEventType.CREATED.getName());

    single =
        getHistoryService()
            .createTaskHistoryQuery()
            .eventTypeIn(TaskHistoryEventType.CREATED.getName(), "xy")
            .idIn("THI:000000000000000000000000000000000003")
            .single();
    assertThat(single.getUserId()).isEqualTo("peter");
  }

  @Test
  void should_ThrowException_When_SingleMethodRetrievesMoreThanOneEventFromDatabase() {

    TaskHistoryQuery query = getHistoryService().createTaskHistoryQuery().userIdIn("peter");

    assertThatThrownBy(() -> query.single()).isInstanceOf(TooManyResultsException.class);
  }

  @Test
  void should_ReturnCountOfEvents_When_UsingCountMethod() {
    long count = getHistoryService().createTaskHistoryQuery().userIdIn("peter").count();
    assertThat(count).isEqualTo(6);

    count = getHistoryService().createTaskHistoryQuery().count();
    assertThat(count).isEqualTo(13);

    count =
        getHistoryService().createTaskHistoryQuery().userIdIn("klaus", "arnold", "benni").count();
    assertThat(count).isZero();
  }

  @Test
  void should_SortQueryByIdAsc_When_Requested() {
    List<TaskHistoryEvent> events =
        getHistoryService()
            .createTaskHistoryQuery()
            .orderByTaskHistoryEventId(SortDirection.ASCENDING)
            .list();

    assertThat(events)
        .extracting(TaskHistoryEvent::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER);
  }

  @Test
  void should_SortQueryByIdDesc_When_Requested() {
    List<TaskHistoryEvent> events =
        getHistoryService()
            .createTaskHistoryQuery()
            .orderByTaskHistoryEventId(SortDirection.DESCENDING)
            .list();

    assertThat(events)
        .extracting(TaskHistoryEvent::getId)
        .isSortedAccordingTo(CASE_INSENSITIVE_ORDER.reversed());
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentInAttributes() {
    List<TaskHistoryEvent> returnValues =
        getHistoryService().createTaskHistoryQuery().businessProcessIdIn("BPI:01", "BPI:02").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createTaskHistoryQuery().parentBusinessProcessIdIn("BPI:01").list();
    assertThat(returnValues).hasSize(6);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .taskIdIn("TKI:000000000000000000000000000000000000")
            .list();
    assertThat(returnValues).hasSize(2);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .eventTypeIn(TaskHistoryEventType.CREATED.getName())
            .list();
    assertThat(returnValues).hasSize(12);

    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    returnValues = getHistoryService().createTaskHistoryQuery().createdWithin(timeInterval).list();
    assertThat(returnValues).isEmpty();

    returnValues = getHistoryService().createTaskHistoryQuery().userIdIn("admin").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createTaskHistoryQuery().domainIn("DOMAIN_A").list();
    assertThat(returnValues).hasSize(12);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .workbasketKeyIn("WBI:100000000000000000000000000000000001")
            .list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createTaskHistoryQuery().porCompanyIn("00").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createTaskHistoryQuery().porSystemIn("PASystem").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createTaskHistoryQuery().porInstanceIn("22").list();
    assertThat(returnValues).hasSize(6);

    returnValues = getHistoryService().createTaskHistoryQuery().porTypeIn("VN").list();
    assertThat(returnValues).isEmpty();

    returnValues = getHistoryService().createTaskHistoryQuery().porValueIn("11223344").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createTaskHistoryQuery().taskClassificationKeyIn("L140101").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createTaskHistoryQuery().taskClassificationCategoryIn("TASK").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .attachmentClassificationKeyIn("DOCTYPE_DEFAULT")
            .list();
    assertThat(returnValues).hasSize(6);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .customAttributeIn(TaskHistoryCustomField.CUSTOM_1, "custom1")
            .list();
    assertThat(returnValues).hasSize(13);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .customAttributeIn(TaskHistoryCustomField.CUSTOM_2, "custom2")
            .list();
    assertThat(returnValues).hasSize(1);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .customAttributeIn(TaskHistoryCustomField.CUSTOM_3, "custom3")
            .list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .customAttributeIn(TaskHistoryCustomField.CUSTOM_4, "custom4")
            .list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createTaskHistoryQuery().oldValueIn("old_val").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createTaskHistoryQuery().newValueIn("new_val").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createTaskHistoryQuery().oldValueLike("old%").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createTaskHistoryQuery().newValueLike("new_%").list();
    assertThat(returnValues).hasSize(7);
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentLikeAttributes() {
    List<TaskHistoryEvent> returnValues =
        getHistoryService().createTaskHistoryQuery().businessProcessIdLike("BPI:0%").list();
    assertThat(returnValues).hasSize(13);

    returnValues =
        getHistoryService()
            .createTaskHistoryQuery()
            .parentBusinessProcessIdLike("BPI:01", " %")
            .list();
    assertThat(returnValues).hasSize(6);

    returnValues =
        getHistoryService().createTaskHistoryQuery().taskIdLike("TKI:000000000000000%").list();
    assertThat(returnValues).hasSize(13);

    returnValues = getHistoryService().createTaskHistoryQuery().oldValueLike("old%").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createTaskHistoryQuery().newValueLike("new_%").list();
    assertThat(returnValues).hasSize(7);
  }

  @Test
  void should_ReturnHistoryEvents_When_ProvidingListValues() {
    List<String> returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.ID, null);
    assertThat(returnedList).hasSize(13);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.BUSINESS_PROCESS_ID, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.PARENT_BUSINESS_PROCESS_ID, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.TASK_ID, null);
    assertThat(returnedList).hasSize(7);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.EVENT_TYPE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CREATED, null);
    assertThat(returnedList).hasSize(13);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.USER_ID, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.DOMAIN, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.WORKBASKET_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.POR_COMPANY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.POR_SYSTEM, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.POR_INSTANCE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.POR_TYPE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.POR_VALUE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.TASK_CLASSIFICATION_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.TASK_CLASSIFICATION_CATEGORY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.ATTACHMENT_CLASSIFICATION_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.OLD_VALUE, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.NEW_VALUE, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CUSTOM_1, null);
    assertThat(returnedList).hasSize(1);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CUSTOM_2, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CUSTOM_3, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createTaskHistoryQuery()
            .listValues(TaskHistoryQueryColumnName.CUSTOM_4, null);
    assertThat(returnedList).hasSize(2);
  }
}
