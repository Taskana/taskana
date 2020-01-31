package pro.taskana.report.internal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.report.internal.item.MonitorQueryItem;
import pro.taskana.report.internal.structure.QueryItemPreprocessor;
import pro.taskana.report.internal.structure.Report;
import pro.taskana.report.internal.structure.Row;

/** Tests for {@link Report}. */
class ReportTest {

  private static final List<TimeIntervalColumnHeader> HEADERS =
      IntStream.range(0, 4).mapToObj(TimeIntervalColumnHeader::new).collect(Collectors.toList());
  private Report<MonitorQueryItem, TimeIntervalColumnHeader> report;
  private MonitorQueryItem item;

  @BeforeEach
  void before() {
    this.report =
        new MonitorQueryItemTimeIntervalColumnHeaderReport(HEADERS, new String[] {"rowDesc"});

    item = new MonitorQueryItem();
    item.setKey("key");
    item.setAgeInDays(0);
    item.setNumberOfTasks(3);
  }

  @Test
  void testEmptyReport() {
    // then
    assertEquals(0, report.getRows().size());
    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {0, 0, 0, 0}, sumRow.getCells());
    assertEquals(0, sumRow.getTotalValue());
  }

  @Test
  void testInsertSingleItem() {
    // when
    report.addItem(item);

    // then
    assertEquals(1, report.getRows().size());
    Row<MonitorQueryItem> row = report.getRow("key");
    assertArrayEquals(new int[] {item.getValue(), 0, 0, 0}, row.getCells());
    assertEquals(item.getValue(), row.getTotalValue());
  }

  @Test
  void testInsertSameItemMultipleTimes() {
    // when
    report.addItem(item);
    report.addItem(item);

    // then
    assertEquals(1, report.getRows().size());
    Row<MonitorQueryItem> row = report.getRow("key");
    assertArrayEquals(new int[] {2 * item.getValue(), 0, 0, 0}, row.getCells());
    assertEquals(2 * item.getValue(), row.getTotalValue());
  }

  @Test
  void testInsertSameItemMultipleTimes2() {
    // given
    MonitorQueryItem item = new MonitorQueryItem();
    item.setKey("key");
    item.setAgeInDays(0);
    item.setNumberOfTasks(3);

    // when
    report.addItems(Arrays.asList(item, item));

    // then
    assertEquals(1, report.getRows().size());
    Row<MonitorQueryItem> row = report.getRow("key");
    assertArrayEquals(new int[] {2 * item.getValue(), 0, 0, 0}, row.getCells());
    assertEquals(2 * item.getValue(), row.getTotalValue());
  }

  @Test
  void testInsertSameItemMultipleTimesWithPreProcessor() {
    // given
    int overrideValue = 5;
    QueryItemPreprocessor<MonitorQueryItem> preprocessor =
        (item) -> {
          item.setNumberOfTasks(overrideValue);
          return item;
        };
    // when
    report.addItems(Arrays.asList(item, item), preprocessor);

    // then
    assertEquals(1, report.getRows().size());
    Row<MonitorQueryItem> row = report.getRow("key");
    assertArrayEquals(new int[] {2 * overrideValue, 0, 0, 0}, row.getCells());
    assertEquals(2 * overrideValue, row.getTotalValue());
  }

  @Test
  void testInsertItemWithNoColumnHeaders() {
    // given
    List<TimeIntervalColumnHeader> headerList = Collections.emptyList();
    report =
        new MonitorQueryItemTimeIntervalColumnHeaderReport(headerList, new String[] {"rowDesc"});

    // when
    report.addItem(item);

    // then
    assertEquals(1, report.getRows().size());
    assertArrayEquals(new int[0], report.getRow("key").getCells());
    assertEquals(item.getValue(), report.getRow("key").getTotalValue());
  }

  @Test
  void testInsertItemWhichIsNotInHeaderScopes() {
    // given
    item.setAgeInDays(-2);
    // when
    report.addItem(item);

    // then
    assertEquals(0, report.getRows().size());
    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {0, 0, 0, 0}, sumRow.getCells());
    assertEquals(0, sumRow.getTotalValue());
  }

  @Test
  void testInsertItemWhichIsInMultipleHeaderScopes() {
    // given
    List<TimeIntervalColumnHeader> headers = new ArrayList<>(HEADERS);
    headers.add(new TimeIntervalColumnHeader(0, 3));
    report = new MonitorQueryItemTimeIntervalColumnHeaderReport(headers, new String[] {"rowDesc"});

    item.setAgeInDays(2);

    // when
    report.addItem(item);

    // then
    assertEquals(1, report.getRows().size());

    Row<MonitorQueryItem> row = report.getRow("key");
    assertArrayEquals(new int[] {0, 0, item.getValue(), 0, item.getValue()}, row.getCells());
    assertEquals(2 * item.getValue(), row.getTotalValue());

    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {0, 0, item.getValue(), 0, item.getValue()}, sumRow.getCells());
    assertEquals(2 * item.getValue(), sumRow.getTotalValue());
  }

  @Test
  void testInsertItemWithPreProcessor() {
    // given
    int overrideValue = 5;
    QueryItemPreprocessor<MonitorQueryItem> preprocessor =
        item -> {
          item.setNumberOfTasks(overrideValue);
          return item;
        };
    item.setAgeInDays(1);

    // when
    report.addItem(item, preprocessor);

    // then
    assertEquals(1, report.getRows().size());

    Row<MonitorQueryItem> row = report.getRow(item.getKey());
    assertArrayEquals(new int[] {0, overrideValue, 0, 0}, row.getCells());
    assertEquals(overrideValue, row.getTotalValue());

    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertArrayEquals(new int[] {0, overrideValue, 0, 0}, sumRow.getCells());
    assertEquals(overrideValue, sumRow.getTotalValue());
  }

  private static class MonitorQueryItemTimeIntervalColumnHeaderReport
      extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public MonitorQueryItemTimeIntervalColumnHeaderReport(
        List<TimeIntervalColumnHeader> headerList, String[] rowDesc) {
      super(headerList, rowDesc);
    }
  }
}
