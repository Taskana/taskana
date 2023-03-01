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
package pro.taskana.common.api;

import java.util.Objects;

/** This class encapsulates key - domain pairs for identification of workbaskets. */
public class KeyDomain {

  private String key;
  private String domain;

  public KeyDomain(String key, String domain) {
    this.key = key;
    this.domain = domain;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getDomain() {
    return domain;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, domain);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof KeyDomain)) {
      return false;
    }
    KeyDomain other = (KeyDomain) obj;
    return Objects.equals(key, other.key) && Objects.equals(domain, other.domain);
  }

  @Override
  public String toString() {
    return "KeyDomain [key=" + key + ", domain=" + domain + "]";
  }
}
