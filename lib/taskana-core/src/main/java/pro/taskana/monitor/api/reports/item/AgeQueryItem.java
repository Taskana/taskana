package pro.taskana.monitor.api.reports.item;

/** The AgeQueryItem contains age in days. */
public interface AgeQueryItem extends QueryItem {

  int getAgeInDays();

  void setAgeInDays(int ageInDays);
}
