package pro.taskana.rest.resource.assembler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.PagedResources.PageMetadata;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base assembler for paged list resources.
 */
public abstract class AbstractRessourcesAssembler {

    UriComponentsBuilder original;

    public AbstractRessourcesAssembler() {
        original = getBuilderForOriginalUri();
    }

    protected UriComponentsBuilder getBuilderForOriginalUri() {
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

    protected PagedResources<?> addPageLinks(PagedResources<?> pagedResources, PageMetadata pageMetadata) {
        UriComponentsBuilder original = getBuilderForOriginalUri();
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

        return pagedResources;
    }

}
