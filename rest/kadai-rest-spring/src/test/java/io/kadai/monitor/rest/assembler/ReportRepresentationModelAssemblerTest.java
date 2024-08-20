package io.kadai.monitor.rest.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import io.kadai.monitor.api.reports.ClassificationReport;
import io.kadai.monitor.api.reports.WorkbasketReport;
import io.kadai.monitor.api.reports.header.TimeIntervalColumnHeader;
import io.kadai.monitor.api.reports.item.DetailedMonitorQueryItem;
import io.kadai.monitor.api.reports.item.MonitorQueryItem;
import io.kadai.monitor.rest.models.ReportRepresentationModel;
import io.kadai.rest.test.KadaiSpringBootTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/** Test for {@link ReportRepresentationModelAssembler}. */
@KadaiSpringBootTest
class ReportRepresentationModelAssemblerTest {

  private final ReportRepresentationModelAssembler reportRepresentationModelAssembler;
  private int daysDiff;
  private LocalDateTime now;
  private List<TimeIntervalColumnHeader> headers;

  @Autowired
  ReportRepresentationModelAssemblerTest(
      ReportRepresentationModelAssembler reportRepresentationModelAssembler) {
    this.reportRepresentationModelAssembler = reportRepresentationModelAssembler;
  }

  @BeforeEach
  void before() {
    now = LocalDate.parse("2019-01-02").atStartOfDay();
    daysDiff = (int) LocalDateTime.now().until(now, ChronoUnit.DAYS);
    headers =
        IntStream.range(daysDiff - 5, daysDiff)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());
  }

  @Test
  void testEmptyReport() {
    // given
    WorkbasketReport report = new WorkbasketReport(headers);
    // when
    ReportRepresentationModel resource =
        reportRepresentationModelAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
    // then

    // meta
    ReportRepresentationModel.MetaInformation meta = resource.getMeta();
    assertThat(meta.getName()).isEqualTo("WorkbasketReport");
    assertThat(meta.getDate()).isEqualTo("2019-01-02T00:00:00Z");
    assertThat(meta.getRowDesc()).isEqualTo(new String[] {"WORKBASKET"});
    assertThat(meta.getHeader())
        .isEqualTo(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray());
    assertThat(meta.getSumRowDesc()).isEqualTo("Total");

    // rows
    assertThat(resource.getRows()).isEmpty();

    // sumRow
    assertThat(resource.getSumRow()).hasSize(1);
    ReportRepresentationModel.RowRepresentationModel sumRow = resource.getSumRow().get(0);
    assertThat(sumRow.getDesc()).isEqualTo(new String[] {"Total"});
    assertThat(sumRow.isDisplay()).isTrue();
    assertThat(sumRow.getDepth()).isZero();
    assertThat(sumRow.getTotal()).isZero();
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 0});
  }

  @Test
  void testOneSingleRow() {
    // given
    final ClassificationReport report = new ClassificationReport(headers);
    MonitorQueryItem item = new MonitorQueryItem();
    item.setAgeInDays(daysDiff - 1);
    item.setNumberOfTasks(2);
    item.setKey("key");
    report.addItem(item);
    // when
    ReportRepresentationModel resource =
        reportRepresentationModelAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
    // then

    // meta
    ReportRepresentationModel.MetaInformation meta = resource.getMeta();
    assertThat(meta.getName()).isEqualTo("ClassificationReport");
    assertThat(meta.getDate()).isEqualTo("2019-01-02T00:00:00Z");
    assertThat(meta.getRowDesc()).isEqualTo(new String[] {"CLASSIFICATION"});
    assertThat(meta.getHeader())
        .isEqualTo(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray());
    assertThat(meta.getSumRowDesc()).isEqualTo("Total");

    // rows
    List<ReportRepresentationModel.RowRepresentationModel> rows = resource.getRows();
    assertThat(rows.size()).isEqualTo(1);
    ReportRepresentationModel.RowRepresentationModel row = rows.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key"});
    assertThat(row.getDepth()).isZero();
    assertThat(row.getTotal()).isEqualTo(2);

    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    // sumRow
    assertThat(resource.getSumRow().size()).isEqualTo(1);
    ReportRepresentationModel.RowRepresentationModel sumRow = resource.getSumRow().get(0);
    assertThat(sumRow.getDesc()).isEqualTo(new String[] {"Total"});
    assertThat(sumRow.isDisplay()).isTrue();
    assertThat(sumRow.getDepth()).isZero();
    assertThat(sumRow.getTotal()).isEqualTo(2);
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});
  }

  @Test
  void testMultipleSingleRows() {
    // given
    final ClassificationReport report = new ClassificationReport(headers);
    MonitorQueryItem item = new MonitorQueryItem();
    item.setAgeInDays(daysDiff - 1);
    item.setNumberOfTasks(2);
    item.setKey("key");
    report.addItem(item);
    item.setKey("key2");
    report.addItem(item);
    // when
    ReportRepresentationModel resource =
        reportRepresentationModelAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
    // then

    // meta
    ReportRepresentationModel.MetaInformation meta = resource.getMeta();
    assertThat(meta.getName()).isEqualTo("ClassificationReport");
    assertThat(meta.getDate()).isEqualTo("2019-01-02T00:00:00Z");
    assertThat(meta.getRowDesc()).isEqualTo(new String[] {"CLASSIFICATION"});
    assertThat(meta.getHeader())
        .isEqualTo(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray());
    assertThat(meta.getSumRowDesc()).isEqualTo("Total");

    // rows
    List<ReportRepresentationModel.RowRepresentationModel> rows = resource.getRows();
    assertThat(rows.size()).isEqualTo(2);

    ReportRepresentationModel.RowRepresentationModel row = rows.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key"});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = rows.get(1);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key2"});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    // sumRow
    assertThat(resource.getSumRow()).hasSize(1);
    ReportRepresentationModel.RowRepresentationModel sumRow = resource.getSumRow().get(0);
    assertThat(sumRow.getDesc()).isEqualTo(new String[] {"Total"});
    assertThat(sumRow.getDepth()).isZero();
    assertThat(sumRow.isDisplay()).isTrue();
    assertThat(sumRow.getTotal()).isEqualTo(4);
    assertThat(sumRow.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 4});
  }

  @Test
  void testOneFoldableRow() {
    // given
    final ClassificationReport.DetailedClassificationReport report =
        new ClassificationReport.DetailedClassificationReport(headers);
    DetailedMonitorQueryItem item = new DetailedMonitorQueryItem();
    item.setAgeInDays(daysDiff - 1);
    item.setNumberOfTasks(2);
    item.setKey("key");
    item.setAttachmentKey("attachment");
    report.addItem(item);
    item.setAttachmentKey(null);
    report.addItem(item);
    // when
    ReportRepresentationModel resource =
        reportRepresentationModelAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
    // then

    // meta
    ReportRepresentationModel.MetaInformation meta = resource.getMeta();
    assertThat(meta.getName()).isEqualTo("DetailedClassificationReport");
    assertThat(meta.getDate()).isEqualTo("2019-01-02T00:00:00Z");
    assertThat(meta.getRowDesc()).isEqualTo(new String[] {"TASK CLASSIFICATION", "ATTACHMENT"});
    assertThat(meta.getHeader())
        .isEqualTo(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray());
    assertThat(meta.getSumRowDesc()).isEqualTo("Total");

    // rows
    List<ReportRepresentationModel.RowRepresentationModel> rows = resource.getRows();
    assertThat(rows).hasSize(1 + 2);

    ReportRepresentationModel.RowRepresentationModel row = rows.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", null});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(4);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 4});

    row = rows.get(1);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", "attachment"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = rows.get(2);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", "N/A"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    // sumRow
    List<ReportRepresentationModel.RowRepresentationModel> sumRow = resource.getSumRow();
    assertThat(sumRow).hasSize(1 + 2);

    row = sumRow.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", null});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(4);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 4});

    row = sumRow.get(1);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", "attachment"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = sumRow.get(2);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", "N/A"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});
  }

  @Test
  void testMultipleFoldableRows() {
    // given
    final ClassificationReport.DetailedClassificationReport report =
        new ClassificationReport.DetailedClassificationReport(headers);
    DetailedMonitorQueryItem item = new DetailedMonitorQueryItem();
    item.setAgeInDays(daysDiff - 1);
    item.setNumberOfTasks(2);
    item.setKey("key");
    item.setAttachmentKey("attachment");
    report.addItem(item);
    item.setAttachmentKey(null);
    report.addItem(item);
    item.setKey("key2");
    report.addItem(item);
    // when
    ReportRepresentationModel resource =
        reportRepresentationModelAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
    // then

    // meta
    ReportRepresentationModel.MetaInformation meta = resource.getMeta();
    assertThat(meta.getName()).isEqualTo("DetailedClassificationReport");
    assertThat(meta.getDate()).isEqualTo("2019-01-02T00:00:00Z");
    assertThat(meta.getRowDesc()).isEqualTo(new String[] {"TASK CLASSIFICATION", "ATTACHMENT"});
    assertThat(meta.getHeader())
        .isEqualTo(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray());
    assertThat(meta.getSumRowDesc()).isEqualTo("Total");

    // rows
    List<ReportRepresentationModel.RowRepresentationModel> rows = resource.getRows();
    assertThat(rows).hasSize((1 + 2) + (1 + 1));

    ReportRepresentationModel.RowRepresentationModel row = rows.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", null});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(4);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 4});

    row = rows.get(1);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", "attachment"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = rows.get(2);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key", "N/A"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = rows.get(3);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key2", null});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = rows.get(4);
    assertThat(row.getDesc()).isEqualTo(new String[] {"key2", "N/A"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    // sumRow

    List<ReportRepresentationModel.RowRepresentationModel> sumRow = resource.getSumRow();
    assertThat(sumRow).hasSize(1 + 2);

    row = sumRow.get(0);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", null});
    assertThat(row.getDepth()).isZero();
    assertThat(row.isDisplay()).isTrue();
    assertThat(row.getTotal()).isEqualTo(6);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 6});

    row = sumRow.get(1);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", "attachment"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(2);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 2});

    row = sumRow.get(2);
    assertThat(row.getDesc()).isEqualTo(new String[] {"Total", "N/A"});
    assertThat(row.getDepth()).isEqualTo(1);
    assertThat(row.isDisplay()).isFalse();
    assertThat(row.getTotal()).isEqualTo(4);
    assertThat(row.getCells()).isEqualTo(new int[] {0, 0, 0, 0, 4});
  }
}
