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
package pro.taskana.classification.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.beans.ConstructorProperties;
import java.util.Collection;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public class ClassificationCollectionRepresentationModel
    extends CollectionRepresentationModel<ClassificationRepresentationModel> {

  @ConstructorProperties("classifications")
  public ClassificationCollectionRepresentationModel(
      Collection<ClassificationRepresentationModel> content) {
    super(content);
  }

  /** the embedded classifications. */
  @Override
  @JsonProperty("classifications")
  public Collection<ClassificationRepresentationModel> getContent() {
    return super.getContent();
  }
}
