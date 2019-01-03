package pro.taskana.rest.resource;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DetailedMonitorQueryItem;
import pro.taskana.impl.report.item.MonitorQueryItem;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.WorkbasketReport;
import pro.taskana.rest.TestConfiguration;

/**
 * Test for {@link ReportAssembler}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@WebAppConfiguration
public class ReportResourceTest {

    @Autowired
    private ReportAssembler reportAssembler;

    private int daysDiff;
    private LocalDateTime now;
    private List<TimeIntervalColumnHeader> headers;

    @Before
    public void before() {
        now = LocalDate.parse("2019-01-02").atStartOfDay();
        daysDiff = (int) LocalDateTime.now().until(now, ChronoUnit.DAYS);
        headers = IntStream.range(daysDiff - 5, daysDiff)
            .mapToObj(TimeIntervalColumnHeader.Date::new)
            .collect(Collectors.toList());

    }

    @Test
    public void testEmptyReport() {
        // given
        WorkbasketReport report = new WorkbasketReport(headers);
        // when
        ReportResource resource = reportAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
        // then

        // meta
        ReportResource.MetaInformation meta = resource.getMeta();
        assertEquals("WorkbasketReport", meta.getName());
        assertEquals("2019-01-02T00:00:00Z", meta.getDate());
        assertEquals("WORKBASKET KEYS", meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        assertTrue(resource.getRows().isEmpty());

        // sumRow
        assertEquals(ReportResource.SingleRowResource.class, resource.getSumRow().getClass());
        assertEquals(0, resource.getSumRow().getTotal());
        Map<String, Integer> cells = resource.getSumRow().getCells();
        assertEquals(5, cells.size());
        assertEquals(0, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());
    }

    @Test
    public void testOneSingleRow() {
        // given
        ClassificationReport report = new ClassificationReport(headers);
        MonitorQueryItem item = new MonitorQueryItem();
        item.setAgeInDays(daysDiff - 1);
        item.setNumberOfTasks(2);
        item.setKey("key");
        report.addItem(item);
        // when
        ReportResource resource = reportAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
        // then

        // meta
        ReportResource.MetaInformation meta = resource.getMeta();
        assertEquals("ClassificationReport", meta.getName());
        assertEquals("2019-01-02T00:00:00Z", meta.getDate());
        assertEquals("CLASSIFICATION KEYS", meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        Map<String, ReportResource.RowResource> rows = resource.getRows();
        assertEquals(1, rows.size());
        ReportResource.RowResource row = rows.get("key");
        assertEquals(ReportResource.SingleRowResource.class, row.getClass());
        assertEquals(2, row.getTotal());
        Map<String, Integer> cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        // sumRow
        ReportResource.RowResource sumRow = resource.getSumRow();
        assertEquals(ReportResource.SingleRowResource.class, sumRow.getClass());
        assertEquals(2, sumRow.getTotal());
        cells = sumRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

    }

    @Test
    public void testMultipleSingleRows() {
        // given
        ClassificationReport report = new ClassificationReport(headers);
        MonitorQueryItem item = new MonitorQueryItem();
        item.setAgeInDays(daysDiff - 1);
        item.setNumberOfTasks(2);
        item.setKey("key");
        report.addItem(item);
        item.setKey("key2");
        report.addItem(item);
        // when
        ReportResource resource = reportAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
        // then

        // meta
        ReportResource.MetaInformation meta = resource.getMeta();
        assertEquals("ClassificationReport", meta.getName());
        assertEquals("2019-01-02T00:00:00Z", meta.getDate());
        assertEquals("CLASSIFICATION KEYS", meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        Map<String, ReportResource.RowResource> rows = resource.getRows();
        assertEquals(2, rows.size());

        ReportResource.RowResource row = rows.get("key");
        assertEquals(ReportResource.SingleRowResource.class, row.getClass());
        assertEquals(2, row.getTotal());
        Map<String, Integer> cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        row = rows.get("key2");
        assertEquals(ReportResource.SingleRowResource.class, row.getClass());
        assertEquals(2, row.getTotal());
        cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        // sumRow
        ReportResource.RowResource sumRow = resource.getSumRow();
        assertEquals(ReportResource.SingleRowResource.class, sumRow.getClass());
        assertEquals(4, sumRow.getTotal());
        cells = sumRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(4, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

    }

    @Test
    public void testOneFoldableRow() {
        // given
        ClassificationReport.DetailedClassificationReport report = new ClassificationReport.DetailedClassificationReport(
            headers);
        DetailedMonitorQueryItem item = new DetailedMonitorQueryItem();
        item.setAgeInDays(daysDiff - 1);
        item.setNumberOfTasks(2);
        item.setKey("key");
        item.setAttachmentKey("attachement");
        report.addItem(item);
        item.setAttachmentKey(null);
        report.addItem(item);
        // when
        ReportResource resource = reportAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
        // then

        // meta
        ReportResource.MetaInformation meta = resource.getMeta();
        assertEquals("DetailedClassificationReport", meta.getName());
        assertEquals("2019-01-02T00:00:00Z", meta.getDate());
        assertEquals("TASK CLASSIFICATION KEYS", meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        Map<String, ReportResource.RowResource> rows = resource.getRows();
        assertEquals(1, rows.size());
        assertEquals(ReportResource.FoldableRowResource.class, rows.get("key").getClass());
        ReportResource.FoldableRowResource row = (ReportResource.FoldableRowResource) rows.get("key");
        assertEquals(4, row.getTotal());
        Map<String, Integer> cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(4, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(2, row.getFoldableRows().size());
        ReportResource.RowResource foldedRow = row.getFoldableRows().get("attachement");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        foldedRow = row.getFoldableRows().get("N/A");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        // sumRow
        ReportResource.RowResource sumRow = resource.getSumRow();
        assertEquals(ReportResource.FoldableRowResource.class, sumRow.getClass());
        assertEquals(4, row.getTotal());
        cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(4, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(2, row.getFoldableRows().size());
        foldedRow = row.getFoldableRows().get("attachement");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        foldedRow = row.getFoldableRows().get("N/A");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());
    }

    @Test
    public void testMultipleFoldableRows() {
        // given
        ClassificationReport.DetailedClassificationReport report = new ClassificationReport.DetailedClassificationReport(
            headers);
        DetailedMonitorQueryItem item = new DetailedMonitorQueryItem();
        item.setAgeInDays(daysDiff - 1);
        item.setNumberOfTasks(2);
        item.setKey("key");
        item.setAttachmentKey("attachement");
        report.addItem(item);
        item.setAttachmentKey(null);
        report.addItem(item);
        item.setKey("key2");
        report.addItem(item);
        // when
        ReportResource resource = reportAssembler.toReportResource(report, now.toInstant(ZoneOffset.UTC));
        // then

        // meta
        ReportResource.MetaInformation meta = resource.getMeta();
        assertEquals("DetailedClassificationReport", meta.getName());
        assertEquals("2019-01-02T00:00:00Z", meta.getDate());
        assertEquals("TASK CLASSIFICATION KEYS", meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        Map<String, ReportResource.RowResource> rows = resource.getRows();
        assertEquals(2, rows.size());

        assertEquals(ReportResource.FoldableRowResource.class, rows.get("key").getClass());
        ReportResource.FoldableRowResource row = (ReportResource.FoldableRowResource) rows.get("key");
        assertEquals(4, row.getTotal());
        Map<String, Integer> cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(4, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(2, row.getFoldableRows().size());
        ReportResource.RowResource foldedRow = row.getFoldableRows().get("attachement");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        foldedRow = row.getFoldableRows().get("N/A");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(ReportResource.FoldableRowResource.class, rows.get("key2").getClass());
        row = (ReportResource.FoldableRowResource) rows.get("key2");
        assertEquals(2, row.getTotal());
        cells = row.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(1, row.getFoldableRows().size());
        foldedRow = row.getFoldableRows().get("N/A");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        // sumRow
        assertEquals(ReportResource.FoldableRowResource.class, resource.getSumRow().getClass());
        ReportResource.FoldableRowResource sumRow = (ReportResource.FoldableRowResource) resource.getSumRow();
        assertEquals(6, sumRow.getTotal());
        cells = sumRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(6, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        assertEquals(2, sumRow.getFoldableRows().size());
        foldedRow = sumRow.getFoldableRows().get("attachement");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(2, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(2, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());

        foldedRow = sumRow.getFoldableRows().get("N/A");
        assertEquals(ReportResource.SingleRowResource.class, foldedRow.getClass());
        assertEquals(4, foldedRow.getTotal());
        cells = foldedRow.getCells();
        assertEquals(5, cells.size());
        assertEquals(4, cells.get("2019-01-01").intValue());
        assertEquals(0, cells.get("2018-12-31").intValue());
        assertEquals(0, cells.get("2018-12-30").intValue());
        assertEquals(0, cells.get("2018-12-29").intValue());
        assertEquals(0, cells.get("2018-12-28").intValue());
    }

}
