package pro.taskana.impl.report.impl;

import java.util.List;

import pro.taskana.exceptions.InvalidArgumentException;
import pro.taskana.impl.DaysToWorkingDaysConverter;
import pro.taskana.impl.report.QueryItemPreprocessor;

/**
 * Uses {@link DaysToWorkingDaysConverter} to convert an &lt;Item&gt;s age to working days.
 * @param <Item> QueryItem which is being processed
 */
public class DaysToWorkingDaysPreProcessor<Item extends MonitorQueryItem> implements QueryItemPreprocessor<Item> {

    private DaysToWorkingDaysConverter instance;

    public DaysToWorkingDaysPreProcessor(List<TimeIntervalColumnHeader> columnHeaders, boolean activate)
        throws InvalidArgumentException {
        if (activate) {
            instance = DaysToWorkingDaysConverter.initialize(columnHeaders);
        }
    }

    @Override
    public Item apply(Item item) {
        if (instance != null) {
            item.setAgeInDays(instance.convertDaysToWorkingDays(item.getAgeInDays()));
        }
        return item;
    }
}
