package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.report.api.ClassificationReport;
import pro.taskana.report.api.TaskStatusReport;
import pro.taskana.report.api.TimestampReport;
import pro.taskana.report.api.WorkbasketReport;
import pro.taskana.report.api.row.FoldableRow;
import pro.taskana.report.api.row.SingleRow;
import pro.taskana.report.api.structure.ColumnHeader;
import pro.taskana.report.api.structure.QueryItem;
import pro.taskana.report.api.structure.Report;
import pro.taskana.report.api.structure.Row;
import pro.taskana.rest.MonitorController;
import pro.taskana.task.api.TaskState;

/** Transforms any {@link Report} into its {@link ReportResource}. */
@Component
public class ReportResourceAssembler {

  public ReportResource toResource(
      TaskStatusReport report, List<String> domains, List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportResource resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getTasksStatusReport(domains, states))
            .withSelfRel()
            .expand());
    return resource;
  }

  public ReportResource toResource(ClassificationReport report)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportResource resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getTasksClassificationReport())
            .withSelfRel()
            .expand());
    return resource;
  }

  public ReportResource toResource(WorkbasketReport report, List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportResource resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getTasksWorkbasketReport(states))
            .withSelfRel()
            .expand());
    return resource;
  }

  public ReportResource toResource(WorkbasketReport report, int daysInPast, List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportResource resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .getTasksWorkbasketPlannedDateReport(daysInPast, states))
            .withSelfRel()
            .expand());
    return resource;
  }

  public ReportResource toResource(TimestampReport report)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportResource resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getDailyEntryExitReport()).withSelfRel().expand());
    return resource;
  }

  <I extends QueryItem, H extends ColumnHeader<? super I>> ReportResource toReportResource(
      Report<I, H> report) {
    return toReportResource(report, Instant.now());
  }

  <I extends QueryItem, H extends ColumnHeader<? super I>> ReportResource toReportResource(
      Report<I, H> report, Instant time) {
    String[] header =
        report.getColumnHeaders().stream().map(H::getDisplayName).toArray(String[]::new);
    ReportResource.MetaInformation meta =
        new ReportResource.MetaInformation(
            report.getClass().getSimpleName(), time.toString(), header, report.getRowDesc());

    // iterate over each Row and transform it to a RowResource while keeping the domain key.
    List<ReportResource.RowResource> rows =
        report.getRows().entrySet().stream()
            .sorted(Comparator.comparing(e -> e.getKey().toLowerCase()))
            .map(
                i ->
                    transformRow(
                        i.getValue(), i.getKey(), new String[report.getRowDesc().length], 0))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    List<ReportResource.RowResource> sumRow =
        transformRow(
            report.getSumRow(), meta.getTotalDesc(), new String[report.getRowDesc().length], 0);

    return new ReportResource(meta, rows, sumRow);
  }

  private <I extends QueryItem> List<ReportResource.RowResource> transformRow(
      Row<I> row, String currentDesc, String[] desc, int depth) {
    // This is a very dirty solution.. Personally I'd prefer to use a visitor-like pattern here.
    // The issue with that: Addition of the visitor code within taskana-core - and having clean code
    // is not
    // a reason to append code somewhere where it doesn't belong.
    if (row.getClass() == SingleRow.class) {
      return Collections.singletonList(
          transformSingleRow((SingleRow<I>) row, currentDesc, desc, depth));
    }
    return transformFoldableRow((FoldableRow<I>) row, currentDesc, desc, depth);
  }

  private <I extends QueryItem> ReportResource.RowResource transformSingleRow(
      SingleRow<I> row, String currentDesc, String[] previousRowDesc, int depth) {
    String[] rowDesc = new String[previousRowDesc.length];
    System.arraycopy(previousRowDesc, 0, rowDesc, 0, depth);
    rowDesc[depth] = currentDesc;
    return new ReportResource.RowResource(
        row.getCells(), row.getTotalValue(), depth, rowDesc, depth == 0);
  }

  private <I extends QueryItem> List<ReportResource.RowResource> transformFoldableRow(
      FoldableRow<I> row, String currentDesc, String[] previousRowDesc, int depth) {
    ReportResource.RowResource baseRow =
        transformSingleRow(row, currentDesc, previousRowDesc, depth);
    List<ReportResource.RowResource> rowList = new LinkedList<>();
    rowList.add(baseRow);
    row.getFoldableRowKeySet().stream()
        .sorted(String.CASE_INSENSITIVE_ORDER)
        .map(s -> transformRow(row.getFoldableRow(s), s, baseRow.getDesc(), depth + 1))
        .flatMap(Collection::stream)
        .forEachOrdered(rowList::add);
    return rowList;
  }
}
