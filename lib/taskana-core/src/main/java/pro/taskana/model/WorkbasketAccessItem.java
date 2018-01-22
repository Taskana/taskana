package pro.taskana.model;

import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * WorkbasketAccessItem entity.
 */
public class WorkbasketAccessItem {

    private String id;
    private String workbasketKey;
    private String accessId;
    private boolean permRead;
    private boolean permOpen;
    private boolean permAppend;
    private boolean permTransfer;
    private boolean permDistribute;
    private boolean permCustom1;
    private boolean permCustom2;
    private boolean permCustom3;
    private boolean permCustom4;
    private boolean permCustom5;
    private boolean permCustom6;
    private boolean permCustom7;
    private boolean permCustom8;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        if (TaskanaEngineConfiguration.shouldUseLowerCaseForAccessIds()) {
            this.accessId = accessId != null ? accessId.toLowerCase() : null;
        } else {
            this.accessId = accessId;
        }
    }

    public boolean isPermRead() {
        return permRead;
    }

    public void setPermRead(boolean permRead) {
        this.permRead = permRead;
    }

    public boolean isPermOpen() {
        return permOpen;
    }

    public void setPermOpen(boolean permOpen) {
        this.permOpen = permOpen;
    }

    public boolean isPermAppend() {
        return permAppend;
    }

    public void setPermAppend(boolean permAppend) {
        this.permAppend = permAppend;
    }

    public boolean isPermTransfer() {
        return permTransfer;
    }

    public void setPermTransfer(boolean permTransfer) {
        this.permTransfer = permTransfer;
    }

    public boolean isPermDistribute() {
        return permDistribute;
    }

    public void setPermDistribute(boolean permDistribute) {
        this.permDistribute = permDistribute;
    }

    public boolean isPermCustom1() {
        return permCustom1;
    }

    public void setPermCustom1(boolean permCustom1) {
        this.permCustom1 = permCustom1;
    }

    public boolean isPermCustom2() {
        return permCustom2;
    }

    public void setPermCustom2(boolean permCustom2) {
        this.permCustom2 = permCustom2;
    }

    public boolean isPermCustom3() {
        return permCustom3;
    }

    public void setPermCustom3(boolean permCustom3) {
        this.permCustom3 = permCustom3;
    }

    public boolean isPermCustom4() {
        return permCustom4;
    }

    public void setPermCustom4(boolean permCustom4) {
        this.permCustom4 = permCustom4;
    }

    public boolean isPermCustom5() {
        return permCustom5;
    }

    public void setPermCustom5(boolean permCustom5) {
        this.permCustom5 = permCustom5;
    }

    public boolean isPermCustom6() {
        return permCustom6;
    }

    public void setPermCustom6(boolean permCustom6) {
        this.permCustom6 = permCustom6;
    }

    public boolean isPermCustom7() {
        return permCustom7;
    }

    public void setPermCustom7(boolean permCustom7) {
        this.permCustom7 = permCustom7;
    }

    public boolean isPermCustom8() {
        return permCustom8;
    }

    public void setPermCustom8(boolean permCustom8) {
        this.permCustom8 = permCustom8;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketAccessItem [id=");
        builder.append(id);
        builder.append(", workbasketKey=");
        builder.append(workbasketKey);
        builder.append(", accessId=");
        builder.append(accessId);
        builder.append(", permRead=");
        builder.append(permRead);
        builder.append(", permOpen=");
        builder.append(permOpen);
        builder.append(", permAppend=");
        builder.append(permAppend);
        builder.append(", permTransfer=");
        builder.append(permTransfer);
        builder.append(", permDistribute=");
        builder.append(permDistribute);
        builder.append(", permCustom1=");
        builder.append(permCustom1);
        builder.append(", permCustom2=");
        builder.append(permCustom2);
        builder.append(", permCustom3=");
        builder.append(permCustom3);
        builder.append(", permCustom4=");
        builder.append(permCustom4);
        builder.append(", permCustom5=");
        builder.append(permCustom5);
        builder.append(", permCustom6=");
        builder.append(permCustom6);
        builder.append(", permCustom7=");
        builder.append(permCustom7);
        builder.append(", permCustom8=");
        builder.append(permCustom8);
        builder.append("]");
        return builder.toString();
    }
}
