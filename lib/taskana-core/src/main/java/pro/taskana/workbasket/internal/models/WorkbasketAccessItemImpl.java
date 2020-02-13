package pro.taskana.workbasket.internal.models;

import java.util.Objects;

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

  public WorkbasketAccessItemImpl() {
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

  @Override
  public String getWorkbasketKey() {
    return workbasketKey;
  }

  public void setWorkbasketKey(String workbasketKey) {
    this.workbasketKey = workbasketKey;
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
    this.accessId = accessId;
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
