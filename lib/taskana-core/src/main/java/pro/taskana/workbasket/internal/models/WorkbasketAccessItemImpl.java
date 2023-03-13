package pro.taskana.workbasket.internal.models;

import java.util.Objects;
import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** WorkbasketAccessItemImpl Entity. */
public class WorkbasketAccessItemImpl implements WorkbasketAccessItem {

  private String id;
  private String workbasketId;
  private String workbasketKey;
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

  public WorkbasketAccessItemImpl() {}

  private WorkbasketAccessItemImpl(WorkbasketAccessItemImpl copyFrom) {
    workbasketId = copyFrom.workbasketId;
    workbasketKey = copyFrom.workbasketKey;
    accessId = copyFrom.accessId;
    accessName = copyFrom.accessName;
    permRead = copyFrom.permRead;
    permOpen = copyFrom.permOpen;
    permAppend = copyFrom.permAppend;
    permTransfer = copyFrom.permTransfer;
    permDistribute = copyFrom.permDistribute;
    permCustom1 = copyFrom.permCustom1;
    permCustom2 = copyFrom.permCustom2;
    permCustom3 = copyFrom.permCustom3;
    permCustom4 = copyFrom.permCustom4;
    permCustom5 = copyFrom.permCustom5;
    permCustom6 = copyFrom.permCustom6;
    permCustom7 = copyFrom.permCustom7;
    permCustom8 = copyFrom.permCustom8;
    permCustom9 = copyFrom.permCustom9;
    permCustom10 = copyFrom.permCustom10;
    permCustom11 = copyFrom.permCustom11;
    permCustom12 = copyFrom.permCustom12;
  }

  @Override
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
  }

  @Override
  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
  }

  @Override
  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  @Override
  public String getAccessName() {
    return accessName;
  }

  @Override
  public void setAccessName(String accessName) {
    this.accessName = accessName;
  }

  @Override
  public void setPermission(WorkbasketPermission permission, boolean value) {
    switch (permission) {
      case READ:
        permRead = value;
        break;
      case OPEN:
        permOpen = value;
        break;
      case APPEND:
        permAppend = value;
        break;
      case TRANSFER:
        permTransfer = value;
        break;
      case DISTRIBUTE:
        permDistribute = value;
        break;
      case CUSTOM_1:
        permCustom1 = value;
        break;
      case CUSTOM_2:
        permCustom2 = value;
        break;
      case CUSTOM_3:
        permCustom3 = value;
        break;
      case CUSTOM_4:
        permCustom4 = value;
        break;
      case CUSTOM_5:
        permCustom5 = value;
        break;
      case CUSTOM_6:
        permCustom6 = value;
        break;
      case CUSTOM_7:
        permCustom7 = value;
        break;
      case CUSTOM_8:
        permCustom8 = value;
        break;
      case CUSTOM_9:
        permCustom9 = value;
        break;
      case CUSTOM_10:
        permCustom10 = value;
        break;
      case CUSTOM_11:
        permCustom11 = value;
        break;
      case CUSTOM_12:
        permCustom12 = value;
        break;
      default:
        throw new SystemException("Unknown permission '" + permission + "'");
    }
  }

  @Override
  public boolean getPermission(WorkbasketPermission permission) {
    switch (permission) {
      case READ:
        return permRead;
      case OPEN:
        return permOpen;
      case APPEND:
        return permAppend;
      case TRANSFER:
        return permTransfer;
      case DISTRIBUTE:
        return permDistribute;
      case CUSTOM_1:
        return permCustom1;
      case CUSTOM_2:
        return permCustom2;
      case CUSTOM_3:
        return permCustom3;
      case CUSTOM_4:
        return permCustom4;
      case CUSTOM_5:
        return permCustom5;
      case CUSTOM_6:
        return permCustom6;
      case CUSTOM_7:
        return permCustom7;
      case CUSTOM_8:
        return permCustom8;
      case CUSTOM_9:
        return permCustom9;
      case CUSTOM_10:
        return permCustom10;
      case CUSTOM_11:
        return permCustom11;
      case CUSTOM_12:
        return permCustom12;
      default:
        throw new SystemException("Unknown permission '" + permission + "'");
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

  public boolean isPermCustom9() {
    return permCustom9;
  }

  public void setPermCustom9(boolean permCustom9) {
    this.permCustom9 = permCustom9;
  }

  public boolean isPermCustom10() {
    return permCustom10;
  }

  public void setPermCustom10(boolean permCustom10) {
    this.permCustom10 = permCustom10;
  }

  public boolean isPermCustom11() {
    return permCustom11;
  }

  public void setPermCustom11(boolean permCustom11) {
    this.permCustom11 = permCustom11;
  }

  public boolean isPermCustom12() {
    return permCustom12;
  }

  public void setPermCustom12(boolean permCustom12) {
    this.permCustom12 = permCustom12;
  }

  @Override
  public WorkbasketAccessItemImpl copy() {
    return new WorkbasketAccessItemImpl(this);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        workbasketId,
        workbasketKey,
        accessId,
        accessName,
        permRead,
        permOpen,
        permAppend,
        permTransfer,
        permDistribute,
        permCustom1,
        permCustom2,
        permCustom3,
        permCustom4,
        permCustom5,
        permCustom6,
        permCustom7,
        permCustom8,
        permCustom9,
        permCustom10,
        permCustom11,
        permCustom12);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof WorkbasketAccessItemImpl)) {
      return false;
    }
    WorkbasketAccessItemImpl other = (WorkbasketAccessItemImpl) obj;
    return permRead == other.permRead
        && permOpen == other.permOpen
        && permAppend == other.permAppend
        && permTransfer == other.permTransfer
        && permDistribute == other.permDistribute
        && permCustom1 == other.permCustom1
        && permCustom2 == other.permCustom2
        && permCustom3 == other.permCustom3
        && permCustom4 == other.permCustom4
        && permCustom5 == other.permCustom5
        && permCustom6 == other.permCustom6
        && permCustom7 == other.permCustom7
        && permCustom8 == other.permCustom8
        && permCustom9 == other.permCustom9
        && permCustom10 == other.permCustom10
        && permCustom11 == other.permCustom11
        && permCustom12 == other.permCustom12
        && Objects.equals(id, other.id)
        && Objects.equals(workbasketId, other.workbasketId)
        && Objects.equals(workbasketKey, other.workbasketKey)
        && Objects.equals(accessId, other.accessId)
        && Objects.equals(accessName, other.accessName);
  }

  @Override
  public String toString() {
    return "WorkbasketAccessItem [id="
        + this.id
        + ", workbasketId="
        + this.workbasketId
        + ", workbasketKey="
        + this.workbasketKey
        + ", accessId="
        + this.accessId
        + ", permRead="
        + this.permRead
        + ", permOpen="
        + this.permOpen
        + ", permAppend="
        + this.permAppend
        + ", permTransfer="
        + this.permTransfer
        + ", permDistribute="
        + this.permDistribute
        + ", permCustom1="
        + this.permCustom1
        + ", permCustom2="
        + this.permCustom2
        + ", permCustom3="
        + this.permCustom3
        + ", permCustom4="
        + this.permCustom4
        + ", permCustom5="
        + this.permCustom5
        + ", permCustom6="
        + this.permCustom6
        + ", permCustom7="
        + this.permCustom7
        + ", permCustom8="
        + this.permCustom8
        + ", permCustom9="
        + this.permCustom9
        + ", permCustom10="
        + this.permCustom10
        + ", permCustom11="
        + this.permCustom11
        + ", permCustom12="
        + this.permCustom12
        + "]";
  }
}
