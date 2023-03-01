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

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.common.rest.models.PageMetadata;
import pro.taskana.common.rest.models.PagedRepresentationModel;

public interface PagedRepresentationModelAssembler<
        T, D extends RepresentationModel<? super D>, P extends PagedRepresentationModel<D>>
    extends RepresentationModelAssembler<T, D> {

  P buildPageableEntity(Collection<D> content, PageMetadata pageMetadata);

  default P toPagedModel(Iterable<T> entities, PageMetadata pageMetadata) {
    return StreamSupport.stream(entities.spliterator(), false)
        .map(this::toModel)
        .collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                content -> addLinksToPagedModel(buildPageableEntity(content, pageMetadata))));
  }

  default P addLinksToPagedModel(P model) {
    final UriComponentsBuilder original = ServletUriComponentsBuilder.fromCurrentRequest();
    final PageMetadata page = model.getPageMetadata();

    model.add(Link.of(original.toUriString()).withSelfRel());
    if (page != null) {
      model.add(
          Link.of(original.replaceQueryParam("page", 1).toUriString())
              .withRel(IanaLinkRelations.FIRST));
      model.add(
          Link.of(original.replaceQueryParam("page", page.getTotalPages()).toUriString())
              .withRel(IanaLinkRelations.LAST));
      if (page.getNumber() > 1) {
        model.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() - 1).toUriString())
                .withRel(IanaLinkRelations.PREV));
      }
      if (page.getNumber() < page.getTotalPages()) {
        model.add(
            Link.of(original.replaceQueryParam("page", page.getNumber() + 1).toUriString())
                .withRel(IanaLinkRelations.NEXT));
      }
    }
    return model;
  }
}
