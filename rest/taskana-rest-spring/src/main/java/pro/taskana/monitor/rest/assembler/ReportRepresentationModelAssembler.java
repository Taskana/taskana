package pro.taskana.monitor.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.Report;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.monitor.api.reports.WorkbasketReport;
import pro.taskana.monitor.api.reports.header.ColumnHeader;
import pro.taskana.monitor.api.reports.item.QueryItem;
import pro.taskana.monitor.api.reports.row.FoldableRow;
import pro.taskana.monitor.api.reports.row.Row;
import pro.taskana.monitor.api.reports.row.SingleRow;
import pro.taskana.monitor.rest.MonitorController;
import pro.taskana.monitor.rest.models.ReportRepresentationModel;
import pro.taskana.monitor.rest.models.ReportRepresentationModel.RowRepresentationModel;
import pro.taskana.task.api.TaskState;

/** Transforms any {@link Report} into its {@link ReportRepresentationModel}. */
@Component
public class ReportRepresentationModelAssembler {

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull TaskStatusReport report,
      @NonNull List<String> domains,
      @NonNull List<TaskState> states,
      @NonNull List<String> workbasketIds,
      @NonNull Integer priorityMinimum)
      throws NotAuthorizedException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .getTaskStatusReport(domains, states, workbasketIds, priorityMinimum))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull ClassificationReport report, TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getClassificationReport(taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull WorkbasketReport report,
      @NonNull List<TaskState> states,
      @NonNull TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(methodOn(MonitorController.class).getWorkbasketReport(states, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull WorkbasketReport report, int daysInPast, @NonNull List<TaskState> states)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .getTasksWorkbasketPlannedDateReport(daysInPast, states))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(@NonNull TimestampReport report)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(linkTo(methodOn(MonitorController.class).getTimestampReport()).withSelfRel());
    return resource;
  }

  public <I extends QueryItem, H extends ColumnHeader<? super I>>
      ReportRepresentationModel toReportResource(Report<I, H> report, Instant time) {
    String[] header =
        report.getColumnHeaders().stream().map(H::getDisplayName).toArray(String[]::new);
    ReportRepresentationModel.MetaInformation meta =
        new ReportRepresentationModel.MetaInformation(
            report.getClass().getSimpleName(),
            time,
            header,
            report.getRowDesc(),
            report.getSumRow().getKey());

    // iterate over each Row and transform it to a RowResource while keeping the domain key.
    List<RowRepresentationModel> rows =
        report.getRows().values().stream()
            .sorted(Comparator.comparing(e -> e.getKey().toLowerCase()))
            .map(i -> transformRow(i, new String[report.getRowDesc().length], 0))
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    List<RowRepresentationModel> sumRow =
        transformRow(report.getSumRow(), new String[report.getRowDesc().length], 0);

    return new ReportRepresentationModel(meta, rows, sumRow);
  }

  <I extends QueryItem, H extends ColumnHeader<? super I>>
      ReportRepresentationModel toReportResource(Report<I, H> report) {
    return toReportResource(report, Instant.now());
  }

  private <I extends QueryItem> List<RowRepresentationModel> transformRow(
      Row<I> row, String[] desc, int depth) {
    // This is a very dirty solution.. Personally I'd prefer to use a visitor-like pattern here.
    // The issue with that: Addition of the visitor code within taskana-core - and having clean code
    // is not
    // a reason to append code somewhere where it doesn't belong.
    if (row.getClass() == SingleRow.class) {
      return Collections.singletonList(transformSingleRow((SingleRow<I>) row, desc, depth));
    }
    return transformFoldableRow((FoldableRow<I>) row, desc, depth);
  }

  private <I extends QueryItem> RowRepresentationModel transformSingleRow(
      SingleRow<I> row, String[] previousRowDesc, int depth) {
    String[] rowDesc = new String[previousRowDesc.length];
    System.arraycopy(previousRowDesc, 0, rowDesc, 0, depth);
    rowDesc[depth] = row.getDisplayName();
    return new RowRepresentationModel(
        row.getCells(), row.getTotalValue(), depth, rowDesc, depth == 0);
  }

  private <I extends QueryItem> List<RowRepresentationModel> transformFoldableRow(
      FoldableRow<I> row, String[] previousRowDesc, int depth) {
    RowRepresentationModel baseRow = transformSingleRow(row, previousRowDesc, depth);
    List<RowRepresentationModel> rowList = new LinkedList<>();
    rowList.add(baseRow);
    row.getFoldableRowKeySet().stream()
        .sorted(String.CASE_INSENSITIVE_ORDER)
        .map(s -> transformRow(row.getFoldableRow(s), baseRow.getDesc(), depth + 1))
        .flatMap(Collection::stream)
        .forEachOrdered(rowList::add);
    return rowList;
  }
}
