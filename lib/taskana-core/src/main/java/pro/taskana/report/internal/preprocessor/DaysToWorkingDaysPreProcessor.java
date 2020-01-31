package pro.taskana.report.internal.preprocessor;

import java.util.List;

import pro.taskana.common.api.exceptions.InvalidArgumentException;
import pro.taskana.report.api.structure.QueryItemPreprocessor;
import pro.taskana.report.internal.DaysToWorkingDaysConverter;
import pro.taskana.report.internal.header.TimeIntervalColumnHeader;
import pro.taskana.report.internal.item.AgeQueryItem;

/**
 * Uses {@link DaysToWorkingDaysConverter} to convert an &lt;I&gt;s age to working days.
 *
 * @param <I> QueryItem which is being processed
 */
public class DaysToWorkingDaysPreProcessor<I extends AgeQueryItem>
    implements QueryItemPreprocessor<I> {

  private DaysToWorkingDaysConverter instance;

  public DaysToWorkingDaysPreProcessor(
      List<? extends TimeIntervalColumnHeader> columnHeaders, boolean activate)
      throws InvalidArgumentException {
    if (activate) {
      instance = DaysToWorkingDaysConverter.initialize(columnHeaders);
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
