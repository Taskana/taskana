package pro.taskana.monitor.internal.reports;

import java.util.Collections;
import java.util.List;
import pro.taskana.common.api.IntInterval;
import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport;
import pro.taskana.monitor.api.reports.WorkbasketPriorityReport.Builder;
import pro.taskana.monitor.api.reports.header.PriorityColumnHeader;
import pro.taskana.monitor.api.reports.item.PriorityQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.task.api.TaskCustomField;
import pro.taskana.task.api.TaskCustomIntField;
import pro.taskana.task.api.TaskState;
import pro.taskana.workbasket.api.WorkbasketType;

import static pro.taskana.common.api.BaseQuery.toLowerCopy;

public class WorkbasketPriorityReportBuilderImpl implements WorkbasketPriorityReport.Builder {

  private final InternalTaskanaEngine taskanaEngine;
  private final MonitorMapper monitorMapper;
  protected List<PriorityColumnHeader> columnHeaders;
  protected boolean inWorkingDays;
  protected String[] workbasketIds;
  protected TaskState[] states;
  protected String[] classificationCategories;
  protected String[] domains;
  protected String[] classificationIds;
  protected String[] excludedClassificationIds;
  private WorkbasketType[] workbasketTypes;
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
  private Integer[] customInt1In;
  private Integer[] customInt1NotIn;
  private Integer[] customInt2In;
  private Integer[] customInt2NotIn;
  private Integer[] customInt3In;
  private Integer[] customInt3NotIn;
  private Integer[] customInt4In;
  private Integer[] customInt4NotIn;
  private Integer[] customInt5In;
  private Integer[] customInt5NotIn;
  private Integer[] customInt6In;
  private Integer[] customInt6NotIn;
  private Integer[] customInt7In;
  private Integer[] customInt7NotIn;
  private Integer[] customInt8In;
  private Integer[] customInt8NotIn;

  private IntInterval[] customInt1Within;
  private IntInterval[] customInt1NotWithin;
  private IntInterval[] customInt2Within;
  private IntInterval[] customInt2NotWithin;
  private IntInterval[] customInt3Within;
  private IntInterval[] customInt3NotWithin;
  private IntInterval[] customInt4Within;
  private IntInterval[] customInt4NotWithin;
  private IntInterval[] customInt5Within;
  private IntInterval[] customInt5NotWithin;
  private IntInterval[] customInt6Within;
  private IntInterval[] customInt6NotWithin;
  private IntInterval[] customInt7Within;
  private IntInterval[] customInt7NotWithin;
  private IntInterval[] customInt8Within;
  private IntInterval[] customInt8NotWithin;

  @SuppressWarnings("unused")
  public WorkbasketPriorityReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
    columnHeaders = Collections.emptyList();
  }

  @Override
  public WorkbasketPriorityReport buildReport() throws NotAuthorizedException {
    taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);

    WorkbasketPriorityReport report = new WorkbasketPriorityReport(columnHeaders);
    List<PriorityQueryItem> items =
        taskanaEngine.executeInDatabaseConnection(() -> monitorMapper.getTaskCountByPriority(this));
    report.addItems(items);
    return report;
  }

  @Override
  public Builder workbasketTypeIn(WorkbasketType... workbasketTypes) {
    this.workbasketTypes = workbasketTypes;
    return this;
  }

  @Override
  public Builder withColumnHeaders(List<PriorityColumnHeader> columnHeaders) {
    this.columnHeaders = columnHeaders;
    return this;
  }

  @Override
  public Builder inWorkingDays() {
    this.inWorkingDays = true;
    return this;
  }

  @Override
  public Builder workbasketIdIn(List<String> workbasketIds) {
    if (workbasketIds != null) {
      this.workbasketIds = workbasketIds.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder stateIn(List<TaskState> states) {
    if (states != null) {
      this.states = states.toArray(new TaskState[0]);
    }
    return this;
  }

  @Override
  public Builder classificationCategoryIn(List<String> classificationCategories) {
    if (classificationCategories != null) {
      this.classificationCategories = classificationCategories.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder classificationIdIn(List<String> classificationIds) {
    if (classificationIds != null) {
      this.classificationIds = classificationIds.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder excludedClassificationIdIn(List<String> excludedClassificationIds) {
    if (excludedClassificationIds != null) {
      this.excludedClassificationIds = excludedClassificationIds.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder domainIn(List<String> domains) {
    if (domains != null) {
      this.domains = domains.toArray(new String[0]);
    }
    return this;
  }

  @Override
  public Builder customAttributeIn(TaskCustomField customField, String... strings)
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

    return this;
  }

  @Override
  public Builder customAttributeNotIn(TaskCustomField customField, String... strings)
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

    return this;
  }

  @Override
  public Builder customIntAttributeIn(TaskCustomIntField customIntField, Integer... values)
      throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one Integer has to be provided as a search parameter");
    }
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1In = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2In = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3In = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4In = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5In = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6In = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7In = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8In = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }

    return this;
  }

  @Override
  public Builder customIntAttributeNotIn(TaskCustomIntField customIntField, Integer... values)
      throws InvalidArgumentException {
    if (values.length == 0) {
      throw new InvalidArgumentException(
          "At least one Integer has to be provided as a search parameter");
    }
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1NotIn = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2NotIn = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3NotIn = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4NotIn = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5NotIn = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6NotIn = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7NotIn = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8NotIn = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }

    return this;
  }

  @Override
  public Builder customIntAttributeWithin(
      TaskCustomIntField customIntField, IntInterval... values) {
    for (IntInterval i : values) {
      if (!i.isValid()) {
        throw new IllegalArgumentException("IntInterval " + i + " is invalid.");
      }
    }
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1Within = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2Within = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3Within = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4Within = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5Within = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6Within = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7Within = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8Within = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }
    return this;
  }

  @Override
  public Builder customIntAttributeNotWithin(
      TaskCustomIntField customIntField, IntInterval... values) {
    for (IntInterval i : values) {
      if (!i.isValid()) {
        throw new IllegalArgumentException("IntInterval " + i + " is invalid.");
      }
    }
    switch (customIntField) {
      case CUSTOM_INT_1:
        this.customInt1NotWithin = values;
        break;
      case CUSTOM_INT_2:
        this.customInt2NotWithin = values;
        break;
      case CUSTOM_INT_3:
        this.customInt3NotWithin = values;
        break;
      case CUSTOM_INT_4:
        this.customInt4NotWithin = values;
        break;
      case CUSTOM_INT_5:
        this.customInt5NotWithin = values;
        break;
      case CUSTOM_INT_6:
        this.customInt6NotWithin = values;
        break;
      case CUSTOM_INT_7:
        this.customInt7NotWithin = values;
        break;
      case CUSTOM_INT_8:
        this.customInt8NotWithin = values;
        break;
      default:
        throw new SystemException("Unknown custom int attribute '" + customIntField + "'");
    }
    return this;
  }

  @Override
  public Builder customAttributeLike(TaskCustomField customField, String... strings)
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

    return this;
  }
}
