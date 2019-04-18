package pro.taskana.rest.resource;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        assertArrayEquals(new String[] {"WORKBASKET KEYS"}, meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        assertTrue(resource.getRows().isEmpty());

        // sumRow
        assertEquals(1, resource.getSumRow().size());
        ReportResource.RowResource sumRow = resource.getSumRow().get(0);
        assertArrayEquals(new String[] {"Total"}, sumRow.getDesc());
        assertTrue(sumRow.isDisplay());
        assertEquals(0, sumRow.getDepth());
        assertEquals(0, sumRow.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 0}, sumRow.getCells());
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
        assertArrayEquals(new String[] {"CLASSIFICATION KEYS"}, meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        List<ReportResource.RowResource> rows = resource.getRows();
        assertEquals(1, rows.size());
        ReportResource.RowResource row = rows.get(0);
        assertArrayEquals(new String[] {"key"}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertEquals(2, row.getTotal());

        assertTrue(row.isDisplay());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        // sumRow
        assertEquals(1, resource.getSumRow().size());
        ReportResource.RowResource sumRow = resource.getSumRow().get(0);
        assertArrayEquals(new String[] {"Total"}, sumRow.getDesc());
        assertTrue(sumRow.isDisplay());
        assertEquals(0, sumRow.getDepth());
        assertEquals(2, sumRow.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, sumRow.getCells());

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
        assertArrayEquals(new String[] {"CLASSIFICATION KEYS"}, meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        List<ReportResource.RowResource> rows = resource.getRows();
        assertEquals(2, rows.size());

        ReportResource.RowResource row = rows.get(0);
        assertArrayEquals(new String[] {"key"}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = rows.get(1);
        assertArrayEquals(new String[] {"key2"}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        // sumRow
        assertEquals(1, resource.getSumRow().size());
        ReportResource.RowResource sumRow = resource.getSumRow().get(0);
        assertArrayEquals(new String[] {"Total"}, sumRow.getDesc());
        assertEquals(0, sumRow.getDepth());
        assertTrue(sumRow.isDisplay());
        assertEquals(4, sumRow.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 4}, sumRow.getCells());
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
        item.setAttachmentKey("attachment");
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
        assertArrayEquals(new String[] {"TASK CLASSIFICATION KEYS", "ATTACHMENT"}, meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        List<ReportResource.RowResource> rows = resource.getRows();
        assertEquals(1 + 2, rows.size());

        ReportResource.RowResource row = rows.get(0);
        assertArrayEquals(new String[] {"key", null}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(4, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 4}, row.getCells());

        row = rows.get(1);
        assertArrayEquals(new String[] {"key", "attachment"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = rows.get(2);
        assertArrayEquals(new String[] {"key", "N/A"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        // sumRow
        List<ReportResource.RowResource> sumRow = resource.getSumRow();
        assertEquals(1 + 2, sumRow.size());

        row = sumRow.get(0);
        assertArrayEquals(new String[] {"Total", null}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(4, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 4}, row.getCells());

        row = sumRow.get(1);
        assertArrayEquals(new String[] {"Total", "attachment"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = sumRow.get(2);
        assertArrayEquals(new String[] {"Total", "N/A"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());
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
        item.setAttachmentKey("attachment");
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
        assertArrayEquals(new String[] {"TASK CLASSIFICATION KEYS", "ATTACHMENT"}, meta.getRowDesc());
        assertArrayEquals(headers.stream().map(TimeIntervalColumnHeader::getDisplayName).toArray(), meta.getHeader());
        assertEquals("Total", meta.getTotalDesc());

        // rows
        List<ReportResource.RowResource> rows = resource.getRows();
        assertEquals((1 + 2) + (1 + 1), rows.size());

        ReportResource.RowResource row = rows.get(0);
        assertArrayEquals(new String[] {"key", null}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(4, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 4}, row.getCells());

        row = rows.get(1);
        assertArrayEquals(new String[] {"key", "attachment"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = rows.get(2);
        assertArrayEquals(new String[] {"key", "N/A"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = rows.get(3);
        assertArrayEquals(new String[] {"key2", null}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = rows.get(4);
        assertArrayEquals(new String[] {"key2", "N/A"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        // sumRow

        List<ReportResource.RowResource> sumRow = resource.getSumRow();
        assertEquals(1 + 2, sumRow.size());

        row = sumRow.get(0);
        assertArrayEquals(new String[] {"Total", null}, row.getDesc());
        assertEquals(0, row.getDepth());
        assertTrue(row.isDisplay());
        assertEquals(6, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 6}, row.getCells());

        row = sumRow.get(1);
        assertArrayEquals(new String[] {"Total", "attachment"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(2, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 2}, row.getCells());

        row = sumRow.get(2);
        assertArrayEquals(new String[] {"Total", "N/A"}, row.getDesc());
        assertEquals(1, row.getDepth());
        assertFalse(row.isDisplay());
        assertEquals(4, row.getTotal());
        assertArrayEquals(new int[] {0, 0, 0, 0, 4}, row.getCells());

    }

}
