package pro.taskana.common.api.exceptions;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ErrorCode implements Serializable {
  private final String key;
  // Unfortunately this is necessary. The Map interface does not implement Serializable..
  private final HashMap<String, Serializable> messageVariables;

  private ErrorCode(String key, Map<String, Serializable> messageVariables) {
    this.key = key;
    this.messageVariables = new HashMap<>(messageVariables);
  }

  public static ErrorCode of(String key, Map<String, Serializable> messageVariables) {
    return new ErrorCode(key, messageVariables);
  }

  public static ErrorCode of(String key) {
    return new ErrorCode(key, Collections.emptyMap());
  }

  public String getKey() {
    return key;
  }

  public Map<String, Serializable> getMessageVariables() {
    return messageVariables;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, messageVariables);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ErrorCode)) {
      return false;
    }
    ErrorCode other = (ErrorCode) obj;
    return Objects.equals(key, other.key)
        && Objects.equals(messageVariables, other.messageVariables);
  }

  @Override
  public String toString() {
    return "ErrorCode [key=" + key + ", messageVariables=" + messageVariables + "]";
  }
}
