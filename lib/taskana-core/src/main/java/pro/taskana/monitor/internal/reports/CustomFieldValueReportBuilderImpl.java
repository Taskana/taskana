package pro.taskana.monitor.internal.reports;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.TaskanaRole;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.CustomFieldValueReport;
import pro.taskana.monitor.api.reports.CustomFieldValueReport.Builder;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.MonitorQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.monitor.internal.preprocessor.DaysToWorkingDaysReportPreProcessor;
import pro.taskana.task.api.CustomField;

/** The implementation of CustomFieldValueReportBuilder. */
public class CustomFieldValueReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CustomFieldValueReport.Builder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomFieldValueReportBuilderImpl.class);

  private CustomField customField;

  public CustomFieldValueReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper, CustomField customField) {
    super(taskanaEngine, monitorMapper);
    this.customField = customField;
  }

  @Override
  public CustomFieldValueReport buildReport()
      throws InvalidArgumentException, NotAuthorizedException {
    LOGGER.debug("entry to buildReport(customField = {}), this = {}", this.customField, this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR);
    try {
      this.taskanaEngine.openConnection();
      CustomFieldValueReport report = new CustomFieldValueReport(this.columnHeaders);
      List<MonitorQueryItem> monitorQueryItems =
          this.monitorMapper.getTaskCountOfCustomFieldValues(
              this.customField,
              this.workbasketIds,
              this.states,
              this.categories,
              this.domains,
              this.classificationIds,
              this.excludedClassificationIds,
              this.customAttributeFilter);

      report.addItems(
          monitorQueryItems,
          new DaysToWorkingDaysReportPreProcessor<>(this.columnHeaders, this.inWorkingDays));
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
    }
  }

  @Override
  protected CustomFieldValueReport.Builder _this() {
    return this;
  }

  @Override
  protected String determineGroupedBy() {
    return customField.name();
  }
}
