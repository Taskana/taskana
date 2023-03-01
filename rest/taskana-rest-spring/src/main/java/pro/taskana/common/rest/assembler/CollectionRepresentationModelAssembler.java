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
package pro.taskana.common.rest.assembler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.common.rest.models.CollectionRepresentationModel;

public interface CollectionRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, C extends CollectionRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  C buildCollectionEntity(List<D> content);

  default C toTaskanaCollectionModel(Iterable<T> entities) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                content -> addLinksToCollectionModel(buildCollectionEntity(content))));
  }

  default C addLinksToCollectionModel(C model) {
    final UriComponentsBuilder original = ServletUriComponentsBuilder.fromCurrentRequest();

    try {
      model.add(Link.of(URLDecoder.decode(original.toUriString(), "UTF-8")).withSelfRel());
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException("UTF-8 encoding not supported. This is unexpected.");
    }
    return model;
  }
}
