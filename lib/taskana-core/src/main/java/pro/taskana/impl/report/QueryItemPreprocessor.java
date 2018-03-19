package pro.taskana.impl.report;

/**
 * The QueryItemPreprocessor is used when adding {@link QueryItem}s into a {@link Report}. It defines a processing
 * step which is executed on each {@link QueryItem} before inserting it into the {@link Report}.
 * @param <Item> Item class which is being pre processed.
 */
@FunctionalInterface
public interface QueryItemPreprocessor<Item extends QueryItem> {

    Item apply(Item item);

}
