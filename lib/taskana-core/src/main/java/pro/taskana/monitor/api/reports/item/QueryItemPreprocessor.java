package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.Report;

/**
 * The QueryItemPreprocessor is used when adding {@linkplain QueryItem QueryItems} into a
 * {@linkplain Report}.
 *
 * <p>It defines a processing step which is executed on each {@linkplain QueryItem} before inserting
 * it into the {@linkplain Report}.
 *
 * @param <I> Item class which is being pre processed
 */
@FunctionalInterface
public interface QueryItemPreprocessor<I extends QueryItem> {

  I apply(I item);
}
