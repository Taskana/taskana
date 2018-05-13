package pro.taskana.rest.resource.assembler;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pro.taskana.TaskState;
import pro.taskana.exceptions.NotAuthorizedException;
import pro.taskana.impl.report.QueryItem;
import pro.taskana.impl.report.Report;
import pro.taskana.impl.report.ReportColumnHeader;
import pro.taskana.impl.report.ReportRow;
import pro.taskana.impl.report.impl.TaskStatusReport;
import pro.taskana.rest.MonitorController;
import pro.taskana.rest.resource.ReportResource;

/**
 * Transforms any {@link Report} into its {@link ReportResource}.
 */
@Component
public class ReportAssembler {

    public ReportResource toResource(TaskStatusReport report, List<String> domains, List<TaskState> states)
        throws NotAuthorizedException {
        ReportResource resource = toResource(report);
        resource.add(
            linkTo(methodOn(MonitorController.class).getTaskStatusReport(domains, states))
                .withSelfRel().expand());
        return resource;
    }

    private <I extends QueryItem, H extends ReportColumnHeader<? super I>> ReportResource toResource(
        Report<I, H> report) {
        String[] header = report.getColumnHeaders()
            .stream()
            .map(ReportColumnHeader::getDisplayName)
            .toArray(String[]::new);
        ReportResource.MetaInformation meta = new ReportResource.MetaInformation(
            report.getClass().getSimpleName(),
            Instant.now().toString(),
            header,
            report.getRowDesc());

        // iterate over each ReportRow and transform it to a RowResource while keeping the domain key.
        Map<String, ReportResource.RowResource> rows = report.getReportRows()
            .entrySet()
            .stream()
            .collect(Collectors.toMap(Map.Entry::getKey, i -> transformRow(i.getValue(), header)));

        ReportResource.RowResource sumRow = transformRow(report.getSumRow(), header);

        return new ReportResource(meta, rows, sumRow);
    }

    private <I extends QueryItem> ReportResource.RowResource transformRow(ReportRow<I> row, String[] header) {
        Map<String, Integer> result = new HashMap<>();
        int[] cells = row.getCells();
        for (int i = 0; i < cells.length; i++) {
            result.put(header[i], cells[i]);
        }
        return new ReportResource.RowResource(result, row.getTotalValue());
    }

}
