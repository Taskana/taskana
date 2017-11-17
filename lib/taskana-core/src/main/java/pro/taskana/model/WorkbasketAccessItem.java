package pro.taskana.model;

/**
 * WorkbasketAccessItem entity.
 */
public class WorkbasketAccessItem {

    private String id;
    private String workbasketId;
    private String userId;
    private String groupId;
    private boolean read;
    private boolean open;
    private boolean append;
    private boolean transfer;
    private boolean distribute;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String workbasketId) {
        this.workbasketId = workbasketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public boolean isDistribute() {
        return distribute;
    }

    public void setDistribute(boolean distribute) {
        this.distribute = distribute;
    }
}
