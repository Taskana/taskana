package acceptance.query;

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
import pro.taskana.simplehistory.impl.SimpleHistoryServiceImpl;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQuery;
import pro.taskana.simplehistory.impl.workbasket.WorkbasketHistoryQueryColumnName;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEvent;
import pro.taskana.spi.history.api.events.workbasket.WorkbasketHistoryEventType;
import pro.taskana.workbasket.api.WorkbasketCustomField;

/** Test for Workbasket History queries. */
class QueryWorkbasketHistoryAccTest extends AbstractAccTest {

  private final SimpleHistoryServiceImpl historyService = getHistoryService();

  public QueryWorkbasketHistoryAccTest() {
    super();
  }

  @Test
  void should_ConfirmEquality_When_UsingListValuesAscendingAndDescending() {
    List<String> defaultList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CREATED, null);
    List<String> ascendingList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CREATED, SortDirection.ASCENDING);

    assertThat(ascendingList).hasSize(10).isEqualTo(defaultList);

    List<String> descendingList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CREATED, SortDirection.DESCENDING);
    Collections.reverse(ascendingList);

    assertThat(ascendingList).isEqualTo(descendingList);
  }

  @Test
  void should_ReturnHistoryEvents_For_ComplexQuery() {

    WorkbasketHistoryQuery query =
        historyService
            .createWorkbasketHistoryQuery()
            .eventTypeIn(WorkbasketHistoryEventType.CREATED.getName())
            .domainLike("%A")
            .customAttributeIn(WorkbasketCustomField.CUSTOM_1, "otherCustom1")
            .orderByCreated(SortDirection.DESCENDING);

    List<WorkbasketHistoryEvent> results = query.list();
    assertThat(results)
        .extracting(WorkbasketHistoryEvent::getUserId)
        .containsOnly("claudia", "peter", "sven");
    results = query.orderByUserId(SortDirection.DESCENDING).list();
    assertThat(results)
        .extracting(WorkbasketHistoryEvent::getUserId)
        .containsOnly("claudia", "peter", "sven");
    assertThat(results).hasSize(3);
  }

  @Test
  void should_ConfirmQueryListOffset_When_ProvidingOffsetAndLimit() {
    List<WorkbasketHistoryEvent> offsetAndLimitResult =
        historyService.createWorkbasketHistoryQuery().list(1, 2);
    List<WorkbasketHistoryEvent> regularResult =
        historyService.createWorkbasketHistoryQuery().list();

    assertThat(offsetAndLimitResult).hasSize(2);
    assertThat(offsetAndLimitResult.get(0).getUserId())
        .isNotEqualTo(regularResult.get(0).getUserId());
    assertThat(offsetAndLimitResult.get(0).getUserId()).isEqualTo(regularResult.get(1).getUserId());
  }

  @Test
  void should_ReturnEmptyList_When_ProvidingWrongContraints() {
    List<WorkbasketHistoryEvent> result =
        historyService.createWorkbasketHistoryQuery().list(1, 1000);
    assertThat(result).hasSize(9);

    result = historyService.createWorkbasketHistoryQuery().list(100, 1000);
    assertThat(result).isEmpty();
  }

  @Test
  void should_ReturnSingleHistoryEvent_When_UsingSingleMethod() {
    WorkbasketHistoryEvent single =
        historyService
            .createWorkbasketHistoryQuery()
            .userIdIn("peter")
            .idIn("WHI:000000000000000000000000000000000000")
            .single();
    assertThat(single.getEventType()).isEqualTo(WorkbasketHistoryEventType.CREATED.getName());

    single =
        historyService
            .createWorkbasketHistoryQuery()
            .eventTypeIn(WorkbasketHistoryEventType.CREATED.getName(), "xy")
            .idIn("WHI:000000000000000000000000000000000000")
            .single();
    assertThat(single.getUserId()).isEqualTo("peter");
  }

  @Test
  void should_ThrowException_When_SingleMethodRetrievesMoreThanOneEventFromDatabase() {

    WorkbasketHistoryQuery query =
        getHistoryService().createWorkbasketHistoryQuery().userIdIn("peter");

    assertThatThrownBy(() -> query.single()).isInstanceOf(TooManyResultsException.class);
  }

  @Test
  void should_ReturnCountOfEvents_When_UsingCountMethod() {
    long count = historyService.createWorkbasketHistoryQuery().userIdIn("peter").count();
    assertThat(count).isEqualTo(6);

    count = historyService.createWorkbasketHistoryQuery().count();
    assertThat(count).isEqualTo(10);

    count =
        historyService.createWorkbasketHistoryQuery().userIdIn("hans", "jürgen", "klaus").count();
    assertThat(count).isZero();
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentInAttributes() {

    List<WorkbasketHistoryEvent> returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .workbasketIdIn("WBI:000000000000000000000000000000000903")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .eventTypeIn(WorkbasketHistoryEventType.CREATED.getName())
            .list();
    assertThat(returnValues).hasSize(6);

    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    returnValues = historyService.createWorkbasketHistoryQuery().createdWithin(timeInterval).list();
    assertThat(returnValues).isEmpty();

    returnValues = historyService.createWorkbasketHistoryQuery().userIdIn("peter").list();
    assertThat(returnValues).hasSize(6);

    returnValues = historyService.createWorkbasketHistoryQuery().domainIn("DOMAIN_A").list();
    assertThat(returnValues).hasSize(10);

    returnValues = historyService.createWorkbasketHistoryQuery().keyIn("soRt003").list();
    assertThat(returnValues).hasSize(5);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_1, "custom1")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_2, "custom2")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_3, "custom3")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .customAttributeIn(WorkbasketCustomField.CUSTOM_4, "custom4")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues = historyService.createWorkbasketHistoryQuery().orgLevel1In("orgLevel1").list();
    assertThat(returnValues).hasSize(5);

    returnValues = historyService.createWorkbasketHistoryQuery().orgLevel2In("orgLevel2").list();
    assertThat(returnValues).hasSize(5);

    returnValues = historyService.createWorkbasketHistoryQuery().orgLevel3In("orgLevel3").list();
    assertThat(returnValues).hasSize(5);

    returnValues = historyService.createWorkbasketHistoryQuery().orgLevel4In("orgLevel4").list();
    assertThat(returnValues).hasSize(5);
  }

  @Test
  void should_ReturnHistoryEvents_For_DifferentLikeAttributes() {

    List<WorkbasketHistoryEvent> returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .workbasketIdLike("WBI:000000000000000%")
            .list();
    assertThat(returnValues).hasSize(10);

    returnValues = historyService.createWorkbasketHistoryQuery().eventTypeLike("C%").list();
    assertThat(returnValues).hasSize(6);

    returnValues = historyService.createWorkbasketHistoryQuery().userIdLike("p%", "c%").list();
    assertThat(returnValues).hasSize(8);

    returnValues = historyService.createWorkbasketHistoryQuery().domainLike("%_A").list();
    assertThat(returnValues).hasSize(10);

    returnValues = historyService.createWorkbasketHistoryQuery().workbasketKeyLike("%Rt0%").list();
    assertThat(returnValues).hasSize(10);

    returnValues = historyService.createWorkbasketHistoryQuery().workbasketTypeLike("TOP%").list();
    assertThat(returnValues).hasSize(10);

    returnValues = historyService.createWorkbasketHistoryQuery().ownerLike("adm%").list();
    assertThat(returnValues).hasSize(10);

    returnValues =
        historyService
            .createWorkbasketHistoryQuery()
            .customAttributeLike(WorkbasketCustomField.CUSTOM_1, "other%")
            .list();
    assertThat(returnValues).hasSize(5);

    returnValues = historyService.createWorkbasketHistoryQuery().orgLevel1Like("org%").list();
    assertThat(returnValues).hasSize(5);
  }

  @Test
  void should_ReturnHistoryEvents_When_ProvidingListValues() {
    List<String> returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.ID, null);
    assertThat(returnedList).hasSize(10);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.WORKBASKET_ID, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.EVENT_TYPE, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CREATED, null);
    assertThat(returnedList).hasSize(10);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.USER_ID, null);
    assertThat(returnedList).hasSize(3);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.DOMAIN, null);
    assertThat(returnedList).hasSize(1);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.KEY, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.TYPE, null);
    assertThat(returnedList).hasSize(1);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.OWNER, null);
    assertThat(returnedList).hasSize(1);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CUSTOM_1, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CUSTOM_2, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CUSTOM_3, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.CUSTOM_4, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.ORGLEVEL_1, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.ORGLEVEL_2, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.ORGLEVEL_3, null);
    assertThat(returnedList).hasSize(2);

    returnedList =
        historyService
            .createWorkbasketHistoryQuery()
            .listValues(WorkbasketHistoryQueryColumnName.ORGLEVEL_4, null);
    assertThat(returnedList).hasSize(2);
  }
}
