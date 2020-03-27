package pro.taskana.common.internal.util;

import java.lang.reflect.Field;

public class FieldAndValuePairTriplet {

  private Field field;
  private Object oldValue;
  private Object newValue;

  public FieldAndValuePairTriplet(Field field, Object oldValue, Object newValue) {
    this.field = field;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public void setOldValue(Object oldValue) {
    this.oldValue = oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }

  public void setNewValue(Object newValue) {
    this.newValue = newValue;
  }
}
