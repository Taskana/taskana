package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;

/**
 * The QueryItemPreprocessor is used when adding {@linkplain QueryItem QueryItems} into a
 * {@linkplain Report}. It defines a processing step which is executed on each {@linkplain
 * QueryItem} before inserting it into the {@linkplain Report}.
 *
 * @param <I> the Item class which is being preprocessed
 */
@FunctionalInterface
public interface QueryItemPreprocessor<I extends QueryItem> {

  I apply(I item);
}
