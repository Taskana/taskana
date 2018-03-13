package pro.taskana.impl.report;

public interface ReportColumnHeader<Item extends QueryItem> {

    String displayName();

    boolean fits(Item item);

}