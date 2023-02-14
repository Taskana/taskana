package pro.taskana.workbasket.internal.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.workbasket.api.WorkbasketPermission;
import pro.taskana.workbasket.api.models.WorkbasketAccessItem;

/** WorkbasketAccessItemImpl Entity. */
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
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

  @Override
  public WorkbasketAccessItemImpl copy() {
    return new WorkbasketAccessItemImpl(this);
  }
}
