package pro.taskana.impl;

import pro.taskana.WorkbasketAccessItem;
import pro.taskana.configuration.TaskanaEngineConfiguration;

/**
 * WorkbasketAccessItem Entity.
 */
public class WorkbasketAccessItemImpl implements WorkbasketAccessItem {

    private String id;
    private String workbasketId;
    private String accessId;
    private String accessName;
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
    private boolean permCustom9;
    private boolean permCustom10;
    private boolean permCustom11;
    private boolean permCustom12;

    WorkbasketAccessItemImpl() {
        super();
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#getId()
     */
    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#getWorkbasketId()
     */
    @Override
    public String getWorkbasketId() {
        return workbasketId;
    }

    public void setWorkbasketId(String workbasketId) {
        this.workbasketId = workbasketId;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#getAccessId()
     */
    @Override
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

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#getAccessName()
     */
    @Override
    public String getAccessName() {
        return accessName;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setAccessName()
     */
    @Override
    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermRead()
     */
    @Override
    public boolean isPermRead() {
        return permRead;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermRead(boolean)
     */
    @Override
    public void setPermRead(boolean permRead) {
        this.permRead = permRead;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermOpen()
     */
    @Override
    public boolean isPermOpen() {
        return permOpen;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermOpen(boolean)
     */
    @Override
    public void setPermOpen(boolean permOpen) {
        this.permOpen = permOpen;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermAppend()
     */
    @Override
    public boolean isPermAppend() {
        return permAppend;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermAppend(boolean)
     */
    @Override
    public void setPermAppend(boolean permAppend) {
        this.permAppend = permAppend;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermTransfer()
     */
    @Override
    public boolean isPermTransfer() {
        return permTransfer;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermTransfer(boolean)
     */
    @Override
    public void setPermTransfer(boolean permTransfer) {
        this.permTransfer = permTransfer;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermDistribute()
     */
    @Override
    public boolean isPermDistribute() {
        return permDistribute;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermDistribute(boolean)
     */
    @Override
    public void setPermDistribute(boolean permDistribute) {
        this.permDistribute = permDistribute;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom1()
     */
    @Override
    public boolean isPermCustom1() {
        return permCustom1;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom1(boolean)
     */
    @Override
    public void setPermCustom1(boolean permCustom1) {
        this.permCustom1 = permCustom1;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom2()
     */
    @Override
    public boolean isPermCustom2() {
        return permCustom2;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom2(boolean)
     */
    @Override
    public void setPermCustom2(boolean permCustom2) {
        this.permCustom2 = permCustom2;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom3()
     */
    @Override
    public boolean isPermCustom3() {
        return permCustom3;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom3(boolean)
     */
    @Override
    public void setPermCustom3(boolean permCustom3) {
        this.permCustom3 = permCustom3;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom4()
     */
    @Override
    public boolean isPermCustom4() {
        return permCustom4;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom4(boolean)
     */
    @Override
    public void setPermCustom4(boolean permCustom4) {
        this.permCustom4 = permCustom4;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom5()
     */
    @Override
    public boolean isPermCustom5() {
        return permCustom5;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom5(boolean)
     */
    @Override
    public void setPermCustom5(boolean permCustom5) {
        this.permCustom5 = permCustom5;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom6()
     */
    @Override
    public boolean isPermCustom6() {
        return permCustom6;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom6(boolean)
     */
    @Override
    public void setPermCustom6(boolean permCustom6) {
        this.permCustom6 = permCustom6;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom7()
     */
    @Override
    public boolean isPermCustom7() {
        return permCustom7;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom7(boolean)
     */
    @Override
    public void setPermCustom7(boolean permCustom7) {
        this.permCustom7 = permCustom7;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom8()
     */
    @Override
    public boolean isPermCustom8() {
        return permCustom8;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom8(boolean)
     */
    @Override
    public void setPermCustom8(boolean permCustom8) {
        this.permCustom8 = permCustom8;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom9()
     */
    @Override
    public boolean isPermCustom9() {
        return permCustom9;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom9(boolean)
     */
    @Override
    public void setPermCustom9(boolean permCustom9) {
        this.permCustom9 = permCustom9;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom10()
     */
    @Override
    public boolean isPermCustom10() {
        return permCustom10;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom10(boolean)
     */
    @Override
    public void setPermCustom10(boolean permCustom10) {
        this.permCustom10 = permCustom10;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom11()
     */
    @Override
    public boolean isPermCustom11() {
        return permCustom11;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom11(boolean)
     */
    @Override
    public void setPermCustom11(boolean permCustom11) {
        this.permCustom11 = permCustom11;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#isPermCustom12()
     */
    @Override
    public boolean isPermCustom12() {
        return permCustom12;
    }

    /*
     * (non-Javadoc)
     * @see pro.taskana.impl.WorkbasketAccessItem#setPermCustom12(boolean)
     */
    @Override
    public void setPermCustom12(boolean permCustom12) {
        this.permCustom12 = permCustom12;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WorkbasketAccessItem [id=");
        builder.append(id);
        builder.append(", workbasketId=");
        builder.append(workbasketId);
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
        builder.append(", permCustom9=");
        builder.append(permCustom9);
        builder.append(", permCustom10=");
        builder.append(permCustom10);
        builder.append(", permCustom11=");
        builder.append(permCustom11);
        builder.append(", permCustom12=");
        builder.append(permCustom12);
        builder.append("]");
        return builder.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessId == null) ? 0 : accessId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + (permAppend ? 1231 : 1237);
        result = prime * result + (permCustom1 ? 1231 : 1237);
        result = prime * result + (permCustom10 ? 1231 : 1237);
        result = prime * result + (permCustom11 ? 1231 : 1237);
        result = prime * result + (permCustom12 ? 1231 : 1237);
        result = prime * result + (permCustom2 ? 1231 : 1237);
        result = prime * result + (permCustom3 ? 1231 : 1237);
        result = prime * result + (permCustom4 ? 1231 : 1237);
        result = prime * result + (permCustom5 ? 1231 : 1237);
        result = prime * result + (permCustom6 ? 1231 : 1237);
        result = prime * result + (permCustom7 ? 1231 : 1237);
        result = prime * result + (permCustom8 ? 1231 : 1237);
        result = prime * result + (permCustom9 ? 1231 : 1237);
        result = prime * result + (permDistribute ? 1231 : 1237);
        result = prime * result + (permOpen ? 1231 : 1237);
        result = prime * result + (permRead ? 1231 : 1237);
        result = prime * result + (permTransfer ? 1231 : 1237);
        result = prime * result + ((workbasketId == null) ? 0 : workbasketId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        WorkbasketAccessItemImpl other = (WorkbasketAccessItemImpl) obj;
        if (accessId == null) {
            if (other.accessId != null) {
                return false;
            }
        } else if (!accessId.equals(other.accessId)) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (permAppend != other.permAppend) {
            return false;
        }
        if (permCustom1 != other.permCustom1) {
            return false;
        }
        if (permCustom10 != other.permCustom10) {
            return false;
        }
        if (permCustom11 != other.permCustom11) {
            return false;
        }
        if (permCustom12 != other.permCustom12) {
            return false;
        }
        if (permCustom2 != other.permCustom2) {
            return false;
        }
        if (permCustom3 != other.permCustom3) {
            return false;
        }
        if (permCustom4 != other.permCustom4) {
            return false;
        }
        if (permCustom5 != other.permCustom5) {
            return false;
        }
        if (permCustom6 != other.permCustom6) {
            return false;
        }
        if (permCustom7 != other.permCustom7) {
            return false;
        }
        if (permCustom8 != other.permCustom8) {
            return false;
        }
        if (permCustom9 != other.permCustom9) {
            return false;
        }
        if (permDistribute != other.permDistribute) {
            return false;
        }
        if (permOpen != other.permOpen) {
            return false;
        }
        if (permRead != other.permRead) {
            return false;
        }
        if (permTransfer != other.permTransfer) {
            return false;
        }
        if (workbasketId == null) {
            if (other.workbasketId != null) {
                return false;
            }
        } else if (!workbasketId.equals(other.workbasketId)) {
            return false;
        }
        return true;
    }

}
