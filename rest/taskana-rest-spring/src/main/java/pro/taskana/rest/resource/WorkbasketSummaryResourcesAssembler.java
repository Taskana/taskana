package pro.taskana.rest.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;

/**
 * @author HH
 */
public class WorkbasketSummaryResourcesAssembler {

    public WorkbasketSummaryResourcesAssembler() {
    }

    public PagedResources<WorkbasketSummaryResource> toResources(List<WorkbasketSummary> workbasketSummaries,
        PageMetadata pageMetadata) {

        WorkbasketSummaryResourceAssembler assembler = new WorkbasketSummaryResourceAssembler();
        List<WorkbasketSummaryResource> resources = assembler.toResources(workbasketSummaries);
        PagedResources<WorkbasketSummaryResource> pagedResources = new PagedResources<WorkbasketSummaryResource>(
            resources,
            pageMetadata);

        UriComponentsBuilder original = AbstractRessourcesAssembler.getBuilderForOriginalUri();
        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        if (pageMetadata != null) {
            pagedResources.add(linkTo(WorkbasketController.class).withRel("allWorkbaskets"));
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
