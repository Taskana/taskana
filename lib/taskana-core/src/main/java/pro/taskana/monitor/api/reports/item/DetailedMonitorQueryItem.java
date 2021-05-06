package pro.taskana.monitor.api.reports.item;

/**
 * The DetailedMonitorQueryItem extends the {@linkplain MonitorQueryItem}.
 *
 * <p>The additional attachmentKey is used for the {@linkplain
 * pro.taskana.monitor.api.reports.ClassificationReport.DetailedClassificationReport
 * DetailedClassificationReport}.
 */
public class DetailedMonitorQueryItem extends MonitorQueryItem {

  private String attachmentKey;

  public String getAttachmentKey() {
    return attachmentKey;
  }

  public void setAttachmentKey(String attachmentKey) {
    this.attachmentKey = attachmentKey;
  }

  @Override
  public String toString() {
    return "DetailedMonitorQueryItem [" + "attachmentKey= " + this.attachmentKey + "]";
  }
}
