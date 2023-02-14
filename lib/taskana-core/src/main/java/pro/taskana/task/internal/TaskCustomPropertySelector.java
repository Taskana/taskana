package pro.taskana.task.internal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import pro.taskana.common.api.exceptions.SystemException;
import pro.taskana.task.api.TaskCustomField;

/** Determines which custom properties are to be updated. */
@Getter
@Setter
@ToString
public class TaskCustomPropertySelector {

  boolean custom1 = false;
  boolean custom2 = false;
  boolean custom3 = false;
  boolean custom4 = false;
  boolean custom5 = false;
  boolean custom6 = false;
  boolean custom7 = false;
  boolean custom8 = false;
  boolean custom9 = false;
  boolean custom10 = false;
  boolean custom11 = false;
  boolean custom12 = false;
  boolean custom13 = false;
  boolean custom14 = false;
  boolean custom15 = false;
  boolean custom16 = false;

  public void setCustomProperty(TaskCustomField customField, boolean value) {
    switch (customField) {
      case CUSTOM_1:
        this.setCustom1(value);
        break;
      case CUSTOM_2:
        this.setCustom2(value);
        break;
      case CUSTOM_3:
        this.setCustom3(value);
        break;
      case CUSTOM_4:
        this.setCustom4(value);
        break;
      case CUSTOM_5:
        this.setCustom5(value);
        break;
      case CUSTOM_6:
        this.setCustom6(value);
        break;
      case CUSTOM_7:
        this.setCustom7(value);
        break;
      case CUSTOM_8:
        this.setCustom8(value);
        break;
      case CUSTOM_9:
        this.setCustom9(value);
        break;
      case CUSTOM_10:
        this.setCustom10(value);
        break;
      case CUSTOM_11:
        this.setCustom11(value);
        break;
      case CUSTOM_12:
        this.setCustom12(value);
        break;
      case CUSTOM_13:
        this.setCustom13(value);
        break;
      case CUSTOM_14:
        this.setCustom14(value);
        break;
      case CUSTOM_15:
        this.setCustom15(value);
        break;
      case CUSTOM_16:
        this.setCustom16(value);
        break;
      default:
        throw new SystemException("Unknown customField '" + customField + "'");
    }
  }
}
