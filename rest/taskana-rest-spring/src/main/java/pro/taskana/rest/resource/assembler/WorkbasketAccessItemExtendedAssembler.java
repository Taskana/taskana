package pro.taskana.rest.resource.assembler;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pro.taskana.WorkbasketAccessItemExtended;
import pro.taskana.rest.WorkbasketAccessItemController;
import pro.taskana.rest.resource.WorkbasketAccesItemExtendedResource;

import java.util.List;

import static pro.taskana.rest.resource.assembler.AbstractRessourcesAssembler.getBuilderForOriginalUri;

/**
 * Transforms {@link WorkbasketAccessItemExtended} to its resource counterpart {@link WorkbasketAccesItemExtendedResource} and vice versa.
 */
@Component
public class WorkbasketAccessItemExtendedAssembler extends ResourceAssemblerSupport<WorkbasketAccessItemExtended, WorkbasketAccesItemExtendedResource> {

    /**
     * Creates a new {@link ResourceAssemblerSupport} using the given controller class and resource type.
     */
    public WorkbasketAccessItemExtendedAssembler() {
        super(WorkbasketAccessItemController.class, WorkbasketAccesItemExtendedResource.class);
    }

    @Override
    public WorkbasketAccesItemExtendedResource toResource(WorkbasketAccessItemExtended workbasketAccessItemExtended) {
        WorkbasketAccesItemExtendedResource resource = createResourceWithId(workbasketAccessItemExtended.getId(), workbasketAccessItemExtended);
        resource.removeLinks();
        BeanUtils.copyProperties(workbasketAccessItemExtended, resource);
        // named different so needs to be set by hand
        resource.setAccessItemId(workbasketAccessItemExtended.getId());
        return resource;
    }

    public PagedResources<WorkbasketAccesItemExtendedResource> toResources(List<WorkbasketAccessItemExtended> workbasketAccessItems,
                                                                           PagedResources.PageMetadata pageMetadata) {

        WorkbasketAccessItemExtendedAssembler assembler = new WorkbasketAccessItemExtendedAssembler();
        List<WorkbasketAccesItemExtendedResource> resources = assembler.toResources(workbasketAccessItems);

        PagedResources<WorkbasketAccesItemExtendedResource> pagedResources = new PagedResources<WorkbasketAccesItemExtendedResource>(
                resources,
                pageMetadata);

        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        if (pageMetadata != null) {
            pagedResources.add(new Link(original.replaceQueryParam("page", 1).toUriString()).withRel(Link.REL_FIRST));
            pagedResources.add(new Link(original.replaceQueryParam("page", pageMetadata.getTotalPages()).toUriString())
                    .withRel(Link.REL_LAST));
            if (pageMetadata.getNumber() > 1) {
                pagedResources
                        .add(new Link(original.replaceQueryParam("page", pageMetadata.getNumber() - 1).toUriString())
                                .withRel(Link.REL_PREVIOUS));
            }
            if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
                pagedResources
                        .add(new Link(original.replaceQueryParam("page", pageMetadata.getNumber() + 1).toUriString())
                                .withRel(Link.REL_NEXT));
            }
        }

        return pagedResources;
    }

}
