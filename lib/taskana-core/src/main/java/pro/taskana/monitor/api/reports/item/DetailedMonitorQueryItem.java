package pro.taskana.monitor.api.reports.item;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * The DetailedMonitorQueryItem extends the {@linkplain MonitorQueryItem}. The additional attachment
 * key is used for the detailed classification report.
 */
@Getter
@Setter
@ToString(callSuper = false)
public class DetailedMonitorQueryItem extends MonitorQueryItem {

  private String attachmentKey;
}
