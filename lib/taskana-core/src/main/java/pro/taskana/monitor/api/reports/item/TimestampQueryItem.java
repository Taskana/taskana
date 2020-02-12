package pro.taskana.monitor.api.reports.item;

import pro.taskana.monitor.api.reports.TimestampReport;
import pro.taskana.task.api.Timestamp;

/** The TimestampQueryItem contains the necessary information for the {@link TimestampReport}. */
public class TimestampQueryItem implements AgeQueryItem {

  private static final String N_A = "N/A";
  private int count;
  private Timestamp status;
  private int ageInDays;
  private String orgLevel1;
  private String orgLevel2;
  private String orgLevel3;
  private String orgLevel4;

  @Override
  public String getKey() {
    return status.name();
  }

  @Override
  public int getValue() {
    return count;
  }

  @Override
  public int getAgeInDays() {
    return ageInDays;
  }

  @Override
  public void setAgeInDays(int ageInDays) {
    this.ageInDays = ageInDays;
  }

  public String getOrgLevel1() {
    return orgLevel1 == null || orgLevel1.isEmpty() ? N_A : orgLevel1;
  }

  public String getOrgLevel2() {
    return orgLevel2 == null || orgLevel2.isEmpty() ? N_A : orgLevel2;
  }

  public String getOrgLevel3() {
    return orgLevel3 == null || orgLevel3.isEmpty() ? N_A : orgLevel3;
  }

  public String getOrgLevel4() {
    return orgLevel4 == null || orgLevel4.isEmpty() ? N_A : orgLevel4;
  }
}
