package pro.taskana.impl.report.preprocessor;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.DaysToWorkingDaysConverter;
import pro.taskana.impl.report.header.TimeIntervalColumnHeader;
import pro.taskana.impl.report.item.DateQueryItem;
import pro.taskana.report.structure.QueryItemPreprocessor;

/**
 * Uses {@link DaysToWorkingDaysConverter} to convert an &lt;I&gt;s age to working days.
 * @param <I> QueryItem which is being processed
 */
public class DaysToWorkingDaysPreProcessor<I extends DateQueryItem> implements QueryItemPreprocessor<I> {

    private DaysToWorkingDaysConverter instance;

    public DaysToWorkingDaysPreProcessor(List<? extends TimeIntervalColumnHeader> columnHeaders, boolean activate)
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
