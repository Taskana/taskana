package pro.taskana.impl;

import pro.taskana.WorkbasketAccessItemExtended;

/**
 * WorkbasketAccessItemExtendedImpl Entity.
 */
public class WorkbasketAccessItemExtendedImpl extends WorkbasketAccessItemImpl implements WorkbasketAccessItemExtended {

    private String workbasketKey;

    @Override
    public String getWorkbasketKey() {
        return workbasketKey;
    }

    public void setWorkbasketKey(String workbasketKey) {
        this.workbasketKey = workbasketKey;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((workbasketKey == null) ? 0 : workbasketKey.hashCode());
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
        WorkbasketAccessItemExtendedImpl other = (WorkbasketAccessItemExtendedImpl) obj;
        if (getAccessId() == null) {
            if (other.getAccessId() != null) {
                return false;
            }
        } else if (!getAccessId().equals(other.getAccessId())) {
            return false;
        }
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        if (isPermAppend() != other.isPermAppend()) {
            return false;
        }
        if (isPermCustom1() != other.isPermCustom1()) {
            return false;
        }
        if (isPermCustom10() != other.isPermCustom10()) {
            return false;
        }
        if (isPermCustom11() != other.isPermCustom11()) {
            return false;
        }
        if (isPermCustom12() != other.isPermCustom12()) {
            return false;
        }
        if (isPermCustom2() != other.isPermCustom2()) {
            return false;
        }
        if (isPermCustom3() != other.isPermCustom3()) {
            return false;
        }
        if (isPermCustom4() != other.isPermCustom4()) {
            return false;
        }
        if (isPermCustom5() != other.isPermCustom5()) {
            return false;
        }
        if (isPermCustom6() != other.isPermCustom6()) {
            return false;
        }
        if (isPermCustom7() != other.isPermCustom7()) {
            return false;
        }
        if (isPermCustom8() != other.isPermCustom8()) {
            return false;
        }
        if (isPermCustom9() != other.isPermCustom9()) {
            return false;
        }
        if (isPermDistribute() != other.isPermDistribute()) {
            return false;
        }
        if (isPermOpen() != other.isPermOpen()) {
            return false;
        }
        if (isPermRead() != other.isPermRead()) {
            return false;
        }
        if (isPermTransfer() != other.isPermTransfer()) {
            return false;
        }
        if (getWorkbasketId() == null) {
            if (other.getWorkbasketId() != null) {
                return false;
            }
        } else if (!getWorkbasketId().equals(other.getWorkbasketId())) {
            return false;
        }
        if (getWorkbasketKey() == null) {
            if (other.getWorkbasketKey() != null) {
                return false;
            }
        } else if (!getWorkbasketKey().equals(other.getWorkbasketKey())) {
            return false;
        }
        return true;
    }

}
