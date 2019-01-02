package pro.taskana.impl.report.item;

import pro.taskana.report.structure.QueryItem;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age in days.
 */
public interface DateQueryItem extends QueryItem {

    int getAgeInDays();

    void setAgeInDays(int ageInDays);

}
