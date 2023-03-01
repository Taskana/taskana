/*-
 * #%L
 * pro.taskana:taskana-rest-spring
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
package pro.taskana.common.rest.models;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/** EntityModel for Access Id. */
public class AccessIdRepresentationModel extends RepresentationModel<AccessIdRepresentationModel> {

  /** The name of this Access Id. */
  private String name;
  /**
   * The value of the Access Id. This value will be used to determine the access to a workbasket.
   */
  private String accessId;

  public AccessIdRepresentationModel() {}

  public AccessIdRepresentationModel(String name, String accessId) {
    this.accessId = accessId;
    this.name = name;
  }

  public String getAccessId() {
    return accessId;
  }

  public void setAccessId(String accessId) {
    this.accessId = accessId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public @NonNull String toString() {
    return "AccessIdResource [" + "name=" + this.name + ", accessId=" + this.accessId + "]";
  }
}
