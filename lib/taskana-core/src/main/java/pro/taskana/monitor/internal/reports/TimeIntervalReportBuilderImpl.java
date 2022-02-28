package pro.taskana.monitor.internal.reports;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.WorkingDaysToDaysConverter;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
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
 * Implementation of {@linkplain TimeIntervalReportBuilder}.
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
  protected String[] workbasketIds;
  protected TaskState[] states;
  protected String[] classificationCategories;
  protected String[] domains;
  protected String[] classificationIds;
  protected String[] excludedClassificationIds;
  protected WorkingDaysToDaysConverter converter;
  private String[] custom1In;
  private String[] custom1NotIn;
  private String[] custom1Like;
  private String[] custom2In;
  private String[] custom2NotIn;
  private String[] custom2Like;
  private String[] custom3In;
  private String[] custom3NotIn;
  private String[] custom3Like;
  private String[] custom4In;
  private String[] custom4NotIn;
  private String[] custom4Like;
  private String[] custom5In;
  private String[] custom5NotIn;
  private String[] custom5Like;
  private String[] custom6In;
  private String[] custom6NotIn;
  private String[] custom6Like;
  private String[] custom7In;
  private String[] custom7NotIn;
  private String[] custom7Like;
  private String[] custom8In;
  private String[] custom8NotIn;
  private String[] custom8Like;
  private String[] custom9In;
  private String[] custom9NotIn;
  private String[] custom9Like;
  private String[] custom10In;
  private String[] custom10NotIn;
  private String[] custom10Like;
  private String[] custom11In;
  private String[] custom11NotIn;
  private String[] custom11Like;
  private String[] custom12In;
  private String[] custom12NotIn;
  private String[] custom12Like;
  private String[] custom13In;
  private String[] custom13NotIn;
  private String[] custom13Like;
  private String[] custom14In;
  private String[] custom14NotIn;
  private String[] custom14Like;
  private String[] custom15In;
  private String[] custom15NotIn;
  private String[] custom15Like;
  private String[] custom16In;
  private String[] custom16NotIn;
  private String[] custom16Like;

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
    if (workbasketIds != null) {
      this.workbasketIds = workbasketIds.toArray(new String[0]);
    }
    return _this();
  }

  @Override
  public B stateIn(List<TaskState> states) {
    if (states != null) {
      this.states = states.toArray(new TaskState[0]);
    }
    return _this();
  }

  @Override
  public B classificationCategoryIn(List<String> classificationCategories) {
    if (classificationCategories != null) {
      this.classificationCategories = classificationCategories.toArray(new String[0]);
    }
    return _this();
  }

  @Override
  public B classificationIdIn(List<String> classificationIds) {
    if (classificationIds != null) {
      this.classificationIds = classificationIds.toArray(new String[0]);
    }
    return _this();
  }

  @Override
  public B excludedClassificationIdIn(List<String> excludedClassificationIds) {
    if (excludedClassificationIds != null) {
      this.excludedClassificationIds = excludedClassificationIds.toArray(new String[0]);
    }
    return _this();
  }

  @Override
  public B domainIn(List<String> domains) {
    if (domains != null) {
      this.domains = domains.toArray(new String[0]);
    }
    return _this();
  }

  @Override
  public B customAttributeIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }
    switch (customField) {
      case CUSTOM_1:
        this.custom1In = strings;
        break;
      case CUSTOM_2:
        this.custom2In = strings;
        break;
      case CUSTOM_3:
        this.custom3In = strings;
        break;
      case CUSTOM_4:
        this.custom4In = strings;
        break;
      case CUSTOM_5:
        this.custom5In = strings;
        break;
      case CUSTOM_6:
        this.custom6In = strings;
        break;
      case CUSTOM_7:
        this.custom7In = strings;
        break;
      case CUSTOM_8:
        this.custom8In = strings;
        break;
      case CUSTOM_9:
        this.custom9In = strings;
        break;
      case CUSTOM_10:
        this.custom10In = strings;
        break;
      case CUSTOM_11:
        this.custom11In = strings;
        break;
      case CUSTOM_12:
        this.custom12In = strings;
        break;
      case CUSTOM_13:
        this.custom13In = strings;
        break;
      case CUSTOM_14:
        this.custom14In = strings;
        break;
      case CUSTOM_15:
        this.custom15In = strings;
        break;
      case CUSTOM_16:
        this.custom16In = strings;
        break;
      default:
        throw new SystemException("Unknown custom attribute '" + customField + "'");
    }

    return _this();
  }

  @Override
  public B customAttributeNotIn(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }
    switch (customField) {
      case CUSTOM_1:
        this.custom1NotIn = strings;
        break;
      case CUSTOM_2:
        this.custom2NotIn = strings;
        break;
      case CUSTOM_3:
        this.custom3NotIn = strings;
        break;
      case CUSTOM_4:
        this.custom4NotIn = strings;
        break;
      case CUSTOM_5:
        this.custom5NotIn = strings;
        break;
      case CUSTOM_6:
        this.custom6NotIn = strings;
        break;
      case CUSTOM_7:
        this.custom7NotIn = strings;
        break;
      case CUSTOM_8:
        this.custom8NotIn = strings;
        break;
      case CUSTOM_9:
        this.custom9NotIn = strings;
        break;
      case CUSTOM_10:
        this.custom10NotIn = strings;
        break;
      case CUSTOM_11:
        this.custom11NotIn = strings;
        break;
      case CUSTOM_12:
        this.custom12NotIn = strings;
        break;
      case CUSTOM_13:
        this.custom13NotIn = strings;
        break;
      case CUSTOM_14:
        this.custom14NotIn = strings;
        break;
      case CUSTOM_15:
        this.custom15NotIn = strings;
        break;
      case CUSTOM_16:
        this.custom16NotIn = strings;
        break;
      default:
        throw new SystemException("Unknown custom attribute '" + customField + "'");
    }

    return _this();
  }

  @Override
  public B customAttributeLike(TaskCustomField customField, String... strings)
      throws InvalidArgumentException {
    if (strings.length == 0) {
      throw new InvalidArgumentException(
          "At least one string has to be provided as a search parameter");
    }

    switch (customField) {
      case CUSTOM_1:
        this.custom1Like = toLowerCopy(strings);
        break;
      case CUSTOM_2:
        this.custom2Like = toLowerCopy(strings);
        break;
      case CUSTOM_3:
        this.custom3Like = toLowerCopy(strings);
        break;
      case CUSTOM_4:
        this.custom4Like = toLowerCopy(strings);
        break;
      case CUSTOM_5:
        this.custom5Like = toLowerCopy(strings);
        break;
      case CUSTOM_6:
        this.custom6Like = toLowerCopy(strings);
        break;
      case CUSTOM_7:
        this.custom7Like = toLowerCopy(strings);
        break;
      case CUSTOM_8:
        this.custom8Like = toLowerCopy(strings);
        break;
      case CUSTOM_9:
        this.custom9Like = toLowerCopy(strings);
        break;
      case CUSTOM_10:
        this.custom10Like = toLowerCopy(strings);
        break;
      case CUSTOM_11:
        this.custom11Like = toLowerCopy(strings);
        break;
      case CUSTOM_12:
        this.custom12Like = toLowerCopy(strings);
        break;
      case CUSTOM_13:
        this.custom13Like = toLowerCopy(strings);
        break;
      case CUSTOM_14:
        this.custom14Like = toLowerCopy(strings);
        break;
      case CUSTOM_15:
        this.custom15Like = toLowerCopy(strings);
        break;
      case CUSTOM_16:
        this.custom16Like = toLowerCopy(strings);
        break;
      default:
        throw new SystemException("Unknown custom field '" + customField + "'");
    }

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
          this,
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
          this, getCombinedClassificationFilter(), taskCustomField);
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

  private String[] toLowerCopy(String... source) {
    if (source == null || source.length == 0) {
      // we are currently aware that this is a code smell. Unfortunately the resolution of this
      // would cause havoc in our queries, since we do not have a concept
      // for a user input validation yet. As soon as that is done we can resolve this code smell.
      return null;
    } else {
      String[] target = new String[source.length];
      for (int i = 0; i < source.length; i++) {
        target[i] = source[i].toLowerCase();
      }
      return target;
    }
  }
}
