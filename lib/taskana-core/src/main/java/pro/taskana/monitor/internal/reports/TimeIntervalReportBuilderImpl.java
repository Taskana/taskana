package pro.taskana.monitor.internal.reports;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.CombinedClassificationFilter;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.TaskTimestamp;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.TimeIntervalReportBuilder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.AgeQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.WorkingDaysToDaysReportConverter;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskState;

/**
 * Implementation of {@link TimeIntervalReportBuilder}.
 *
 * @param <B> the true Builder behind this Interface
 * @param <I> the true AgeQueryItem inside the Report
 * @param <H> the column header
 */
abstract class TimeIntervalReportBuilderImpl<
        B extends TimeIntervalReportBuilder<B, I, H>,
        I extends AgeQueryItem,
        H extends TimeIntervalColumnHeader>
    implements TimeIntervalReportBuilder<B, I, H> {

  protected InternalTaskanaEngine taskanaEngine;
  protected MonitorMapper monitorMapper;
  protected List<H> columnHeaders;
  protected boolean inWorkingDays;
  protected List<String> workbasketIds;
  protected List<TaskState> states;
  protected List<String> classificationCategory;
  protected List<String> domains;
  protected List<String> classificationIds;
  protected List<String> excludedClassificationIds;
  protected Map<TaskCustomField, String> customAttributeFilter;
  protected WorkingDaysToDaysConverter converter;

  TimeIntervalReportBuilderImpl(InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    this.columnHeaders = Collections.emptyList();
    converter = taskanaEngine.getEngine().getWorkingDaysToDaysConverter();
  }

  @Override
  public B withColumnHeaders(List<H> columnHeaders) {
    this.columnHeaders = columnHeaders;
    return _this();
  }

  @Override
  public B inWorkingDays() {
    this.inWorkingDays = true;
    return _this();
  }

  @Override
  public B workbasketIdIn(List<String> workbasketIds) {
    this.workbasketIds = new ArrayList<>(workbasketIds);
    return _this();
  }

  @Override
  public B stateIn(List<TaskState> states) {
    this.states = new ArrayList<>(states);
    return _this();
  }

  @Override
  public B classificationCategoryIn(List<String> classificationCategory) {
    this.classificationCategory = new ArrayList<>(classificationCategory);
    return _this();
  }

  @Override
  public B classificationIdIn(List<String> classificationIds) {
    this.classificationIds = new ArrayList<>(classificationIds);
    return _this();
  }

  @Override
  public B excludedClassificationIdIn(List<String> excludedClassificationIds) {
    this.excludedClassificationIds = new ArrayList<>(excludedClassificationIds);
    return _this();
  }

  @Override
  public B domainIn(List<String> domains) {
    this.domains = new ArrayList<>(domains);
    return _this();
  }

  @Override
  public B customAttributeFilterIn(Map<TaskCustomField, String> customAttributeFilter) {
    this.customAttributeFilter = new HashMap<>(customAttributeFilter);
    return _this();
  }

  @Override
  public List<String> listTaskIdsForSelectedItems(
      List<SelectedItem> selectedItems, TaskTimestamp timestamp)
      throws NotAuthorizedException, InvalidArgumentException {

    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      if (this.columnHeaders == null) {
        throw new InvalidArgumentException("ColumnHeader must not be null.");
      }
      if (selectedItems == null || selectedItems.isEmpty()) {
        throw new InvalidArgumentException("SelectedItems must not be null or empty.");
      }
      boolean joinWithAttachments = subKeyIsSet(selectedItems);
      if (!(this instanceof ClassificationReport.Builder) && joinWithAttachments) {
        throw new InvalidArgumentException("SubKeys are supported for ClassificationReport only.");
      }
      List<CombinedClassificationFilter> combinedClassificationFilter =
          getCombinedClassificationFilter();
      joinWithAttachments |= combinedClassificationFilter != null;
      if (this.inWorkingDays) {
        selectedItems = convertWorkingDaysToDays(selectedItems, this.columnHeaders);
      }
      return this.monitorMapper.getTaskIdsForSelectedItems(
          Instant.now(),
          this.workbasketIds,
          this.states,
          this.classificationCategory,
          this.domains,
          this.classificationIds,
          this.excludedClassificationIds,
          this.customAttributeFilter,
          combinedClassificationFilter,
          determineGroupedBy(),
          timestamp,
          selectedItems,
          joinWithAttachments);
    } finally {
      this.taskanaEngine.returnConnection();
    }
  }

  @Override
  public List<String> listCustomAttributeValuesForCustomAttributeName(
      TaskCustomField taskCustomField) throws NotAuthorizedException {
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      return monitorMapper.getCustomAttributeValuesForReport(
          this.workbasketIds,
          this.states,
          this.classificationCategory,
          this.domains,
          this.classificationIds,
          this.excludedClassificationIds,
          this.customAttributeFilter,
          getCombinedClassificationFilter(),
          taskCustomField);
    } finally {
      this.taskanaEngine.returnConnection();
    }
  }

  @SuppressWarnings("checkstyle:MethodName")
  protected abstract B _this();

  protected abstract String determineGroupedBy();

  protected List<CombinedClassificationFilter> getCombinedClassificationFilter() {
    // we are currently aware that this is a code smell. Unfortunately the resolution of this would
    // cause havoc in our queries, since we do not have a concept for a user input validation yet.
    // As soon as that is done we can resolve this code smell.
    return null;
  }

  private List<SelectedItem> convertWorkingDaysToDays(
      List<SelectedItem> selectedItems, List<H> columnHeaders) throws InvalidArgumentException {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(columnHeaders, converter);
    return selectedItems.stream()
        .map(
            s ->
                new SelectedItem(
                    s.getKey(),
                    s.getSubKey(),
                    Collections.min(instance.convertWorkingDaysToDays(s.getLowerAgeLimit())),
                    Collections.max(instance.convertWorkingDaysToDays(s.getUpperAgeLimit()))))
        .collect(Collectors.toList());
  }

  private boolean subKeyIsSet(List<SelectedItem> selectedItems) {
    for (SelectedItem selectedItem : selectedItems) {
      if (selectedItem.getSubKey() != null && !selectedItem.getSubKey().isEmpty()) {
        return true;
      }
    }
    return false;
  }
}
