package pro.taskana.rest.resource.mapper;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import pro.taskana.WorkbasketSummary;
import pro.taskana.rest.WorkbasketController;
import pro.taskana.rest.resource.WorkbasketSummaryResource;

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

        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(new Link(original.toUriString()).withSelfRel());
        pagedResources.add(linkTo(WorkbasketController.class).withRel("allWorkbaskets"));

        return pagedResources;
    }

    private UriComponentsBuilder getBuilderForOriginalUri() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
            .getRequest();
        UriComponentsBuilder baseUri = ServletUriComponentsBuilder.fromServletMapping(request)
            .path(request.getRequestURI());
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            for (String value : entry.getValue()) {
                baseUri.queryParam(entry.getKey(), value);
            }
        }
        UriComponentsBuilder original = baseUri;
        return original;
    }

}
