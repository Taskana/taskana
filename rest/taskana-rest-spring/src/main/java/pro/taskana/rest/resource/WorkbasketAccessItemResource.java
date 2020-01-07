package pro.taskana.rest.resource;

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.core.Relation;

import pro.taskana.WorkbasketAccessItem;

/** Resource class for {@link pro.taskana.WorkbasketAccessItem}. */
@Relation(collectionRelation = "accessItems")
public class WorkbasketAccessItemResource extends ResourceSupport {

  public String accessItemId;

  @NotNull public String workbasketId;

  @NotNull public String workbasketKey;

  @NotNull public String accessId;

  public String accessName;

  public boolean permRead;
  public boolean permOpen;
  public boolean permAppend;
  public boolean permTransfer;
  public boolean permDistribute;
  public boolean permCustom1;
  public boolean permCustom2;
  public boolean permCustom3;
  public boolean permCustom4;
  public boolean permCustom5;
  public boolean permCustom6;
  public boolean permCustom7;
  public boolean permCustom8;
  public boolean permCustom9;
  public boolean permCustom10;
  public boolean permCustom11;
  public boolean permCustom12;

  public WorkbasketAccessItemResource() {}

  public WorkbasketAccessItemResource(WorkbasketAccessItem workbasketAccessItem) {
    this.accessItemId = workbasketAccessItem.getId();
    this.workbasketId = workbasketAccessItem.getWorkbasketId();
    this.workbasketKey = workbasketAccessItem.getWorkbasketKey();
    this.accessId = workbasketAccessItem.getAccessId();
    this.accessName = workbasketAccessItem.getAccessName();
    this.permRead = workbasketAccessItem.isPermRead();
    this.permOpen = workbasketAccessItem.isPermOpen();
    this.permAppend = workbasketAccessItem.isPermAppend();
    this.permTransfer = workbasketAccessItem.isPermTransfer();
    this.permDistribute = workbasketAccessItem.isPermDistribute();
    this.permCustom1 = workbasketAccessItem.isPermCustom1();
    this.permCustom2 = workbasketAccessItem.isPermCustom2();
    this.permCustom3 = workbasketAccessItem.isPermCustom3();
    this.permCustom4 = workbasketAccessItem.isPermCustom4();
    this.permCustom5 = workbasketAccessItem.isPermCustom5();
    this.permCustom6 = workbasketAccessItem.isPermCustom6();
    this.permCustom7 = workbasketAccessItem.isPermCustom7();
    this.permCustom8 = workbasketAccessItem.isPermCustom8();
    this.permCustom9 = workbasketAccessItem.isPermCustom9();
    this.permCustom10 = workbasketAccessItem.isPermCustom10();
    this.permCustom11 = workbasketAccessItem.isPermCustom11();
    this.permCustom12 = workbasketAccessItem.isPermCustom12();
  }

  public String getAccessItemId() {
    return accessItemId;
  }

  public void setAccessItemId(String accessItemId) {
    this.accessItemId = accessItemId;
  }

  public String getWorkbasketId() {
    return workbasketId;
  }

  public void setWorkbasketId(String workbasketId) {
    this.workbasketId = workbasketId;
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
    this.accessId = accessId;
  }

  public String getAccessName() {
    return accessName;
  }

  public void setAccessName(String accessName) {
    this.accessName = accessName;
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
  public String toString() {
    return "WorkbasketAccessItemResource ["
        + "accessItemId= "
        + this.accessItemId
        + "workbasketId= "
        + this.workbasketId
        + "workbasketKey= "
        + this.workbasketKey
        + "accessId= "
        + this.accessId
        + "accessName= "
        + this.accessName
        + "]";
  }
}
