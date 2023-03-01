/*-
 * #%L
 * pro.taskana:taskana-common
 * %%
 * Copyright (C) 2019 - 2023 original authors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
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
