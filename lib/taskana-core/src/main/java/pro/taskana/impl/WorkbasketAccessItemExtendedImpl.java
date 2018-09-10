package pro.taskana.impl;

import java.util.Objects;

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
        return Objects.hash(super.hashCode(), workbasketKey);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkbasketAccessItemExtendedImpl)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        WorkbasketAccessItemExtendedImpl that = (WorkbasketAccessItemExtendedImpl) o;
        return Objects.equals(workbasketKey, that.workbasketKey);
    }
}
