package pro.taskana.report.internal;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.CustomField;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.report.api.CustomFieldValueReport;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.report.internal.item.MonitorQueryItem;
import pro.taskana.report.internal.preprocessor.DaysToWorkingDaysPreProcessor;
import pro.taskana.task.api.TaskanaRole;

/** The implementation of CustomFieldValueReportBuilder. */
public class CustomFieldValueReportBuilderImpl
    extends TimeIntervalReportBuilderImpl<
        CustomFieldValueReport.Builder, MonitorQueryItem, TimeIntervalColumnHeader>
    implements CustomFieldValueReport.Builder {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(CustomFieldValueReportBuilderImpl.class);

  private CustomField customField;

  CustomFieldValueReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine,
      TaskMonitorMapper taskMonitorMapper,
      CustomField customField) {
    super(taskanaEngine, taskMonitorMapper);
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
          this.taskMonitorMapper.getTaskCountOfCustomFieldValues(
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
          new DaysToWorkingDaysPreProcessor<>(this.columnHeaders, this.inWorkingDays));
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
