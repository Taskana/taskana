package pro.taskana.monitor.internal.reports;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.taskana.common.api.exceptions.NotAuthorizedException;
import pro.taskana.common.internal.InternalTaskanaEngine;
import pro.taskana.monitor.api.reports.TaskStatusReport;
import pro.taskana.monitor.api.reports.item.TaskQueryItem;
import pro.taskana.monitor.internal.MonitorMapper;
import pro.taskana.task.api.TaskState;
import pro.taskana.task.api.TaskanaRole;

/** The implementation of TaskStatusReportBuilder. */
public class TaskStatusReportBuilderImpl implements TaskStatusReport.Builder {

  private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusReportBuilderImpl.class);
  private InternalTaskanaEngine taskanaEngine;
  private MonitorMapper monitorMapper;
  private List<String> domains;
  private List<TaskState> states;

  public TaskStatusReportBuilderImpl(
      InternalTaskanaEngine taskanaEngine, MonitorMapper monitorMapper) {
    this.taskanaEngine = taskanaEngine;
    this.monitorMapper = monitorMapper;
  }

  @Override
  public TaskStatusReport buildReport() throws NotAuthorizedException {
    LOGGER.debug("entry to buildReport(), this = {}", this);
    this.taskanaEngine.getEngine().checkRoleMembership(TaskanaRole.MONITOR, TaskanaRole.ADMIN);
    try {
      this.taskanaEngine.openConnection();
      List<TaskQueryItem> tasks =
          this.monitorMapper.getTasksCountByState(this.domains, this.states);
      TaskStatusReport report = new TaskStatusReport(this.states);
      report.addItems(tasks);
      return report;
    } finally {
      this.taskanaEngine.returnConnection();
      LOGGER.debug("exit from buildReport().");
    }
  }

  @Override
  public TaskStatusReportBuilderImpl stateIn(List<TaskState> states) {
    this.states = states;
    return this;
  }

  @Override
  public TaskStatusReportBuilderImpl domainIn(List<String> domains) {
    this.domains = domains;
    return this;
  }
}
