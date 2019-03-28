package pro.taskana.impl.report.item;

import pro.taskana.impl.report.structure.QueryItem;

/**
 * The MonitorQueryItem entity contains the number of tasks for a key (e.g. workbasketKey) and age in days.
 */
public interface AgeQueryItem extends QueryItem {

    int getAgeInDays();

    void setAgeInDays(int ageInDays);

}
