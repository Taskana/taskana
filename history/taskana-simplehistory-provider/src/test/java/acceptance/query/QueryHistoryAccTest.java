package acceptance.query;

import static org.assertj.core.api.Assertions.assertThat;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

import pro.taskana.common.api.BaseQuery.SortDirection;
import pro.taskana.common.api.TimeInterval;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.query.HistoryQuery;
import pro.taskana.simplehistory.query.HistoryQueryColumnName;
import pro.taskana.spi.history.api.events.TaskanaHistoryEvent;

/** Test for History queries. */
class QueryHistoryAccTest extends AbstractAccTest {

  public QueryHistoryAccTest() {
    super();
  }

  @Test
  void should_ConfirmEquality_When_UsingListValuesAscendingAndDescending() {
    List<String> defaultList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CREATED, null);
    List<String> ascendingList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.CREATED, SortDirection.ASCENDING);

    assertThat(ascendingList).hasSize(13);
    assertThat(ascendingList).isEqualTo(defaultList);

    List<String> descendingList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.CREATED, SortDirection.DESCENDING);
    Collections.reverse(ascendingList);

    assertThat(ascendingList).isEqualTo(descendingList);
  }

  @Test
  void should_ReturnHistoryEvents_For_ComplexQuery() {
    HistoryQuery query =
        getHistoryService()
            .createHistoryQuery()
            .businessProcessIdLike("just some string", "BPI:%")
            .domainLike("%A")
            .orderByCreated(SortDirection.DESCENDING);

    List<HistoryEventImpl> results = query.list();
    assertThat(results).extracting(TaskanaHistoryEvent::getUserId).containsOnly("admin", "peter");
    results = query.orderByUserId(SortDirection.DESCENDING).list();
    assertThat(results).extracting(TaskanaHistoryEvent::getUserId).containsOnly("admin", "peter");
    assertThat(query.domainLike().count()).isEqualTo(13);
  }

  @Test
  void should_ConfirmQueryListOffset_When_ProvidingOffsetAndLimit() {
    List<HistoryEventImpl> result = getHistoryService().createHistoryQuery().list(1, 2);
    List<HistoryEventImpl> wrongList = getHistoryService().createHistoryQuery().list();

    assertThat(result).hasSize(2);
    assertThat(result.get(0).getUserId()).isNotEqualTo(wrongList.get(0).getUserId());
    assertThat(result.get(0).getUserId()).isEqualTo(wrongList.get(1).getUserId());
  }

  @Test
  void should_ReturnEmptyList_When_ProvidingWrongContraints() {
    List<HistoryEventImpl> result = getHistoryService().createHistoryQuery().list(1, 1000);
    assertThat(result).hasSize(12);

    result = getHistoryService().createHistoryQuery().list(100, 1000);
    assertThat(result).isEmpty();
  }

  @Test
  void should_ReturnSingleHistoryEvent_When_UsingSingleMethod() {
    HistoryEventImpl single = getHistoryService().createHistoryQuery().userIdIn("peter").single();
    assertThat(single.getEventType()).isEqualTo("TASK_CREATED");

    single = getHistoryService().createHistoryQuery().eventTypeIn("TASK_CREATED", "xy").single();
    assertThat(single.getUserId()).isEqualTo("peter");
  }

  @Test
  void should_ReturnCountOfEvents_When_UsingCountMethod() {
    long count = getHistoryService().createHistoryQuery().userIdIn("peter").count();
    assertThat(count).isEqualTo(6);

    count = getHistoryService().createHistoryQuery().count();
    assertThat(count).isEqualTo(13);

    count = getHistoryService().createHistoryQuery().userIdIn("klaus", "arnold", "benni").count();
    assertThat(count).isZero();
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentInAttributes() {
    List<HistoryEventImpl> returnValues =
        getHistoryService().createHistoryQuery().businessProcessIdIn("BPI:01", "BPI:02").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createHistoryQuery().parentBusinessProcessIdIn("BPI:01").list();
    assertThat(returnValues).hasSize(6);

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .taskIdIn("TKI:000000000000000000000000000000000000")
            .list();
    assertThat(returnValues).hasSize(2);

    returnValues = getHistoryService().createHistoryQuery().eventTypeIn("TASK_CREATED").list();
    assertThat(returnValues).hasSize(12);

    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    returnValues = getHistoryService().createHistoryQuery().createdWithin(timeInterval).list();
    assertThat(returnValues).isEmpty();

    returnValues = getHistoryService().createHistoryQuery().userIdIn("admin").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createHistoryQuery().domainIn("DOMAIN_A").list();
    assertThat(returnValues).hasSize(12);

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .workbasketKeyIn("WBI:100000000000000000000000000000000001")
            .list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createHistoryQuery().porCompanyIn("00").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createHistoryQuery().porSystemIn("PASystem").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createHistoryQuery().porInstanceIn("22").list();
    assertThat(returnValues).hasSize(6);

    returnValues = getHistoryService().createHistoryQuery().porTypeIn("VN").list();
    assertThat(returnValues).isEmpty();

    returnValues = getHistoryService().createHistoryQuery().porValueIn("11223344").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createHistoryQuery().taskClassificationKeyIn("L140101").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService().createHistoryQuery().taskClassificationCategoryIn("TASK").list();
    assertThat(returnValues).hasSize(7);

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .attachmentClassificationKeyIn("DOCTYPE_DEFAULT")
            .list();
    assertThat(returnValues).hasSize(6);

    returnValues = getHistoryService().createHistoryQuery().custom1In("custom1").list();
    assertThat(returnValues).hasSize(13);

    returnValues = getHistoryService().createHistoryQuery().custom2In("custom2").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().custom3In("custom3").list();
    assertThat(returnValues).hasSize(7);

    returnValues = getHistoryService().createHistoryQuery().custom4In("custom4").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().oldValueIn("old_val").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().newValueIn("new_val").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().oldValueLike("old%").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().newValueLike("new_%").list();
    assertThat(returnValues).hasSize(7);
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentLikeAttributes() {
    List<HistoryEventImpl> returnValues =
        getHistoryService().createHistoryQuery().businessProcessIdLike("BPI:0%").list();
    assertThat(returnValues).hasSize(13);

    returnValues =
        getHistoryService().createHistoryQuery().parentBusinessProcessIdLike("BPI:01", " %").list();
    assertThat(returnValues).hasSize(6);

    returnValues =
        getHistoryService().createHistoryQuery().taskIdLike("TKI:000000000000000%").list();
    assertThat(returnValues).hasSize(13);

    returnValues = getHistoryService().createHistoryQuery().oldValueLike("old%").list();
    assertThat(returnValues).hasSize(1);

    returnValues = getHistoryService().createHistoryQuery().newValueLike("new_%").list();
    assertThat(returnValues).hasSize(7);
  }

  @Test
  void should_ReturnHistoryEvents_When_ProvidingListValues() {
    List<String> returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.ID, null);
    assertThat(returnedList).hasSize(13);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.BUSINESS_PROCESS_ID, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.PARENT_BUSINESS_PROCESS_ID, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.TASK_ID, null);
    assertThat(returnedList).hasSize(7);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.EVENT_TYPE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CREATED, null);
    assertThat(returnedList).hasSize(13);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.USER_ID, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.DOMAIN, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.WORKBASKET_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_COMPANY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_SYSTEM, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_INSTANCE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.POR_TYPE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.POR_VALUE, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.TASK_CLASSIFICATION_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.TASK_CLASSIFICATION_CATEGORY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.ATTACHMENT_CLASSIFICATION_KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.OLD_VALUE, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.NEW_VALUE, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_1, null);
    assertThat(returnedList).hasSize(1);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_2, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_3, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_4, null);
    assertThat(returnedList).hasSize(2);
  }
}
