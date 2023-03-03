/*-
 * #%L
 * pro.taskana:taskana-common-security
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
package pro.taskana.common.api.security;

import java.security.Principal;
import java.util.Objects;

/** Represents a user with a name. */
public class UserPrincipal implements Principal {

  private final String name;

  public UserPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof UserPrincipal)) {
      return false;
    }
    UserPrincipal other = (UserPrincipal) obj;
    return Objects.equals(name, other.name);
  }

  @Override
  public String toString() {
    return "UserPrincipal [name=" + name + "]";
  }
}
