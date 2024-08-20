package io.kadai.monitor.rest.assembler;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.kadai.common.api.exceptions.InvalidArgumentException;
import io.kadai.common.api.exceptions.NotAuthorizedException;
import io.kadai.monitor.api.TaskTimestamp;
import io.kadai.monitor.api.reports.ClassificationCategoryReport;
import io.kadai.monitor.api.reports.ClassificationReport;
import io.kadai.monitor.api.reports.ClassificationReport.DetailedClassificationReport;
import io.kadai.monitor.api.reports.Report;
import io.kadai.monitor.api.reports.TaskCustomFieldValueReport;
import io.kadai.monitor.api.reports.TaskStatusReport;
import io.kadai.monitor.api.reports.TimestampReport;
import io.kadai.monitor.api.reports.WorkbasketPriorityReport;
import io.kadai.monitor.api.reports.WorkbasketReport;
import io.kadai.monitor.api.reports.header.ColumnHeader;
import io.kadai.monitor.api.reports.item.QueryItem;
import io.kadai.monitor.api.reports.row.FoldableRow;
import io.kadai.monitor.api.reports.row.Row;
import io.kadai.monitor.api.reports.row.SingleRow;
import io.kadai.monitor.rest.MonitorController;
import io.kadai.monitor.rest.PriorityReportFilterParameter;
import io.kadai.monitor.rest.TimeIntervalReportFilterParameter;
import io.kadai.monitor.rest.models.PriorityColumnHeaderRepresentationModel;
import io.kadai.monitor.rest.models.ReportRepresentationModel;
import io.kadai.monitor.rest.models.ReportRepresentationModel.RowRepresentationModel;
import io.kadai.task.api.TaskCustomField;
import io.kadai.task.api.TaskState;
import io.kadai.workbasket.api.WorkbasketType;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/** Transforms any {@link Report} into its {@link ReportRepresentationModel}. */
@Component
public class ReportRepresentationModelAssembler {

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull WorkbasketReport report,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      @NonNull TaskTimestamp taskTimestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeWorkbasketReport(filterParameter, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull WorkbasketPriorityReport report,
      @NonNull PriorityReportFilterParameter filterParameter,
      WorkbasketType[] workbasketTypes,
      PriorityColumnHeaderRepresentationModel[] columnHeaders)
      throws InvalidArgumentException, NotAuthorizedException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computePriorityWorkbasketReport(
                        filterParameter, workbasketTypes, columnHeaders))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull ClassificationCategoryReport report,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      @NonNull TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeClassificationCategoryReport(filterParameter, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull ClassificationReport report,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      @NonNull TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeClassificationReport(filterParameter, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull DetailedClassificationReport report,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      @NonNull TaskTimestamp taskTimestamp)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeDetailedClassificationReport(filterParameter, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull TaskCustomFieldValueReport report,
      @NonNull TaskCustomField customField,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      @NonNull TaskTimestamp taskTimestamp)
      throws InvalidArgumentException, NotAuthorizedException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeTaskCustomFieldValueReport(customField, filterParameter, taskTimestamp))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      TaskStatusReport report,
      List<String> domain,
      List<TaskState> state,
      List<String> workbasketIds,
      Integer priorityMinimum)
      throws NotAuthorizedException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeTaskStatusReport(domain, state, workbasketIds, priorityMinimum))
            .withSelfRel());
    return resource;
  }

  @NonNull
  public ReportRepresentationModel toModel(
      @NonNull TimestampReport report,
      @NonNull TimeIntervalReportFilterParameter filterParameter,
      TaskTimestamp[] timestamps)
      throws NotAuthorizedException, InvalidArgumentException {
    ReportRepresentationModel resource = toReportResource(report);
    resource.add(
        linkTo(
                methodOn(MonitorController.class)
                    .computeTimestampReport(filterParameter, timestamps))
            .withSelfRel());
    return resource;
  }

  <I extends QueryItem, H extends ColumnHeader<? super I>>
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
    // The issue with that: Addition of the visitor code within kadai-core - and having clean code
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
