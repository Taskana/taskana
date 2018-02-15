package pro.taskana.impl;

/**
 * The DetailedMonitorQueryItem extends the MonitorQueryItem. The additional attachment key is used for the detailed
 * classification report.
 */
public class DetailedMonitorQueryItem extends MonitorQueryItem {

    private String attachmentKey;

    public DetailedMonitorQueryItem() {
        super();
    }

    public String getAttachmentKey() {
        return attachmentKey;
    }

    public void setAttachmentKey(String attachmentKey) {
        this.attachmentKey = attachmentKey;
    }

}
