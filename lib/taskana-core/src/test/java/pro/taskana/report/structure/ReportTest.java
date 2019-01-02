package pro.taskana.report.structure;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.MonitorQueryItem;

/**
 * Tests for {@link Report}.
 */
public class ReportTest {

    private static final List<TimeIntervalColumnHeader> HEADERS = IntStream.range(0, 4)
        .mapToObj(TimeIntervalColumnHeader::new)
        .collect(Collectors.toList());
    private Report<MonitorQueryItem, TimeIntervalColumnHeader> report;
    private MonitorQueryItem item;

    @Before
    public void before() {
        this.report = new Report<MonitorQueryItem, TimeIntervalColumnHeader>(HEADERS, "rowDesc") {

        };

        item = new MonitorQueryItem();
        item.setKey("key");
        item.setAgeInDays(0);
        item.setNumberOfTasks(3);
    }

    @Test
    public void testEmptyReport() {
        //then
        assertEquals(0, report.getRows().size());
        Row<MonitorQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {0, 0, 0, 0}, sumRow.getCells());
        assertEquals(0, sumRow.getTotalValue());
    }

    @Test
    public void testInsertSingleItem() {
        //when
        report.addItem(item);

        //then
        assertEquals(1, report.getRows().size());
        Row<MonitorQueryItem> row = report.getRow("key");
        assertArrayEquals(new int[] {item.getValue(), 0, 0, 0}, row.getCells());
        assertEquals(item.getValue(), row.getTotalValue());

    }

    @Test
    public void testInsertSameItemMultipleTimes() {
        //when
        report.addItem(item);
        report.addItem(item);

        //then
        assertEquals(1, report.getRows().size());
        Row<MonitorQueryItem> row = report.getRow("key");
        assertArrayEquals(new int[] {2 * item.getValue(), 0, 0, 0}, row.getCells());
        assertEquals(2 * item.getValue(), row.getTotalValue());

    }

    @Test
    public void testInsertSameItemMultipleTimes2() {
        //given
        MonitorQueryItem item = new MonitorQueryItem();
        item.setKey("key");
        item.setAgeInDays(0);
        item.setNumberOfTasks(3);

        //when
        report.addItems(Arrays.asList(item, item));

        //then
        assertEquals(1, report.getRows().size());
        Row<MonitorQueryItem> row = report.getRow("key");
        assertArrayEquals(new int[] {2 * item.getValue(), 0, 0, 0}, row.getCells());
        assertEquals(2 * item.getValue(), row.getTotalValue());

    }

    @Test
    public void testInsertSameItemMultipleTimesWithPreProcessor() {
        //given
        int overrideValue = 5;
        QueryItemPreprocessor<MonitorQueryItem> preprocessor = (item) -> {
            item.setNumberOfTasks(overrideValue);
            return item;
        };
        //when
        report.addItems(Arrays.asList(item, item), preprocessor);

        //then
        assertEquals(1, report.getRows().size());
        Row<MonitorQueryItem> row = report.getRow("key");
        assertArrayEquals(new int[] {2 * overrideValue, 0, 0, 0}, row.getCells());
        assertEquals(2 * overrideValue, row.getTotalValue());

    }

    @Test
    public void testInsertItemWithNoColumnHeaders() {
        //given
        report = new Report<MonitorQueryItem, TimeIntervalColumnHeader>(Collections.emptyList(), "rowDesc") {

        };

        //when
        report.addItem(item);

        //then
        assertEquals(1, report.getRows().size());
        assertArrayEquals(new int[0], report.getRow("key").getCells());
        assertEquals(item.getValue(), report.getRow("key").getTotalValue());
    }

    @Test
    public void testInsertItemWhichIsNotInHeaderScopes() {
        //given
        item.setAgeInDays(-2);
        //when
        report.addItem(item);

        //then
        assertEquals(0, report.getRows().size());
        Row<MonitorQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {0, 0, 0, 0}, sumRow.getCells());
        assertEquals(0, sumRow.getTotalValue());
    }

    @Test
    public void testInsertItemWhichIsInMultipleHeaderScopes() {
        //given
        List<TimeIntervalColumnHeader> headers = new ArrayList<>(HEADERS);
        headers.add(new TimeIntervalColumnHeader(0, 3));
        report = new Report<MonitorQueryItem, TimeIntervalColumnHeader>(headers, "rowDesc") {

        };
        item.setAgeInDays(2);

        //when
        report.addItem(item);

        //then
        assertEquals(1, report.getRows().size());

        Row<MonitorQueryItem> row = report.getRow("key");
        assertArrayEquals(new int[] {0, 0, item.getValue(), 0, item.getValue()}, row.getCells());
        assertEquals(2 * item.getValue(), row.getTotalValue());

        Row<MonitorQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {0, 0, item.getValue(), 0, item.getValue()}, sumRow.getCells());
        assertEquals(2 * item.getValue(), sumRow.getTotalValue());

    }

    @Test
    public void testInsertItemWithPreProcessor() {
        //given
        int overrideValue = 5;
        QueryItemPreprocessor<MonitorQueryItem> preprocessor = item -> {
            item.setNumberOfTasks(overrideValue);
            return item;
        };
        item.setAgeInDays(1);

        //when
        report.addItem(item, preprocessor);

        //then
        assertEquals(1, report.getRows().size());

        Row<MonitorQueryItem> row = report.getRow(item.getKey());
        assertArrayEquals(new int[] {0, overrideValue, 0, 0}, row.getCells());
        assertEquals(overrideValue, row.getTotalValue());

        Row<MonitorQueryItem> sumRow = report.getSumRow();
        assertArrayEquals(new int[] {0, overrideValue, 0, 0}, sumRow.getCells());
        assertEquals(overrideValue, sumRow.getTotalValue());

    }

}
