package pro.taskana.impl.report;

/**
 * A ReportColumnHeader is an element of a {@link Report}.
 * It determines weather a given &lt;Item&gt; belongs into the representing column.
 *
 * @param <Item> {@link QueryItem} on which the {@link Report} is based on.
 */
public interface ReportColumnHeader<Item extends QueryItem> {

    String getDisplayName();

    boolean fits(Item item);

}
