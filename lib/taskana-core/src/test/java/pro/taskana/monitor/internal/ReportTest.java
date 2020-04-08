package pro.taskana.monitor.internal;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.api.reports.item.QueryItemPreprocessor;
import pro.taskana.monitor.api.reports.row.Row;

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
    assertThat(report.getRows()).isEmpty();
    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(0);
  }

  @Test
  void testInsertSingleItem() {
    // when
    report.addItem(item);

    // then
    assertThat(report.getRows()).hasSize(1);
    Row<MonitorQueryItem> row = report.getRow("key");
    assertThat(row.getCells()).isEqualTo(new int[] {item.getValue(), 0, 0, 0});
    assertThat(row.getTotalValue()).isEqualTo(item.getValue());
  }

  @Test
  void testInsertSameItemMultipleTimes() {
    // when
    report.addItem(item);
    report.addItem(item);

    // then
    assertThat(report.getRows()).hasSize(1);
    Row<MonitorQueryItem> row = report.getRow("key");
    assertThat(row.getCells()).isEqualTo(new int[] {2 * item.getValue(), 0, 0, 0});
    assertThat(row.getTotalValue()).isEqualTo(2 * item.getValue());
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
    assertThat(report.getRows()).hasSize(1);
    Row<MonitorQueryItem> row = report.getRow("key");
    assertThat(row.getCells()).isEqualTo(new int[] {2 * item.getValue(), 0, 0, 0});
    assertThat(row.getTotalValue()).isEqualTo(2 * item.getValue());
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
    assertThat(report.getRows()).hasSize(1);
    Row<MonitorQueryItem> row = report.getRow("key");
    assertThat(row.getCells()).isEqualTo(new int[] {2 * overrideValue, 0, 0, 0});
    assertThat(row.getTotalValue()).isEqualTo(2 * overrideValue);
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
    assertThat(report.getRows()).hasSize(1);
    assertThat(report.getRow("key").getCells()).isEqualTo(new int[0]);
    assertThat(report.getRow("key").getTotalValue()).isEqualTo(item.getValue());
  }

  @Test
  void testInsertItemWhichIsNotInHeaderScopes() {
    // given
    item.setAgeInDays(-2);
    // when
    report.addItem(item);

    // then
    assertThat(report.getRows()).isEmpty();
    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(0);
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
    assertThat(report.getRows()).hasSize(1);

    Row<MonitorQueryItem> row = report.getRow("key");
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, item.getValue(), 0, item.getValue()});
    assertThat(row.getTotalValue()).isEqualTo(2 * item.getValue());

    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, item.getValue(), 0, item.getValue()});
    assertThat(sumRow.getTotalValue()).isEqualTo(2 * item.getValue());
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
    assertThat(report.getRows()).hasSize(1);

    Row<MonitorQueryItem> row = report.getRow(item.getKey());
    assertThat(row.getCells()).isEqualTo(new int[] {0, overrideValue, 0, 0});
    assertThat(row.getTotalValue()).isEqualTo(overrideValue);

    Row<MonitorQueryItem> sumRow = report.getSumRow();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, overrideValue, 0, 0});
    assertThat(sumRow.getTotalValue()).isEqualTo(overrideValue);
  }

  private static class MonitorQueryItemTimeIntervalColumnHeaderReport
      extends Report<MonitorQueryItem, TimeIntervalColumnHeader> {

    public MonitorQueryItemTimeIntervalColumnHeaderReport(
        List<TimeIntervalColumnHeader> headerList, String[] rowDesc) {
      super(headerList, rowDesc);
    }
  }
}
