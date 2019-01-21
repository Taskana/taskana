package pro.taskana.impl.report;

/**
 * The DetailedMonitorQueryItem extends the {@link MonitorQueryItem}.
 * The additional attachment key is used for the detailed classification report.
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
        return "DetailedMonitorQueryItem [" +
            "attachmentKey= " + this.attachmentKey +
            "]";
    }

}
