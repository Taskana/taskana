package pro.taskana.monitor.internal.reports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.LoggerUtils;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.common.internal.util.WorkingDaysToDaysConverter;
import pro.taskana.monitor.api.SelectedItem;
import pro.taskana.monitor.api.reports.ClassificationReport;
import pro.taskana.monitor.api.reports.TimeIntervalReportBuilder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.AgeQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.WorkingDaysToDaysReportConverter;
import pro.taskana.task.api.CustomField;
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

  private static final Logger LOGGER = LoggerFactory.getLogger(TimeIntervalReportBuilder.class);

  protected InternalTaskanaEngine taskanaEngine;
  protected MonitorMapper monitorMapper;
  protected List<H> columnHeaders;
  protected boolean inWorkingDays;
  protected List<String> workbasketIds;
  protected List<TaskState> states;
  protected List<String> categories;
  protected List<String> domains;
  protected List<String> classificationIds;
  protected List<String> excludedClassificationIds;
  protected Map<CustomField, String> customAttributeFilter;

  TimeIntervalReportBuilderImpl(InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    this.columnHeaders = Collections.emptyList();
    configureWorkingDaysToDaysConverter();
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
  public B categoryIn(List<String> categories) {
    this.categories = new ArrayList<>(categories);
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
  public B customAttributeFilterIn(Map<CustomField, String> customAttributeFilter) {
    this.customAttributeFilter = new HashMap<>(customAttributeFilter);
    return _this();
  }

  @Override
  public List<String> listTaskIdsForSelectedItems(List<SelectedItem> selectedItems)
      throws NotAuthorizedException, InvalidArgumentException {
    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug(
          "entry to listTaskIdsForSelectedItems(selectedItems = {}), this = {}",
          LoggerUtils.listToString(selectedItems),
          this);
    }

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
      if (this.inWorkingDays) {
        selectedItems = convertWorkingDaysToDays(selectedItems, this.columnHeaders);
      }
      return this.monitorMapper.getTaskIdsForSelectedItems(
          this.workbasketIds,
          this.states,
          this.categories,
          this.domains,
          this.classificationIds,
          this.excludedClassificationIds,
          this.customAttributeFilter,
          determineGroupedBy(),
          selectedItems,
          joinWithAttachments);
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from listTaskIdsForSelectedItems().");
    }
  }

  @Override
  public List<String> listCustomAttributeValuesForCustomAttributeName(CustomField customField)
      throws NotAuthorizedException {
    LOGGER.debug(
        "entry to listCustomAttributeValuesForCustomAttributeName(customField = {}), this = {}",
        customField,
        this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      return monitorMapper.getCustomAttributeValuesForReport(
          this.workbasketIds,
          this.states,
          this.categories,
          this.domains,
          this.classificationIds,
          this.excludedClassificationIds,
          this.customAttributeFilter,
          customField);
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from listCustomAttributeValuesForCustomAttributeName().");
    }
  }

  @SuppressWarnings("checkstyle:MethodName")
  protected abstract B _this();

  protected abstract String determineGroupedBy();

  private void configureWorkingDaysToDaysConverter() {
    WorkingDaysToDaysConverter.setCustomHolidays(
        this.taskanaEngine.getEngine().getConfiguration().getCustomHolidays());
    WorkingDaysToDaysConverter.setGermanPublicHolidaysEnabled(
        this.taskanaEngine.getEngine().getConfiguration().isGermanPublicHolidaysEnabled());
  }

  private List<SelectedItem> convertWorkingDaysToDays(
      List<SelectedItem> selectedItems, List<H> columnHeaders) throws InvalidArgumentException {
    WorkingDaysToDaysReportConverter instance =
        WorkingDaysToDaysReportConverter.initialize(columnHeaders);
    for (SelectedItem selectedItem : selectedItems) {
      selectedItem.setLowerAgeLimit(
          Collections.min(instance.convertWorkingDaysToDays(selectedItem.getLowerAgeLimit())));
      selectedItem.setUpperAgeLimit(
          Collections.max(instance.convertWorkingDaysToDays(selectedItem.getUpperAgeLimit())));
    }
    return selectedItems;
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
