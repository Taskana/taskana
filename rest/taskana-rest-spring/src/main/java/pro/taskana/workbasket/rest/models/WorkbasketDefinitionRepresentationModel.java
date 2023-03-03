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
package pro.taskana.workbasket.rest.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.springframework.hateoas.RepresentationModel;

/** this class represents a workbasket including its distro targets and authorisations. */
public class WorkbasketDefinitionRepresentationModel
    extends RepresentationModel<WorkbasketDefinitionRepresentationModel> {

  /** The workbasket which is represented. */
  @JsonIgnoreProperties("_links")
  private WorkbasketRepresentationModel workbasket;
  /** The workbasket authorizations. */
  private Collection<WorkbasketAccessItemRepresentationModel> authorizations = new ArrayList<>();
  /** The distribution targets for this workbasket. */
  private Set<String> distributionTargets = new HashSet<>();

  public Set<String> getDistributionTargets() {
    return distributionTargets;
  }

  public void setDistributionTargets(Set<String> distributionTargets) {
    this.distributionTargets = distributionTargets;
  }

  public Collection<WorkbasketAccessItemRepresentationModel> getAuthorizations() {
    return authorizations;
  }

  public void setAuthorizations(
      Collection<WorkbasketAccessItemRepresentationModel> authorizations) {
    this.authorizations = authorizations;
  }

  public WorkbasketRepresentationModel getWorkbasket() {
    return workbasket;
  }

  public void setWorkbasket(WorkbasketRepresentationModel workbasket) {
    this.workbasket = workbasket;
  }
}
