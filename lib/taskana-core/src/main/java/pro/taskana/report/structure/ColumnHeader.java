package pro.taskana.report.structure;

/**
 * A ColumnHeader is an element of a {@link Report}.
 * It determines weather a given &lt;Item&gt; belongs into the representing column.
 *
 * @param <I> {@link QueryItem} on which the {@link Report} is based on.
 */
public interface ColumnHeader<I extends QueryItem> {

    /**
     * The display name is the string representation of this column.
     * Used to give this column a name during presentation.
     * @return String representation of this column.
     */
    String getDisplayName();

    /**
     * Determines if a specific item is meant part of this column.
     *
     * @param item the given item to check.
     *
     * @return True, if the item is supposed to be part of this column. Otherwise false.
     */
    boolean fits(I item);

}
