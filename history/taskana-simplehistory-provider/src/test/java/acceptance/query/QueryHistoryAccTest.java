package acceptance.query;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import acceptance.AbstractAccTest;
import java.time.Instant;
import java.util.List;
import org.junit.Test;

import pro.taskana.BaseQuery.SortDirection;
import pro.taskana.TimeInterval;
import pro.taskana.simplehistory.impl.HistoryEventImpl;
import pro.taskana.simplehistory.query.HistoryQuery;
import pro.taskana.simplehistory.query.HistoryQueryColumnName;

/** Test for History queries. */
public class QueryHistoryAccTest extends AbstractAccTest {

  public QueryHistoryAccTest() {
    super();
  }

  @Test
  public void testListValuesAscendingAndDescending() {
    List<String> defaultList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.COMMENT, null);
    List<String> ascendingList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.COMMENT, SortDirection.ASCENDING);
    List<String> descendingList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.COMMENT, SortDirection.DESCENDING);

    assertEquals(3, ascendingList.size());
    assertArrayEquals(defaultList.toArray(), ascendingList.toArray());
    assertEquals(ascendingList.get(2), descendingList.get(0));
  }

  @Test
  public void testComplexQuery() {
    HistoryQuery query =
        getHistoryService()
            .createHistoryQuery()
            .businessProcessIdLike("just some string", "BPI:%")
            .domainLike("%A")
            .orderByCreated(SortDirection.DESCENDING);

    List<HistoryEventImpl> results = query.list();
    assertEquals(2, results.size());
    assertEquals("admin", results.get(0).getUserId());
    assertEquals("peter", results.get(1).getUserId());

    results = query.orderByUserId(SortDirection.DESCENDING).list();
    assertEquals(2, results.size());
    assertEquals("admin", results.get(0).getUserId());
    assertEquals("peter", results.get(1).getUserId());
    assertEquals(3, query.domainLike().count());
  }

  @Test
  public void testQueryListOffset() {
    List<HistoryEventImpl> result = getHistoryService().createHistoryQuery().list(1, 2);
    List<HistoryEventImpl> wrongList = getHistoryService().createHistoryQuery().list();

    assertEquals(2, result.size());

    assertNotEquals(wrongList.get(0).getUserId(), result.get(0).getUserId());
    assertEquals(wrongList.get(1).getUserId(), result.get(0).getUserId());
  }

  @Test
  public void testCorrectResultWithWrongConstraints() {
    List<HistoryEventImpl> result = getHistoryService().createHistoryQuery().list(1, 1000);
    assertEquals(2, result.size());
    assertEquals("created by Peter", result.get(0).getComment());

    result = getHistoryService().createHistoryQuery().list(100, 1000);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testSingle() {
    HistoryEventImpl single = getHistoryService().createHistoryQuery().userIdIn("peter").single();
    assertEquals("CREATE", single.getEventType());

    single = getHistoryService().createHistoryQuery().eventTypeIn("CREATE", "xy").single();
    assertEquals("admin", single.getUserId());
  }

  @Test
  public void testCount() {
    long count = getHistoryService().createHistoryQuery().userIdIn("peter").count();
    assertEquals(1, count);

    count = getHistoryService().createHistoryQuery().count();
    assertEquals(3, count);

    count = getHistoryService().createHistoryQuery().userIdIn("klaus", "arnold", "benni").count();
    assertEquals(0, count);
  }

  @Test
  public void testQueryAttributesIn() {
    List<HistoryEventImpl> returnValues =
        getHistoryService().createHistoryQuery().businessProcessIdIn("BPI:01", "BPI:02").list();
    assertEquals(2, returnValues.size());

    returnValues =
        getHistoryService().createHistoryQuery().parentBusinessProcessIdIn("BPI:01").list();
    assertEquals(1, returnValues.size());

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .taskIdIn("TKI:000000000000000000000000000000000000")
            .list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().eventTypeIn("CREATE").list();
    assertEquals(3, returnValues.size());

    TimeInterval timeInterval = new TimeInterval(Instant.now().minusSeconds(10), Instant.now());
    returnValues = getHistoryService().createHistoryQuery().createdWithin(timeInterval).list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().userIdIn("admin").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().domainIn("DOMAIN_A").list();
    assertEquals(2, returnValues.size());

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .workbasketKeyIn("WBI:100000000000000000000000000000000001")
            .list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().porCompanyIn("00").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().porSystemIn("PASystem").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().porInstanceIn("22").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().porTypeIn("VN").list();
    assertEquals(0, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().porValueIn("11223344").list();
    assertEquals(2, returnValues.size());

    returnValues =
        getHistoryService().createHistoryQuery().taskClassificationKeyIn("L140101").list();
    assertEquals(2, returnValues.size());

    returnValues =
        getHistoryService().createHistoryQuery().taskClassificationCategoryIn("TASK").list();
    assertEquals(2, returnValues.size());

    returnValues =
        getHistoryService()
            .createHistoryQuery()
            .attachmentClassificationKeyIn("DOCTYPE_DEFAULT")
            .list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().custom1In("custom1").list();
    assertEquals(3, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().custom2In("custom2").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().custom3In("custom3").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().custom4In("custom4").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().commentIn("created a bug").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().oldValueIn("old_val").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newValueIn("new_val").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().oldDataIn("123").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newDataIn("456").list();
    assertEquals(3, returnValues.size());
    returnValues = getHistoryService().createHistoryQuery().oldValueLike("old%").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newValueLike("new_%").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().oldDataLike("%23%").list();
    assertEquals(3, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newDataLike("456%").list();
    assertEquals(3, returnValues.size());
  }

  @Test
  public void testSomeLikeMethods() {
    List<HistoryEventImpl> returnValues =
        getHistoryService().createHistoryQuery().businessProcessIdLike("BPI:0%").list();
    assertEquals(3, returnValues.size());

    returnValues =
        getHistoryService().createHistoryQuery().parentBusinessProcessIdLike("BPI:01", " %").list();
    assertEquals(1, returnValues.size());

    returnValues =
        getHistoryService().createHistoryQuery().taskIdLike("TKI:000000000000000%").list();
    assertEquals(3, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().oldValueLike("old%").list();
    assertEquals(1, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newValueLike("new_%").list();
    assertEquals(2, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().oldDataLike("%23%").list();
    assertEquals(3, returnValues.size());

    returnValues = getHistoryService().createHistoryQuery().newDataLike("456%").list();
    assertEquals(3, returnValues.size());
  }

  @Test
  public void testListValues() {
    List<String> returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.ID, null);
    assertEquals(3, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.BUSINESS_PROCESS_ID, null);
    assertEquals(3, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.PARENT_BUSINESS_PROCESS_ID, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.TASK_ID, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.EVENT_TYPE, null);
    assertEquals(1, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CREATED, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.USER_ID, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.DOMAIN, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.WORKBASKET_KEY, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_COMPANY, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_SYSTEM, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.POR_INSTANCE, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.POR_TYPE, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.POR_VALUE, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.TASK_CLASSIFICATION_KEY, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.TASK_CLASSIFICATION_CATEGORY, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService()
            .createHistoryQuery()
            .listValues(HistoryQueryColumnName.ATTACHMENT_CLASSIFICATION_KEY, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.COMMENT, null);
    assertEquals(3, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.OLD_VALUE, null);
    assertEquals(3, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.NEW_VALUE, null);
    assertEquals(3, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_1, null);
    assertEquals(1, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_2, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_3, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.CUSTOM_4, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.OLD_DATA, null);
    assertEquals(2, returnedList.size());

    returnedList =
        getHistoryService().createHistoryQuery().listValues(HistoryQueryColumnName.NEW_DATA, null);
    assertEquals(1, returnedList.size());
  }
}
