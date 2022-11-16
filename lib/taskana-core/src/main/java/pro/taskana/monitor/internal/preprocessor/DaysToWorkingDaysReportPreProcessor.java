package pro.taskana.monitor.internal.preprocessor;

import java.util.List;

import pro.taskana.common.api.WorkingTimeCalculator;
import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.monitor.api.reports.header.TimeIntervalColumnHeader;
import pro.taskana.monitor.api.reports.item.AgeQueryItem;
import pro.taskana.monitor.api.reports.item.QueryItemPreprocessor;

/**
 * Uses {@linkplain WorkingDaysToDaysReportConverter} to convert an &lt;I&gt;s age to working days.
 *
 * @param <I> QueryItem which is being processed
 */
public class DaysToWorkingDaysReportPreProcessor<I extends AgeQueryItem>
    implements QueryItemPreprocessor<I> {

  private WorkingDaysToDaysReportConverter instance;

  public DaysToWorkingDaysReportPreProcessor(
      List<? extends TimeIntervalColumnHeader> columnHeaders,
      WorkingTimeCalculator workingTimeCalculator,
      boolean activate)
      throws InvalidArgumentException {
    if (activate) {
      instance = WorkingDaysToDaysReportConverter.initialize(columnHeaders, workingTimeCalculator);
    }
  }

  @Override
  public I apply(I item) {
    if (instance != null) {
      item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
    }
    return item;
  }
}
