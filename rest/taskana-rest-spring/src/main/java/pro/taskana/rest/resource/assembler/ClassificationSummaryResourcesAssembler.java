package pro.taskana.rest.resource.assembler;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.util.UriComponentsBuilder;
import pro.taskana.ClassificationSummary;
import pro.taskana.rest.ClassificationController;
import pro.taskana.rest.resource.ClassificationSummaryResource;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static pro.taskana.rest.resource.assembler.AbstractRessourcesAssembler.getBuilderForOriginalUri;

/**
 * @author HH
 */
public class ClassificationSummaryResourcesAssembler {

    public ClassificationSummaryResourcesAssembler() {
    }

    public PagedResources<ClassificationSummaryResource> toResources(
        List<ClassificationSummary> classificationSummaries,
        PageMetadata pageMetadata) {

        ClassificationSummaryResourceAssembler assembler = new ClassificationSummaryResourceAssembler();
        List<ClassificationSummaryResource> resources = assembler.toResources(classificationSummaries);
        PagedResources<ClassificationSummaryResource> pagedResources = new PagedResources<ClassificationSummaryResource>(
            resources,
            pageMetadata);

        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        if (pageMetadata != null) {
            pagedResources.add(linkTo(ClassificationController.class).withRel("allClassifications"));
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
