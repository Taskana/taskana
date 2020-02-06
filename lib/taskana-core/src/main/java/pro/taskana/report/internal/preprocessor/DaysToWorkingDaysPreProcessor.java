package pro.taskana.report.internal.preprocessor;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.common.internal.util.DaysToWorkingDaysConverter;
import pro.taskana.report.api.header.TimeIntervalColumnHeader;
import pro.taskana.report.api.item.AgeQueryItem;
import pro.taskana.report.api.structure.QueryItemPreprocessor;
import pro.taskana.report.internal.DaysToWorkingDaysReportConverter;

/**
 * Uses {@link DaysToWorkingDaysConverter} to convert an &lt;I&gt;s age to working days.
 *
 * @param <I> QueryItem which is being processed
 */
public class DaysToWorkingDaysPreProcessor<I extends AgeQueryItem>
    implements QueryItemPreprocessor<I> {

  private DaysToWorkingDaysReportConverter instance;

  public DaysToWorkingDaysPreProcessor(
      List<? extends TimeIntervalColumnHeader> columnHeaders, boolean activate)
      throws InvalidArgumentException {
    if (activate) {
      instance = DaysToWorkingDaysReportConverter.initialize(columnHeaders);
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
