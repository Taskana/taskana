package pro.taskana.rest.resource;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;


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
        Iterator var2 = request.getParameterMap().entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, String[]> entry = (Map.Entry) var2.next();
            String[] var4 = (String[]) entry.getValue();
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String value = var4[var6];
                baseUri.queryParam((String) entry.getKey(), new Object[] {value});
            }
        }

        return baseUri;
    }

    protected PagedResources<?> addPageLinks(PagedResources<?> pagedResources,
        PagedResources.PageMetadata pageMetadata) {
        UriComponentsBuilder original = getBuilderForOriginalUri();
        pagedResources.add(
            (new Link(original.replaceQueryParam("page", new Object[] {1}).toUriString())).withRel("first"));
        pagedResources.add((new Link(
            original.replaceQueryParam("page", new Object[] {pageMetadata.getTotalPages()}).toUriString())).withRel(
            "last"));
        if (pageMetadata.getNumber() > 1L) {
            pagedResources.add((new Link(
                original.replaceQueryParam("page", new Object[] {pageMetadata.getNumber() - 1L})
                    .toUriString())).withRel("prev"));
        }

        if (pageMetadata.getNumber() < pageMetadata.getTotalPages()) {
            pagedResources.add((new Link(
                original.replaceQueryParam("page", new Object[] {pageMetadata.getNumber() + 1L})
                    .toUriString())).withRel("next"));
        }

        return pagedResources;
    }
}
