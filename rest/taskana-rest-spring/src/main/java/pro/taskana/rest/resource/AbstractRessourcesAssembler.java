package pro.taskana.rest.resource;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Abstract resources assembler for taskana REST controller with pageable resources.
 * This method is deprecated, it can be removed after fixing taskana-simple-history references
 */
@Deprecated
public abstract class AbstractRessourcesAssembler {

    UriComponentsBuilder original = getBuilderForOriginalUri();

    public AbstractRessourcesAssembler() {
    }

    protected static UriComponentsBuilder getBuilderForOriginalUri() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        UriComponentsBuilder baseUri = ServletUriComponentsBuilder.fromServletMapping(request)
            .path(request.getRequestURI());

        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            String[] var4 = entry.getValue();
            for (String value : var4) {
                baseUri.queryParam(entry.getKey(), value);
            }
        }

        return baseUri;
    }

    protected PagedResources<?> addPageLinks(PagedResources<?> pagedResources,
        PagedResources.PageMetadata pageMetadata) {
        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(
            (new Link(original.replaceQueryParam("page", 1).toUriString())).withRel("first"));
        pagedResources.add((new Link(
            original.replaceQueryParam("page", pageMetadata.getTotalPages()).toUriString())).withRel(
            "last"));
        if (pageMetadata.getNumber() > 1L) {
            pagedResources.add((new Link(
                original.replaceQueryParam("page", pageMetadata.getNumber() - 1L)
                    .toUriString())).withRel("prev"));
        }

        if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
            pagedResources.add((new Link(
                original.replaceQueryParam("page", pageMetadata.getNumber() + 1L)
                    .toUriString())).withRel("next"));
        }

        return pagedResources;
    }
}
