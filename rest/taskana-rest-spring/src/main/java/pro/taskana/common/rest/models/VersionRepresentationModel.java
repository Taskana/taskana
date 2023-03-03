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

import javax.validation.constraints.NotNull;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.lang.NonNull;

/** EntityModel class for version information. */
public class VersionRepresentationModel extends RepresentationModel<VersionRepresentationModel> {

  /** The current TASKANA version of the REST Service. */
  @NotNull private String version;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  @Override
  public @NonNull String toString() {
    return "VersionResource [" + "version= " + this.version + "]";
  }
}
