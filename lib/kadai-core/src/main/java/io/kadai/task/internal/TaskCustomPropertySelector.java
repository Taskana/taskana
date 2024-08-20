package io.kadai.task.internal;

import io.kadai.common.api.exceptions.SystemException;
import io.kadai.task.api.TaskCustomField;

/** Determines which custom properties are to be updated. */
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

  public boolean isCustom1() {
    return custom1;
  }

  public void setCustom1(boolean custom1) {
    this.custom1 = custom1;
  }

  public boolean isCustom2() {
    return custom2;
  }

  public void setCustom2(boolean custom2) {
    this.custom2 = custom2;
  }

  public boolean isCustom3() {
    return custom3;
  }

  public void setCustom3(boolean custom3) {
    this.custom3 = custom3;
  }

  public boolean isCustom4() {
    return custom4;
  }

  public void setCustom4(boolean custom4) {
    this.custom4 = custom4;
  }

  public boolean isCustom5() {
    return custom5;
  }

  public void setCustom5(boolean custom5) {
    this.custom5 = custom5;
  }

  public boolean isCustom6() {
    return custom6;
  }

  public void setCustom6(boolean custom6) {
    this.custom6 = custom6;
  }

  public boolean isCustom7() {
    return custom7;
  }

  public void setCustom7(boolean custom7) {
    this.custom7 = custom7;
  }

  public boolean isCustom8() {
    return custom8;
  }

  public void setCustom8(boolean custom8) {
    this.custom8 = custom8;
  }

  public boolean isCustom9() {
    return custom9;
  }

  public void setCustom9(boolean custom9) {
    this.custom9 = custom9;
  }

  public boolean isCustom10() {
    return custom10;
  }

  public void setCustom10(boolean custom10) {
    this.custom10 = custom10;
  }

  public boolean isCustom11() {
    return custom11;
  }

  public void setCustom11(boolean custom11) {
    this.custom11 = custom11;
  }

  public boolean isCustom12() {
    return custom12;
  }

  public void setCustom12(boolean custom12) {
    this.custom12 = custom12;
  }

  public boolean isCustom13() {
    return custom13;
  }

  public void setCustom13(boolean custom13) {
    this.custom13 = custom13;
  }

  public boolean isCustom14() {
    return custom14;
  }

  public void setCustom14(boolean custom14) {
    this.custom14 = custom14;
  }

  public boolean isCustom15() {
    return custom15;
  }

  public void setCustom15(boolean custom15) {
    this.custom15 = custom15;
  }

  public boolean isCustom16() {
    return custom16;
  }

  public void setCustom16(boolean custom16) {
    this.custom16 = custom16;
  }

  @Override
  public String toString() {
    return "CustomPropertySelector [custom1="
        + custom1
        + ", custom2="
        + custom2
        + ", custom3="
        + custom3
        + ", custom4="
        + custom4
        + ", custom5="
        + custom5
        + ", custom6="
        + custom6
        + ", custom7="
        + custom7
        + ", custom8="
        + custom8
        + ", custom9="
        + custom9
        + ", custom10="
        + custom10
        + ", custom11="
        + custom11
        + ", custom12="
        + custom12
        + ", custom13="
        + custom13
        + ", custom14="
        + custom14
        + ", custom15="
        + custom15
        + ", custom16="
        + custom16
        + "]";
  }
}
