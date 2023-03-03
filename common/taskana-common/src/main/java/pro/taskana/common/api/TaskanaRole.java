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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pro.taskana.common.api.exceptions.SystemException;

/** The TaskanaRole enum contains all roles that are known to TASKANA. */
public enum TaskanaRole {
  USER("taskana.roles.user"),
  BUSINESS_ADMIN("taskana.roles.businessadmin"),
  ADMIN("taskana.roles.admin"),
  MONITOR("taskana.roles.monitor"),
  TASK_ADMIN("taskana.roles.taskadmin"),
  TASK_ROUTER("taskana.roles.taskrouter");

  private final String propertyName;

  TaskanaRole(String propertyName) {
    this.propertyName = propertyName;
  }

  public static TaskanaRole fromPropertyName(String name) {
    return Arrays.stream(TaskanaRole.values())
        .filter(x -> x.propertyName.equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(
            () ->
                new SystemException("Internal System error when processing role property " + name));
  }

  public static List<String> getValidPropertyNames() {
    return Arrays.stream(values()).map(TaskanaRole::getPropertyName).collect(Collectors.toList());
  }

  public String getPropertyName() {
    return propertyName;
  }
}
