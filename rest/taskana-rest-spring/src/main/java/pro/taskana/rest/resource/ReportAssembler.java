package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pro.taskana.TaskState;
import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.row.FoldableRow;
import pro.taskana.impl.report.row.SingleRow;
import pro.taskana.report.ClassificationReport;
import pro.taskana.report.DailyEntryExitReport;
import pro.taskana.report.TaskStatusReport;
import pro.taskana.report.WorkbasketReport;
import pro.taskana.report.structure.ColumnHeader;
import pro.taskana.report.structure.QueryItem;
import pro.taskana.report.structure.Report;
import pro.taskana.report.structure.Row;
import pro.taskana.rest.MonitorController;

/**
 * Transforms any {@link Report} into its {@link ReportResource}.
 */
@Component
public class ReportAssembler {

    public ReportResource toResource(TaskStatusReport report, List<String> domains, List<TaskState> states)
        throws NotAuthorizedException, InvalidArgumentException {
        ReportResource resource = toReportResource(report);
        resource.add(
            linkTo(methodOn(MonitorController.class).getTasksStatusReport(domains, states))
                .withSelfRel().expand());
        return resource;
    }

    public ReportResource toResource(ClassificationReport report)
        throws NotAuthorizedException, InvalidArgumentException {
        ReportResource resource = toReportResource(report);
        resource.add(
            linkTo(methodOn(MonitorController.class).getTasksClassificationReport())
                .withSelfRel().expand());
        return resource;
    }

    public ReportResource toResource(WorkbasketReport report, int daysInPast, List<TaskState> states)
        throws NotAuthorizedException, InvalidArgumentException {
        ReportResource resource = toReportResource(report);
        resource.add(
            linkTo(methodOn(MonitorController.class).getTasksWorkbasketReport(daysInPast, states))
                .withSelfRel().expand());
        return resource;
    }

    public ReportResource toResource(DailyEntryExitReport report)
        throws NotAuthorizedException, InvalidArgumentException {
        ReportResource resource = toReportResource(report);
        resource.add(linkTo(methodOn(MonitorController.class).getDailyEntryExitReport()).withSelfRel().expand());
        return resource;
    }

    <I extends QueryItem, H extends ColumnHeader<? super I>> ReportResource toReportResource(Report<I, H> report) {
        return toReportResource(report, Instant.now());
    }

    <I extends QueryItem, H extends ColumnHeader<? super I>> ReportResource toReportResource(
        Report<I, H> report, Instant time) {
        String[] header = report.getColumnHeaders()
            .stream()
            .map(ColumnHeader::getDisplayName)
            .toArray(String[]::new);
        ReportResource.MetaInformation meta = new ReportResource.MetaInformation(
            report.getClass().getSimpleName(),
            time.toString(),
            header,
            report.getRowDesc());

        // iterate over each Row and transform it to a RowResource while keeping the domain key.
        Map<String, ReportResource.RowResource> rows = report.getRows()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, i -> transformRow(i.getValue(), header)));

        ReportResource.RowResource sumRow = transformRow(report.getSumRow(), header);

        return new ReportResource(meta, rows, sumRow);
    }

    private <I extends QueryItem> ReportResource.RowResource transformRow(Row<I> row, String[] header) {
        // This is a very dirty solution.. Personally I'd prefer to use a visitor-like pattern here.
        // The issue with that: Addition of the visitor code within taskana-core - and having clean code is not
        // a reason to append code somewhere where it doesn't belong.
        if (row.getClass() == SingleRow.class) {
            return transformSingleRow((SingleRow<I>) row, header);
        }
        return transformFoldableRow((FoldableRow<I>) row, header);
    }

    private <I extends QueryItem> ReportResource.SingleRowResource transformSingleRow(SingleRow<I> row,
        String[] header) {
        Map<String, Integer> result = new HashMap<>();
        int[] cells = row.getCells();
        for (int i = 0; i < cells.length; i++) {
            result.put(header[i], cells[i]);
        }
        return new ReportResource.SingleRowResource(result, row.getTotalValue());
    }

    private <I extends QueryItem> ReportResource.FoldableRowResource transformFoldableRow(FoldableRow<I> row,
        String[] header) {
        ReportResource.FoldableRowResource base = new ReportResource.FoldableRowResource(
            transformSingleRow(row, header));
        row.getFoldableRowKeySet().stream()
            .map(k -> new Pair<>(k, row.getFoldableRow(k)))
            .map(p -> new Pair<>(p.key, transformRow(p.value, header)))
            .forEachOrdered(p -> base.addRow(p.key, p.value));
        return base;
    }

    /**
     * Simple Pair (tuple).
     * @param <K> key
     * @param <V> value
     */
    private class Pair<K, V> {

        private final K key;
        private final V value;

        Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

    }

}
